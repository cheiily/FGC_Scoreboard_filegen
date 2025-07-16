package pl.cheily.filegen.ResourceModules;

import org.slf4j.Logger;
import pl.cheily.filegen.LocalData.DataManagerNotInitializedException;
import pl.cheily.filegen.LocalData.LocalResourcePath;

import java.nio.file.Path;

public class ResourceModuleFetcher {
    public static final Logger logger = org.slf4j.LoggerFactory.getLogger(ResourceModuleFetcher.class);

    public static ResourceModule fetchModule(ResourceModuleDefinition definition) {
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
}
