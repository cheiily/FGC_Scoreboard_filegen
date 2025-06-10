package pl.cheily.filegen.Notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZonedDateTime;

public class NotificationCache {
    private final static Logger logger = LoggerFactory.getLogger(NotificationCache.class);

    public int last_id;
    public ZonedDateTime last_postTime;
    public ZonedDateTime checkedAt;

    public NotificationCache(int last_id, ZonedDateTime last_postTime, ZonedDateTime checkedAt) {
        this.last_id = last_id;
        this.last_postTime = last_postTime;
        this.checkedAt = checkedAt;
    }

    public NotificationCache() {
        this.last_id = 0;
        this.last_postTime = null;
        this.checkedAt = ZonedDateTime.now().minusDays(1);
    }

    public static NotificationCache handledNow(NotificationData notification) {
        var ret = new NotificationCache(notification.id, notification.postTime, ZonedDateTime.now());
        logger.warn("TODO : Implement saving of notification cache to file.");
        return ret;
    }
}
