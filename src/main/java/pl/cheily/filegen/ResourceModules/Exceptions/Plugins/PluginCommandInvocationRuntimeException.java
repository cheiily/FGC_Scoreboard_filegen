package pl.cheily.filegen.ResourceModules.Exceptions.Plugins;

public class PluginCommandInvocationRuntimeException extends RuntimeException {
    private static final String MESSAGE = "Failed to invoke plugin command %s.";

    private PluginCommandInvocationRuntimeException(String message) {
        super(message);
    }

    public static PluginCommandInvocationRuntimeException forCommand(String commandName, Throwable cause) {
        var message = String.format(MESSAGE, commandName);
        var exception = new PluginCommandInvocationRuntimeException(message);
        exception.initCause(cause);
        return exception;
    }

    public static PluginCommandInvocationRuntimeException forCommand(String commandName) {
        var message = String.format(MESSAGE, commandName);
        return new PluginCommandInvocationRuntimeException(message);
    }
}
