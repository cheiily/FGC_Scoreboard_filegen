package pl.cheily.filegen.ResourceModules.Installation;

import pl.cheily.filegen.ResourceModules.Exceptions.ArchiveFormatNotSupportedException;
import pl.cheily.filegen.ResourceModules.Exceptions.UnarchivingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipInputStream;

public class UnarchiverFactory {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UnarchiverFactory.class);

    @FunctionalInterface
    public interface Unarchiver {
        Path apply(Path archive, Path destination) throws UnarchivingException;
    }

    public static Unarchiver getFor(String format) throws ArchiveFormatNotSupportedException {
        return switch (format.toLowerCase()) {
            case ".zip" -> UnarchiverFactory::extractZip;
            default -> throw ArchiveFormatNotSupportedException.fromFormat(format);
        };
    }

    public static Path extractZip(Path zipFile, Path destination) throws UnarchivingException {
        try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(zipFile))) {
            java.util.zip.ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                Path entryPath = destination.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    Files.copy(zipIn, entryPath);
                }
                zipIn.closeEntry();
            }

            logger.info("Extracted ZIP file: {} to {}", zipFile, destination);
            return destination;
        } catch (IOException e) {
            logger.error("Failed extracting ZIP file: {}", zipFile, e);
            throw UnarchivingException.fromArchiveAndDestination(
                    zipFile.toAbsolutePath().toString(),
                    destination.toAbsolutePath().toString(),
                    e
            );
        }
    }
}
