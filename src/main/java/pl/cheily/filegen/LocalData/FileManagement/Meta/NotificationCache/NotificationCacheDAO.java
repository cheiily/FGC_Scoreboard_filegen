package pl.cheily.filegen.LocalData.FileManagement.Meta.NotificationCache;

import pl.cheily.filegen.LocalData.FileManagement.Meta.StringKVCachedDAO;
import pl.cheily.filegen.LocalData.ResourcePath;
import pl.cheily.filegen.Notifications.NotificationCache;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationCacheDAO extends StringKVCachedDAO {
    public NotificationCacheDAO(ResourcePath path) {
        super(path);
    }

    @Override
    protected String sectionName() {
        return "NOTIFICATION CACHE";
    }

    public void set(NotificationCache cache) {
        if (cache == null) return;
        set("last_id", String.valueOf(cache.last_id));
        set("last_postTime", cache.last_postTime.format(DateTimeFormatter.ISO_DATE_TIME));
        set("checked_at", cache.checkedAt.format(DateTimeFormatter.ISO_DATE_TIME));
        store();
    }

    public NotificationCache get() {
        String lastId = get("last_id");
        String lastPostTime = get("last_postTime");
        String checkedAt = get("checked_at");

        NotificationCache ret = NotificationCache.empty();
        if (lastId == null || lastId.isEmpty()) {
            ret.last_id = 0; // Default to 0 if not set
        } else {
            try {
                ret.last_id = Integer.parseInt(lastId);
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse last_id from cache file: {}", lastId, e);
                ret.last_id = 0; // Handle parsing error
            }
        }
        if (lastPostTime == null || lastPostTime.isEmpty()) {
            ret.last_postTime = null;
        } else {
            try {
                ret.last_postTime = ZonedDateTime.parse(lastPostTime, DateTimeFormatter.ISO_DATE_TIME);
            } catch (Exception e) {
                logger.warn("Failed to parse last_postTime from cache file: {}", lastPostTime, e);
                ret.last_postTime = null; // Handle parsing error
            }
        }
        if (checkedAt == null || checkedAt.isEmpty()) {
            ret.checkedAt = ZonedDateTime.now().minusDays(1); // Default to yesterday if not set
        } else {
            try {
                ret.checkedAt = ZonedDateTime.parse(checkedAt, DateTimeFormatter.ISO_DATE_TIME);
            } catch (Exception e) {
                logger.warn("Failed to parse checked_at from cache file: {}", checkedAt, e);
                ret.checkedAt = ZonedDateTime.now().minusDays(1); // Handle parsing error
            }
        }
        return ret;
    }
}
