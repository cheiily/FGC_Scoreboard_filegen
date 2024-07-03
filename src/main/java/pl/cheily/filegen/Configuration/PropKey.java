package pl.cheily.filegen.Configuration;

import java.nio.file.Path;
import java.util.function.Function;

/**
 * Set of defined configuration property keys complete with their stringified names for event-based subscription,
 * as well as parameter validation functionality.
 */
public enum PropKey {
    CHALLONGE_API("CHALLONGE_API", String.class, null),
    AUTOCOMPLETE_ON("AUTOCOMPLETE_ON", Boolean.class, null),
    MAKE_RAW_OUTPUT("MAKE_RAW_OUTPUT", Boolean.class, null),
    MAKE_HTML_OUTPUT("MAKE_HTML_OUTPUT", Boolean.class, null),
    GF_RADIO_ON_LABEL_MATCH("GF_RADIO_ON_LABEL_MATCH", Boolean.class, null),
    PUT_FLAGS("PUT_FLAGS", Boolean.class, null),
    FLAG_EXTENSION("FLAG_EXTENSION", String.class, null),
    FLAG_DIRECTORY( "FLAG_DIRECTORY", Path.class, obj ->
            obj != null && (obj.getClass() == String.class || obj.getClass() == Path.of("").getClass())
    );

    /**
     * Stringified property name. May be used for issuing & subscribing to events, saving a configuration file, etc.
     */
    public final String propName;

    /**
     * Additional validation functionality, if more than a basic null & class check is required.
     */
    private final Function<Object, Boolean> validator;
    /**
     * Class of the configuration field.
     */
    private final Class paramType;

    PropKey(String propName, Class paramType, Function<Object, Boolean> validator) {
        this.propName = propName;
        this.paramType = paramType;
        this.validator = validator;
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

    /**
     * Check whether {@code param} is a valid and acceptable configuration value.
     * Checks for null and correct class.
     * Uses {@link PropKey#validator} if assigned.
     *
     * @param param valid not-null, class-matching parameter
     * @return true if valid, false otherwise
     */
    public boolean validateParam(Object param) {
        if ( validator != null )
            return validator.apply(param);
        else return defaultValidator(param);
    }

    public boolean defaultValidator(Object param) {
        return param != null && param.getClass() == this.paramType;
    }
}
