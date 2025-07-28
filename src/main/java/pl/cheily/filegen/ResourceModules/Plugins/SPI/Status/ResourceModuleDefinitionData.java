package pl.cheily.filegen.ResourceModules.Plugins.SPI.Status;

public record ResourceModuleDefinitionData(
        String definitionVersion,
        String name,
        String category,
        String installPath,
        String shortDescription,
        String description,
        String version,
        String isoDate,
        String author,
        String url,
        boolean externalUrl,
        String resourceType,
        String archiveType,
        boolean autoinstall,
        boolean autorun,
        String checksum
) {}
