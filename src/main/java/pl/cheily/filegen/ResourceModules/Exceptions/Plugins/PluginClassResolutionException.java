package pl.cheily.filegen.ResourceModules.Exceptions.Plugins;

import pl.cheily.filegen.ResourceModules.Definition.ResourceModuleDefinition;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleDefinitionData;

public class PluginClassResolutionException extends PluginException {
    private static String MESSAGE = "Failed to resolve service interface implementation class for module {%s}.";

    protected PluginClassResolutionException(String message) {
        super(message);
    }

    private PluginClassResolutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public static PluginClassResolutionException forModule(ResourceModuleDefinition moduleDefinition, Throwable cause) {
        return new PluginClassResolutionException(String.format(
                MESSAGE + "Cause: %s",
                moduleDefinition.qualifiedName(),
                cause.getMessage()
        ));
    }

    public static PluginClassResolutionException forModule(ResourceModuleDefinition moduleDefinition, String withReason) {
        return new PluginClassResolutionException(String.format(
                MESSAGE + "Reason: %s",
                moduleDefinition.qualifiedName(),
                withReason
        ));
    }
}
