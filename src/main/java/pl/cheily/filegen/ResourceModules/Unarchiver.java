package pl.cheily.filegen.ResourceModules;

import java.nio.file.Path;
import java.util.function.BiFunction;

public class Unarchiver {
    public static BiFunction<Path, Path, Path> getFor(String format) {
        return switch (format.toLowerCase()) {
            case "zip" -> Unarchiver::extractZip;
            default -> null;
        };
    }

    public static Path extractZip(Path zipFile, Path destination) {
        // todo
        return null;
    }
}
