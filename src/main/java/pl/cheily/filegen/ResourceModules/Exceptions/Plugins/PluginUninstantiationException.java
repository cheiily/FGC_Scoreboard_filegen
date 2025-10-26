package pl.cheily.filegen.ResourceModules.Exceptions.Plugins;

import pl.cheily.filegen.ResourceModules.Definition.ResourceModuleDefinition;

public class PluginUninstantiationException extends PluginException {
    protected PluginUninstantiationException(String message) {
        super(message);
    }

    private PluginUninstantiationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static PluginUninstantiationException forModule(ResourceModuleDefinition moduleDefinition) {
        return new PluginUninstantiationException("Failed to uninstantiate plugin for resource module: " + moduleDefinition.qualifiedName());
    }

    public static PluginUninstantiationException forModule(ResourceModuleDefinition moduleDefinition, Throwable cause) {
        return new PluginUninstantiationException("Failed to uninstantiate plugin for resource module: " + moduleDefinition.qualifiedName(), cause);
    }

    public static PluginUninstantiationException forModule(ResourceModuleDefinition moduleDefinition, String withReason) {
        return new PluginUninstantiationException(
                "Failed to uninstantiate plugin for resource module: " + moduleDefinition.qualifiedName() + ". Reason: " + withReason
        );
    }
}
