package pl.cheily.filegen.LocalData.FileManagement.Meta.Notifications;

import pl.cheily.filegen.LocalData.FileManagement.Meta.DAO;
import pl.cheily.filegen.Notifications.RepeatingNotificationMemory;

public interface RepeatingNotificationMemoryDAO extends DAO<RepeatingNotificationMemory> {
    public RepeatingNotificationMemory get(int id);
    public void set(RepeatingNotificationMemory value);
}
