package pl.cheily.filegen.Configuration;

/**
 * Set of defined configuration property keys complete with their stringified names for event-based subscription.
 */
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

    /**
     * Finds the key with the passed value.
     *
     * @param value to find key by.
     * @return valid {@code PropKey} if found, {@code null} otherwise.
     */
    public static PropKey of(String value) {
        for (PropKey key : PropKey.values()) {
            if ( key.propName.equals(value) ) return key;
        }
        return null;
    }
}
