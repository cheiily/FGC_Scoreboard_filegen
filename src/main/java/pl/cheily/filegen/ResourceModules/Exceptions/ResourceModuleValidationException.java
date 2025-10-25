package pl.cheily.filegen.ResourceModules.Exceptions;

import pl.cheily.filegen.ResourceModules.ResourceModule;
import pl.cheily.filegen.ResourceModules.Validation.ValidationEvent;

import java.util.List;

public class ResourceModuleValidationException extends ResourceModuleInstallationManagementException {
    private static final String MESSAGE = "%s validation for resource module {%s} failed with the following errors: {%s}.\nPath: \"%s\".";

    public ResourceModuleValidationException(String message) {
        super(message);
    }

    public ResourceModuleValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ResourceModuleValidationException fromErrors(ValidationEvent event, List<Error> errors, ResourceModule module) {
        String errorMessages = errors.stream()
                .map(Error::getMessage)
                .reduce((a, b) -> a + "; " + b)
                .orElse("No validation errors provided");
        return new ResourceModuleValidationException(
                String.format(MESSAGE,
                        event.name(),
                        module.getDefinition().qualifiedName(),
                        errorMessages,
                        module.getDefinition().getInstallDirPath()
                ));
    }
}
