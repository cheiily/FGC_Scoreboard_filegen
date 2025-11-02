package pl.cheily.filegen.ResourceModules.Plugins.Decorators.SafeInvocation;

import pl.cheily.filegen.ResourceModules.Exceptions.Plugins.PluginCommandInvocationRuntimeException;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.IPluginBase;

import java.util.function.Supplier;

public class PluginCommandSafeInvocationDecorator {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PluginCommandSafeInvocationDecorator.class);

    public IPluginBase plugin;
    public PluginCommandSafeInvocationDecorator(IPluginBase plugin) {
        this.plugin = plugin;
    }

    public <R> R invoke(Supplier<R> block, String commandName) throws PluginCommandInvocationRuntimeException {
        try {
            return block.get();
        } catch (Throwable e) {
            logger.info("Plugin safe invocation environment caught an error: {}", e.getMessage(), e);
            throw PluginCommandInvocationRuntimeException.forCommand(commandName, e);
        }
    }
}
