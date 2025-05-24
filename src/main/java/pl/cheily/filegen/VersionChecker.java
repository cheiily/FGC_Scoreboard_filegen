package pl.cheily.filegen;

import javafx.application.Platform;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.action.Action;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.module.ModuleDescriptor;
import java.net.URL;
import java.nio.file.Path;

public class VersionChecker {
    private final static Logger logger = LoggerFactory.getLogger(VersionChecker.class);
    private static final String GITHUB_API_URL = "https://api.github.com/repos/cheiily/FGC_Scoreboard_filegen/releases";
    private static final String GITHUB_API_VERSION = "2022-11-28";

    public static class VersionData {
        boolean isNew;
        String count;
        String url;
        String downloadUrl;

        public VersionData(boolean isNew, String count, String url, String downloadUrl) {
            this.isNew = isNew;
            this.count = count;
            this.url = url;
            this.downloadUrl = downloadUrl;
        }
    }

    public static void queueUpdateCheck() {
        Platform.runLater(() -> {
            var version = getNewestVersion();
            if (version == null || !version.isNew) {
                return;
            }

            Notifications.create()
                    .title("New version available.")
                    .text("Version " + version.count + " has been released since you last updated.")
                    .action(
                        new Action("View", event -> {
                            ScoreboardApplication.instance.getHostServices().showDocument(version.url);
                        }), new Action("Download", event -> {
                            ScoreboardApplication.instance.getHostServices().showDocument(version.downloadUrl);
                        }))
                    .hideAfter(Duration.seconds(10))
                    .show();
        });
    }

    public static VersionData getNewestVersion() {
        String currentVer = Launcher.class.getPackage().getImplementationVersion();
        if (currentVer == null) {
            logger.warn("Current version is undefined. Cannot check for updates.");
            return null;
        }

        String response;
        try {
            HttpsURLConnection connection = getConnection();
            if (connection.getResponseCode() == 401) {
                logger.error("Unauthorized access to GitHub API.");
                return null;
            }

            InputStream inputStream = connection.getInputStream();
            StringBuilder responseBuilder = new StringBuilder();

            try (BufferedReader rd = new BufferedReader(new java.io.InputStreamReader(inputStream))) {
                String line;
                while ((line = rd.readLine()) != null) {
                    responseBuilder.append(line);
                }
                response = responseBuilder.toString();
            } catch (IOException e) {
                logger.error("Failed to read response from GitHub API.", e);
                return null;
            }

        } catch (IOException e) {
            logger.error("Failed to connect to GitHub API.", e);
            return null;
        }

        JSONArray json = new JSONArray(response);
        JSONObject release = json.getJSONObject(0);
        String newestVer = release.getString("tag_name");
        String url = release.getString("html_url");
        String downloadUrl = release.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");

        if (compareSemVer(currentVer, newestVer) < 0) {
            logger.info("New version available: {}", newestVer);
            return new VersionData(true, newestVer, url, downloadUrl);
        } else {
            logger.info("Current version {} is up to date (new: {}).", currentVer, newestVer);
            return new VersionData(false, newestVer, url, downloadUrl);
        }
    }

    private static HttpsURLConnection getConnection() throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) (new URL(GITHUB_API_URL).openConnection());
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
        connection.setRequestProperty("X-GitHub-Api-Version", GITHUB_API_VERSION);
        return connection;
    }

    // negative if ver1 < ver2, zero if equal, positive otherwise
    public static int compareSemVer(String ver1, String ver2) {
        ModuleDescriptor.Version vers1 = ModuleDescriptor.Version.parse(ver1);
        ModuleDescriptor.Version vers2 = ModuleDescriptor.Version.parse(ver2);
        return vers1.compareTo(vers2);
    }

    private static Path getJarPath() {
        return Path.of(ScoreboardApplication.instance.getHostServices().getCodeBase());
    }
}
