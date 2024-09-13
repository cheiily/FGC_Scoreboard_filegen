package pl.cheily.filegen.LocalData.FileManagement.Meta.Config;

import pl.cheily.filegen.Configuration.PropKey;
import pl.cheily.filegen.LocalData.FileManagement.Meta.DAO;

import java.util.List;
import java.util.Map;

public interface ConfigDAO extends DAO<String> {
    public List<String> getKeys();
    public List<String> getVals();
    public Map<String, String> getAllAsMap();
    public void saveAll();
    public void loadAll();

    public void set(PropKey key, String value);
    public String get(PropKey key);
    public void delete(PropKey key);
}
