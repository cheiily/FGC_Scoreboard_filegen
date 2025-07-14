package pl.cheily.filegen.ResourceModules;

import net.harawata.appdirs.AppDirsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class DownloadUtils {
    public static final Logger logger = LoggerFactory.getLogger(DownloadUtils.class.getName());

    public static Path downloadFile(String url, Path targetPath) {
        try {
            logger.trace("Downloading file URL: {}", url);

            URL downloadUrl = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) downloadUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/octet-stream");

            if (!Files.exists(targetPath)) {
                Files.createDirectories(targetPath.getParent());
                Files.createFile(targetPath);
            }

            try (ReadableByteChannel readableByteChannel = Channels.newChannel(connection.getInputStream());
                 FileChannel fileChannel = FileChannel.open(targetPath)) {
                long bytesTransferred = fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                logger.trace("Downloaded {} bytes to {}", bytesTransferred, targetPath);
            } catch (IOException e) {
                logger.error("Failed to download file URL: {}", url, e);
                throw e;
            }

            logger.info("File downloaded successfully: {}", targetPath);
            return targetPath;

        } catch (MalformedURLException e) {
            logger.error("Failed to download file, invalid URL: {}", url, e);
        } catch (IOException e) {
            logger.error("Failed to download file: {}", url, e);
        }

        return null;
    }

    public static Path downloadTempFile(String url, Path dir) {
        try {
            Files.createDirectories(dir);
            return downloadFile(url, Files.createTempFile(dir, "rsc_mdl_def_", ResourceModuleDefinition.EXTENSION));
        } catch (IOException e) {
            logger.error("Failed to download temporary file, URL: {}", url, e);
            return null;
        }
    }
}
