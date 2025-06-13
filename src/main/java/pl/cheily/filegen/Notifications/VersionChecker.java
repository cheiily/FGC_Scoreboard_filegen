package pl.cheily.filegen.Notifications;

import javafx.application.Platform;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.action.Action;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cheily.filegen.Launcher;
import pl.cheily.filegen.ScoreboardApplication;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class VersionChecker {
    private final static Logger logger = LoggerFactory.getLogger(VersionChecker.class);
    private static final String GITHUB_API_URL = "https://api.github.com/repos/cheiily/FGC_Scoreboard_filegen/releases";
    private static final String GITHUB_API_VERSION = "2022-11-28";

    public static class VersionData {
        boolean isNew;
        String count;
        String url;
        List<String> assetDownloadURLs;
        List<Long> sizes;
        Long totalSize;

        public VersionData(boolean isNew, String count, String url, List<String> assetDownloadURLs, List<Long> sizes, Long totalSize) {
            this.isNew = isNew;
            this.count = count;
            this.url = url;
            this.assetDownloadURLs = assetDownloadURLs;
            this.sizes = sizes;
            this.totalSize = totalSize;
        }

        @Override
        public String toString() {
            return "VersionData{" +
                    "isNew=" + isNew +
                    ", count='" + count + '\'' +
                    ", url='" + url + '\'' +
                    ", assetDownloadURLs=[" + String.join(",", assetDownloadURLs) + ']' +
                    ", size=" + totalSize +
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
                                        .hideAfter(Duration.seconds(10))
                                        .hideCloseButton() // for consistency with the API notifications
                                        .showConfirm();
                                else
                                    Notifications.create()
                                        .title("Download failed.")
                                        .text(result.second())
                                        .hideAfter(Duration.seconds(10))
                                        .hideCloseButton() // for consistency with the API notifications
                                        .showError();
                            }).run();
                        }))
                    .hideAfter(Duration.seconds(10))
                    .hideCloseButton() // for consistency with the API notifications
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
        AtomicLong totalSize = new AtomicLong();

        JSONArray assets = release.getJSONArray("assets");
        ArrayList<String> assetDownloadURLs = new ArrayList<>(assets.length());
        ArrayList<Long> sizes = new ArrayList<>(assets.length());
        assets.forEach(jasset -> {
            var asset = (JSONObject) jasset;
            assetDownloadURLs.add(asset.getString("browser_download_url"));
            long size = asset.getLong("size");
            sizes.add(size);
            totalSize.addAndGet(size);
        });

        if (compareSemVer(currentVer, newestVer) < 0) {
            VersionData versionData = new VersionData(true, newestVer, url, assetDownloadURLs, sizes, totalSize.get());
            logger.info("New version available: {}", versionData);
            return versionData;
        } else {
            logger.info("Current version {} is up to date (new: {}).", currentVer, newestVer);
            return new VersionData(false, newestVer, url, assetDownloadURLs, sizes, totalSize.get());
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
        try {
            Path jarPath = Path.of(ScoreboardApplication.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            if (jarPath.toString().startsWith("file:/")) {
                jarPath = Path.of(jarPath.toString().substring(5));
            }
            return jarPath;
        } catch (URISyntaxException e) {
            logger.error("Failed to locate jar path.", e);
            return null;
        }
    }

    private static Pair<Boolean, String> downloadRelease(VersionData version) {
        Pair<Boolean, String> totalResult = new Pair<>(true, "");

        Path directory = getJarPath().resolve("filegen-" + version.count);

        for (int i = 0; i < version.assetDownloadURLs.size(); i++) {
            var result = downloadFile(version.assetDownloadURLs.get(i), directory, version.sizes.get(i));
            totalResult = new Pair<>(
                    totalResult.first() && result.first(),
                    totalResult.second() + result.second() + '\n'
            );
        }

        if (totalResult.first())
            totalResult = new Pair<>(
                    true,
                    "Saved new release to: " + directory + ".\nNew version ready to launch."
            );
        return totalResult;
    }

    private static Pair<Boolean, String> downloadFile(String URL, Path directory, long size) {
        try {
            logger.trace("Downloading file: {}", URL);

            URL downloadUrl = new URL(URL);
            HttpsURLConnection connection = (HttpsURLConnection) downloadUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/octet-stream");

            Path target = directory.resolve(Path.of(downloadUrl.getPath()).getFileName()).toAbsolutePath();

            if (Files.exists(target)) {
                logger.warn("Target path exists, aborting download: {}", target);
                return new Pair<>(false, "File already exists: " + target);
            }

            Files.createDirectories(target.getParent());
            Files.createFile(target);

            try (FileChannel out = FileChannel.open(target, java.nio.file.StandardOpenOption.WRITE);
                 ReadableByteChannel in = Channels.newChannel(connection.getInputStream())) {
                long bytesTransferred = 0;
                while (bytesTransferred < size) {
                    long bytes = out.transferFrom(in, bytesTransferred, size - bytesTransferred);
                    logger.trace("Downloaded {} bytes to {}", bytes, target);
                    bytesTransferred += bytes;
                }
                logger.info("Download complete. Total {} bytes. File saved to {}", bytesTransferred, target.toAbsolutePath());
            }

            return new Pair<>(true, "Saved release to: " + target + ".\nNew version ready to launch.");

        } catch (MalformedURLException e) {
            logger.error("Failed to download release: Invalid URL.", e);
            return new Pair<>(false, "Invalid URL. " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to download release: Unknown.", e);
            return new Pair<>(false, "Unexpected error. " + e.getMessage());
        }
    }
}
