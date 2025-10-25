package pl.cheily.filegen.ResourceModules.Definition;

import pl.cheily.filegen.LocalData.LocalResourcePath;
import pl.cheily.filegen.ResourceModules.Exceptions.ResourceModuleDefinitionSerializationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public record ResourceModuleDefinition(
        String definitionVersion,
        String name,
        String category,
        String installPath,
        String installFileName,
        String shortDescription,
        String description,
        String version,
        String isoDate,
        String author,
        String url,
        boolean externalUrl,
        String resourceType,
        String serviceInterface,
        boolean autoinstall,
        boolean autorun,
        String checksum
) {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ResourceModuleDefinition.class);
    public static final String EXTENSION = ".sscm.json";

    public static final String V1 = "1";

    public static final String KEY_DEFINITION_VERSION = "definitionVersion";
    public static final String KEY_NAME = "name";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_INSTALL_PATH = "installPath";
    public static final String KEY_INSTALL_FILE_NAME = "installFileName";
    public static final String KEY_SHORT_DESCRIPTION = "shortDescription";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_VERSION = "version";
    public static final String KEY_ISO_DATE = "isoDate";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_URL = "url";
    public static final String KEY_EXTERNAL_URL = "externalUrl";
    public static final String KEY_RESOURCE_TYPE = "resourceType";
    public static final String KEY_SERVICE_INTERFACE = "serviceInterface";
    public static final String KEY_AUTOINSTALL = "autoinstall";
    public static final String KEY_AUTORUN = "autorun";
    public static final String KEY_CHECKSUM = "checksum";

    public static void store(ResourceModuleDefinition definition, Path path, ResourceModuleDefinitionSerializer serializer) {
        try {
            Files.writeString(path, serializer.serialize(definition), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            var ex = ResourceModuleDefinitionSerializationException.from(
                    definition.name,
                    path.toAbsolutePath().toString(),
                    e.getMessage(),
                    e
            );
            logger.error(ex.getMessage(), ex);
        } catch (ResourceModuleDefinitionSerializationException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public Path getInstallDirPath() {
        return LocalResourcePath.RESOURCE_MODULE_INSTALL.toStaticPath()
                .resolve(installPath());
    }

    public Path getExtractDirPath() {
        return LocalResourcePath.RESOURCE_MODULE_INSTALL.toStaticPath()
                .resolve(installPath())
                .resolve("extracted");
    }
    
    public Path getInstallFilePath() {
        return LocalResourcePath.RESOURCE_MODULE_INSTALL.toStaticPath()
                .resolve(installPath())
                .resolve(installFileName());
    }

    public String qualifiedName() {
        return "[" + category() + "] " + name() + " - " + version();
    }

    public String versionName() {
        return name() + " (" + version() + ")";
    }

    public record Property(
        String name,
        Class type,
        Object value
    ) {}

    public List<Property> getProperties() {
        return new ArrayList<>(List.of(
                new Property(KEY_DEFINITION_VERSION, String.class, definitionVersion),
                new Property(KEY_NAME, String.class, name),
                new Property(KEY_CATEGORY, String.class, category),
                new Property(KEY_INSTALL_PATH, String.class, installPath),
                new Property(KEY_INSTALL_FILE_NAME, String.class, installFileName),
                new Property(KEY_SHORT_DESCRIPTION, String.class, shortDescription),
                new Property(KEY_DESCRIPTION, String.class, description),
                new Property(KEY_VERSION, String.class, version),
                new Property(KEY_ISO_DATE, String.class, isoDate),
                new Property(KEY_AUTHOR, String.class, author),
                new Property(KEY_URL, String.class, url),
                new Property(KEY_EXTERNAL_URL, Boolean.class, externalUrl),
                new Property(KEY_RESOURCE_TYPE, String.class, resourceType),
                new Property(KEY_SERVICE_INTERFACE, String.class, serviceInterface),
                new Property(KEY_AUTOINSTALL, Boolean.class, autoinstall),
                new Property(KEY_AUTORUN, Boolean.class, autorun),
                new Property(KEY_CHECKSUM, String.class, checksum)
        ));
    }
}