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
            DefaultOutputFormatter::new,
            DefaultOutputFormatter::getPreset,
            DefaultOutputFormatter.defaultName,
            DefaultOutputFormatter::deserialize
    ),
    FULLY_SEPARATING(
            List.of(OutputType.FILE),
            FullySeparatingOutputFormatter::new,
            FullySeparatingOutputFormatter::new,
            FullySeparatingOutputFormatter::getPreset,
            FullySeparatingOutputFormatter.defaultName,
            FullySeparatingOutputFormatter::deserialize
    );

    public List<OutputType> supportedWriterTypes;
    public Supplier<OutputFormatter> simpleCtor;
    public BiFunction<String, List<FormattingUnit>, OutputFormatter> paramCtor;
    public Supplier<List<FormattingUnit>> presetSupplier;
    public Function<OutputFormatterDeserializerParams, OutputFormatter> deserializer;
    public String description;
    OutputFormatterType(List<OutputType> supportedWriterTypes, Supplier<OutputFormatter> ctor, BiFunction<String, List<FormattingUnit>, OutputFormatter> parameterizedCtor, Supplier<List<FormattingUnit>> presetSupplier, String description, Function<OutputFormatterDeserializerParams, OutputFormatter> deserializer) {
        this.supportedWriterTypes = supportedWriterTypes;
        this.simpleCtor = ctor;
        this.paramCtor = parameterizedCtor;
        this.presetSupplier = presetSupplier;
        this.description = description;
        this.deserializer = deserializer;
    }


    public static List<OutputFormatterType> getSupportedTypes(OutputType type) {
        return List.of(OutputFormatterType.values()).stream().filter(fmt -> fmt.supportedWriterTypes.contains(type)).toList();
    }
}
