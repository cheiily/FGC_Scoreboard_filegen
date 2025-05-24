package pl.cheily.filegen;

import javafx.application.Platform;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.action.Action;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cheily.filegen.Utils.Pair;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.module.ModuleDescriptor;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
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
        Long size;

        public VersionData(boolean isNew, String count, String url, String downloadUrl, Long size) {
            this.isNew = isNew;
            this.count = count;
            this.url = url;
            this.downloadUrl = downloadUrl;
            this.size = size;
        }

        @Override
        public String toString() {
            return "VersionData{" +
                    "isNew=" + isNew +
                    ", count='" + count + '\'' +
                    ", url='" + url + '\'' +
                    ", downloadUrl='" + downloadUrl + '\'' +
                    ", size=" + size +
                    '}';
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
                    .text("Version " + version.count + " has been released since you last updated.\n")
                    .action(
                        new Action("View", event -> {
                            ScoreboardApplication.instance.getHostServices().showDocument(version.url);
                        }), new Action("Download", event -> {
                            new Thread(() -> {
                                var result = downloadRelease(version);
                                if (result.first())
                                    Notifications.create()
                                        .title("Download complete.")
                                        .text(result.second())
                                        .showConfirm();
                                else
                                    Notifications.create()
                                        .title("Download failed.")
                                        .text("Failed to download new release. " + result.second())
                                        .showError();
                            }).run();
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
        long size = release.getJSONArray("assets").getJSONObject(0).getLong("size");

        if (compareSemVer(currentVer, newestVer) < 0) {
            VersionData versionData = new VersionData(true, newestVer, url, downloadUrl, size);
            logger.info("New version available: {}", versionData);
            return versionData;
        } else {
            logger.info("Current version {} is up to date (new: {}).", currentVer, newestVer);
            return new VersionData(false, newestVer, url, downloadUrl, size);
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

    private static Path getJarPath() throws URISyntaxException {
        Path jarPath = Path.of(ScoreboardApplication.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        logger.debug("Jar path: {}", jarPath);
        if (jarPath.toString().startsWith("file:/")) {
            jarPath = Path.of(jarPath.toString().substring(5));
        }
        logger.debug("Jar path: {}", jarPath);
        return jarPath;
    }

    private static Pair<Boolean, String> downloadRelease(VersionData version) {
        try {
            logger.info("Downloading version: {}", version);

            URL downloadUrl = new URL(version.downloadUrl);
            HttpsURLConnection connection = (HttpsURLConnection) downloadUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/octet-stream");

            boolean replacedOld = false;
            Path target = getJarPath().resolve("filegen.jar").toAbsolutePath();
            if (Files.exists(target)) {
                logger.info("Removing old filegen.jar");
                try {
                    Files.delete(target);
                } catch (Exception e) {
                    logger.error("Failed to delete old filegen.jar. Creating a second file.", e);
                }
            }
            replacedOld = !Files.exists(target);
            if (!replacedOld) {
                target = target.getParent().resolve("filegen-" + version.count + ".jar").toAbsolutePath();
                int i = 1;
                while (Files.exists(target)) {
                    target = target.getParent().resolve("filegen-" + version.count + "-" + i + ".jar").toAbsolutePath();
                    i++;
                }
            }
            Files.createFile(target);

            try (FileChannel out = FileChannel.open(target, java.nio.file.StandardOpenOption.WRITE);
                ReadableByteChannel in = Channels.newChannel(connection.getInputStream())) {
                long bytesTransferred = 0;
                while (bytesTransferred < version.size) {
                    long bytes = out.transferFrom(in, bytesTransferred, version.size - bytesTransferred);
                    logger.debug("Downloaded {} bytes to {}", bytes, target);
                    bytesTransferred += bytes;
                }
                logger.info("Download complete. Total {} bytes. File saved to {}", bytesTransferred, target.toAbsolutePath());
            }

            return new Pair<>(true, (replacedOld ? "Replaced old version @ " : "Saved release to: ") + target.toString() + ".\nRestart the application now to apply changes.");

        } catch (MalformedURLException e) {
            logger.error("Failed to download release: Invalid URL.", e);
            return new Pair<>(false, "Invalid URL. " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to download release: Unknown.", e);
            return new Pair<>(false, "Unexpected error. " + e.getMessage());
        }
    }
}
