package pl.cheily.filegen.ResourceModules.Validation;

import pl.cheily.filegen.ResourceModules.ResourceModuleType;

import java.util.List;

public interface ResourceModuleValidatorFactory {
    public ValidationEvent validates();
    public List<Verifier> getFor(ResourceModuleType type);
    public List<Verifier> getAll();
}
