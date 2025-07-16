package pl.cheily.filegen.ResourceModules;

import java.nio.file.Path;
import java.util.function.Function;

import static pl.cheily.filegen.ResourceModules.ResourceModuleType.*;

public class ResourceModuleDownloadValidator {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ResourceModuleDownloadValidator.class);

    public static Function<Path, Boolean> getFor(ResourceModuleType type) {
        return switch (type) {
            case STATICS_COLLECTION -> ResourceModuleDownloadValidator::validateStaticsCollection;
            case STATIC_FILE -> ResourceModuleDownloadValidator::validateStaticFile;
            case EXECUTABLE_COMMAND -> ResourceModuleDownloadValidator::validateExecutableCommand;
            case PROPERTIES_JSON -> ResourceModuleDownloadValidator::validatePropertiesJson;
        };
    }

    public static boolean validateStaticsCollection(Path extractedPath) {
        if (!extractedPath.toFile().isDirectory()) {
            logger.error("Resource module validation error. Extracted path is not a directory. Type: {}, Path: {}", STATICS_COLLECTION, extractedPath);
            return false;
        }

        var files = extractedPath.toFile().listFiles();
        if (files == null || files.length == 0) {
            logger.error("Resource module validation error. No files found in the directory. Type: {}, Path: {}", STATICS_COLLECTION, extractedPath);
            return false;
        }

        for (var file : files) {
            if (!file.isFile() || !file.canRead()) {
                logger.error("Resource module validation error. File is not readable or not a file. Type: {}, Path: {}", STATICS_COLLECTION, file.getPath());
                return false;
            }
        }

        logger.trace("Resource module validation successful. Type: {}, Path: {}", STATICS_COLLECTION, extractedPath);
        return true;
    }

    private static Boolean validateStaticFile(Path path) {
        logger.warn("Resource module validation is not implemented. Type: {}, Path: {}", STATIC_FILE, path);
        return true;
    }

    private static Boolean validatePropertiesJson(Path path) {
        logger.warn("Resource module validation is not implemented. Type: {}, Path: {}", PROPERTIES_JSON, path);
        return true;
    }

    private static Boolean validateExecutableCommand(Path path) {
        logger.warn("Resource module validation is not implemented. Type: {}, Path: {}", EXECUTABLE_COMMAND, path);
        return true;
    }
    
}
