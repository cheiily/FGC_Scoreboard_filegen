package pl.cheily.filegen.Configuration;

import org.ini4j.Ini;
import org.ini4j.Profile;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.file.Path;

import static pl.cheily.filegen.Configuration.PropKey.*;

/**
 * Stores the current global configuration.
 *
 * @see Defaults
 * @see PropKey
 */
public class AppConfig {
    private static final java.beans.PropertyChangeSupport _pcs = new PropertyChangeSupport(AppConfig.class);
    private static final String SECTION_NAME = "SETTINGS";
    /*==============================PROPERTIES==============================*/
    private static String _challongeAPI = Defaults.CHALLONGE_API;
    private static boolean _autocompleteOn = Defaults.AUTOCOMPLETE_ON;
    private static boolean _makeRawOutput = Defaults.MAKE_RAW_OUTPUT;
    private static boolean _makeHtmlOutput = Defaults.MAKE_HTML_OUTPUT;
    private static boolean _putFlags = Defaults.PUT_FLAGS;
    private static String _flagExtension = Defaults.FLAG_EXTENSION;
    private static Path _flagDirectory = Defaults.FLAG_DIRECTORY;
    /*==============================PROPERTIES==============================*/

    /**
     * Resets the values to default state. Fires change event for all of them.
     *
     * @see Defaults
     */
    public static void reset() {
        CHALLONGE_API(Defaults.CHALLONGE_API);
        AUTOCOMPLETE_ON(Defaults.AUTOCOMPLETE_ON);
        MAKE_RAW_OUTPUT(Defaults.MAKE_RAW_OUTPUT);
        MAKE_HTML_OUTPUT(Defaults.MAKE_HTML_OUTPUT);
        PUT_FLAGS(Defaults.PUT_FLAGS);
        FLAG_EXTENSION(Defaults.FLAG_EXTENSION);
        FLAG_DIRECTORY(Defaults.FLAG_DIRECTORY);
    }

    /**
     * @param toProp   property to subscribe to
     * @param listener listener to add as a subscriber
     */
    public static void subscribe(PropKey toProp, PropertyChangeListener listener) {
        _pcs.addPropertyChangeListener(toProp.propName, listener);
    }

    /**
     * Subscribes to all properties.
     *
     * @param listener listener to add as a subscriber
     */
    public static void subscribeAll(PropertyChangeListener listener) {
        _pcs.addPropertyChangeListener(listener);
    }

    /**
     * @param fromProp property to unsubscribe from
     * @param listener listener to remove from subscribers
     */
    public static void unsubscribe(PropKey fromProp, PropertyChangeListener listener) {
        _pcs.removePropertyChangeListener(fromProp.propName, listener);
    }

    /**
     * Unsubscribes from all properties.
     *
     * @param listener listener to remove from subscribers
     */
    public static void unsubscribeAll(PropertyChangeListener listener) {
        _pcs.removePropertyChangeListener(listener);
    }

    /**
     * @return An {@link Ini} object with such a structure that:<br>
     * • All entries are under the section {@link AppConfig#SECTION_NAME}.<br>
     * • The key of every property is its corresponding {@link PropKey#toString()}.
     */
    public synchronized static Ini getAsIni() {
        Ini ini = new Ini();
        ini.put(SECTION_NAME, CHALLONGE_API.propName, CHALLONGE_API());
        ini.put(SECTION_NAME, AUTOCOMPLETE_ON.propName, AUTOCOMPLETE_ON());
        ini.put(SECTION_NAME, MAKE_RAW_OUTPUT.propName, MAKE_RAW_OUTPUT());
        ini.put(SECTION_NAME, MAKE_HTML_OUTPUT.propName, MAKE_HTML_OUTPUT());
        ini.put(SECTION_NAME, PUT_FLAGS.propName, PUT_FLAGS());
        ini.put(SECTION_NAME, FLAG_EXTENSION.propName, FLAG_EXTENSION());
        ini.put(SECTION_NAME, FLAG_DIRECTORY.propName, FLAG_DIRECTORY());

        return ini;
    }

