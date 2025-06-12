package pl.cheily.filegen.Notifications;

import pl.cheily.filegen.ScoreboardApplication;

public class RepeatingNotificationMemory {
    public int notificationID = -1;
    public int version = 1;
    public boolean interacted = false;
    public int timesShown = 0;

    public RepeatingNotificationMemory(int notificationID, int version, boolean interacted, int timesShown) {
        this.notificationID = notificationID;
        this.version = version;
        this.interacted = interacted;
        this.timesShown = timesShown;
    }

    public RepeatingNotificationMemory(int notificationID, int version) {
        this(notificationID, version, false, 0);
    }

    public static final String PROP_VERSION = "version";
    public static final String PROP_INTERACTED = "interacted";
    public static final String PROP_TIMES_SHOWN = "num_shown";

    public static void incrementShowCount(NotificationData notification) {
        var memo = ScoreboardApplication.dataManager.repeatingNotificationMemoryDAO.get(notification.id);
        if (memo == null) memo = new RepeatingNotificationMemory(notification.id, notification.version);
        ScoreboardApplication.dataManager.repeatingNotificationMemoryDAO.set(new RepeatingNotificationMemory(
                memo.notificationID,
                memo.version,
                memo.interacted,
                memo.timesShown + 1
        ));
    }

    public static void setInteracted(NotificationData notification) {
        var memo = ScoreboardApplication.dataManager.repeatingNotificationMemoryDAO.get(notification.id);
        if (memo == null) memo = new RepeatingNotificationMemory(notification.id, notification.version);
        ScoreboardApplication.dataManager.repeatingNotificationMemoryDAO.set(new RepeatingNotificationMemory(
                memo.notificationID,
                memo.version,
                true,
                memo.timesShown
        ));
    }
}
