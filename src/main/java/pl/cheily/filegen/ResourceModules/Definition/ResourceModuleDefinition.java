package pl.cheily.filegen.ResourceModules.Definition;

import org.json.JSONObject;
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
        String shortDescription,
        String description,
        String version,
        String isoDate,
        String author,
        String url,
        boolean externalUrl,
        String resourceType,
        String archiveType,
        boolean autoinstall,
        boolean autorun,
        String checksum
) {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ResourceModuleDefinition.class);
    public static final String EXTENSION = ".sscm";

    public static final String V1 = "1";

    public static final String KEY_DEFINITION_VERSION = "definitionVersion";
    public static final String KEY_NAME = "name";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_INSTALL_PATH = "installPath";
    public static final String KEY_SHORT_DESCRIPTION = "shortDescription";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_VERSION = "version";
    public static final String KEY_ISO_DATE = "isoDate";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_URL = "url";
    public static final String KEY_EXTERNAL_URL = "externalUrl";
    public static final String KEY_RESOURCE_TYPE = "resourceType";
    public static final String KEY_ARCHIVE_TYPE = "archiveType";
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

    public Path getInstallContainerDirPath() {
        return LocalResourcePath.RESOURCE_MODULE_INSTALL.toStaticPath()
                .resolve(installPath());
    }

    public Path getInstallDirPath() {
        return LocalResourcePath.RESOURCE_MODULE_INSTALL.toStaticPath()
                .resolve(installPath())
                .resolve(installPath());
    }
    
    public Path getInstallFilePath() {
        return LocalResourcePath.RESOURCE_MODULE_INSTALL.toStaticPath()
                .resolve(installPath())
                .resolve(installPath() + archiveType());
    }

    public record Property(
        String name,
        Class type,
        Object value
    ) {}

    public List<Property> getProperties() {
        List<Property> properties = new ArrayList<>();
        properties.add(new Property("definitionVersion", String.class, definitionVersion));
        properties.add(new Property("name", String.class, name));
        properties.add(new Property("category", String.class, category));
        properties.add(new Property("installPath", String.class, installPath));
        properties.add(new Property("shortDescription", String.class, shortDescription));
        properties.add(new Property("description", String.class, description));
        properties.add(new Property("version", String.class, version));
        properties.add(new Property("isoDate", String.class, isoDate));
        properties.add(new Property("author", String.class, author));
        properties.add(new Property("url", String.class, url));
        properties.add(new Property("externalUrl", Boolean.class, externalUrl));
        properties.add(new Property("resourceType", String.class, resourceType));
        properties.add(new Property("archiveType", String.class, archiveType));
        properties.add(new Property("autoinstall", Boolean.class, autoinstall));
        properties.add(new Property("autorun", Boolean.class, autorun));
        properties.add(new Property("checksum", String.class, checksum));

        return properties;
    }
}