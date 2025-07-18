package pl.cheily.filegen.ResourceModules;

import pl.cheily.filegen.LocalData.DataManagerNotInitializedException;

import java.io.IOException;
import java.nio.file.Files;

public class ResourceModule {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ResourceModule.class);

    final ResourceModuleDefinition definition;

    // todo perhaps this could be an enum
    // Download state, false indicates this object represents a module that's available for download from a remote source.
    // At the bottom of the state stack.
    private boolean isDownloaded;

    // SPI integration health, only stored in runtime memory. Also always true for static resources.
    // Hierarchically between downloaded and enabled.
    private boolean isInstalled;

    // Current working state, also persists as a file to auto-enable at scan-time.
    // Hierarchically at the top of the stack, cannot enable without both downloading and installing.
    private boolean isEnabled;

    public ResourceModuleDefinition getDefinition() {
        return definition;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public boolean isInstalled() {
        return isInstalled;
    }

    public void setInstalled(boolean installed) {
        isInstalled = installed;
        if (installed)
            touchInstalled();
        else
            removeInstalled();
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
        if (enabled)
            touchEnabled();
        else
            removeEnabled();
    }

    public ResourceModule(ResourceModuleDefinition definition) {
        this(definition, false, false, false);
    }

    public ResourceModule(ResourceModuleDefinition definition, boolean isDownloaded, boolean isInstalled, boolean isEnabled) {
        this.definition = definition;
        this.isDownloaded = isDownloaded;
        this.isInstalled = isInstalled;
        this.isEnabled = isEnabled;
    }

    public static ResourceModule scannedLocal(ResourceModuleDefinition definition) {
        boolean enabled = false;
        boolean installed = false;
        try {
            installed = Files.exists(definition.getInstallContainerDirPath().resolve(".installed"));
            enabled = Files.exists(definition.getInstallContainerDirPath().resolve(".enabled"));
        } catch (DataManagerNotInitializedException ignored) {}
        return new ResourceModule(
                definition,
                true,
                installed,
                enabled);
    }

    public static ResourceModule scannedRemote(ResourceModuleDefinition definition) {
        return new ResourceModule(definition,
                false,
                false,
                false);
    }


    private void touchInstalled() {
        try {
            Files.createFile(definition.getInstallContainerDirPath().resolve(".installed"));
        } catch (IOException | DataManagerNotInitializedException e) {
            logger.error("Failed to create .installed file for resource module: {}", definition.installName(), e);
        }
    }

    private void removeInstalled() {
        try {
            Files.deleteIfExists(definition.getInstallContainerDirPath().resolve(".installed"));
        } catch (IOException | DataManagerNotInitializedException e) {
            logger.error("Failed to remove .installed file for resource module: {}", definition.installName(), e);
        }
    }

    private void touchEnabled() {
        try {
            Files.createFile(definition.getInstallContainerDirPath().resolve(".enabled"));
        } catch (IOException | DataManagerNotInitializedException e) {
            logger.error("Failed to create .enabled file for resource module: {}", definition.installName(), e);
        }
    }

    private void removeEnabled() {
        try {
            Files.deleteIfExists(definition.getInstallContainerDirPath().resolve(".enabled"));
        } catch (IOException | DataManagerNotInitializedException e) {
            logger.error("Failed to remove .enabled file for resource module: {}", definition.installName(), e);
        }
    }
}
