package pl.cheily.filegen.ResourceModules.Validation.Factories;

import pl.cheily.filegen.ResourceModules.ResourceModuleType;
import pl.cheily.filegen.ResourceModules.Validation.ValidationEvent;
import pl.cheily.filegen.ResourceModules.Validation.Verifier;

import java.util.List;

public interface ResourceModuleValidatorFactory {
    public ValidationEvent validates();
    public List<Verifier> getFor(ResourceModuleType type);
    public List<Verifier> getAll();
}
