package pl.cheily.filegen.ResourceModules;

import org.slf4j.Logger;
import org.slf4j.MarkerFactory;
import pl.cheily.filegen.LocalData.DataManagerNotInitializedException;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class ResourceModuleInstallationManager {
    public static final Logger logger = org.slf4j.LoggerFactory.getLogger(ResourceModuleInstallationManager.class);

    public static ResourceModule downloadAndInstallModule(ResourceModuleDefinition definition) {
        ResourceModule module = new ResourceModule(definition);
        try {
            var type = ResourceModuleType.valueOf(definition.resourceType());
            var archivePath = DownloadUtils.downloadFile(
                    definition.url(),
                    definition.getInstallFilePath()
            );

            definition.store(definition.getInstallContainerDirPath().resolve(definition.installName() + ResourceModuleDefinition.EXTENSION));
            module.setDownloaded(true);

            if (definition.archiveType() != null) {
                var unarchiver = Unarchiver.getFor(definition.archiveType());
                if (unarchiver == null) {
                    logger.error("Unsupported archive type: {}", definition.archiveType());
                    return null;
                }
                unarchiver.apply(
                        archivePath,
                        definition.getInstallDirPath()
                );
                try {
                    Files.deleteIfExists(archivePath);
                } catch (IOException e) {
                    logger.error("Failed to cleanup archive file after extraction: {}", archivePath, e);
                }
            }

            if (!ResourceModuleDownloadValidator.getFor(type).apply(module.getDefinition().getInstallDirPath())) {
                return module;
            }

            logger.trace("Resource module installation is a TODO feature, intended for JAR plugins.");
            module.setInstalled(true);

            module.setEnabled(true);
            return module;

        } catch (IllegalArgumentException e) {
            logger.error("Invalid resource module type: {}", definition.resourceType(), e);
            return null;
        } catch (DataManagerNotInitializedException e) {
            // not happening
            logger.error("Data manager is not initialized, cannot fetch resource module.", e);
            return null;
        }
    }

    public static void deleteModule(ResourceModule module) {
        try {
            module.setEnabled(false);
            module.setInstalled(false);

            var installPath = module.definition.getInstallContainerDirPath();
            DeleteNonEmptyDirectory.deleteRecursively(installPath);
            logger.info("Resource module deleted: {}", module.definition.name());
            module.setDownloaded(false);
        } catch (DataManagerNotInitializedException ignored) {
        } catch (IOException e) {
            try {
                logger.error(MarkerFactory.getMarker("ALERT"), "Failed to delete resource module: {}. Please remove it manually from {}.", module.definition.name(), module.definition.getInstallDirPath(), e);
            } catch (DataManagerNotInitializedException ignored) {}
        }
    }


    private static class DeleteNonEmptyDirectory extends SimpleFileVisitor<Path> {

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.deleteIfExists(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.deleteIfExists(dir);
            return FileVisitResult.CONTINUE;
        }

        public static void deleteRecursively(Path file) throws IOException {
            DeleteNonEmptyDirectory deleter = new DeleteNonEmptyDirectory();
            Files.walkFileTree(file, deleter);
        }
    }
}
