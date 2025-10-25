package pl.cheily.filegen.ResourceModules.Installation;

import org.slf4j.Logger;
import org.slf4j.MarkerFactory;
import pl.cheily.filegen.ResourceModules.Definition.ResourceModuleDefinition;
import pl.cheily.filegen.ResourceModules.Definition.ResourceModuleDefinitionHandlerFactory;
import pl.cheily.filegen.ResourceModules.Exceptions.*;
import pl.cheily.filegen.ResourceModules.Exceptions.Plugins.PluginInstantiationException;
import pl.cheily.filegen.ResourceModules.Installation.UnarchiverFactory.Unarchiver;
import pl.cheily.filegen.ResourceModules.ResourceModule;
import pl.cheily.filegen.ResourceModules.ResourceModuleType;
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

        ResourceModuleDefinition.store(
                definition,
                definition.getInstallDirPath().resolve(definition.installPath() + ResourceModuleDefinition.EXTENSION),
                ResourceModuleDefinitionHandlerFactory.getSerializer(definition.definitionVersion())
        );
        module.setDownloaded(true);

        if (definition.resourceType().equals(ResourceModuleType.STATICS_COLLECTION.name())) {
            Unarchiver unarchiver;
            try {
                String[] ifnWords = definition.installFileName().split("\\.");
                String extension = ifnWords.length > 0 ? "." + ifnWords[ifnWords.length - 1] : "";
                unarchiver = UnarchiverFactory.getFor(extension);
            } catch (ArchiveFormatNotSupportedException e) {
                logger.error(MarkerFactory.getMarker("ALERT"),
                        "Failed unarchiving resource module: {}. Error: {}", definition.name(), e.getMessage());
                return module;
            }
            try {
                unarchiver.apply(
                        archivePath,
                        definition.getExtractDirPath()
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
            resourceModuleRegistry.pluginRegistry.register(module);
        } catch (PluginInstantiationException e) {
            logger.error(MarkerFactory.getMarker("ALERT"),
                    String.format("Failed loading plugins from resource module {%s}. Error: %s", module.getDefinition().qualifiedName(), e.getMessage()),
                    e
            );
            return;
        }

        try {
            resourceModuleRegistry.validator.validateThrowing(module, ValidationEvent.INSTALLATION);
        } catch (ResourceModuleValidationException e) {
            logger.error(MarkerFactory.getMarker("ALERT"), e.getMessage(), e);

            return;
        }

        module.setInstalled(true);
    }

    public static void deleteModule(ResourceModule module) {
        module.setEnabled(false);
        module.setInstalled(false);
        var installPath = module.getDefinition().getInstallDirPath();

        try {
            DeleteNonEmptyDirectory.deleteRecursively(installPath);
            logger.info("Resource module deleted: {}", module.getDefinition().name());
            module.setDownloaded(false);
        } catch (IOException e) {
            var ex = ResourceModuleDeletionException.fromPath(
                    module.getDefinition().name(),
                    module.getDefinition().getInstallDirPath().toAbsolutePath().toString(),
                    e.getMessage(),
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
