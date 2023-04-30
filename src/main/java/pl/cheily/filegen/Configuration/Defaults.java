package pl.cheily.filegen.Configuration;

import java.nio.file.Path;

public class Defaults {

    public static final String CHALLONGE_API = "";

    public static final boolean AUTOCOMPLETE_ON = true;

    public static final boolean MAKE_RAW_OUTPUT = true;

    public static final boolean MAKE_HTML_OUTPUT = false;

    public static final boolean PUT_FLAGS = true;

    public static final String FLAG_EXTENSION = ".png";

    public static final Path FLAG_DIRECTORY = Path.of("./flags").toAbsolutePath();

}
