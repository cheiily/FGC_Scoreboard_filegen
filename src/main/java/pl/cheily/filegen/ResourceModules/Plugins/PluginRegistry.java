package pl.cheily.filegen.ResourceModules.Plugins;

import org.slf4j.Logger;
import pl.cheily.filegen.ResourceModules.Definition.ResourceModuleDefinitionHandlerFactory;
import pl.cheily.filegen.ResourceModules.Events.ResourceModuleEventType;
import pl.cheily.filegen.ResourceModules.Exceptions.ResourceModuleDefinitionSPIMappingException;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleStatus;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.IPluginBase;
import pl.cheily.filegen.ResourceModules.ResourceModule;
import pl.cheily.filegen.ResourceModules.ResourceModuleRegistry;

import java.beans.PropertyChangeListener;
import java.util.List;

public class PluginRegistry {
    private final static Logger logger = org.slf4j.LoggerFactory.getLogger(PluginRegistry.class);

    public List<IPluginBase> plugins;
    private ResourceModuleRegistry registry;
    private PluginEventForwarder eventForwarder;
    private PropertyChangeListener listener;

    public PluginRegistry(ResourceModuleRegistry registry) {
        this.registry = registry;
        this.plugins = new java.util.ArrayList<>();
        this.eventForwarder = new PluginEventForwarder(registry, this);
        this.listener = evt -> {
            switch(ResourceModuleEventType.valueOf(evt.getPropertyName())) {
                default: break;
            }
            logger.info("TODO plugin loader.");
        };
    }

    public void register(IPluginBase plugin) {
        if (plugin == null) {
            logger.warn("Attempted to register a null plugin");
        }
        plugins.add(plugin);
    }

    public void unregister(IPluginBase plugin) {
        if (plugins.remove(plugin)) {
            logger.info("Plugin {} unregistered successfully", plugin.getInfo().name());
        } else {
            logger.warn("Plugin {} not found in registry", plugin.getInfo().name());
        }
    }

    public ResourceModuleStatus mapToSPIStatus(ResourceModule module) {
        try {
            return new ResourceModuleStatus(
                    module.isDownloaded(),
                    module.isInstalled(),
                    module.isEnabled(),
                    ResourceModuleDefinitionHandlerFactory.spiMapping(module.getDefinition()),
                    module.getDefinition().getInstallDirPath(),
                    module.getDefinition().getInstallFilePath(),
                    module.getDefinition().getInstallContainerDirPath()
            );
        } catch (ResourceModuleDefinitionSPIMappingException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
