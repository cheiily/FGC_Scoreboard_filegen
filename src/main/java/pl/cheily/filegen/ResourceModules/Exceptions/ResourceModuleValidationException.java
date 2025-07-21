package pl.cheily.filegen.ResourceModules.Exceptions;

import pl.cheily.filegen.ResourceModules.Exceptions.Validation.ValidationError;

import java.util.List;

public class ResourceModuleValidationException extends ResourceModuleInstallationManagementException {
    private static final String MESSAGE = "Resource module validation failed with the following errors: %s. Module \"%s\", path \"%s\".";

    public ResourceModuleValidationException(String message) {
        super(message);
    }

    public ResourceModuleValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ResourceModuleValidationException fromErrors(List<ValidationError> errors, String moduleName, String path) {
        String errorMessages = errors.stream()
                .map(ValidationError::getMessage)
                .reduce((a, b) -> a + "; " + b)
                .orElse("No validation errors provided");
        return new ResourceModuleValidationException(String.format(MESSAGE, errorMessages, moduleName, path));
    }
}
