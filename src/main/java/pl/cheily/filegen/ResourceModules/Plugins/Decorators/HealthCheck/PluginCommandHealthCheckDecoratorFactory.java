package pl.cheily.filegen.ResourceModules.Plugins.Decorators.HealthCheck;

import pl.cheily.filegen.ResourceModules.Plugins.Decorators.HealthCheck.Concrete.FlagProviderHealthCheckDecorator;
import pl.cheily.filegen.ResourceModules.Plugins.Decorators.SafeInvocation.Concrete.FlagProviderSafeInvocationDecorator;
import pl.cheily.filegen.ResourceModules.Plugins.Decorators.SafeInvocation.PluginCommandSafeInvocationDecoratorFactory;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Concrete.FlagProvider.IFlagProvider;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.IPluginBase;

public class PluginCommandHealthCheckDecoratorFactory {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PluginCommandHealthCheckDecoratorFactory.class);

    @SuppressWarnings("unchecked")
    public static <T extends IPluginBase> T get(T plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }

        if (plugin instanceof IFlagProvider) {
            return (T) new FlagProviderHealthCheckDecorator((IFlagProvider) plugin);
        }
        logger.error("No safe invocation decorator found for plugin of type: {}", plugin.getClass().getName());
        return plugin;
    }
}
