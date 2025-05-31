package pl.cheily.filegen.LocalData.FileManagement.Output.Writing;

import pl.cheily.filegen.LocalData.FileManagement.Output.Formatting.OutputFormatter;

public class OutputWriterDeserializerParams {
    public boolean enabled;
    public String name;
    public OutputFormatter formatter;

    public OutputWriterDeserializerParams(boolean enabled, String name, OutputFormatter formatter) {
        this.enabled = enabled;
        this.name = name;
        this.formatter = formatter;
    }
}
