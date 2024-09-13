package pl.cheily.filegen.LocalData.FileManagement.Meta.RoundSet;

import org.ini4j.Config;
import pl.cheily.filegen.LocalData.FileManagement.Meta.CachedIniDAOBase;
import pl.cheily.filegen.LocalData.ResourcePath;
import pl.cheily.filegen.Utils.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class RoundLabelDAOIni extends CachedIniDAOBase implements RoundLabelDAO {
    private final static List<String> _DEFAULT_ROUNDS = List.of(
            //w rounds
            "Winners' R1", "Winners' R2", "Winners' R3", "Winners' R4",
            //l rounds
            "Losers' R1", "Losers' R2", "Losers' R3", "Losers' R4",
            //w top 8
            "Winners' Semis", "Winners' Finals",
            //l top 8
            "Losers' Eights", "Losers' Quarters", "Losers' Semis", "Losers' Finals",
            //gf
            "Grand Finals",
            //Extra
            "Top 8", "Winners' top 8", "Losers' top 8", "Losers' top 6", "Losers' top 4",
            "Winners' Eights", "Winners' Quarters", "Pools"
    );

    private static Config getConfig() {
        Config config = new Config();
        config.setEmptyOption(true);
        return config;
    }

    public RoundLabelDAOIni(ResourcePath path) {
        super(path, getConfig());
        if (cache.isEmpty()) {
            setAll(_DEFAULT_ROUNDS);
        }
    }

    @Override
    public List<String> getAll() {
        if (cacheInvalid()) refresh();
        return cache.keySet().stream().toList();
    }

    @Override
    public List<String> getAllSorted(Comparator<String> sorter) {
        if (cacheInvalid()) refresh();
        List<String> sorted = new ArrayList<>(cache.keySet());
        sorted.sort(sorter);
        return sorted;
    }

    @Override
    public List<String> getAllSorted() {
        return getAllSorted(Util.roundComparator);
    }

    @Override
    public String get(String key) {
        return key;
    }

    @Override
    public List<String> find(String key, Predicate<String> filter) {
        if (cacheInvalid()) refresh();
        return cache.keySet().stream().filter(filter).toList();
    }

    @Override
    public void set(String key, String value) {
        cache.add(key);
        store();
    }

    @Override
    public void setAll(List<String> keys, List<String> unused) {
        setAll(keys);
    }

    @Override
    public void setAll(List<String> labels) {
        for (String label : labels) {
            cache.add(label);
        }
        store();
    }

    @Override
    public void delete(String key) {
        cache.remove(key);
        store();
    }

    @Override
    public void deleteAll() {
        cache.clear();
        store();
    }

    @Override
    public List<String> getDefault() {
        return _DEFAULT_ROUNDS;
    }
}
