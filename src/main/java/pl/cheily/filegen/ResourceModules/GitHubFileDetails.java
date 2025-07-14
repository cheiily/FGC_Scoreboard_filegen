package pl.cheily.filegen.ResourceModules;

import org.json.JSONObject;

public record GitHubFileDetails(
        String name,
        String path,
        String sha,
        int size,
        String url,
        String type,
        String downloadUrl
) {

    public static final String TYPE_FILE = "file";
    public static final String TYPE_DIR = "dir";


    public static GitHubFileDetails fromJson(JSONObject json) {
        return new GitHubFileDetails(
                json.getString("name"),
                json.getString("path"),
                json.getString("sha"),
                json.getInt("size"),
                json.getString("url"),
                json.getString("type"),
                json.optString("download_url", null)
        );
    }
}
