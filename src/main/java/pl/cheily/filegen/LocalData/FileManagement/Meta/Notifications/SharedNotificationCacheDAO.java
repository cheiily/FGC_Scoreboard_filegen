package pl.cheily.filegen.LocalData.FileManagement.Meta.Notifications;

import pl.cheily.filegen.LocalData.FileManagement.Meta.DAO;
import pl.cheily.filegen.Notifications.SharedNotificationCache;

public interface SharedNotificationCacheDAO extends DAO<String> {
    public void set(SharedNotificationCache cache);
    public SharedNotificationCache get();
}
