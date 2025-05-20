package pl.cheily.filegen.LocalData.FileManagement.Meta.WriterConfig;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile;
import pl.cheily.filegen.LocalData.FileManagement.Meta.CachedIniDAOBase;
import pl.cheily.filegen.LocalData.FileManagement.Meta.Match.MatchDataKey;
import pl.cheily.filegen.LocalData.FileManagement.Output.Formatting.*;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputWriter;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputWriterDeserializerParams;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputWriterType;
import pl.cheily.filegen.LocalData.ResourcePath;

import java.util.List;
import java.util.function.Predicate;

import static pl.cheily.filegen.LocalData.FileManagement.Meta.WriterConfig.OutputWriterPropKey.*;

public class OutputWriterDAOIni extends CachedIniDAOBase implements OutputWriterDAO {

    private static Config getConfig() {
        Config config = new Config();
        config.setMultiOption(true);
        config.setMultiSection(true);
        return config;
    }

    public OutputWriterDAOIni(ResourcePath path) {
        super(path, getConfig());
    }

    @Override
    public List<OutputWriter> getAll() {
        if (cacheInvalid()) refresh();
        List<Profile.Section> writer_secs = cache.values().stream().filter(section -> !section.getName().contains(".fmt")).toList();
        
        return writer_secs.stream().map(this::deserializeWriter).toList();
    }

    @Override
    public OutputWriter get(String key) {
        if (key == null) return null;
        if (cacheInvalid()) refresh();
        return deserializeWriter(cache.get(key));
    }

    @Override
    public List<OutputWriter> find(String key, Predicate<OutputWriter> filter) {
        return getAll().stream().filter(filter).toList();
    }

    @Override
    public void set(String key, OutputWriter value) {
        if (key == null || value == null) return;
        if (cacheInvalid()) refresh();
        delete(key);
        serializeWriter(value);
        store();
    }

    @Override
    public void setAll(List<String> keys, List<OutputWriter> values) {
        cache.clear();
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i) == null || values.get(i) == null) continue;
            set(keys.get(i), values.get(i));
        }
        store();
    }

    @Override
    public void delete(String key) {
        if (key == null) return;
        OutputWriter writer = get(key);
        cache.remove(key);

        if (writer == null) return;
        cache.remove(writer.getFormatter().getName() + ".fmt");

        store();
    }

    @Override
    public void deleteAll() {
        cache.clear();
        store();
    }

    private OutputWriter deserializeWriter(Profile.Section wrt_sec) {
        if (wrt_sec == null) return null;
        try {
            return OutputWriterType.valueOf(wrt_sec.get(WRITER_TYPE.toString())).deserializer.apply(new OutputWriterDeserializerParams(
                    wrt_sec.get(ENABLED.toString(), Boolean.class),
                    wrt_sec.getName(),
                    deserializeFormatter(wrt_sec)
            ));
        } catch (IllegalArgumentException e) {
            logger.error("Couldn't deserialize writer " + wrt_sec + " because of the following error.", e);
        }
        return null;
    }

    private OutputFormatter deserializeFormatter(Profile.Section wrt_sec) {
        OutputFormatter formatter;
        try {
            formatter = OutputFormatterType.valueOf(wrt_sec.get(FORMATTER_TYPE.toString())).deserializer.apply(new OutputFormatterDeserializerParams(
                    wrt_sec.get(FORMATTER_NAME.toString()),
                    cache.getAll(wrt_sec.get(FORMATTER_NAME.toString()) + ".fmt").stream().map(this::deserializeFormattingUnit).toList()
            ));
        } catch (IllegalArgumentException e) {
            logger.error("Couldn't deserialize formatter " + wrt_sec.get(FORMATTER_NAME.toString()) + " because of the following error.", e);
            return null;
        }
        var nulls = formatter.getFormats().stream().filter(f -> f == null).toList();
        if (!nulls.isEmpty()) {
            logger.error("Couldn't deserialize " + nulls.size() + " formatting units for formatter " + wrt_sec.get(FORMATTER_NAME.toString()) + ". The formatter will function with the correctly assigned units.");
            formatter.getFormats().removeIf(f -> f == null);
        }
        return formatter;
    }

    private FormattingUnit deserializeFormattingUnit(Profile.Section fmt_sec) {
        try {
            return FormattingUnit.deserialize(
                fmt_sec.get(ENABLED.toString(), Boolean.class),
                fmt_sec.getAll(FMT_UNIT_INPUT.toString()).stream().map(MatchDataKey::fromString).toList(),
                ResourcePath.fromString(fmt_sec.get(FMT_UNIT_DESTINATION.toString())),
                fmt_sec.get(FMT_UNIT_SAMPLE_OUTPUT.toString()),
                fmt_sec.get(FMT_UNIT_TEMPLATE.toString()),
                FormattingUnitMethodReference.valueOf(fmt_sec.get(FMT_UNIT_METHOD.toString()))
            );
        } catch (IllegalArgumentException e) {
            logger.error("Couldn't deserialize formatting unit " + fmt_sec + " because of the following error.", e);
            return null;
        }
    }

    private void serializeWriter(OutputWriter writer) {
        var section = cache.add(writer.getName());
        section.put(ENABLED.toString(), Boolean.toString(writer.isEnabled()));
        section.put(WRITER_TYPE.toString(), writer.getWriterType().toString());
        section.put(FORMATTER_NAME.toString(), writer.getFormatter().getName());
        section.put(FORMATTER_TYPE.toString(), writer.getFormatter().getType().toString());
        writer.getFormatter().getFormats().forEach(fmt -> serializeFormattingUnit(fmt, writer.getFormatter().getName(), cache));
    }
    
    private void serializeFormattingUnit(FormattingUnit fmt, String formatterName, Ini targetIni) {
        Profile.Section sec = targetIni.add(formatterName + ".fmt");
        sec.put(ENABLED.toString(), Boolean.toString(fmt.enabled));
        sec.put(FMT_UNIT_DESTINATION.toString(), fmt.destination.toString());
        sec.put(FMT_UNIT_SAMPLE_OUTPUT.toString(), fmt.sampleOutput);
        sec.put(FMT_UNIT_TEMPLATE.toString(), fmt.customInterpolationFormat);
        sec.put(FMT_UNIT_METHOD.toString(), fmt.formatType.toString());
        sec.putAll(FMT_UNIT_INPUT.toString(), fmt.inputKeys.toArray());
    }
}
