package pl.cheily.filegen.LocalData.FileManagement.Meta.NotificationCache;

import pl.cheily.filegen.LocalData.FileManagement.Meta.StringKVCachedDAO;
import pl.cheily.filegen.LocalData.ResourcePath;

public class NotificationCacheDAO extends StringKVCachedDAO {
    protected NotificationCacheDAO(ResourcePath path) {
        super(path);
    }

    @Override
    protected String sectionName() {
        return "NOTIFICATION CACHE";
    }
}
