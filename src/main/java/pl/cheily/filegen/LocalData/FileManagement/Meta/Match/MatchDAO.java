package pl.cheily.filegen.LocalData.FileManagement.Meta.Match;

import pl.cheily.filegen.LocalData.FileManagement.Meta.DAO;

import java.util.List;
import java.util.Map;

public interface MatchDAO extends DAO<String> {
    public List<String> getKeys();
    public List<String> getVals();
    public Map<String, String> getAllAsMap();

    public void set(MatchDataKey key, String value);
    public String get(MatchDataKey key);
    public void delete(MatchDataKey key);
}
