package pl.cheily.filegen.LocalData.FileManagement.Output.Writing;

import pl.cheily.filegen.LocalData.FileManagement.Output.Formatting.OutputFormatter;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum OutputWriterType {
    RAW(
            RawOutputWriter::new,
            RawOutputWriter.defaultName,
            RawOutputWriter::deserialize
    ),
    //HTML_OVERLAY,
    ;

    public BiFunction<String, OutputFormatter, OutputWriter> ctor;
    public Function<OutputWriterDeserializerParams, OutputWriter> deserializer;
    public String description;

    OutputWriterType(BiFunction<String, OutputFormatter, OutputWriter> ctor, String description, Function<OutputWriterDeserializerParams, OutputWriter> deserializer) {
        this.ctor = ctor;
        this.description = description;
        this.deserializer = deserializer;
    }
}
