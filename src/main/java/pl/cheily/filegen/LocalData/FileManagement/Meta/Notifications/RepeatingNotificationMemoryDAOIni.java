package pl.cheily.filegen.LocalData.FileManagement.Meta.Notifications;

import org.ini4j.Profile;
import pl.cheily.filegen.LocalData.FileManagement.Meta.CachedIniDAOBase;
import pl.cheily.filegen.LocalData.ResourcePath;
import pl.cheily.filegen.Notifications.RepeatingNotificationMemory;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class RepeatingNotificationMemoryDAOIni extends CachedIniDAOBase implements RepeatingNotificationMemoryDAO {
    public RepeatingNotificationMemoryDAOIni(ResourcePath path) {
        super(path);
    }

    @Override
    public List<RepeatingNotificationMemory> getAll() {
        if (cacheInvalid()) refresh();

        return cache.keySet().stream()
                .map(this::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public RepeatingNotificationMemory get(String key) {
        if (cacheInvalid()) refresh();

        Profile.Section section = cache.get(key);
        if (section == null) return null;
        try {
            return new RepeatingNotificationMemory(
                    Integer.parseInt(section.getName()),
                    Integer.parseInt(section.get(RepeatingNotificationMemory.PROP_VERSION)),
                    Boolean.parseBoolean(section.get(RepeatingNotificationMemory.PROP_INTERACTED)),
                    Integer.parseInt(section.get(RepeatingNotificationMemory.PROP_TIMES_SHOWN))
            );
        } catch (NumberFormatException e) {
            logger.error("Error parsing RepeatingNotificationMemory for key: {}", key, e);
            return null;
        }
    }

    @Override
    public List<RepeatingNotificationMemory> find(String key, Predicate<RepeatingNotificationMemory> filter) {
        return getAll().stream().filter(filter).toList();
    }

    @Override
    public void set(String key, RepeatingNotificationMemory value) {
        set(key, value, true);
    }

    private void set(String key, RepeatingNotificationMemory value, boolean store) {
        cache.put(key, RepeatingNotificationMemory.PROP_VERSION, String.valueOf(value.version));
        cache.put(key, RepeatingNotificationMemory.PROP_TIMES_SHOWN, String.valueOf(value.timesShown));
        cache.put(key, RepeatingNotificationMemory.PROP_INTERACTED, String.valueOf(value.interacted));
        if (store) store();
    }

    @Override
    public void setAll(List<String> keys, List<RepeatingNotificationMemory> values) {
        if (keys.size() != values.size()) {
            logger.warn("RepeatingNotificationMemoryDAOIni setting all values with different key and value set sizes.");
        }
        for (int i = 0; i < keys.size(); i++) {
            RepeatingNotificationMemory val = i >= values.size() ? null : values.get(i);
            set(keys.get(i), val, false);
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

    @Override
    public RepeatingNotificationMemory get(int id) {
        return get(String.valueOf(id));
    }

    @Override
    public void set(RepeatingNotificationMemory value) {
        set(String.valueOf(value.notificationID), value, true);
    }
}
