package pl.cheily.filegen.ResourceModules.Exceptions;

import pl.cheily.filegen.ResourceModules.Validation.Errors.ValidationError;
import pl.cheily.filegen.ResourceModules.Validation.ValidationEvent;

import java.util.List;

public class ResourceModuleValidationException extends ResourceModuleInstallationManagementException {
    private static final String MESSAGE = "Resource module validation for %s failed with the following errors: %s.\nModule: \"%s\"\nPath: \"%s\".";

    public ResourceModuleValidationException(String message) {
        super(message);
    }

    public ResourceModuleValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ResourceModuleValidationException fromErrors(ValidationEvent event, List<ValidationError> errors, String moduleName, String path) {
        String errorMessages = errors.stream()
                .map(ValidationError::getMessage)
                .reduce((a, b) -> a + "; " + b)
                .orElse("No validation errors provided");
        return new ResourceModuleValidationException(String.format(MESSAGE, event.name(), errorMessages, moduleName, path));
    }
}
