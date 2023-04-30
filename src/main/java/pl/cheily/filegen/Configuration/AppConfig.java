package pl.cheily.filegen.Configuration;

import org.ini4j.Ini;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.file.Path;

import static pl.cheily.filegen.Configuration.PropKey.*;

/**
 * Stores the current global configuration
 */

public class AppConfig {
    private static java.beans.PropertyChangeSupport _pcs = new PropertyChangeSupport(AppConfig.class);


    public static void reset() {
        CHALLONGE_API(Defaults.CHALLONGE_API);
        AUTOCOMPLETE_ON(Defaults.AUTOCOMPLETE_ON);
        MAKE_RAW_OUTPUT(Defaults.MAKE_RAW_OUTPUT);
        MAKE_HTML_OUTPUT(Defaults.MAKE_HTML_OUTPUT);
        PUT_FLAGS(Defaults.PUT_FLAGS);
        FLAG_EXTENSION(Defaults.FLAG_EXTENSION);
        FLAG_DIRECTORY(Defaults.FLAG_DIRECTORY);
    }

    public static void subscribe(PropKey toProp, PropertyChangeListener listener) {
        _pcs.addPropertyChangeListener(toProp.propName, listener);
    }

    public static void subscribeAll(PropertyChangeListener listener) {
        _pcs.addPropertyChangeListener(listener);
    }

    public static void unsubscribe(PropKey fromProp, PropertyChangeListener listener) {
        _pcs.removePropertyChangeListener(fromProp.propName, listener);
    }

    public static void unsubscribeAll(PropertyChangeListener listener) {
        _pcs.removePropertyChangeListener(listener);
    }

    private static String _challongeAPI = Defaults.CHALLONGE_API;

    public static String CHALLONGE_API() {
        return _challongeAPI;
    }

    public static void CHALLONGE_API(String newValue) {
        String old = _challongeAPI;
        _challongeAPI = newValue;
        _pcs.firePropertyChange(CHALLONGE_API.propName, old, _challongeAPI);
    }


    private static boolean _autocompleteOn = Defaults.AUTOCOMPLETE_ON;

    public static boolean AUTOCOMPLETE_ON() {
        return _autocompleteOn;
    }

    public static void AUTOCOMPLETE_ON(boolean newValue) {
        if ( newValue == _autocompleteOn ) return;

        boolean old = _autocompleteOn;
        _autocompleteOn = newValue;
        _pcs.firePropertyChange(AUTOCOMPLETE_ON.propName, old, _autocompleteOn);
    }


    private static boolean _makeRawOutput = Defaults.MAKE_RAW_OUTPUT;

    public static boolean MAKE_RAW_OUTPUT() {
        return _makeRawOutput;
    }

    public static void MAKE_RAW_OUTPUT(boolean newValue) {
        if ( newValue == _makeRawOutput ) return;

        boolean old = _makeRawOutput;
        _makeRawOutput = newValue;
        _pcs.firePropertyChange(MAKE_RAW_OUTPUT.propName, old, _makeRawOutput);
    }


    private static boolean _makeHtmlOutput = Defaults.MAKE_HTML_OUTPUT;

    public static boolean MAKE_HTML_OUTPUT() {
        return _makeHtmlOutput;
    }

    public static void MAKE_HTML_OUTPUT(boolean newValue) {
        if ( newValue == _makeHtmlOutput ) return;

        boolean old = _makeHtmlOutput;
        _makeHtmlOutput = newValue;
        _pcs.firePropertyChange(MAKE_HTML_OUTPUT.propName, old, _makeHtmlOutput);
    }


    private static boolean _putFlags = Defaults.PUT_FLAGS;

    public static boolean PUT_FLAGS() {
        return _putFlags;
    }

    public static void PUT_FLAGS(boolean newValue) {
        if ( newValue == _putFlags ) return;

        boolean old = _putFlags;
        _putFlags = newValue;
        _pcs.firePropertyChange(PUT_FLAGS.propName, old, _putFlags);
    }


    private static String _flagExtension = Defaults.FLAG_EXTENSION;

    public static String FLAG_EXTENSION() {
        return _flagExtension;
    }

    public static void FLAG_EXTENSION(String newValue) {
        if ( newValue.equals(_flagExtension) ) return;

        String old = _flagExtension;
        _flagExtension = newValue;
        _pcs.firePropertyChange(FLAG_EXTENSION.propName, old, _flagExtension);
    }


    private static Path _flagDirectory = Defaults.FLAG_DIRECTORY;

    public static Path FLAG_DIRECTORY() {
        return _flagDirectory;
    }

    public static void FLAG_DIRECTORY(Path newValue) {
        if ( newValue.equals(_flagDirectory) ) return;

        Path old = _flagDirectory;
        _flagDirectory = newValue;
        _pcs.firePropertyChange(FLAG_DIRECTORY.propName, old, _flagDirectory);
    }
}

