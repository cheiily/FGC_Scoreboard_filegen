package pl.cheily.filegen.ResourceModules;

import pl.cheily.filegen.LocalData.DataManagerNotInitializedException;

import java.io.IOException;
import java.nio.file.Files;

public class ResourceModule {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ResourceModule.class);

    ResourceModuleDefinition definition;
    private boolean isDownloaded;
    private boolean isInstalled;
    private boolean isEnabled;

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

    public static ResourceModule installed(ResourceModuleDefinition definition) {
        boolean enabled = false;
        try {
            enabled = Files.exists(definition.getInstallDirPath().resolve(".enabled"));
        } catch (DataManagerNotInitializedException ignored) {}
        return new ResourceModule(
                definition,
                true,
                true,
                enabled);
    }

    public static ResourceModule remote(ResourceModuleDefinition definition) {
        return new ResourceModule(definition,
                false,
                false,
                false);
    }

    private void touchEnabled() {
        try {
            Files.createFile(definition.getInstallDirPath().resolve(".enabled"));
        } catch (IOException | DataManagerNotInitializedException e) {
            logger.error("Failed to create .enabled file for resource module: {}", definition.installName(), e);
        }
    }

    private void removeEnabled() {
        try {
            Files.deleteIfExists(definition.getInstallDirPath().resolve(".enabled"));
        } catch (IOException | DataManagerNotInitializedException e) {
            logger.error("Failed to remove .enabled file for resource module: {}", definition.installName(), e);
        }
    }
}
