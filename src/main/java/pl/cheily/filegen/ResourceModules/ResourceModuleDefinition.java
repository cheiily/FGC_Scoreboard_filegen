package pl.cheily.filegen.ResourceModules;

import org.json.JSONObject;
import pl.cheily.filegen.LocalData.DataManagerNotInitializedException;
import pl.cheily.filegen.LocalData.LocalResourcePath;

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

    public static final String KEY_DEFINITION_VERSION = "definitionVersion";
    public static final String KEY_NAME = "name";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_INSTALL_PATH = "installPath";
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


    public static ResourceModuleDefinition fromJson(JSONObject json) {
        return new ResourceModuleDefinition(
            json.getString("definitionVersion"),
            json.getString("name"),
            json.optString("category"),
            json.getString("installPath"),
            json.getString("description"),
            json.getString("version"),
            json.optString("isoDate"),
            json.getString("author"),
            json.getString("url"),
            json.getBoolean("externalUrl"),
            json.getString("resourceType"),
            json.optString("archiveType", null),
            json.optBoolean("autoinstall", false),
            json.optBoolean("autorun", false),
            json.optString("checksum", null));
    }

    public String toJson() {
        return toJson(true);
    }

    public String toJson(boolean pretty) {
        JSONObject json = new JSONObject();
        json.put("definitionVersion", definitionVersion);
        json.put("name", name);
        json.put("category", category);
        json.put("installPath", installPath);
        json.put("description", description);
        json.put("version", version);
        json.put("isoDate", isoDate);
        json.put("author", author);
        json.put("url", url);
        json.put("externalUrl", externalUrl);
        json.put("resourceType", resourceType);
        json.putOpt("archiveType", archiveType);
        json.putOpt("autoinstall", autoinstall);
        json.putOpt("autorun", autorun);
        json.putOpt("checksum", checksum);

        return pretty ? json.toString(4) : json.toString();
    }

    public void store(Path path) {
        try {
            Files.writeString(path, toJson(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            logger.error("Failed to store resource module definition to file: {}", path, e);
        }
    }

    public Path getInstallContainerDirPath() throws DataManagerNotInitializedException {
        return LocalResourcePath.RESOURCE_MODULE_INSTALL.toPath()
                .resolve(installPath());
    }

    public Path getInstallDirPath() throws DataManagerNotInitializedException {
        return LocalResourcePath.RESOURCE_MODULE_INSTALL.toPath()
                .resolve(installPath())
                .resolve(installPath());
    }
    
    public Path getInstallFilePath() throws DataManagerNotInitializedException {
        return LocalResourcePath.RESOURCE_MODULE_INSTALL.toPath()
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