package pl.cheily.filegen.ResourceModules.Plugins;

import org.slf4j.Logger;
import pl.cheily.filegen.ResourceModules.Events.ResourceModuleEventType;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.IPluginBase;

import java.beans.PropertyChangeListener;
import java.util.List;

public class PluginRegistry {
    private final static Logger logger = org.slf4j.LoggerFactory.getLogger(PluginRegistry.class);

    public List<IPluginBase> plugins;
    private PluginEventForwarder eventForwarder;
    private PropertyChangeListener listener;

    public PluginRegistry() {
        this.plugins = new java.util.ArrayList<>();
        this.eventForwarder = new PluginEventForwarder();
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
}
