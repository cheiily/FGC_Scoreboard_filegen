package pl.cheily.filegen.ResourceModules.Definition;

import pl.cheily.filegen.ResourceModules.Exceptions.ResourceModuleDefinitionSPIUnmappingException;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleDefinitionData;

@FunctionalInterface
public interface ResourceModuleDefinitionSPIUnmapper {
    ResourceModuleDefinition unmap(ResourceModuleDefinitionData definition) throws ResourceModuleDefinitionSPIUnmappingException;
}
