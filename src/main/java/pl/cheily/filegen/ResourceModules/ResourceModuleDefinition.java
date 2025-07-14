package pl.cheily.filegen.ResourceModules;

import org.json.JSONObject;

public class ResourceModuleDefinition {
    public static final String EXTENSION = ".sscm";

    public String name;
    public String description;
    public String version;
    public String isoDate;
    public String author;
    public String url;
    public String resourceType;
    public String externalType;
    public String checksum;

    public ResourceModuleDefinition(String name, String description, String version, String isoDate, String author, String url, String resourceType, String externalType, String checksum) {
        this.name = name;
        this.description = description;
        this.version = version;
        this.isoDate = isoDate;
        this.author = author;
        this.url = url;
        this.resourceType = resourceType;
        this.externalType = externalType;
        this.checksum = checksum;
    }

    public static ResourceModuleDefinition fromJson(JSONObject json) {
        return new ResourceModuleDefinition(
                json.getString("name"),
                json.getString("description"),
                json.getString("version"),
                json.getString("isoDate"),
                json.getString("author"),
                json.getString("url"),
                json.getString("resourceType"),
                json.optString("externalType", null),
                json.optString("checksum", null)
        );
    }
}
