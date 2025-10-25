package pl.cheily.filegen.ResourceModules.Exceptions.Plugins;

import pl.cheily.filegen.ResourceModules.Definition.ResourceModuleDefinition;

public class PluginInstantiationException extends PluginException {
    protected PluginInstantiationException(String message) {
        super(message);
    }

    public PluginInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static PluginInstantiationException forModule(ResourceModuleDefinition moduleDefinition) {
        return new PluginInstantiationException("Failed to instantiate plugin for resource module: " + moduleDefinition.qualifiedName());
    }

    public static PluginInstantiationException forModule(ResourceModuleDefinition moduleDefinition, Throwable cause) {
        return new PluginInstantiationException("Failed to instantiate plugin for resource module: " + moduleDefinition.qualifiedName(), cause);
    }

    public static PluginInstantiationException forModule(ResourceModuleDefinition moduleDefinition, String withReason) {
        return new PluginInstantiationException(
                "Failed to instantiate plugin for resource module: " + moduleDefinition.qualifiedName() + ". Reason: " + withReason
        );
    }
}