    /**
     * Loads data from the passed {@link Ini} object. The object must have such a structure that:<br>
     * • All entries are under the section {@link AppConfig#SECTION_NAME}.<br>
     * • The key of every property is its corresponding {@link PropKey#toString()}.
     * <p>
     * Fires all update events.
     * 
     * @param ini a valid ini object
     * @return success value
     * @implSpec The actions can be considered atomic - either all fields are loaded or none, as well as that no other action may access the data if the operation is not complete, by virtue of synchronization.
     */
    public static boolean loadFromIni(Ini ini) {
        Profile.Section cfg_sec = ini.get(SECTION_NAME);
        if ( cfg_sec == null ) return false;

        String newApi = cfg_sec.get(CHALLONGE_API.propName, String.class);
        Boolean newAutocomplete = cfg_sec.get(AUTOCOMPLETE_ON.propName, Boolean.class);
        Boolean newRawOut = cfg_sec.get(MAKE_RAW_OUTPUT.propName, Boolean.class);
        Boolean newHtmlOut = cfg_sec.get(MAKE_HTML_OUTPUT.propName, Boolean.class);
        Boolean newPutFlags = cfg_sec.get(PUT_FLAGS.propName, Boolean.class);
        String newFlagExt = cfg_sec.get(FLAG_EXTENSION.propName, String.class);
        Path newFlagPth = cfg_sec.get(FLAG_DIRECTORY.propName, Path.class);

        boolean correct = CHALLONGE_API.validateParam(newApi)
                        && AUTOCOMPLETE_ON.validateParam(newAutocomplete)
                        && MAKE_RAW_OUTPUT.validateParam(newRawOut)
                        && MAKE_HTML_OUTPUT.validateParam(newHtmlOut)
                        && PUT_FLAGS.validateParam(newPutFlags)
                        && FLAG_EXTENSION.validateParam(newFlagExt)
                        && FLAG_DIRECTORY.validateParam(newFlagPth);

        if ( !correct ) return false;

        String oldApi;
        Boolean oldAutocomplete;
        Boolean oldRawOut;
        Boolean oldHtmlOut;
        Boolean oldPutFlags;
        String oldFlagExt;
        Path oldFlagPth;

        synchronized (AppConfig.class) {
            oldApi = _challongeAPI;
            oldAutocomplete = _autocompleteOn;
            oldRawOut = _makeRawOutput;
            oldHtmlOut = _makeHtmlOutput;
            oldPutFlags = _putFlags;
            oldFlagExt = _flagExtension;
            oldFlagPth = _flagDirectory;

            _challongeAPI = newApi;
            _autocompleteOn = newAutocomplete;
            _makeRawOutput = newRawOut;
            _makeHtmlOutput = newHtmlOut;
            _putFlags = newPutFlags;
            _flagExtension = newFlagExt;
            _flagDirectory = newFlagPth;
        }

        _pcs.firePropertyChange(CHALLONGE_API.propName, oldApi, newApi);
        _pcs.firePropertyChange(AUTOCOMPLETE_ON.propName, oldAutocomplete, newAutocomplete);
        _pcs.firePropertyChange(MAKE_RAW_OUTPUT.propName, oldRawOut, newRawOut);
        _pcs.firePropertyChange(MAKE_HTML_OUTPUT.propName, oldHtmlOut, newHtmlOut);
        _pcs.firePropertyChange(PUT_FLAGS.propName, oldPutFlags, newPutFlags);
        _pcs.firePropertyChange(FLAG_EXTENSION.propName, oldFlagExt, newFlagExt);
        _pcs.firePropertyChange(FLAG_DIRECTORY.propName, oldFlagPth, newFlagPth);

        return true;
    }

    /*==============================PROPERTIES==============================*/

    /**
     * Getter method for {@link PropKey#CHALLONGE_API}.<br/>
     * This property does not have a default value and must be manually set upon every app launch.
     *
     * @return The currently stored Challonge API key.
     */
    public synchronized static String CHALLONGE_API() {
        return _challongeAPI;
    }

    /**
     * Setter method for {@link PropKey#CHALLONGE_API}.
     * No changes applied if the value is invalid.
     *
     * @param newValue
     * @return true if value was valid & was applied, false otherwise
     * @see #CHALLONGE_API()
     */
    public static boolean CHALLONGE_API(String newValue) {
        if ( !CHALLONGE_API.validateParam(newValue) ) return false;
        String old;

        synchronized (CHALLONGE_API) {
            if ( newValue.equals(_challongeAPI) ) return true;

            old = _challongeAPI;
            _challongeAPI = newValue;
        }
        _pcs.firePropertyChange(CHALLONGE_API.propName, old, _challongeAPI);

        return true;
    }

    /**
     * Getter method for {@link PropKey#AUTOCOMPLETE_ON}.
     *
     * @return whether the {@link pl.cheily.filegen.Utils.AutocompleteWrapper}s should be enabled.
     */
    public synchronized static boolean AUTOCOMPLETE_ON() {
        return _autocompleteOn;
    }

    /**
     * Setter method for {@link PropKey#AUTOCOMPLETE_ON}.
     * No changes applied if the value is invalid.
     *
     * @param newValue
     * @return true if value was valid & was applied, false otherwise
     * @see #AUTOCOMPLETE_ON()
     */
    public static boolean AUTOCOMPLETE_ON(Boolean newValue) {
        if ( !AUTOCOMPLETE_ON.validateParam(newValue) ) return false;

        boolean old;
        synchronized (AUTOCOMPLETE_ON) {
            if ( newValue == _autocompleteOn ) return true;

            old = _autocompleteOn;
            _autocompleteOn = newValue;
        }
        _pcs.firePropertyChange(AUTOCOMPLETE_ON.propName, old, _autocompleteOn);

        return true;
    }

    /**
     * Getter method for {@link PropKey#MAKE_RAW_OUTPUT}.
     *
     * @return whether the app should make "raw" file output.
     * @see pl.cheily.filegen.LocalData.RawOutputWriter
     * @see pl.cheily.filegen.LocalData.DataManager
     */
    public synchronized static boolean MAKE_RAW_OUTPUT() {
        return _makeRawOutput;
    }

