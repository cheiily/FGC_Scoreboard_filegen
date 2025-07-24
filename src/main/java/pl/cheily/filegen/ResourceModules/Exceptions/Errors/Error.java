package pl.cheily.filegen.ResourceModules.Exceptions.Errors;

public final class Error {
    private final String message;

    Error(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Error withDetails(String details) {
        return new Error(message + " Details: " + details);
    }
}

