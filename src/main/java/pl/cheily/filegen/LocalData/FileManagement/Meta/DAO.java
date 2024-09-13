package pl.cheily.filegen.LocalData.FileManagement.Meta;

import java.util.List;
import java.util.function.Predicate;

public interface DAO<T> {

    public List<T> getAll();
    public T get(String key);
    public List<T> find(String key, Predicate<T> filter);

    public void set(String key, T value);
    public void setAll(List<String> keys, List<T> values);

    public void delete(String key);
    public void deleteAll();
}
