package pl.cheily.filegen.ResourceModules.Installation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cheily.filegen.ResourceModules.Definition.ResourceModuleDefinition;
import pl.cheily.filegen.ResourceModules.Exceptions.ResourceModuleDownloadException;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class DownloadUtils {
    public static final Logger logger = LoggerFactory.getLogger(DownloadUtils.class.getName());

    public static Path downloadFile(String url, Path targetPath) throws ResourceModuleDownloadException {
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
                 FileChannel fileChannel = FileChannel.open(targetPath, StandardOpenOption.WRITE)) {
                long bytesTransferred = fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                logger.trace("Downloaded {} bytes to {}", bytesTransferred, targetPath);
            } catch (IOException e) {
                logger.error("Failed to download file URL: {}", url, e);
                throw ResourceModuleDownloadException.fromURL(url, targetPath.toAbsolutePath().toString(), e);
            }

            logger.info("File downloaded successfully: {}", targetPath);
            return targetPath;

        } catch (MalformedURLException e) {
            logger.error("Failed to download file, invalid URL: {}", url, e);
            throw ResourceModuleDownloadException.fromURL(url, targetPath.toAbsolutePath().toString(), "Invalid URL.", e);
        } catch (IOException e) {
            logger.error("Failed to download file: {}", url, e);
            throw ResourceModuleDownloadException.fromURL(url, targetPath.toAbsolutePath().toString(), e);
            // todo try cleanup on catch
        }
    }

    public static Path downloadTempFile(String url, Path dir) throws ResourceModuleDownloadException {
        try {
            Files.createDirectories(dir);
            File tempfile = Files.createTempFile(dir, "rsc_mdl_def_", ResourceModuleDefinition.EXTENSION).toFile();
            tempfile.deleteOnExit();
            return downloadFile(url, tempfile.toPath());
        } catch (IOException e) {
            logger.error("Failed to download temporary file, URL: {}", url, e);
            throw ResourceModuleDownloadException.fromURL(url, dir.toAbsolutePath().toString(), e);
        }
    }
}
