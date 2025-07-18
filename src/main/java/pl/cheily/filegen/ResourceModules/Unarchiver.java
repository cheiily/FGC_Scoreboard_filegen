package pl.cheily.filegen.ResourceModules;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.zip.ZipInputStream;

public class Unarchiver {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Unarchiver.class);

    public static BiFunction<Path, Path, Path> getFor(String format) {
        return switch (format.toLowerCase()) {
            case ".zip" -> Unarchiver::extractZip;
            default -> null;
        };
    }

    public static Path extractZip(Path zipFile, Path destination) {
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
            logger.error("Failed to extract ZIP file: {}", zipFile, e);
        }
        return null;
    }
}
