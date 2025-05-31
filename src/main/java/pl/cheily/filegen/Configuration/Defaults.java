package pl.cheily.filegen.Configuration;

import java.nio.file.Path;

/**
 * Set of default configuration values, loaded on app-launch.<br/>
 * May be bulk-reassigned via {@link AppConfig#reset()}.
 *
 * @see AppConfig
 */
public class Defaults {

    public static final String CHALLONGE_API = "";

    public static final boolean AUTOCOMPLETE_ON = true;

    public static final boolean MAKE_RAW_OUTPUT = true;

    public static final boolean MAKE_HTML_OUTPUT = false;

    public static final boolean GF_RADIO_ON_LABEL_MATCH = true;

    public static final boolean WRITE_COMM_3 = false;

    public static final boolean PUT_FLAGS = true;

    public static final String FLAG_EXTENSION = ".png";

    public static final Path FLAG_DIRECTORY = Path.of("flags").toAbsolutePath();

}
