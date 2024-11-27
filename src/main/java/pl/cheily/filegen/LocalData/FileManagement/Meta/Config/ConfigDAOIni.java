package pl.cheily.filegen.LocalData.FileManagement.Meta.Config;

import javafx.scene.control.Alert;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import pl.cheily.filegen.Configuration.AppConfig;
import pl.cheily.filegen.Configuration.PropKey;
import pl.cheily.filegen.LocalData.DataManagerNotInitializedException;
import pl.cheily.filegen.LocalData.FileManagement.Meta.CachedIniDAOBase;
import pl.cheily.filegen.LocalData.ResourcePath;

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static pl.cheily.filegen.Configuration.AppConfig.AUTOCOMPLETE_ON;
import static pl.cheily.filegen.Configuration.AppConfig.CHALLONGE_API;
import static pl.cheily.filegen.Configuration.AppConfig.FLAG_DIRECTORY;
import static pl.cheily.filegen.Configuration.AppConfig.FLAG_EXTENSION;
import static pl.cheily.filegen.Configuration.AppConfig.GF_RADIO_ON_LABEL_MATCH;
import static pl.cheily.filegen.Configuration.AppConfig.MAKE_HTML_OUTPUT;
import static pl.cheily.filegen.Configuration.AppConfig.MAKE_RAW_OUTPUT;
import static pl.cheily.filegen.Configuration.AppConfig.PUT_FLAGS;
import static pl.cheily.filegen.Configuration.AppConfig.WRITE_COMM_3;
import static pl.cheily.filegen.Configuration.PropKey.*;

public final class ConfigDAOIni extends CachedIniDAOBase implements ConfigDAO {
    private static final String SECTION_NAME = "SETTINGS";

    public ConfigDAOIni(ResourcePath path) {
        super(path);
        if (cache.get(SECTION_NAME) == null)
            cache.add(SECTION_NAME);
    }

    @Override
    public List<String> getAll() {
        if (cacheInvalid()) refresh();
        Profile.Section cfg_sec = cache.get(SECTION_NAME);
        if (verifyAndLogOnError(cfg_sec)) return List.of();

        List<String> vals = new ArrayList<>();
        vals.add(cfg_sec.getOrDefault(CHALLONGE_API.propName, ""));
        vals.add(cfg_sec.getOrDefault(AUTOCOMPLETE_ON.propName, ""));
        vals.add(cfg_sec.getOrDefault(MAKE_RAW_OUTPUT.propName, ""));
        vals.add(cfg_sec.getOrDefault(MAKE_HTML_OUTPUT.propName, ""));
        vals.add(cfg_sec.getOrDefault(GF_RADIO_ON_LABEL_MATCH.propName, ""));
        vals.add(cfg_sec.getOrDefault(PUT_FLAGS.propName, ""));
        vals.add(cfg_sec.getOrDefault(FLAG_EXTENSION.propName, ""));
        vals.add(cfg_sec.getOrDefault(FLAG_DIRECTORY.propName, ""));
        vals.add(cfg_sec.getOrDefault(WRITE_COMM_3.propName, ""));

        return vals;
    }

    @Override
    public String get(String key) {
        if (cacheInvalid()) refresh();
        Profile.Section cfg_sec = cache.get(SECTION_NAME);
        if (cfg_sec == null) {
            logger.error("Invalid config.ini file - missing \"{}\" section", SECTION_NAME);
            return "";
        }

        return cfg_sec.getOrDefault(key, "");
    }

    @Override
    public String get(PropKey key) {
        return get(key.propName);
    }

    @Override
    public void set(String key, String value) {
        Profile.Section cfg_sec = cache.get(SECTION_NAME);
        if (verifyAndLogOnError(cfg_sec)) return;

        cfg_sec.put(key, value);
        store();
    }

    @Override
    public void set(PropKey key, String value) {
        set(key.propName, value);
    }

    @Override
    public void delete(String key) {
        Profile.Section cfg_sec = cache.get(SECTION_NAME);
        if (verifyAndLogOnError(cfg_sec)) return;

        cfg_sec.remove(key);
        store();
    }

    @Override
    public void delete(PropKey key) {
        delete(key.propName);
    }

