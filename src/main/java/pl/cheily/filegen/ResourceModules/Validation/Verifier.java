package pl.cheily.filegen.ResourceModules.Validation;

import pl.cheily.filegen.ResourceModules.Exceptions.Errors.Error;
import pl.cheily.filegen.ResourceModules.ResourceModule;

import java.util.List;

@FunctionalInterface
public interface Verifier {
    public List<Error> validate(ResourceModule module);
}
