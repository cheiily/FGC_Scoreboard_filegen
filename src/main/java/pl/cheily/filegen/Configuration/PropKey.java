package pl.cheily.filegen.Configuration;

public enum PropKey {
    CHALLONGE_API("CHALLONGE_APi"),
    AUTOCOMPLETE_ON("AUTOCOMPLETE_ON"),
    MAKE_RAW_OUTPUT("MAKE_RAW_OUTPUT"),
    MAKE_HTML_OUTPUT("MAKE_HTML_OUTPUT"),
    PUT_FLAGS("PUT_FLAGS"),
    FLAG_EXTENSION("FLAG_EXTENSION"),
    FLAG_DIRECTORY("FLAG_DIRECTORY");

    public final String propName;

    PropKey(String propName) {
        this.propName = propName;
    }

    public static PropKey of(String value) {
        for (PropKey key : PropKey.values()) {
            if ( key.propName.equals(value) ) return key;
        }
        return null;
    }
}