    @Override
    public List<String> getKeys() {
        if (cacheInvalid()) refresh();
        Profile.Section cfg_sec = cache.get(SECTION_NAME);
        if (verifyAndLogOnError(cfg_sec)) return List.of();

        return cfg_sec.keySet().stream().toList();
    }

    @Override
    public List<String> getVals() {
        if (cacheInvalid()) refresh();
        Profile.Section cfg_sec = cache.get(SECTION_NAME);
        if (verifyAndLogOnError(cfg_sec)) return List.of();

        return cfg_sec.values().stream().toList();
    }

    @Override
    public Map<String, String> getAllAsMap() {
        if (cacheInvalid()) refresh();
        Profile.Section cfg_sec = cache.get(SECTION_NAME);
        if (verifyAndLogOnError(cfg_sec)) return Map.of();

        return Map.copyOf(cfg_sec);
    }

    @Override
    public List<String> find(String key, Predicate<String> filter) {
        return getAll().stream().filter(filter).toList();
    }

    @Override
    public void setAll(List<String> keys, List<String> values) {
        if (keys.size() != values.size()) {
            logger.warn("ConfigDAO tried to setAll from list of mismatched sizes.");
            return;
        }

        Profile.Section cfg_sec = cache.get(SECTION_NAME);
        if (verifyAndLogOnError(cfg_sec)) return;

        for (int i = 0; i < keys.size(); i++) {
            cfg_sec.put(keys.get(i), values.get(i));
        }
        store();
    }

    @Override
    public void deleteAll() {
        Profile.Section cfg_sec = cache.get(SECTION_NAME);
        if (verifyAndLogOnError(cfg_sec)) return;

        cfg_sec.clear();
        store();
    }

    @Override
    public boolean saveAll() {
        synchronized (AppConfig.class) {
            cache.put(SECTION_NAME, CHALLONGE_API.propName, CHALLONGE_API());
            cache.put(SECTION_NAME, AUTOCOMPLETE_ON.propName, AUTOCOMPLETE_ON());
            cache.put(SECTION_NAME, MAKE_RAW_OUTPUT.propName, MAKE_RAW_OUTPUT());
            cache.put(SECTION_NAME, MAKE_HTML_OUTPUT.propName, MAKE_HTML_OUTPUT());
            cache.put(SECTION_NAME, GF_RADIO_ON_LABEL_MATCH.propName, GF_RADIO_ON_LABEL_MATCH());
            cache.put(SECTION_NAME, PUT_FLAGS.propName, PUT_FLAGS());
            cache.put(SECTION_NAME, FLAG_EXTENSION.propName, FLAG_EXTENSION());
            cache.put(SECTION_NAME, FLAG_DIRECTORY.propName, FLAG_DIRECTORY());
            cache.put(SECTION_NAME, WRITE_COMM_3.propName, WRITE_COMM_3());
        }
        return store();
    }

