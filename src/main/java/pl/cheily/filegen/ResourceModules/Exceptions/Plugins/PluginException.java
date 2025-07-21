package pl.cheily.filegen.ResourceModules.Exceptions.Plugins;

import pl.cheily.filegen.ResourceModules.Exceptions.ResourceModuleException;

public class PluginException extends ResourceModuleException {
    public PluginException(String message) {
        super(message);
    }
    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }
}
