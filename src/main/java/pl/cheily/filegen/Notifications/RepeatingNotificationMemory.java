package pl.cheily.filegen.Notifications;

public class RepeatingNotificationMemory {
    public int notificationID = -1;
    public boolean interacted = false;
    public int timesShown = 0;

    public RepeatingNotificationMemory(int notificationID, boolean interacted, int timesShown) {
        this.notificationID = notificationID;
        this.interacted = interacted;
        this.timesShown = timesShown;
    }



    public static String PROP_INTERACTED = "interacted";
    public static String PROP_SHOWN = "num_shown";
}
