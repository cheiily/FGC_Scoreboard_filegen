package pl.cheily.filegen.Utils;

public class SafeInvocationUtil {
    public static <T> T getOrNull(ThrowingSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            return null;
        }
    }
}
