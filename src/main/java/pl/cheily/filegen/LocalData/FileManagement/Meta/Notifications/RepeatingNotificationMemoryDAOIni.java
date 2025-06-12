package pl.cheily.filegen.LocalData.FileManagement.Meta.Notifications;

import pl.cheily.filegen.LocalData.FileManagement.Meta.CachedIniDAOBase;
import pl.cheily.filegen.LocalData.ResourcePath;
import pl.cheily.filegen.Notifications.RepeatingNotificationMemory;

import java.util.List;
import java.util.function.Predicate;

public class RepeatingNotificationMemoryDAOIni extends CachedIniDAOBase implements RepeatingNotificationMemoryDAO {
    protected RepeatingNotificationMemoryDAOIni(ResourcePath path) {
        super(path);
    }

    @Override
    public List<RepeatingNotificationMemory> getAll() {
        return List.of();
    }

    @Override
    public RepeatingNotificationMemory get(String key) {
        return null;
    }

    @Override
    public List<RepeatingNotificationMemory> find(String key, Predicate<RepeatingNotificationMemory> filter) {
        return List.of();
    }

    @Override
    public void set(String key, RepeatingNotificationMemory value) {

    }

    @Override
    public void setAll(List<String> keys, List<RepeatingNotificationMemory> values) {

    }

    @Override
    public void delete(String key) {

    }

    @Override
    public void deleteAll() {

    }
}
