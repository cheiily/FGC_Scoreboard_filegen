package pl.cheily.filegen.LocalData.FileManagement.Meta.RoundSet;

import org.ini4j.Config;
import pl.cheily.filegen.LocalData.FileManagement.Meta.CachedIniDAOBase;
import pl.cheily.filegen.LocalData.ResourcePath;

import java.util.List;
import java.util.function.Predicate;

public class RoundLabelDAOIni extends CachedIniDAOBase implements RoundLabelDAO {
    private static Config getConfig() {
        Config config = new Config();
        config.setEmptyOption(true);
        return config;
    }

    public RoundLabelDAOIni(ResourcePath path) {
        super(path, getConfig());
    }

    @Override
    public List<String> getAll() {
        if (cacheInvalid()) refresh();
        return cache.keySet().stream().toList();
    }

    @Override
    public String get(String key) {
        return key;
    }

    @Override
    public List<String> find(String key, Predicate<String> filter) {
        if (cacheInvalid()) refresh();
        return cache.keySet().stream().filter(filter).toList();
    }

    @Override
    public void set(String key, String value) {
        cache.add(key);
        store();
    }

    @Override
    public void setAll(List<String> keys, List<String> values) {
        for (String key : keys) {
            cache.add(key);
        }
        store();
    }

    @Override
    public void delete(String key) {
        cache.remove(key);
        store();
    }

    @Override
    public void deleteAll() {
        cache.clear();
        store();
    }
}
