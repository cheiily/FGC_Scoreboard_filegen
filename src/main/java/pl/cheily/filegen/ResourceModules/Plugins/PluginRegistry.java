package pl.cheily.filegen.ResourceModules.Plugins;

import org.slf4j.Logger;
import pl.cheily.filegen.ResourceModules.Definition.ResourceModuleDefinitionHandlerFactory;
import pl.cheily.filegen.ResourceModules.Events.ResourceModuleEventType;
import pl.cheily.filegen.ResourceModules.Exceptions.Plugins.PluginClassLoaderUnloadingException;
import pl.cheily.filegen.ResourceModules.Exceptions.Plugins.PluginClassResolutionException;
import pl.cheily.filegen.ResourceModules.Exceptions.Plugins.PluginInstantiationException;
import pl.cheily.filegen.ResourceModules.Exceptions.Plugins.PluginUninstantiationException;
import pl.cheily.filegen.ResourceModules.Exceptions.ResourceModuleDefinitionSPIMappingException;
import pl.cheily.filegen.ResourceModules.Exceptions.ResourceModuleDefinitionSPIUnmappingException;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Requires;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleDefinitionData;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.IPluginBase;
import pl.cheily.filegen.ResourceModules.ResourceModule;
import pl.cheily.filegen.ResourceModules.ResourceModuleRegistry;

import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class PluginRegistry {
    private final static Logger logger = org.slf4j.LoggerFactory.getLogger(PluginRegistry.class);

    public List<IPluginBase> plugins;
    private ResourceModuleRegistry registry;
    private PluginMultiClassLoader pluginLoader;
    private PluginEventForwarder eventForwarder;
    private PropertyChangeListener listener;

    public PluginRegistry(ResourceModuleRegistry registry) {
        this.registry = registry;
        this.pluginLoader = new PluginMultiClassLoader();
        this.plugins = new java.util.ArrayList<>();
        this.eventForwarder = new PluginEventForwarder(registry, this);
        this.listener = evt -> {
            switch(ResourceModuleEventType.valueOf(evt.getPropertyName())) {
                default: break;
            }
            logger.info("TODO plugin loader.");
        };
    }

    private void register(IPluginBase plugin) {
        if (plugin == null) {
            logger.warn("Attempted to register a null plugin");
        }
        plugins.add(plugin);
    }

    public IPluginBase register(ResourceModule module) throws PluginInstantiationException {
        var definition = module.getDefinition();
        try {
            var clazz = pluginLoader.findClass(definition);
            var instance = clazz.getDeclaredConstructor().newInstance();
            if (instance instanceof IPluginBase plugin) {
                register(plugin);
                logger.info("Plugin {} registered successfully.", plugin.getInfo().qualifiedName());
            } else
                throw PluginInstantiationException.forModule(definition, "Class does not implement IPluginBase");

            return plugin;
        } catch (PluginClassResolutionException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException | UnsupportedClassVersionError e) {
            pluginLoader.unload(module.getDefinition()); // cleanup on failure
            throw PluginInstantiationException.forModule(definition, e);
        }
    }

    public IPluginBase getExisting(ResourceModule module) throws ResourceModuleDefinitionSPIMappingException {
        var def = ResourceModuleDefinitionHandlerFactory.spiMapping(module.getDefinition());

        return plugins.stream()
                .filter(plugin -> plugin.getInfo().equals(def))
                .findFirst()
                .orElse(null);
    }

    public void unregister(IPluginBase plugin) throws PluginClassLoaderUnloadingException {
        if (plugins.remove(plugin)) {
            logger.info("Plugin {} unregistered successfully", plugin.getInfo().name());
        } else {
            logger.warn("Plugin {} not found in registry", plugin.getInfo().name());
        }

        var canUnloadClassLoader = plugins.stream()
                .filter(loadedPlugin -> loadedPlugin.getInfo().equals(plugin.getInfo()))
                .findAny().isEmpty();
        if (canUnloadClassLoader) {
            try {
                pluginLoader.unload(
                        ResourceModuleDefinitionHandlerFactory.fromSpiMapping(plugin.getInfo())
                );
            } catch (ResourceModuleDefinitionSPIUnmappingException e) {
                throw PluginClassLoaderUnloadingException.forModule(plugin.getInfo(), e);
            }
        }
    }

    public void unregister(ResourceModule module) throws PluginUninstantiationException {
        ResourceModuleDefinitionData definition;
        try {
            definition = ResourceModuleDefinitionHandlerFactory.spiMapping(module.getDefinition());
        } catch (ResourceModuleDefinitionSPIMappingException e) {
            throw PluginUninstantiationException.forModule(module.getDefinition(), e);
        }
        var toUnregister = plugins.stream().filter(plugin -> plugin.getInfo().equals(definition)).toList();
        plugins.removeAll(toUnregister);

        pluginLoader.unload(module.getDefinition());
        logger.info("Plugins of module type \"{}\" unregistered successfully", module.getDefinition().qualifiedName());
    }

    public void updateWithDependencies(ResourceModule module) {
        IPluginBase plugin = null;
        try {
            plugin = getExisting(module);
        } catch (ResourceModuleDefinitionSPIMappingException e) {
            logger.error("Cannot update plugin dependencies for module {{}}. Cause: {}", module.getDefinition().qualifiedName(), e.getMessage(), e);
        }
        if (plugin == null)
            return;

        Requires req = plugin.getClass().getAnnotation(Requires.class);
        if (req == null)
            return;

        List<String> requiredModules = List.of(req.resourceModules());
        List<String> requiredCategories = List.of(req.resourceModuleCategories());

        var dependencyStatuses = registry.modules
                .stream().filter(mdl ->
                    requiredModules.contains(mdl.getDefinition().name())
                    || requiredCategories.contains(mdl.getDefinition().category())
                ).map(ResourceModuleDefinitionHandlerFactory::spiStatus)
                .toList();

        plugin.acceptRequiredModuleStatus(dependencyStatuses);
    }

    public void updateDependents(ResourceModule module) {
        var status = List.of(ResourceModuleDefinitionHandlerFactory.spiStatus(module));

        plugins.stream().filter(plugin -> {
            Requires req = plugin.getClass().getAnnotation(Requires.class);
            if (req == null)
                return false;

            List<String> requiredModules = List.of(req.resourceModules());
            List<String> requiredCategories = List.of(req.resourceModuleCategories());

            return requiredModules.contains(plugin.getInfo().name())
                    || requiredCategories.contains(plugin.getInfo().category());
        }).forEach(dependentPlugin -> dependentPlugin.acceptRequiredModuleStatus(status));
    }
}
