package pl.cheily.filegen.ResourceModules.Exceptions.Errors;

public sealed interface ErrorCode permits GeneralResourceModuleErrorCode, PluginInstallationErrorCode, ResourceModuleDownloadErrorCode {
    String getMessage();

    default Error asError() {
        return new Error(getMessage());
    }

    default Error asError(String details) {
        return new Error(getMessage() + " Details: " + details);
    }
}
