package pl.cheily.filegen.ResourceModules.Validation.Errors;

public sealed interface ValidationErrorCode permits GeneralValidationErrorCode, ResourceModuleDownloadValidationErrorCode {
    String getMessage();

    default ValidationError asError() {
        return new ValidationError(getMessage());
    }

    default ValidationError asError(String details) {
        return new ValidationError(getMessage() + " Details: " + details);
    }
}
