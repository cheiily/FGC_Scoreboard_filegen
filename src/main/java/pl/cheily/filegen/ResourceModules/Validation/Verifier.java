package pl.cheily.filegen.ResourceModules.Validation;

import pl.cheily.filegen.ResourceModules.Exceptions.Validation.ValidationError;
import pl.cheily.filegen.ResourceModules.ResourceModule;

import java.util.List;

@FunctionalInterface
public interface Verifier {
    public List<ValidationError> validate(ResourceModule module);
}
