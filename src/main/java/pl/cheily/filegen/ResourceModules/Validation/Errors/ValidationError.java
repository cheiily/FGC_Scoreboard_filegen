package pl.cheily.filegen.ResourceModules.Validation.Errors;

public final class ValidationError {
    private final String message;

    ValidationError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public ValidationError withDetails(String details) {
        return new ValidationError(message + " Details: " + details);
    }
}

