package pl.cheily.filegen.ResourceModules;

import org.json.JSONObject;
import pl.cheily.filegen.LocalData.DataManagerNotInitializedException;
import pl.cheily.filegen.LocalData.LocalResourcePath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public record ResourceModuleDefinition(
    String definitionVersion,
    String name,
    String installName,
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

    public static ResourceModuleDefinition fromJson(JSONObject json) {
        return new ResourceModuleDefinition(
            json.getString("definitionVersion"),
            json.getString("name"),
            json.getString("installName"),
            json.getString("description"),
            json.getString("version"),
            json.getString("isoDate"),
            json.getString("author"),
            json.getString("url"),
            json.getBoolean("externalUrl"),
            json.getString("resourceType"),
            json.optString("archiveType", null),
            json.optBoolean("autoinstall", false),
            json.optBoolean("autorun", false),
            json.optString("checksum", null)
        );
    }

    public String toJson() {
        return toJson(true);
    }

    public String toJson(boolean pretty) {
        JSONObject json = new JSONObject();
        json.put("definitionVersion", definitionVersion);
        json.put("name", name);
        json.put("installName", installName);
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
    
    public Path getInstallDirPath() throws DataManagerNotInitializedException {
        return LocalResourcePath.RESOURCE_MODULE_INSTALL.toPath()
                .resolve(installName());
    }
    
    public Path getInstallFilePath() throws DataManagerNotInitializedException {
        return LocalResourcePath.RESOURCE_MODULE_INSTALL.toPath()
                .resolve(installName())
                .resolve(installName() + archiveType());
    }

    // for bean property generator
    public String getDefinitionVersion() { return definitionVersion; }
    public String getName() { return name; }
    public String getInstallName() { return installName; }
    public String getDescription() { return description; }
    public String getVersion() { return version; }
    public String getIsoDate() { return isoDate; }
    public String getAuthor() { return author; }
    public String getUrl() { return url; }
    public boolean isExternalUrl() { return externalUrl; }
    public String getResourceType() { return resourceType; }
    public String getArchiveType() { return archiveType; }
    public boolean isAutoinstall() { return autoinstall; }
    public boolean isAutorun() { return autorun; }
    public String getChecksum() { return checksum; }
}