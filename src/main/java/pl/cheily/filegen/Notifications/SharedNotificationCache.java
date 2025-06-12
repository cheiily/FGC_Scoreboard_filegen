package pl.cheily.filegen.Notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cheily.filegen.ScoreboardApplication;

import java.time.ZonedDateTime;

public class SharedNotificationCache {
    private final static Logger logger = LoggerFactory.getLogger(SharedNotificationCache.class);

    public int last_id;
    public ZonedDateTime last_postTime;
    public ZonedDateTime checkedAt;

    public SharedNotificationCache(int last_id, ZonedDateTime last_postTime, ZonedDateTime checkedAt) {
        this.last_id = last_id;
        this.last_postTime = last_postTime;
        this.checkedAt = checkedAt;
    }

    public static SharedNotificationCache empty() {
        return new SharedNotificationCache(0, null, ZonedDateTime.now().minusDays(1));
    }

    public static SharedNotificationCache load() {
        return ScoreboardApplication.dataManager.notificationCacheDAO.get();
    }

    public static SharedNotificationCache handledNow(NotificationData notification) {
        var ret = new SharedNotificationCache(notification.id, notification.postTime, ZonedDateTime.now());
        ScoreboardApplication.dataManager.notificationCacheDAO.set(ret);
        return ret;
    }
}
