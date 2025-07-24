package pl.cheily.filegen.ResourceModules.Exceptions.Errors;

public sealed interface ErrorCode permits GeneralResourceModuleErrorCode, ResourceModuleDownloadValidationErrorCode {
    String getMessage();

    default Error asError() {
        return new Error(getMessage());
    }

    default Error asError(String details) {
        return new Error(getMessage() + " Details: " + details);
    }
}
