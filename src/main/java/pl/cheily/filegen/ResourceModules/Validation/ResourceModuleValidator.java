package pl.cheily.filegen.ResourceModules.Validation;

import pl.cheily.filegen.ResourceModules.Exceptions.ResourceModuleValidationException;
import pl.cheily.filegen.ResourceModules.Exceptions.Validation.ValidationError;
import pl.cheily.filegen.ResourceModules.ResourceModule;

import java.util.HashMap;
import java.util.List;

public class ResourceModuleValidator {
    private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ResourceModuleValidator.class);

    private HashMap<ValidationEvent, ResourceModuleValidatorFactory> factoryPerAction;
    {
        factoryPerAction = new HashMap<>();
        factoryPerAction.put(ValidationEvent.DOWNLOAD, new ResourceModuleDownloadValidatorFactory());
    }

    public ResourceModuleValidator() {}

    public List<ValidationError> validate(ResourceModule module, ValidationEvent event) {
        ResourceModuleValidatorFactory factory = factoryPerAction.get(event);
        if (factory == null) {
            logger.warn("No validator factory found for event: {}", event);
            return List.of();
        }

        List<Verifier> verifiers = factory.getAll();
        return verifiers.stream()
                .map(verifier -> verifier.validate(module))
                .flatMap(List::stream)
                .toList();
    }

    public void validateThrowing(ResourceModule module, ValidationEvent event) throws ResourceModuleValidationException {
        ResourceModuleValidatorFactory factory = factoryPerAction.get(event);
        if (factory == null) {
            logger.warn("No validator factory found for event: {}", event);
            return;
        }

        List<Verifier> verifiers = factory.getAll();
        List<ValidationError> errors = verifiers.stream()
                .map(verifier -> verifier.validate(module))
                .flatMap(List::stream)
                .toList();
        if (!errors.isEmpty()) {
            throw ResourceModuleValidationException.fromErrors(
                errors,
                module.getDefinition().name(),
                module.getDefinition().getInstallDirPath().toString()
            );
        }
    }
}
