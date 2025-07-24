package pl.cheily.filegen.ResourceModules.Validation.Errors;

public enum GeneralValidationErrorCode implements ValidationErrorCode {
    INVALID_MODULE_TYPE("Invalid module type."),
    ;

    private final String message;

    GeneralValidationErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
