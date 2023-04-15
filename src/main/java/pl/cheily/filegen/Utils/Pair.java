package pl.cheily.filegen.Utils;

public record Pair<T, U>(
        T first,
        U second
) {
    public T left() {
        return first;
    }
    public U right() {
        return second;
    }
}

