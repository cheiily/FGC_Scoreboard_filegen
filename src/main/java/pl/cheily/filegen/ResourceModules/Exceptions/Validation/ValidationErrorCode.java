package pl.cheily.filegen.ResourceModules.Exceptions.Validation;

public sealed interface ValidationErrorCode permits ResourceModuleDownloadValidationErrorCode {
    String getMessage();

    default ValidationError asError() {
        return new ValidationError(getMessage());
    }

    default ValidationError asError(String details) {
        return new ValidationError(getMessage() + " Details: " + details);
    }
}
