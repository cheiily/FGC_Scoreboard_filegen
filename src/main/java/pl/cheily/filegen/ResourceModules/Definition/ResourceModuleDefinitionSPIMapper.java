package pl.cheily.filegen.ResourceModules.Definition;

import pl.cheily.filegen.ResourceModules.Exceptions.ResourceModuleDefinitionSPIMappingException;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleDefinitionData;

@FunctionalInterface
public interface ResourceModuleDefinitionSPIMapper {
    ResourceModuleDefinitionData map(ResourceModuleDefinition definition) throws ResourceModuleDefinitionSPIMappingException;
}
