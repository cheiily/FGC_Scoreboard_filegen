package pl.cheily.filegen.ResourceModules;

import org.slf4j.Logger;
import org.slf4j.MarkerFactory;
import pl.cheily.filegen.LocalData.DataManagerNotInitializedException;
import pl.cheily.filegen.ResourceModules.Exceptions.*;
import pl.cheily.filegen.ResourceModules.UnarchiverFactory.Unarchiver;
import pl.cheily.filegen.ResourceModules.Validation.ValidationEvent;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static pl.cheily.filegen.ScoreboardApplication.resourceModuleRegistry;

public class ResourceModuleInstallationManager {
    public static final Logger logger = org.slf4j.LoggerFactory.getLogger(ResourceModuleInstallationManager.class);

    public static ResourceModule downloadAndInstallModule(ResourceModuleDefinition definition) {
        ResourceModule module = new ResourceModule(definition);

        Path archivePath;
        try {
            archivePath = DownloadUtils.downloadFile(
                    definition.url(),
                    definition.getInstallFilePath()
            );
        } catch (ResourceModuleDownloadException e) {
            logger.error(MarkerFactory.getMarker("ALERT"), e.getMessage(), e);
            return null;
        }

        definition.store(definition.getInstallContainerDirPath().resolve(definition.installPath() + ResourceModuleDefinition.EXTENSION));
        module.setDownloaded(true);

        if (definition.archiveType() != null) {
            Unarchiver unarchiver;
            try {
                unarchiver = UnarchiverFactory.getFor(definition.archiveType());
            } catch (ArchiveFormatNotSupportedException e) {
                logger.error(MarkerFactory.getMarker("ALERT"),
                        "Failed unarchiving resource module: {}. Error: {}", definition.name(), e.getMessage());
                return module;
            }
            try {
                unarchiver.apply(
                        archivePath,
                        definition.getInstallDirPath()
                );
            } catch (UnarchivingException e) {
                logger.error(MarkerFactory.getMarker("ALERT"), e.getMessage(), e);
                return module;
            }
            try {
                Files.deleteIfExists(archivePath);
            } catch (IOException e) {
                logger.error(MarkerFactory.getMarker("ALERT"),
                        "Failed removing archive file after extraction: {}. Error: {}", archivePath, e.getMessage(), e);
            }
        }

        try {
            resourceModuleRegistry.validator.validateThrowing(module, ValidationEvent.DOWNLOAD);
        } catch (ResourceModuleValidationException e) {
            logger.error(MarkerFactory.getMarker("ALERT"),
                    "{}\n\nModule is possibly corrupted or invalid. Removing module!", e.getMessage(), e);

            deleteModule(module);

            return null;
        }

        installModule(module);

        module.setEnabled(true);
        return module;
    }

    public static void installModule(ResourceModule module) {
        try {
            resourceModuleRegistry.validator.validateThrowing(module, ValidationEvent.INSTALLATION);
        } catch (ResourceModuleValidationException e) {
            logger.error(MarkerFactory.getMarker("ALERT"),
                    "{}\n\nModule is possibly corrupted or invalid. It is recommended to remove it.", e.getMessage(), e);

            return;
        }

        logger.trace("Resource module installation is a TODO feature, intended for JAR plugins.");
        module.setInstalled(true);
    }

    public static void deleteModule(ResourceModule module) {
        module.setEnabled(false);
        module.setInstalled(false);
        var installPath = module.definition.getInstallContainerDirPath();

        try {
            DeleteNonEmptyDirectory.deleteRecursively(installPath);
            logger.info("Resource module deleted: {}", module.definition.name());
            module.setDownloaded(false);
        } catch (IOException e) {
            var ex = ResourceModuleDeletionException.fromPath(
                    module.definition.name(),
                    module.definition.getInstallContainerDirPath().toAbsolutePath().toString(),
                    e
            );
            logger.error(MarkerFactory.getMarker("ALERT"),
                    ex.getMessage(),
                    ex
            );
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
