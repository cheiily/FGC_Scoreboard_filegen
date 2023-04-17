package pl.cheily.filegen.Configuration;

import java.nio.file.Path;

/**
 * Stores the current global configuration
 */
public class AppConfig {

    public static String CHALLONGE_API = null;

    public static boolean IGNORE_CASE = Defaults.IGNORE_CASE;

    public static boolean MAKE_RAW_OUTPUT = Defaults.MAKE_RAW_OUTPUT;

    public static boolean MAKE_HTML_OUTPUT = Defaults.MAKE_HTML_OUTPUT;

    public static boolean PUT_FLAGS = Defaults.PUT_FLAGS;

    public static String FLAG_EXTENSION = Defaults.FLAG_EXTENSION;

    public static Path FLAGS_DIR = Defaults.FLAGS_DIR;

}
