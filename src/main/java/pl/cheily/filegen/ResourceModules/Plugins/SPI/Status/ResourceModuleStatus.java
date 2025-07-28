package pl.cheily.filegen.ResourceModules.Plugins.SPI.Status;

import java.nio.file.Path;

public record ResourceModuleStatus(
        boolean isDownloaded,
        boolean isInstalled,
        boolean isEnabled,
        ResourceModuleDefinitionData definition,
        Path installDirPath,
        Path installFilePath,
        Path installContainerDirPath
) {
}
