package pl.cheily.filegen.ResourceModules.Plugins;

import pl.cheily.filegen.ResourceModules.Definition.ResourceModuleDefinition;
import pl.cheily.filegen.ResourceModules.Events.ResourceModuleEventType;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.IPluginBase;
import pl.cheily.filegen.ResourceModules.ResourceModule;
import pl.cheily.filegen.Utils.SafeInvocationUtil;

import java.beans.PropertyChangeListener;
import java.util.List;

import static pl.cheily.filegen.ScoreboardApplication.resourceModuleRegistry;

public class PluginHandle<P extends IPluginBase> {
    private final Class<P> pluginClass;
    private P plugin;
    private ResourceModuleDefinition pluginDefinition;
    private ResourceModule resourceModule;
    private boolean initialized = false;


    private PluginHandle(Class<P> pluginClass) {
        this.pluginClass = pluginClass;
    }

    private static final List<ResourceModuleEventType> initEvents = List.of(
            ResourceModuleEventType.INSTALLED_MODULE,
            ResourceModuleEventType.LOADED_INSTALLATIONS,
            ResourceModuleEventType.ENABLED_MODULE
    );

    private final PropertyChangeListener stateChangeListener = evt -> {
        if (evt.getNewValue() instanceof ResourceModule module && module.getDefinition().equals(pluginDefinition)) {
            resourceModule = module;
            System.out.println("isEnabled() = " + isEnabled());
        }
    };

    private final PropertyChangeListener typeInitListener = (event) -> {
        if (initEvents.contains(SafeInvocationUtil.getOrNull(() -> ResourceModuleEventType.valueOf(event.getPropertyName())))) {
            tryInit();
        }
    };

    public static <P extends IPluginBase> PluginHandle<P> ofType(Class<P> pluginClass) {
        var ret = new PluginHandle<>(pluginClass);
        var found = resourceModuleRegistry.pluginRegistry.getOfType(pluginClass);
        if (found == null) {
            initEvents.forEach(evt -> resourceModuleRegistry.eventPipeline.subscribe(evt, ret.typeInitListener));
        } else {
            ret.tryInit(found);
        }
        resourceModuleRegistry.eventPipeline.subscribeToAll(ret.stateChangeListener);
        return ret;
    }

    private void tryInit() {
        var plugin = resourceModuleRegistry.pluginRegistry.getOfType(pluginClass);
        tryInit(plugin);
    }

    private void tryInit(P plugin) {
        if (plugin == null)
            return;

        var module = resourceModuleRegistry.pluginRegistry.findModule(plugin);
        if (module.isEmpty())
            return;

        this.plugin = plugin;
        this.resourceModule = module.get();
        this.pluginDefinition = module.get().getDefinition();

        initEvents.forEach(evt -> resourceModuleRegistry.eventPipeline.unsubscribe(evt, this.typeInitListener));
        this.initialized = true;
        System.out.println("PluginUnit.tryInit");
    }

    public boolean isEnabled() {
        return initialized && resourceModule != null && resourceModule.isEnabled();
    }

    public P get() throws IllegalStateException {
        if (!initialized) {
            throw new IllegalStateException("Plugin of type " + pluginClass.getName() + " is not initialized yet.");
        }
        return plugin;
    }

    public P getOrNull() {
        return initialized ? plugin : null;
    }
}
