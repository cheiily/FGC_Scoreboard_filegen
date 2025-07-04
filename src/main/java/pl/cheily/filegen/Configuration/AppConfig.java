package pl.cheily.filegen.Configuration;

import org.ini4j.Ini;
import org.ini4j.Profile;
import pl.cheily.filegen.LocalData.FileManagement.Meta.Config.ConfigDAO;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputWriter;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.RawOutputWriter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.file.Path;
import java.util.Objects;

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
    private static boolean _gfRadio = Defaults.GF_RADIO_ON_LABEL_MATCH;
    private static boolean _writeComm3 = Defaults.WRITE_COMM_3;
    private static boolean _checkNotifications = Defaults.CHECK_NOTIFICATIONS;
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
        GF_RADIO_ON_LABEL_MATCH(Defaults.GF_RADIO_ON_LABEL_MATCH);
        FLAG_EXTENSION(Defaults.FLAG_EXTENSION);
        FLAG_DIRECTORY(Defaults.FLAG_DIRECTORY);
        // don't reset writeComm3, it's only loaded on init & changed by a separate button
        CHECK_NOTIFICATIONS(Defaults.CHECK_NOTIFICATIONS);
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
     * Getter method for {@link PropKey#GF_RADIO_ON_LABEL_MATCH}.
     *
     * @return whether the GF radio will be automatically turned on depending on the current round label.
     */
    public synchronized static boolean GF_RADIO_ON_LABEL_MATCH() { return _gfRadio; }

    /**
     * Setter method for {@link PropKey#GF_RADIO_ON_LABEL_MATCH}.
     * No changes applied if the value is invalid.
     *
     * @param newValue
     * @return true if value was valid & was applied, false otherwise
     * @see #GF_RADIO_ON_LABEL_MATCH()
     */
    public static boolean GF_RADIO_ON_LABEL_MATCH(Boolean newValue) {
        if ( !GF_RADIO_ON_LABEL_MATCH.validateParam(newValue) ) return false;

        boolean old;
        synchronized (GF_RADIO_ON_LABEL_MATCH) {
            if ( newValue == _gfRadio ) return true;

            old = _gfRadio;
            _gfRadio = newValue;
        }
        _pcs.firePropertyChange(GF_RADIO_ON_LABEL_MATCH.propName, old, _gfRadio);

        return true;
    }

    /**
     * Getter method for {@link PropKey#WRITE_COMM_3}.
     *
     * @return whether the app should write output for commentator 3 even if there is non selected.
     */
    public synchronized static boolean WRITE_COMM_3() {
        return _writeComm3;
    }

    /**
     * Setter method for {@link PropKey#WRITE_COMM_3}.
     * No changes applied if the value is invalid.
     *
     * @param newValue
     * @return true if value was valid & was applied, false otherwise
     * @see #WRITE_COMM_3()
     */
    public static boolean WRITE_COMM_3(Boolean newValue) {
        if (!WRITE_COMM_3.validateParam(newValue)) return false;

        boolean old;
        synchronized (WRITE_COMM_3) {
            if (newValue == _writeComm3) return true;

            old = _writeComm3;
            _writeComm3 = newValue;
        }
        _pcs.firePropertyChange(WRITE_COMM_3.propName, old, _writeComm3);

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
    
    /**
     * Getter method for {@link PropKey#CHECK_NOTIFICATIONS}.
     *
     * @return the declared source directory for flag files.
     */
    public synchronized static boolean CHECK_NOTIFICATIONS() {
        return _checkNotifications;
    }

    /**
     * Setter method for {@link PropKey#CHECK_NOTIFICATIONS}.
     * No changes applied if the value is invalid.
     *
     * @param newValue
     * @return true if value was valid & was applied, false otherwise
     * @see #CHECK_NOTIFICATIONS()
     */
    public static boolean CHECK_NOTIFICATIONS(boolean newValue) {
        if ( !CHECK_NOTIFICATIONS.validateParam(newValue) ) return false;
        boolean old;

        synchronized (CHECK_NOTIFICATIONS) {
            if (newValue == _checkNotifications) return true;

            old = _checkNotifications;
            _checkNotifications = newValue;
        }
        _pcs.firePropertyChange(CHECK_NOTIFICATIONS.propName, old, _checkNotifications);

        return true;
    }

    /*==============================DAO FRIEND ACCESS METHODS==============================*/
    public static PropertyChangeSupport getInternalPCS(ConfigDAO accessor) {
        Objects.requireNonNull(accessor);
        return _pcs;
    }

    public static void setInternalChallongeAPI(ConfigDAO accessor, String val) {
        Objects.requireNonNull(accessor);
        _challongeAPI = val;
    }

    public static void setInternalAutocompleteOn(ConfigDAO accessor, boolean val) {
        Objects.requireNonNull(accessor);
        _autocompleteOn = val;
    }

    public static void setInternalGfRadio(ConfigDAO accessor, boolean val) {
        Objects.requireNonNull(accessor);
        _gfRadio = val;
    }

    public static void setInternalFlagExtension(ConfigDAO accessor, String val) {
        Objects.requireNonNull(accessor);
        _flagExtension = val;
    }

    public static void setInternalFlagDirectory(ConfigDAO accessor, Path val) {
        Objects.requireNonNull(accessor);
        _flagDirectory = val;
    }

    public static void setInternalWriteComm3(ConfigDAO accessor, boolean val) {
        Objects.requireNonNull(accessor);
        _writeComm3 = val;
    }

    public static void setInternalCheckNotifications(ConfigDAO accessor, boolean val) {
        Objects.requireNonNull(accessor);
        _checkNotifications = val;
    }
    /*==============================DAO FRIEND ACCESS METHODS==============================*/
}

