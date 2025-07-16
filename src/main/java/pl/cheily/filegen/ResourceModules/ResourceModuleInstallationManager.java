package pl.cheily.filegen.ResourceModules;

import org.slf4j.Logger;
import org.slf4j.MarkerFactory;
import pl.cheily.filegen.LocalData.DataManagerNotInitializedException;

import java.io.IOException;
import java.nio.file.Files;

public class ResourceModuleInstallationManager {
    public static final Logger logger = org.slf4j.LoggerFactory.getLogger(ResourceModuleInstallationManager.class);

    public static ResourceModule downloadAndInstallModule(ResourceModuleDefinition definition) {
        try {
            var type = ResourceModuleType.valueOf(definition.resourceType());
            var filepath = DownloadUtils.downloadFile(
                    definition.url(),
                    definition.getInstallDirPath()
            );

            if (definition.archiveType() != null) {
                var unarchiver = Unarchiver.getFor(definition.archiveType());
                if (unarchiver == null) {
                    logger.error("Unsupported archive type: {}", definition.archiveType());
                    return null;
                }
                filepath = unarchiver.apply(
                        filepath,
                        definition.getInstallFilePath()
                );
            }

            definition.store(definition.getInstallDirPath().resolve(definition.installName() + ResourceModuleDefinition.EXTENSION));

            logger.trace("Resource module installation is a TODO feature, intended for JAR plugins.");

            if (!ResourceModuleDownloadValidator.getFor(type).apply(filepath)) {
                logger.error("Resource module validation failed, not enabling.");
                return null;
            }

            return new ResourceModule(definition, true, true, false);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid resource module type: {}", definition.resourceType(), e);
            return null;
        } catch (DataManagerNotInitializedException e) {
            // not happening
            logger.error("Data manager is not initialized, cannot fetch resource module.", e);
            return null;
        }
    }

    public static boolean deleteModule(ResourceModule module) {
        try {
            module.setEnabled(false);
            module.setInstalled(false);

            var installPath = module.definition.getInstallDirPath();
            var result = Files.deleteIfExists(installPath);
            if (result) {
                logger.info("Resource module deleted: {}", module.definition.name());
                module.setDownloaded(false);
            } else {
                logger.warn("Resource module not found or already deleted: {}", module.definition.name());
            }
            return result;
        } catch (DataManagerNotInitializedException ignored) {
        } catch (IOException e) {
            try {
                logger.error(MarkerFactory.getMarker("ALERT"), "Failed to delete resource module: {}. Please remove it manually from {}.", module.definition.name(), module.definition.getInstallDirPath(), e);
            } catch (DataManagerNotInitializedException ignored) {}
        }
        return false;
    }
}
