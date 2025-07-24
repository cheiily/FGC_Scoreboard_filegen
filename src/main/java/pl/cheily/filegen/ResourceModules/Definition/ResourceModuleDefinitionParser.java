package pl.cheily.filegen.ResourceModules.Definition;

import pl.cheily.filegen.ResourceModules.Exceptions.ResourceModuleDefinitionParseException;

@FunctionalInterface
public interface ResourceModuleDefinitionParser {
    ResourceModuleDefinition parse(String jsonString) throws ResourceModuleDefinitionParseException;
}

