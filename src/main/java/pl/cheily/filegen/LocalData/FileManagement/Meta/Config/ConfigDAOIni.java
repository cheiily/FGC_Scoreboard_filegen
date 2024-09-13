package pl.cheily.filegen.LocalData.FileManagement.Meta.Config;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile;
import pl.cheily.filegen.Configuration.AppConfig;
import pl.cheily.filegen.Configuration.PropKey;
import pl.cheily.filegen.LocalData.DataManagerNotInitializedException;
import pl.cheily.filegen.LocalData.FileManagement.Meta.CachedIniDAOBase;
import pl.cheily.filegen.LocalData.ResourcePath;

import java.beans.PropertyChangeSupport;
import java.io.File;
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
import static pl.cheily.filegen.Configuration.PropKey.AUTOCOMPLETE_ON;
import static pl.cheily.filegen.Configuration.PropKey.CHALLONGE_API;
import static pl.cheily.filegen.Configuration.PropKey.FLAG_DIRECTORY;
import static pl.cheily.filegen.Configuration.PropKey.FLAG_EXTENSION;
import static pl.cheily.filegen.Configuration.PropKey.GF_RADIO_ON_LABEL_MATCH;
import static pl.cheily.filegen.Configuration.PropKey.MAKE_HTML_OUTPUT;
import static pl.cheily.filegen.Configuration.PropKey.MAKE_RAW_OUTPUT;
import static pl.cheily.filegen.Configuration.PropKey.PUT_FLAGS;

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
        if (cfg_sec == null) {
            logger.error("Invalid config.ini file - missing \"{}\" section", SECTION_NAME);
            return List.of();
        }

        List<String> vals = new ArrayList<>();
        vals.add(cfg_sec.get(CHALLONGE_API.propName, String.class));
        vals.add(cfg_sec.get(AUTOCOMPLETE_ON.propName, String.class));
        vals.add(cfg_sec.get(MAKE_RAW_OUTPUT.propName, String.class));
        vals.add(cfg_sec.get(MAKE_HTML_OUTPUT.propName, String.class));
        vals.add(cfg_sec.get(GF_RADIO_ON_LABEL_MATCH.propName, String.class));
        vals.add(cfg_sec.get(PUT_FLAGS.propName, String.class));
        vals.add(cfg_sec.get(FLAG_EXTENSION.propName, String.class));
        vals.add(cfg_sec.get(FLAG_DIRECTORY.propName, String.class));

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

        return cfg_sec.get(key);
    }

    @Override
    public String get(PropKey key) {
        return get(key.propName);
    }

    @Override
    public void set(String key, String value) {
        Profile.Section cfg_sec = cache.get(SECTION_NAME);
        if (cfg_sec == null) {
            logger.error("Invalid config.ini file - missing \"{}\" section", SECTION_NAME);
            return;
        }

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
        if (cfg_sec == null) {
            logger.error("Invalid config.ini file - missing \"{}\" section", SECTION_NAME);
            return;
        }

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
        if (cfg_sec == null) {
            logger.error("Invalid config.ini file - missing \"{}\" section", SECTION_NAME);
            return List.of();
        }

        return cfg_sec.keySet().stream().toList();
    }

    @Override
    public List<String> getVals() {
        if (cacheInvalid()) refresh();
        Profile.Section cfg_sec = cache.get(SECTION_NAME);
        if (cfg_sec == null) {
            logger.error("Invalid config.ini file - missing \"{}\" section", SECTION_NAME);
            return List.of();
        }

        return cfg_sec.values().stream().toList();
    }

    @Override
    public Map<String, String> getAllAsMap() {
        if (cacheInvalid()) refresh();
        Profile.Section cfg_sec = cache.get(SECTION_NAME);
        if (cfg_sec == null) {
            logger.error("Invalid config.ini file - missing \"{}\" section", SECTION_NAME);
            return Map.of();
        }

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
        if (cfg_sec == null) {
            logger.error("Invalid config.ini file - missing \"{}\" section", SECTION_NAME);
            return;
        }

        for (int i = 0; i < keys.size(); i++) {
            cfg_sec.put(keys.get(i), values.get(i));
        }
        store();
    }

    @Override
    public void deleteAll() {
        Profile.Section cfg_sec = cache.get(SECTION_NAME);
        if (cfg_sec == null) {
            logger.error("Invalid config.ini file - missing \"{}\" section", SECTION_NAME);
            return;
        }

        cfg_sec.clear();
        store();
    }

    @Override
    public void saveAll() {
        synchronized (AppConfig.class) {
            cache.put(SECTION_NAME, CHALLONGE_API.propName, CHALLONGE_API());
            cache.put(SECTION_NAME, AUTOCOMPLETE_ON.propName, AUTOCOMPLETE_ON());
            cache.put(SECTION_NAME, MAKE_RAW_OUTPUT.propName, MAKE_RAW_OUTPUT());
            cache.put(SECTION_NAME, MAKE_HTML_OUTPUT.propName, MAKE_HTML_OUTPUT());
            cache.put(SECTION_NAME, GF_RADIO_ON_LABEL_MATCH.propName, GF_RADIO_ON_LABEL_MATCH());
            cache.put(SECTION_NAME, PUT_FLAGS.propName, PUT_FLAGS());
            cache.put(SECTION_NAME, FLAG_EXTENSION.propName, FLAG_EXTENSION());
            cache.put(SECTION_NAME, FLAG_DIRECTORY.propName, FLAG_DIRECTORY());
        }
        store();
    }

    @Override
    public void loadAll() {
        if (cacheInvalid()) refresh();
        try {
            cache.load(this.path.toPath().toFile());
            PropertyChangeSupport _pcs = AppConfig.getInternalPCS(this);

            Profile.Section cfg_sec = cache.get(SECTION_NAME);
            if (cfg_sec == null) {
                logger.error("Invalid config.ini file - missing \"{}\" section", SECTION_NAME);
                return;
            }

            String newApi = cfg_sec.get(CHALLONGE_API.propName, String.class);
            Boolean newAutocomplete = cfg_sec.get(AUTOCOMPLETE_ON.propName, Boolean.class);
            Boolean newRawOut = cfg_sec.get(MAKE_RAW_OUTPUT.propName, Boolean.class);
            Boolean newHtmlOut = cfg_sec.get(MAKE_HTML_OUTPUT.propName, Boolean.class);
            Boolean newGFRadio = cfg_sec.get(GF_RADIO_ON_LABEL_MATCH.propName, Boolean.class);
            Boolean newPutFlags = cfg_sec.get(PUT_FLAGS.propName, Boolean.class);
            String newFlagExt = cfg_sec.get(FLAG_EXTENSION.propName, String.class);
            String strFlagPth = cfg_sec.get(FLAG_DIRECTORY.propName, String.class);

            boolean correct = CHALLONGE_API.validateParam(newApi)
                    && AUTOCOMPLETE_ON.validateParam(newAutocomplete)
                    && MAKE_RAW_OUTPUT.validateParam(newRawOut)
                    && MAKE_HTML_OUTPUT.validateParam(newHtmlOut)
                    && GF_RADIO_ON_LABEL_MATCH.validateParam(newGFRadio)
                    && PUT_FLAGS.validateParam(newPutFlags)
                    && FLAG_EXTENSION.validateParam(newFlagExt)
                    && FLAG_DIRECTORY.validateParam(strFlagPth);

            if (!correct) {
                logger.error("Invalid config.ini file - loaded data didn't pass validation.");
                return;
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

            synchronized (AppConfig.class) {
                oldApi = CHALLONGE_API();
                oldAutocomplete = AUTOCOMPLETE_ON();
                oldRawOut = MAKE_RAW_OUTPUT();
                oldHtmlOut = MAKE_HTML_OUTPUT();
                oldGFRadio = GF_RADIO_ON_LABEL_MATCH();
                oldPutFlags = PUT_FLAGS();
                oldFlagExt = FLAG_EXTENSION();
                oldFlagPth = FLAG_DIRECTORY();

                AppConfig.setInternalChallongeAPI(this, newApi);
                AppConfig.setInternalAutocompleteOn(this, newAutocomplete);
                AppConfig.setInternalMakeRawOutput(this, newRawOut);
                AppConfig.setInternalMakeHtmlOutput(this, newHtmlOut);
                AppConfig.setInternalGfRadio(this, newGFRadio);
                AppConfig.setInternalPutFlags(this, newPutFlags);
                AppConfig.setInternalFlagExtension(this, newFlagExt);
                AppConfig.setInternalFlagDirectory(this, newFlagPth);
            }

            _pcs.firePropertyChange(CHALLONGE_API.propName, oldApi, newApi);
            _pcs.firePropertyChange(AUTOCOMPLETE_ON.propName, oldAutocomplete, newAutocomplete);
            _pcs.firePropertyChange(MAKE_RAW_OUTPUT.propName, oldRawOut, newRawOut);
            _pcs.firePropertyChange(MAKE_HTML_OUTPUT.propName, oldHtmlOut, newHtmlOut);
            _pcs.firePropertyChange(GF_RADIO_ON_LABEL_MATCH.propName, oldGFRadio, newGFRadio);
            _pcs.firePropertyChange(PUT_FLAGS.propName, oldPutFlags, newPutFlags);
            _pcs.firePropertyChange(FLAG_EXTENSION.propName, oldFlagExt, newFlagExt);
            _pcs.firePropertyChange(FLAG_DIRECTORY.propName, oldFlagPth, newFlagPth);
        } catch (DataManagerNotInitializedException e) {
            logger.warn("Attempted config read while Data Manager isn't initialized.", e);
        } catch (InvalidFileFormatException e) {
            logger.error("Failed parsing of {} for config data.", this.path, e);
        } catch (IOException e) {
            logger.error("Failed read from {} for config data.", this.path, e);
        }
    }
}
