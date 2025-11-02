package pl.cheily.filegen.ResourceModules.Plugins.Decorators.SafeInvocation;

import pl.cheily.filegen.ResourceModules.Plugins.Decorators.HealthCheck.PluginCommandHealthCheckDecorator;
import pl.cheily.filegen.ResourceModules.Plugins.Decorators.SafeInvocation.Concrete.FlagProviderSafeInvocationDecorator;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Concrete.FlagProvider.IFlagProvider;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.IPluginBase;

public class PluginCommandSafeInvocationDecoratorFactory {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PluginCommandSafeInvocationDecoratorFactory.class);

    @SuppressWarnings("unchecked")
    public static <T extends IPluginBase> T get(T plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }

        if (plugin instanceof IFlagProvider) {
            return (T) new FlagProviderSafeInvocationDecorator((IFlagProvider) plugin);
        }
        logger.info("No safe invocation decorator found for plugin of type: {}", plugin.getClass().getName());
        return plugin;
    }
}
