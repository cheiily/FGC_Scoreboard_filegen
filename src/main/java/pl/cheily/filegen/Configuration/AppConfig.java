package pl.cheily.filegen.Configuration;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.file.Path;

import static pl.cheily.filegen.Configuration.PropKey.*;

/**
 * Stores the current global configuration.
 *
 * @see Defaults
 */
public class AppConfig {
    private static java.beans.PropertyChangeSupport _pcs = new PropertyChangeSupport(AppConfig.class);
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


    /*==============================PROPERTIES==============================*/

    /**
     * Getter method for {@link PropKey#CHALLONGE_API}.<br/>
     * This property does not have a default value and must be manually set upon every app launch.
     *
     * @return The currently stored Challonge API key.
     */
    public static String CHALLONGE_API() {
        return _challongeAPI;
    }

    /**
     * Setter method for {@link PropKey#CHALLONGE_API}.
     *
     * @param newValue
     * @see #CHALLONGE_API()
     */
    public static void CHALLONGE_API(String newValue) {
        String old = _challongeAPI;
        _challongeAPI = newValue;
        _pcs.firePropertyChange(CHALLONGE_API.propName, old, _challongeAPI);
    }

    /**
     * Getter method for {@link PropKey#AUTOCOMPLETE_ON}.
     *
     * @return whether the {@link pl.cheily.filegen.Utils.AutocompleteWrapper}s should be enabled.
     */
    public static boolean AUTOCOMPLETE_ON() {
        return _autocompleteOn;
    }

    /**
     * Setter method for {@link PropKey#AUTOCOMPLETE_ON}.
     *
     * @param newValue
     * @see #AUTOCOMPLETE_ON()
     */
    public static void AUTOCOMPLETE_ON(boolean newValue) {
        if ( newValue == _autocompleteOn ) return;

        boolean old = _autocompleteOn;
        _autocompleteOn = newValue;
        _pcs.firePropertyChange(AUTOCOMPLETE_ON.propName, old, _autocompleteOn);
    }

    /**
     * Setter method for {@link PropKey#MAKE_RAW_OUTPUT}.
     *
     * @return whether the app should make "raw" file output.
     * @see pl.cheily.filegen.LocalData.RawOutputWriter
     * @see pl.cheily.filegen.LocalData.DataManager
     */
    public static boolean MAKE_RAW_OUTPUT() {
        return _makeRawOutput;
    }

    /**
     * Getter method for {@link PropKey#MAKE_RAW_OUTPUT}.
     *
     * @param newValue
     * @see #MAKE_RAW_OUTPUT()
     */
    public static void MAKE_RAW_OUTPUT(boolean newValue) {
        if ( newValue == _makeRawOutput ) return;

        boolean old = _makeRawOutput;
        _makeRawOutput = newValue;
        _pcs.firePropertyChange(MAKE_RAW_OUTPUT.propName, old, _makeRawOutput);
    }

    /**
     * Getter method for {@link PropKey#MAKE_HTML_OUTPUT}.
     *
     * @return whether the app should make browser source output.
     * @see pl.cheily.filegen.LocalData.OutputWriter
     * @see pl.cheily.filegen.LocalData.DataManager
     */
    public static boolean MAKE_HTML_OUTPUT() {
        return _makeHtmlOutput;
    }

    /**
     * Setter method for {@link PropKey#MAKE_HTML_OUTPUT}.
     *
     * @param newValue
     * @see #MAKE_HTML_OUTPUT()
     */
    public static void MAKE_HTML_OUTPUT(boolean newValue) {
        if ( newValue == _makeHtmlOutput ) return;

        boolean old = _makeHtmlOutput;
        _makeHtmlOutput = newValue;
        _pcs.firePropertyChange(MAKE_HTML_OUTPUT.propName, old, _makeHtmlOutput);
    }

    /**
     * Getter method for {@link PropKey#PUT_FLAGS}.<br/>
     * Note that, depending on the current version, the output may be not written at all,
     * or the flags may be substituted with a transparent pixel image.
     *
     * @return whether the app should write flag images in the output.
     */
    public static boolean PUT_FLAGS() {
        return _putFlags;
    }

    /**
     * Setter method for {@link PropKey#PUT_FLAGS}.
     *
     * @param newValue
     * @see #PUT_FLAGS()
     */
    public static void PUT_FLAGS(boolean newValue) {
        if ( newValue == _putFlags ) return;

        boolean old = _putFlags;
        _putFlags = newValue;
        _pcs.firePropertyChange(PUT_FLAGS.propName, old, _putFlags);
    }

    /**
     * Getter method for {@link PropKey#FLAG_EXTENSION}.
     *
     * @return the declared extension to save flag images with.
     */
    public static String FLAG_EXTENSION() {
        return _flagExtension;
    }

    /**
     * Setter method for {@link PropKey#FLAG_EXTENSION}.
     *
     * @param newValue
     * @see #FLAG_EXTENSION()
     */
    public static void FLAG_EXTENSION(String newValue) {
        if ( newValue.equals(_flagExtension) ) return;

        String old = _flagExtension;
        _flagExtension = newValue;
        _pcs.firePropertyChange(FLAG_EXTENSION.propName, old, _flagExtension);
    }

    /**
     * Setter method for {@link PropKey#FLAG_DIRECTORY}.
     *
     * @return the declared source directory for flag files.
     */
    public static Path FLAG_DIRECTORY() {
        return _flagDirectory;
    }

    /**
     * Getter method for {@link PropKey#FLAG_DIRECTORY}.
     *
     * @param newValue
     * @see #FLAG_DIRECTORY()
     */
    public static void FLAG_DIRECTORY(Path newValue) {
        if ( newValue.equals(_flagDirectory) ) return;

        Path old = _flagDirectory;
        _flagDirectory = newValue;
        _pcs.firePropertyChange(FLAG_DIRECTORY.propName, old, _flagDirectory);
    }
}

