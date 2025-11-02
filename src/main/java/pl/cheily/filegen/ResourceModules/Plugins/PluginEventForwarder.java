package pl.cheily.filegen.ResourceModules.Plugins;

import pl.cheily.filegen.ResourceModules.Events.ResourceModuleEventType;
import pl.cheily.filegen.ResourceModules.ResourceModule;
import pl.cheily.filegen.ResourceModules.ResourceModuleRegistry;
import pl.cheily.filegen.ResourceModules.ResourceModuleType;

import java.beans.PropertyChangeListener;

import static pl.cheily.filegen.ResourceModules.Events.ResourceModuleEventType.LOADED_INSTALLATIONS;
import static pl.cheily.filegen.ScoreboardApplication.resourceModuleRegistry;

public class PluginEventForwarder {
    private PluginRegistry pluginRegistry;
    private PropertyChangeListener listener = (event) -> {
        if (event.getNewValue() instanceof ResourceModule module) {
            pluginRegistry.updateDependents(module);
            pluginRegistry.updateWithDependencies(module);
        }
    };

    public PluginEventForwarder(ResourceModuleRegistry registry, PluginRegistry pluginRegistry) {
        registry.eventPipeline.subscribe(ResourceModuleEventType.INSTALLED_MODULE, listener);
        registry.eventPipeline.subscribe(ResourceModuleEventType.UNINSTALLED_MODULE, listener);
        registry.eventPipeline.subscribe(ResourceModuleEventType.ENABLED_MODULE, listener);
        registry.eventPipeline.subscribe(ResourceModuleEventType.DISABLED_MODULE, listener);
        this.pluginRegistry = pluginRegistry;
    }
}
