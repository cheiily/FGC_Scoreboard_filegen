package pl.cheily.filegen.ResourceModules.Plugins;

import pl.cheily.filegen.ResourceModules.Events.ResourceModuleEventType;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.IPluginBase;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleStatus;
import pl.cheily.filegen.ResourceModules.ResourceModule;
import pl.cheily.filegen.ResourceModules.ResourceModuleRegistry;
import pl.cheily.filegen.ResourceModules.ResourceModuleType;

import java.beans.PropertyChangeListener;
import java.util.List;

import static pl.cheily.filegen.ScoreboardApplication.resourceModuleRegistry;

public class PluginEventForwarder {
    private PluginRegistry pluginRegistry;
    private PropertyChangeListener listener = (event) -> {
        if (event.getNewValue() instanceof ResourceModule module) {
            if (module.getDefinition().resourceType().equals(ResourceModuleType.PLUGIN_JAR.name())) {
                updatePlugins(module);
            }
        }
    };

    public PluginEventForwarder(ResourceModuleRegistry registry, PluginRegistry pluginRegistry) {
        registry.eventPipeline.subscribe(ResourceModuleEventType.INSTALLED_MODULE, listener);
        registry.eventPipeline.subscribe(ResourceModuleEventType.UNINSTALLED_MODULE, listener);
        registry.eventPipeline.subscribe(ResourceModuleEventType.ENABLED_MODULE, listener);
        registry.eventPipeline.subscribe(ResourceModuleEventType.DISABLED_MODULE, listener);
        this.pluginRegistry = pluginRegistry;
    }

    public void updatePlugins(ResourceModule module) {
        ResourceModuleStatus status = pluginRegistry.mapToSPIStatus(module);
        if (status == null) {
            // logged on mapping level
            return;
        }

        resourceModuleRegistry.pluginRegistry.plugins.stream().filter((plugin) -> {
            var def = resourceModuleRegistry.modules.stream().filter(mdl -> mdl.getDefinition().name().equals(module.getDefinition().name())).findAny();
            return def.filter(resourceModule -> ResourceModuleType.valueOf(resourceModule.getDefinition().resourceType()) == ResourceModuleType.PLUGIN_JAR).isPresent();
        }).filter(plugin -> {
            IPluginBase.Requires req = plugin.getClass().getAnnotation(IPluginBase.Requires.class);
            IPluginBase.RequiresCategory reqCat = plugin.getClass().getAnnotation(IPluginBase.RequiresCategory.class);
            if (req == null && reqCat == null)
                return false;

            return req != null
                ? req.resourceModule().equals(module.getDefinition().name())
                : reqCat.resourceModuleCategory().equals(module.getDefinition().category());
        }).forEach(plugin ->
                plugin.acceptRequiredModuleStatus(List.of(status))
        );
    }
}
