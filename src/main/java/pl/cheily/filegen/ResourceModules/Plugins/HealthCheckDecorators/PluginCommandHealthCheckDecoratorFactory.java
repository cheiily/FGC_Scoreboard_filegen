package pl.cheily.filegen.ResourceModules.Plugins.HealthCheckDecorators;

import pl.cheily.filegen.ResourceModules.Plugins.SPI.Concrete.FlagProvider.IFlagProvider;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.IPluginBase;

public class PluginCommandHealthCheckDecoratorFactory {
    public static PluginCommandHealthCheckDecorator get(IPluginBase plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }

        if (plugin instanceof IFlagProvider) {
            return new PluginCommandHealthCheckDecorator(plugin);
        }
        return null;
    }
}
