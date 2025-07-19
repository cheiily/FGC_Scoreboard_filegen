package pl.cheily.filegen.ResourceModules.Plugins.SPI;

public record PluginData (
    String name,
    String description,
    String version,
    String versionReleaseIsoDate,
    String author
) {}
