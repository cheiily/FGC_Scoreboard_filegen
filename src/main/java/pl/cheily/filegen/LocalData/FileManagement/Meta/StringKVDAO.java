package pl.cheily.filegen.LocalData.FileManagement.Meta;

import java.util.List;
import java.util.Map;

public interface StringKVDAO extends DAO<String> {
    public List<String> getKeys();
    public List<String> getVals();
    public Map<String, String> getAllAsMap();
}
