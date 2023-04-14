package pl.cheily.filegen.LocalData;

import org.ini4j.Ini;

public class DataManager {
    private final OutputFormatter formatter;
    private OutputWriter writer;

    private final Ini metadata = new Ini();
    private final Ini player_list = new Ini();
    private final Ini comms_list = new Ini();


    public DataManager(OutputFormatter formatter, OutputWriter writer) {
        this.formatter = formatter;
        this.writer = writer;
    }




    public void put_meta(MetaKey section, MetaKey key, String value) {
        metadata.put(section.toString(), key.toString(), value);
    }

    public String get_meta(MetaKey section, MetaKey key) {
        String ret = metadata.get(section.toString(), key.toString());
        return ret == null ? "" : ret;
    }

    public <T> T get_meta(MetaKey section, MetaKey key, Class<T> clazz) {
        return metadata.get(section.toString(), key.toString(), clazz);
    }
}
