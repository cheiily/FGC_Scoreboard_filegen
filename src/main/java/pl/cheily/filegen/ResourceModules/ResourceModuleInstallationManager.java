package pl.cheily.filegen.ResourceModules;

import org.slf4j.Logger;
import org.slf4j.MarkerFactory;
import pl.cheily.filegen.LocalData.DataManagerNotInitializedException;
import pl.cheily.filegen.ResourceModules.Exceptions.ResourceModuleInstallationManagementException;
import pl.cheily.filegen.ResourceModules.Validation.ResourceModuleDownloadValidatorFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class ResourceModuleInstallationManager {
    public static final Logger logger = org.slf4j.LoggerFactory.getLogger(ResourceModuleInstallationManager.class);

    public static ResourceModule downloadAndInstallModule(ResourceModuleDefinition definition) throws ResourceModuleInstallationManagementException {
        ResourceModule module = new ResourceModule(definition);
        try {
            var type = ResourceModuleType.valueOf(definition.resourceType());
            var archivePath = DownloadUtils.downloadFile(
                    definition.url(),
                    definition.getInstallFilePath()
            );

            definition.store(definition.getInstallContainerDirPath().resolve(definition.installPath() + ResourceModuleDefinition.EXTENSION));
            module.setDownloaded(true);

            if (definition.archiveType() != null) {
                var unarchiver = UnarchiverFactory.getFor(definition.archiveType());
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

            if (!ResourceModuleDownloadValidatorFactory.getFor(type).apply(module.getDefinition().getInstallDirPath())) {
                return module;
            }

            logger.trace("Resource module installation is a TODO feature, intended for JAR plugins.");
            module.setInstalled(true);

            module.setEnabled(true);
            return module;

        } catch (IllegalArgumentException e) {
            logger.error("Invalid resource module type: {}", definition.resourceType(), e);
            return null;
        }
    }

    public static ResourceModule downloadModule(ResourceModuleDefinition definition) throws ResourceModuleInstallationManagementException {
        ResourceModule module = new ResourceModule(definition);
        var type = ResourceModuleType.valueOf(definition.resourceType());

        var archivePath = DownloadUtils.downloadFile(
                definition.url(),
                definition.getInstallFilePath()
        );

        definition.store(definition.getInstallContainerDirPath().resolve(definition.installPath() + ResourceModuleDefinition.EXTENSION));
        module.setDownloaded(true);

        if (definition.archiveType() != null) {
            var unarchiver = UnarchiverFactory.getFor(definition.archiveType());
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

        if (!ResourceModuleDownloadValidatorFactory.getFor(type).apply(module.getDefinition().getInstallDirPath())) {
            return module;
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
                logger.error(
                        MarkerFactory.getMarker("ALERT"),
                        String.format("Failed to delete resource module: \"%s\". Please remove it manually from \"%s\" and restart the app.", module.definition.name(), module.definition.getInstallContainerDirPath()),
                        e);
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
