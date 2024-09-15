package pl.cheily.filegen.LocalData.FileManagement.Meta.Match;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import pl.cheily.filegen.LocalData.DataManagerNotInitializedException;
import pl.cheily.filegen.LocalData.FileManagement.Meta.CachedIniDAOBase;
import pl.cheily.filegen.LocalData.ResourcePath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class MatchDAOIni extends CachedIniDAOBase implements MatchDAO {
    private static final String SECTION_NAME = "MATCH DATA";

    public MatchDAOIni(ResourcePath path) {
        super(path);
        if (cache.get(SECTION_NAME) == null)
            cache.add(SECTION_NAME);
    }

    @Override
    public List<String> getAll() {
        if (cacheInvalid()) refresh();
        Profile.Section mtch_sec = cache.get(SECTION_NAME);
        if (verifyAndLogOnError(mtch_sec)) return List.of();

        List<String> vals = new ArrayList<>();
        for (MatchDataKey key : MatchDataKey.values()) {
            vals.add(mtch_sec.get(key.toString(), String.class));
        }

        return vals;
    }

    @Override
    public String get(String key) {
        if (cacheInvalid()) refresh();
        Profile.Section mtch_sec = cache.get(SECTION_NAME);
        if (verifyAndLogOnError(mtch_sec)) return "";

        return mtch_sec.get(key, String.class);
    }

    @Override
    public void set(String key, String value) {
        Profile.Section mtch_sec = cache.get(SECTION_NAME);
        if (verifyAndLogOnError(mtch_sec)) return;

        mtch_sec.put(key, value);
        store();
    }

    @Override
    public void delete(String key) {
        Profile.Section mtch_sec = cache.get(SECTION_NAME);
        if (verifyAndLogOnError(mtch_sec)) return;

        mtch_sec.remove(key);
        store();
    }

    private boolean verifyAndLogOnError(Profile.Section mtch_sec) {
        if (mtch_sec == null) {
            logger.error(MarkerFactory.getMarker("ALERT"), "Invalid match data storage file ({}) - missing \"{}\" section", path, SECTION_NAME);
            return true;
        }
        return false;
    }

    @Override
    public List<String> getKeys() {
        if (cacheInvalid()) refresh();
        Profile.Section mtch_sec = cache.get(SECTION_NAME);
    if (verifyAndLogOnError(mtch_sec)) return List.of();

        return mtch_sec.keySet().stream().toList();
    }

    @Override
    public List<String> getVals() {
        if (cacheInvalid()) refresh();
        Profile.Section mtch_sec = cache.get(SECTION_NAME);
    if (verifyAndLogOnError(mtch_sec)) return List.of();

        return mtch_sec.values().stream().toList();
    }

    @Override
    public Map<String, String> getAllAsMap() {
        if (cacheInvalid()) refresh();
        Profile.Section mtch_sec = cache.get(SECTION_NAME);
        if (verifyAndLogOnError(mtch_sec)) return Map.of();

        return Map.copyOf(mtch_sec);
    }

    @Override
    public List<String> find(String key, Predicate<String> filter) {
        return getAll().stream().filter(filter).toList();
    }

    @Override
    public void setAll(List<String> keys, List<String> values) {
        if (keys.size() != values.size()) {
            logger.warn("MatchDAO tried to setAll from list of mismatched sizes.");
            return;
        }

        Profile.Section mtch_sec = cache.get(SECTION_NAME);
        if (verifyAndLogOnError(mtch_sec)) return;

        for (int i = 0; i < keys.size(); i++) {
            mtch_sec.put(keys.get(i), values.get(i));
        }
        store();
    }

    @Override
    public void deleteAll() {
        Profile.Section mtch_sec = cache.get(SECTION_NAME);
        if (verifyAndLogOnError(mtch_sec)) return;

        mtch_sec.clear();
        store();
    }

    @Override
    public void set(MatchDataKey key, String value) {
        set(key.toString(), value);
    }

    @Override
    public String get(MatchDataKey key) {
        return get(key.toString());
    }

    @Override
    public void delete(MatchDataKey key) {
        delete(key.toString());
    }
}
