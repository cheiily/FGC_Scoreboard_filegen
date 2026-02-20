package pl.cheily.filegen.LocalData.FileManagement.Meta.Config;

import org.ini4j.Config;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile;
import org.slf4j.MarkerFactory;
import pl.cheily.filegen.Configuration.AppConfig;
import pl.cheily.filegen.Configuration.PropKey;
import pl.cheily.filegen.LocalData.DataManagerNotInitializedException;
import pl.cheily.filegen.LocalData.FileManagement.Meta.CachedIniDAOBase;
import pl.cheily.filegen.LocalData.LocalResourcePath;

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static pl.cheily.filegen.Configuration.AppConfig.AUTOCOMPLETE_ON;
import static pl.cheily.filegen.Configuration.AppConfig.CHALLONGE_API;
import static pl.cheily.filegen.Configuration.AppConfig.CHECK_NOTIFICATIONS;
import static pl.cheily.filegen.Configuration.AppConfig.GF_RADIO_ON_LABEL_MATCH;
import static pl.cheily.filegen.Configuration.AppConfig.WRITE_COMM_3;
import static pl.cheily.filegen.Configuration.PropKey.*;

public final class ConfigDAOIni extends CachedIniDAOBase implements ConfigDAO {
    private static final String SECTION_NAME = "SETTINGS";

    private static Config getConfig() {
        Config config = new Config();
        config.setMultiOption(false);
        config.setMultiSection(false);
        return config;
    }

    public ConfigDAOIni(LocalResourcePath path) {
        super(path, getConfig());
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
        vals.add(cfg_sec.getOrDefault(GF_RADIO_ON_LABEL_MATCH.propName, ""));
        vals.add(cfg_sec.getOrDefault(WRITE_COMM_3.propName, ""));
        vals.add(cfg_sec.getOrDefault(CHECK_NOTIFICATIONS.propName, ""));

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
            cache.clear();
            cache.put(SECTION_NAME, CHALLONGE_API.propName, CHALLONGE_API());
            cache.put(SECTION_NAME, AUTOCOMPLETE_ON.propName, AUTOCOMPLETE_ON());
            cache.put(SECTION_NAME, GF_RADIO_ON_LABEL_MATCH.propName, GF_RADIO_ON_LABEL_MATCH());
            cache.put(SECTION_NAME, WRITE_COMM_3.propName, WRITE_COMM_3());
            cache.put(SECTION_NAME, CHECK_NOTIFICATIONS.propName, CHECK_NOTIFICATIONS());
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
            Boolean newGFRadio = cfg_sec.get(GF_RADIO_ON_LABEL_MATCH.propName, Boolean.class);
            Boolean newWriteComm3 = cfg_sec.get(WRITE_COMM_3.propName, Boolean.class);
            Boolean newCheckNotifications = cfg_sec.get(CHECK_NOTIFICATIONS.propName, Boolean.class);

            boolean correct = CHALLONGE_API.validateParam(newApi)
                    && AUTOCOMPLETE_ON.validateParam(newAutocomplete)
                    && GF_RADIO_ON_LABEL_MATCH.validateParam(newGFRadio)
                    && WRITE_COMM_3.validateParam(newWriteComm3)
                    && CHECK_NOTIFICATIONS.validateParam(newCheckNotifications);

            if (!correct) {
                logger.error("Invalid config.ini file - loaded data didn't pass validation.");
                return false;
            }

            String oldApi;
            Boolean oldAutocomplete;
            Boolean oldGFRadio;
            Boolean oldWriteComm3;
            Boolean oldCheckNotifications;

            synchronized (AppConfig.class) {
                oldApi = CHALLONGE_API();
                oldAutocomplete = AUTOCOMPLETE_ON();
                oldGFRadio = GF_RADIO_ON_LABEL_MATCH();
                oldWriteComm3 = WRITE_COMM_3();
                oldCheckNotifications = CHECK_NOTIFICATIONS();

                AppConfig.setInternalChallongeAPI(this, newApi);
                AppConfig.setInternalAutocompleteOn(this, newAutocomplete);
                AppConfig.setInternalGfRadio(this, newGFRadio);
                AppConfig.setInternalWriteComm3(this, newWriteComm3);
                AppConfig.setInternalCheckNotifications(this, newCheckNotifications);
            }

            _pcs.firePropertyChange(CHALLONGE_API.propName, oldApi, newApi);
            _pcs.firePropertyChange(AUTOCOMPLETE_ON.propName, oldAutocomplete, newAutocomplete);
            _pcs.firePropertyChange(GF_RADIO_ON_LABEL_MATCH.propName, oldGFRadio, newGFRadio);
            _pcs.firePropertyChange(WRITE_COMM_3.propName, oldWriteComm3, newWriteComm3);
            _pcs.firePropertyChange(CHECK_NOTIFICATIONS.propName, oldCheckNotifications, newCheckNotifications);
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