    @Override
    public boolean loadAll() {
        if (cacheInvalid()) refresh();
        try {
            cache.load(this.path.toPath().toFile());
            PropertyChangeSupport _pcs = AppConfig.getInternalPCS(this);

            Profile.Section cfg_sec = cache.get(SECTION_NAME);
            if (verifyAndLogOnError(cfg_sec)) return false;

            // purposefully don't return defaults to flow into the catch clause if there are invalid values saved
            String newApi = cfg_sec.get(CHALLONGE_API.propName, String.class);
            Boolean newAutocomplete = cfg_sec.get(AUTOCOMPLETE_ON.propName, Boolean.class);
            Boolean newRawOut = cfg_sec.get(MAKE_RAW_OUTPUT.propName, Boolean.class);
            Boolean newHtmlOut = cfg_sec.get(MAKE_HTML_OUTPUT.propName, Boolean.class);
            Boolean newGFRadio = cfg_sec.get(GF_RADIO_ON_LABEL_MATCH.propName, Boolean.class);
            Boolean newPutFlags = cfg_sec.get(PUT_FLAGS.propName, Boolean.class);
            String newFlagExt = cfg_sec.get(FLAG_EXTENSION.propName, String.class);
            String strFlagPth = cfg_sec.get(FLAG_DIRECTORY.propName, String.class);
            Boolean newWriteComm3 = cfg_sec.get(WRITE_COMM_3.propName, Boolean.class);

            boolean correct = CHALLONGE_API.validateParam(newApi)
                    && AUTOCOMPLETE_ON.validateParam(newAutocomplete)
                    && MAKE_RAW_OUTPUT.validateParam(newRawOut)
                    && MAKE_HTML_OUTPUT.validateParam(newHtmlOut)
                    && GF_RADIO_ON_LABEL_MATCH.validateParam(newGFRadio)
                    && PUT_FLAGS.validateParam(newPutFlags)
                    && FLAG_EXTENSION.validateParam(newFlagExt)
                    && FLAG_DIRECTORY.validateParam(strFlagPth)
                    && WRITE_COMM_3.validateParam(newWriteComm3);

            if (!correct) {
                logger.error("Invalid config.ini file - loaded data didn't pass validation.");
                return false;
            }

            Path newFlagPth = Path.of(strFlagPth);

            String oldApi;
            Boolean oldAutocomplete;
            Boolean oldRawOut;
            Boolean oldHtmlOut;
            Boolean oldGFRadio;
            Boolean oldPutFlags;
            String oldFlagExt;
            Path oldFlagPth;
            Boolean oldWriteComm3;

            synchronized (AppConfig.class) {
                oldApi = CHALLONGE_API();
                oldAutocomplete = AUTOCOMPLETE_ON();
                oldRawOut = MAKE_RAW_OUTPUT();
                oldHtmlOut = MAKE_HTML_OUTPUT();
                oldGFRadio = GF_RADIO_ON_LABEL_MATCH();
                oldPutFlags = PUT_FLAGS();
                oldFlagExt = FLAG_EXTENSION();
                oldFlagPth = FLAG_DIRECTORY();
                oldWriteComm3 = WRITE_COMM_3();

                AppConfig.setInternalChallongeAPI(this, newApi);
                AppConfig.setInternalAutocompleteOn(this, newAutocomplete);
                AppConfig.setInternalMakeRawOutput(this, newRawOut);
                AppConfig.setInternalMakeHtmlOutput(this, newHtmlOut);
                AppConfig.setInternalGfRadio(this, newGFRadio);
                AppConfig.setInternalPutFlags(this, newPutFlags);
                AppConfig.setInternalFlagExtension(this, newFlagExt);
                AppConfig.setInternalFlagDirectory(this, newFlagPth);
                AppConfig.setInternalWriteComm3(this, newWriteComm3);
            }

            _pcs.firePropertyChange(CHALLONGE_API.propName, oldApi, newApi);
            _pcs.firePropertyChange(AUTOCOMPLETE_ON.propName, oldAutocomplete, newAutocomplete);
            _pcs.firePropertyChange(MAKE_RAW_OUTPUT.propName, oldRawOut, newRawOut);
            _pcs.firePropertyChange(MAKE_HTML_OUTPUT.propName, oldHtmlOut, newHtmlOut);
            _pcs.firePropertyChange(GF_RADIO_ON_LABEL_MATCH.propName, oldGFRadio, newGFRadio);
            _pcs.firePropertyChange(PUT_FLAGS.propName, oldPutFlags, newPutFlags);
            _pcs.firePropertyChange(FLAG_EXTENSION.propName, oldFlagExt, newFlagExt);
            _pcs.firePropertyChange(FLAG_DIRECTORY.propName, oldFlagPth, newFlagPth);
            _pcs.firePropertyChange(WRITE_COMM_3.propName, oldWriteComm3, newWriteComm3);
        } catch (DataManagerNotInitializedException e) {
            logger.warn("Attempted config read while Data Manager isn't initialized.", e);
            return false;
        } catch (InvalidFileFormatException e) {
            logger.error(MarkerFactory.getMarker("ALERT"), "Failed parsing of {} for config data.", this.path, e);
            return false;
        } catch (IOException e) {
            logger.error(MarkerFactory.getMarker("ALERT"), "Failed read from {} for config data.", this.path, e);
            return false;
        }
        return true;
    }

    private boolean verifyAndLogOnError(Profile.Section cfg_sec) {
        if (cfg_sec == null) {
            logger.error(MarkerFactory.getMarker("ALERT"), "Invalid config storage file ({}) - missing \"{}\" section", path, SECTION_NAME);
            return true;
        }
        return false;
    }
}
