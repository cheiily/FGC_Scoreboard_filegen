package pl.cheily.filegen.ResourceModules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cheily.filegen.ResourceModules.Definition.ResourceModuleDefinition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceModule {
    private static final Logger logger = LoggerFactory.getLogger(ResourceModule.class);

    private static final String FILE_INSTALLED = ".installed";
    private static final String FILE_ENABLED = ".enabled";

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
        boolean enabled = Files.exists(definition.getInstallContainerDirPath().resolve(".enabled"));
        boolean installed = Files.exists(definition.getInstallContainerDirPath().resolve(".installed"));

        return new ResourceModule(
                definition,
                true,
                installed,
                enabled
        );
    }

    public static ResourceModule scannedRemote(ResourceModuleDefinition definition) {
        return new ResourceModule(definition,
                false,
                false,
                false
        );
    }

    public void copyFrom(ResourceModule other) {
        this.isDownloaded = other.isDownloaded;
        this.isInstalled = other.isInstalled;
        this.isEnabled = other.isEnabled;
    }


    private void touchInstalled() {
        touch(FILE_INSTALLED);
    }

    private void removeInstalled() {
        untouch(FILE_INSTALLED);
    }

    private void touchEnabled() {
        touch(FILE_ENABLED);
    }

    private void removeEnabled() {
        untouch(FILE_ENABLED);
    }

    private void touch(String filename) {
        Path path = definition.getInstallContainerDirPath().resolve(filename);

        if (Files.exists(path)) {
            logger.info("Resource module {} is already installed, skipping creation of {} file.", definition.installPath(), filename);
            return;
        }

        try {
            Files.createFile(path);
        } catch (IOException e) {
            logger.error("Failed to create {} file for resource module: {}", filename, definition.installPath(), e);
        }
    }

    private void untouch(String filename) {
        try {
            Files.deleteIfExists(definition.getInstallContainerDirPath().resolve(filename));
        } catch (IOException e) {
            logger.error("Failed to remove {} file for resource module: {}", definition.installPath(), filename, e);
        }
    }
}
