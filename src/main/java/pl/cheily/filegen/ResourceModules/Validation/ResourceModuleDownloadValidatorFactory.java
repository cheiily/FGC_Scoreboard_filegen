package pl.cheily.filegen.ResourceModules.Validation;

import pl.cheily.filegen.ResourceModules.Exceptions.Validation.ResourceModuleDownloadValidationErrorCode;
import pl.cheily.filegen.ResourceModules.Exceptions.Validation.ValidationError;
import pl.cheily.filegen.ResourceModules.Exceptions.Validation.ValidationErrorCode;
import pl.cheily.filegen.ResourceModules.ResourceModule;
import pl.cheily.filegen.ResourceModules.ResourceModuleType;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

import static pl.cheily.filegen.ResourceModules.ResourceModuleType.*;

public class ResourceModuleDownloadValidatorFactory implements ResourceModuleValidatorFactory {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ResourceModuleDownloadValidatorFactory.class);

    public ResourceModuleDownloadValidatorFactory() {}

    public ValidationEvent validates() {
        return ValidationEvent.DOWNLOAD;
    }

    public List<Verifier> getFor(ResourceModuleType type) {
        return switch (type) {
            case STATICS_COLLECTION -> List.of(
                    this::statics_isDirectory,
                    this::statics_hasFiles,
                    this::statics_filesAreReadable
                );
            case STATIC_FILE -> List.of(this::validateStaticFile);
            case PROPERTIES_JSON -> List.of(this::validatePropertiesJson);
            case EXECUTABLE_COMMAND -> List.of(this::validateExecutableCommand);
            case PLUGIN_JAR -> List.of(this::validatePluginJar);
        };
    }

    public List<Verifier> getAll() {
        return List.of(
                this::statics_isDirectory,
                this::statics_hasFiles,
                this::statics_filesAreReadable,
                this::validateStaticFile,
                this::validatePropertiesJson,
                this::validateExecutableCommand,
                this::validatePluginJar
        );
    }

    private List<ValidationError> statics_isDirectory(ResourceModule module) {
        Path extractedPath = module.getDefinition().getInstallDirPath();
        if (!extractedPath.toFile().isDirectory()) {
            return List.of(
                    ResourceModuleDownloadValidationErrorCode.NOT_A_DIRECTORY.asError(String.format(
                            " Module: %s, Path: %s",
                            module, extractedPath
                    ))
            );
        }
        return List.of();
    }

    private List<ValidationError> statics_hasFiles(ResourceModule module) {
        Path extractedPath = module.getDefinition().getInstallDirPath();
        var files = extractedPath.toFile().listFiles();
        if (files == null || files.length == 0) {
            return List.of(
                    ResourceModuleDownloadValidationErrorCode.NO_FILES_FOUND.asError(String.format(
                            " Module: %s, Path: %s",
                            module, extractedPath
                    ))
            );
        }
        return List.of();
    }

    private List<ValidationError> statics_filesAreReadable(ResourceModule module) {
        Path extractedPath = module.getDefinition().getInstallDirPath();
        var files = extractedPath.toFile().listFiles();
        if (files == null) files = new File[0];
        for (var file : files) {
            if (!file.canRead()) {
                return List.of(
                        ResourceModuleDownloadValidationErrorCode.CANNOT_READ_FILE.asError(String.format(
                                " Module: %s, Path: %s",
                                module, file.getPath()
                        ))
                );
            }
        }
        return List.of();
    }



    private List<ValidationError> validateStaticFile(ResourceModule module) {
        logger.warn("Resource module validation is not implemented. Type: {}, Path: {}", STATIC_FILE, module.getDefinition().getInstallFilePath());
        return List.of();
    }

    private List<ValidationError> validatePropertiesJson(ResourceModule module) {
        logger.warn("Resource module validation is not implemented. Type: {}, Path: {}", PROPERTIES_JSON, module.getDefinition().getInstallFilePath());
        return List.of();
    }

    private List<ValidationError> validateExecutableCommand(ResourceModule module) {
        logger.warn("Resource module validation is not implemented. Type: {}, Path: {}", EXECUTABLE_COMMAND, module.getDefinition().getInstallFilePath());
        return List.of();
    }

    private List<ValidationError> validatePluginJar(ResourceModule module) {
        logger.warn("Resource module validation is not implemented. Type: {}, Path: {}", PLUGIN_JAR, module.getDefinition().getInstallFilePath());
        return List.of();
    }
    
}
