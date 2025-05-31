package pl.cheily.filegen.LocalData.FileManagement.Output.Writing;

import pl.cheily.filegen.LocalData.FileManagement.Output.Formatting.OutputFormatter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public enum OutputWriterType {
    RAW(
            OutputType.FILE,
            RawOutputWriter::new,
            RawOutputWriter.defaultName,
            RawOutputWriter::deserialize
    ),
    //HTML_OVERLAY,
    ;

    public OutputType supportedWriterType;
    public BiFunction<String, OutputFormatter, OutputWriter> ctor;
    public Function<OutputWriterDeserializerParams, OutputWriter> deserializer;
    public String description;

    OutputWriterType(OutputType supportedWriterType, BiFunction<String, OutputFormatter, OutputWriter> ctor, String description, Function<OutputWriterDeserializerParams, OutputWriter> deserializer) {
        this.supportedWriterType = supportedWriterType;
        this.ctor = ctor;
        this.description = description;
        this.deserializer = deserializer;
    }

    public static List<OutputWriterType> getSupportingType(OutputType type) {
        return Arrays.stream(OutputWriterType.values()).filter(fmt -> fmt.supportedWriterType == type).toList();
    }
}
