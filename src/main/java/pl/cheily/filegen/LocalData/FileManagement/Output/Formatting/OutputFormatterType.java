package pl.cheily.filegen.LocalData.FileManagement.Output.Formatting;

import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputType;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public enum OutputFormatterType {
    DEFAULT(
            List.of(OutputType.FILE),
            DefaultOutputFormatter::new,
            DefaultOutputFormatter.defaultName,
            DefaultOutputFormatter::deserialize
    ),
    FULLY_SEPARATING(
            List.of(OutputType.FILE),
            FullySeparatingOutputFormatter::new,
            FullySeparatingOutputFormatter.defaultName,
            FullySeparatingOutputFormatter::deserialize
    );

    public List<OutputType> supportedWriterTypes;
    public Supplier<OutputFormatter> ctor;
    public Function<OutputFormatterDeserializerParams, OutputFormatter> deserializer;
    public String description;
    OutputFormatterType(List<OutputType> supportedWriterTypes, Supplier<OutputFormatter> ctor, String description, Function<OutputFormatterDeserializerParams, OutputFormatter> deserializer) {
        this.supportedWriterTypes = supportedWriterTypes;
        this.ctor = ctor;
        this.description = description;
        this.deserializer = deserializer;
    }


    public static List<OutputFormatterType> getSupportedTypes(OutputType type) {
        return List.of(OutputFormatterType.values()).stream().filter(fmt -> fmt.supportedWriterTypes.contains(type)).toList();
    }
}
