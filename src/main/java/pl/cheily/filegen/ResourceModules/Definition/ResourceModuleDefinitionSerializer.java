package pl.cheily.filegen.ResourceModules.Definition;

import pl.cheily.filegen.ResourceModules.Exceptions.ResourceModuleDefinitionSerializationException;

@FunctionalInterface
public interface ResourceModuleDefinitionSerializer {
    String serialize(ResourceModuleDefinition definition) throws ResourceModuleDefinitionSerializationException;
}
