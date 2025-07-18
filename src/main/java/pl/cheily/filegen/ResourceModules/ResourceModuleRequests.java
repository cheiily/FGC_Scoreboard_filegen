package pl.cheily.filegen.ResourceModules;

import org.json.JSONArray;
import pl.cheily.filegen.Utils.Pair;

import javax.net.ssl.HttpsURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ResourceModuleRequests {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ResourceModuleRequests.class);
    private static final String GITHUB_API_BASE_URL = "https://api.github.com/repos/cheiily/SSC_Resources/contents/";

    public static List<GitHubFileDetails> githubContents(String path) {
        try {
            URL url = new URL(GITHUB_API_BASE_URL + path);
            logger.trace("Checking contents: {}", url);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            connection.setRequestProperty("GitHub-Api-Version", "2022-11-28");
            connection.setRequestProperty("Authorization", "Bearer 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");

            if (connection.getResponseCode() != 200) {
                logger.error("Failed to fetch GitHub resource module contents: HTTP {}, message \"{}\"", connection.getResponseCode(), connection.getResponseMessage());
                return List.of();
            }

            String response = new String(connection.getInputStream().readAllBytes());
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.isEmpty()) {
                logger.warn("No contents found at path: {}", path);
                return List.of();
            }

            ArrayList<GitHubFileDetails> fileDetailsList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                fileDetailsList.add(GitHubFileDetails.fromJson(jsonArray.getJSONObject(i)));
            }
            return fileDetailsList;

        } catch (MalformedURLException e) {
            logger.error("Failed to fetch GitHub resource module contents: Invalid URL.", e);
            return List.of();
        } catch (Exception e) {
            logger.error("Failed to download release: Unknown/other.", e);
            return List.of();
        }
    }
}
