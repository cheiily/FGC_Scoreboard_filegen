package pl.cheily.filegen.Utils;

import org.slf4j.Logger;

public class SafeInvocationUtil {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(SafeInvocationUtil.class);

    public static <T> T getOrNull(ThrowingSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
