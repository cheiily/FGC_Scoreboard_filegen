package pl.cheily.filegen.ResourceModules.Plugins.Decorators;

import pl.cheily.filegen.ResourceModules.Plugins.Decorators.HealthCheck.PluginCommandHealthCheckDecoratorFactory;
import pl.cheily.filegen.ResourceModules.Plugins.Decorators.SafeInvocation.PluginCommandSafeInvocationDecoratorFactory;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.IPluginBase;

public class PluginDecorators {
    public static <T extends IPluginBase> T decorate(T plugin) {
        return PluginCommandHealthCheckDecoratorFactory.get(
                PluginCommandSafeInvocationDecoratorFactory.get(
                        plugin
                )
        );
    }

    @SuppressWarnings("unchecked")
    public static <T extends IPluginBase> T strip(T plugin) {
        IPluginBase ibase = plugin;
        while (ibase instanceof IPluginDecorator) {
            ibase = ((IPluginDecorator) ibase).getUnderlying();
        }
        return (T) ibase;
    }
}
