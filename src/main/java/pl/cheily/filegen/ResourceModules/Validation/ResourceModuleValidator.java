package pl.cheily.filegen.ResourceModules.Validation;

import pl.cheily.filegen.ResourceModules.Exceptions.ResourceModuleValidationException;
import pl.cheily.filegen.ResourceModules.Validation.Errors.GeneralValidationErrorCode;
import pl.cheily.filegen.ResourceModules.Validation.Errors.ValidationError;
import pl.cheily.filegen.ResourceModules.ResourceModule;
import pl.cheily.filegen.ResourceModules.ResourceModuleType;
import pl.cheily.filegen.ResourceModules.Validation.Factories.ResourceModuleValidatorFactory;
import pl.cheily.filegen.ResourceModules.Validation.Factories.StaticsCollectionDownloadValidatorFactory;

import java.util.HashMap;
import java.util.List;

public class ResourceModuleValidator {
    private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ResourceModuleValidator.class);

    private HashMap<ValidationEvent, ResourceModuleValidatorFactory> factoryPerAction;
    {
        factoryPerAction = new HashMap<>();
        factoryPerAction.put(ValidationEvent.DOWNLOAD, new StaticsCollectionDownloadValidatorFactory());
    }

    public ResourceModuleValidator() {}

    public List<ValidationError> validate(ResourceModule module, ValidationEvent event) {
        ResourceModuleValidatorFactory factory = factoryPerAction.get(event);
        if (factory == null) {
            logger.warn("No validator factory found for event: {}", event);
            return List.of();
        }

        ResourceModuleType type;
        try {
            type = ResourceModuleType.valueOf(module.getDefinition().resourceType());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid module, couldn't parse type! : {}", module, e);
            return List.of(GeneralValidationErrorCode.INVALID_MODULE_TYPE.asError(" Type: " + module.getDefinition().resourceType()));
        }

        List<Verifier> verifiers = factory.getFor(type);
        return verifiers.stream()
                .map(verifier -> verifier.validate(module))
                .flatMap(List::stream)
                .toList();
    }

    public void validateThrowing(ResourceModule module, ValidationEvent event) throws ResourceModuleValidationException {
        List<ValidationError> errors = validate(module, event);

        if (!errors.isEmpty()) {
            throw ResourceModuleValidationException.fromErrors(
                event,
                errors,
                module.getDefinition().name(),
                module.getDefinition().getInstallDirPath().toString()
            );
        }
    }
}
