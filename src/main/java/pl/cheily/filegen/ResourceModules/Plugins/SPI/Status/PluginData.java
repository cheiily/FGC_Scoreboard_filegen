package pl.cheily.filegen.ResourceModules.Plugins.SPI.Status;

public record PluginData (
    String name,
    String description,
    String version,
    String versionReleaseIsoDate,
    String author
) {}
