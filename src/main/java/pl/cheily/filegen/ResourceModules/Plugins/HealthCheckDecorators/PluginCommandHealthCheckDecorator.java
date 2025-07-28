package pl.cheily.filegen.ResourceModules.Plugins.HealthCheckDecorators;

import pl.cheily.filegen.ResourceModules.Plugins.SPI.IPluginBase;

import java.util.function.Supplier;

public class PluginCommandHealthCheckDecorator {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PluginCommandHealthCheckDecorator.class);

    public IPluginBase plugin;
    public PluginCommandHealthCheckDecorator(IPluginBase plugin) {
        this.plugin = plugin;
    }

    public <R> R invoke(Supplier<R> block) {
        try {
            return block.get();
        } catch (Exception e) {
            logger.error("Error during plugin command execution: {}", e.getMessage(), e);
            logger.info("Plugin health status: {}", plugin.getHealthStatus().healthRecords());
        }
        return null;
    }
}
