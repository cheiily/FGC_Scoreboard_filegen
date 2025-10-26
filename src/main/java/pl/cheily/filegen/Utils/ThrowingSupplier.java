package pl.cheily.filegen.Utils;

@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get() throws Throwable;
}