    /**
     * Setter method for {@link PropKey#MAKE_RAW_OUTPUT}.
     * No changes applied if the value is invalid.
     *
     * @param newValue
     * @return true if value was valid & was applied, false otherwise
     * @see #MAKE_RAW_OUTPUT()
     */
    public static boolean MAKE_RAW_OUTPUT(Boolean newValue) {
        if ( !MAKE_RAW_OUTPUT.validateParam(newValue) ) return false;

        boolean old;
        synchronized (MAKE_RAW_OUTPUT) {
            if ( newValue == _makeRawOutput ) return true;

            old = _makeRawOutput;
            _makeRawOutput = newValue;
        }
        _pcs.firePropertyChange(MAKE_RAW_OUTPUT.propName, old, _makeRawOutput);

        return true;
    }

    /**
     * Getter method for {@link PropKey#MAKE_HTML_OUTPUT}.
     *
     * @return whether the app should make browser source output.
     * @see pl.cheily.filegen.LocalData.OutputWriter
     * @see pl.cheily.filegen.LocalData.DataManager
     */
    public synchronized static boolean MAKE_HTML_OUTPUT() {
        return _makeHtmlOutput;
    }

    /**
     * Setter method for {@link PropKey#MAKE_HTML_OUTPUT}.
     * No changes applied if the value is invalid.
     *
     * @param newValue
     * @return true if value was valid & was applied, false otherwise
     * @see #MAKE_HTML_OUTPUT()
     */
    public static boolean MAKE_HTML_OUTPUT(Boolean newValue) {
        if ( !MAKE_HTML_OUTPUT.validateParam(newValue) ) return false;

        boolean old;
        synchronized (MAKE_HTML_OUTPUT) {
            if ( newValue == _makeHtmlOutput ) return true;

            old = _makeHtmlOutput;
            _makeHtmlOutput = newValue;
        }
        _pcs.firePropertyChange(MAKE_HTML_OUTPUT.propName, old, _makeHtmlOutput);

        return true;
    }

    /**
     * Getter method for {@link PropKey#PUT_FLAGS}.<br/>
     * Note that, depending on the current version, the output may be not written at all,
     * or the flags may be substituted with a transparent pixel image.
     *
     * @return whether the app should write flag images in the output.
     */
    public synchronized static boolean PUT_FLAGS() {
        return _putFlags;
    }

    /**
     * Setter method for {@link PropKey#PUT_FLAGS}.
     * No changes applied if the value is invalid.
     *
     * @param newValue
     * @return true if value was valid & was applied, false otherwise
     * @see #PUT_FLAGS()
     */
    public static boolean PUT_FLAGS(Boolean newValue) {
        if ( !PUT_FLAGS.validateParam(newValue) ) return false;

        boolean old;
        synchronized (PUT_FLAGS) {
            if ( newValue == _putFlags ) return true;

            old = _putFlags;
            _putFlags = newValue;
        }
        _pcs.firePropertyChange(PUT_FLAGS.propName, old, _putFlags);
        return true;
    }

    /**
     * Getter method for {@link PropKey#FLAG_EXTENSION}.
     *
     * @return the declared extension to save flag images with.
     */
    public synchronized static String FLAG_EXTENSION() {
        return _flagExtension;
    }

    /**
     * Setter method for {@link PropKey#FLAG_EXTENSION}.
     * No changes applied if the value is invalid.
     *
     * @param newValue
     * @return true if value was valid & was applied, false otherwise
     * @see #FLAG_EXTENSION()
     */
    public static boolean FLAG_EXTENSION(String newValue) {
        if ( !FLAG_EXTENSION.validateParam(newValue) ) return false;
        String old;

        synchronized (FLAG_EXTENSION) {
            if ( newValue.equals(_flagExtension) ) return true;

            old = _flagExtension;
            _flagExtension = newValue;
        }
        _pcs.firePropertyChange(FLAG_EXTENSION.propName, old, _flagExtension);

        return true;
    }

    /**
     * Getter method for {@link PropKey#FLAG_DIRECTORY}.
     *
     * @return the declared source directory for flag files.
     */
    public synchronized static Path FLAG_DIRECTORY() {
        return _flagDirectory;
    }

    /**
     * Setter method for {@link PropKey#FLAG_DIRECTORY}.
     * No changes applied if the value is invalid.
     *
     * @param newValue
     * @return true if value was valid & was applied, false otherwise
     * @see #FLAG_DIRECTORY()
     */
    public static boolean FLAG_DIRECTORY(Path newValue) {
        if ( !FLAG_DIRECTORY.validateParam(newValue) ) return false;
        Path old;

        synchronized (FLAG_DIRECTORY) {
            if ( newValue.equals(_flagDirectory) ) return true;

            old = _flagDirectory;
            _flagDirectory = newValue;
        }
        _pcs.firePropertyChange(FLAG_DIRECTORY.propName, old, _flagDirectory);

        return true;
    }
}

