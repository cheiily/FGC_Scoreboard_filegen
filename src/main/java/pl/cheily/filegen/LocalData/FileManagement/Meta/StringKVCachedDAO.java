package pl.cheily.filegen.LocalData.FileManagement.Meta;

import org.ini4j.Profile;
import pl.cheily.filegen.LocalData.ResourcePath;

import java.util.List;
import java.util.function.Predicate;

public abstract class StringKVCachedDAO extends CachedIniDAOBase implements DAO<String> {
    protected abstract String sectionName();

    protected StringKVCachedDAO(ResourcePath path) {
        super(path);
        if (cache.get(sectionName()) == null)
            cache.add(sectionName());
    }


    @Override
    public List<String> getAll() {
        if (cacheInvalid()) refresh();
        Profile.Section section = cache.get(sectionName());

        return section.values().stream().toList();
    }

    @Override
    public String get(String key) {
        if (cacheInvalid()) refresh();
        return cache.get(sectionName()).get(key);
    }

    @Override
    public List<String> find(String key, Predicate<String> filter) {
        return getAll().stream().filter(filter).toList();
    }

    @Override
    public void set(String key, String value) {
        Profile.Section section = cache.get(sectionName());
        section.put(key, value);
        store();
    }

    @Override
    public void setAll(List<String> keys, List<String> values) {
        if (keys.size() != values.size()) {
            logger.warn("StringKVCachedDAO setting all values with different key and value set sizes.");
        }

        for (int i = 0; i < keys.size(); i++) {
            String val = i >= values.size() ? "" : values.get(i);
            set(keys.get(i), val);
        }
        store();
    }

    @Override
    public void delete(String key) {
        Profile.Section section = cache.get(sectionName());
        section.remove(key);
        store();
    }

    @Override
    public void deleteAll() {
        Profile.Section section = cache.get(sectionName());
        section.clear();
        store();
    }
}
