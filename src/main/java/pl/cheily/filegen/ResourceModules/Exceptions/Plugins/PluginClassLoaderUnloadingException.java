package pl.cheily.filegen.ResourceModules.Exceptions.Plugins;

import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleDefinitionData;

public class PluginClassLoaderUnloadingException extends PluginException {
    protected PluginClassLoaderUnloadingException(String message) {
        super(message);
    }

    private PluginClassLoaderUnloadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public static PluginClassLoaderUnloadingException forModule(ResourceModuleDefinitionData moduleDefinition) {
        return new PluginClassLoaderUnloadingException("Failed to uninstantiate plugin class loader for resource module: " + moduleDefinition.qualifiedName());
    }

    public static PluginClassLoaderUnloadingException forModule(ResourceModuleDefinitionData moduleDefinition, Throwable cause) {
        return new PluginClassLoaderUnloadingException("Failed to uninstantiate plugin class loader for resource module: " + moduleDefinition.qualifiedName(), cause);
    }

    public static PluginClassLoaderUnloadingException forModule(ResourceModuleDefinitionData moduleDefinition, String withReason) {
        return new PluginClassLoaderUnloadingException(
                "Failed to uninstantiate plugin class loader for resource module: " + moduleDefinition.qualifiedName() + ". Reason: " + withReason
        );
    }
}
