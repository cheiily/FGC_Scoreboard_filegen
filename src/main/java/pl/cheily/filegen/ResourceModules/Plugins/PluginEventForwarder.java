package pl.cheily.filegen.ResourceModules.Plugins;

import pl.cheily.filegen.ResourceModules.Events.ResourceModuleEventType;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.IPluginBase;
import pl.cheily.filegen.ResourceModules.ResourceModule;
import pl.cheily.filegen.ResourceModules.ResourceModuleType;

import java.beans.PropertyChangeListener;
import java.util.List;

import static pl.cheily.filegen.ScoreboardApplication.resourceModuleRegistry;

public class PluginEventForwarder {
    private PropertyChangeListener listener = (event) -> {
        if (event.getNewValue() instanceof ResourceModule module) {
            if (module.getDefinition().resourceType().equals(ResourceModuleType.PLUGIN_JAR.name())) {
                updatePlugins(module);
            }
        }
    };

    public PluginEventForwarder() {
        resourceModuleRegistry.eventPipeline.subscribe(ResourceModuleEventType.INSTALLED_MODULE, listener);
        resourceModuleRegistry.eventPipeline.subscribe(ResourceModuleEventType.UNINSTALLED_MODULE, listener);
        resourceModuleRegistry.eventPipeline.subscribe(ResourceModuleEventType.ENABLED_MODULE, listener);
        resourceModuleRegistry.eventPipeline.subscribe(ResourceModuleEventType.DISABLED_MODULE, listener);
    }

    public void updatePlugins(ResourceModule module) {
        resourceModuleRegistry.pluginRegistry.plugins.stream().filter(
                IPluginBase::isEnabled
        ).filter((plugin) -> {
            var def = resourceModuleRegistry.modules.stream().filter(mdl -> mdl.getDefinition().name().equals(module.getDefinition().name())).findAny();
            return def.filter(resourceModule -> ResourceModuleType.valueOf(resourceModule.getDefinition().resourceType()) == ResourceModuleType.PLUGIN_JAR).isPresent();
        }).filter(plugin -> {
            IPluginBase.Requires req;
            if ((req = plugin.getClass().getAnnotation(IPluginBase.Requires.class)) == null)
                return false;

            return req.resourceModule().equals(module.getDefinition().name());
        }).forEach(plugin ->
                plugin.acceptRequiredModuleStatus(List.of(module))
        );
    }
}
