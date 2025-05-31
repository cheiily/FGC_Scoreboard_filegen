package pl.cheily.filegen.LocalData.FileManagement.Output.Writing;

import pl.cheily.filegen.LocalData.FileManagement.Output.Formatting.OutputFormatter;

public abstract class OutputWriterBase implements OutputWriter {
    protected OutputFormatter formatter;
    protected boolean enabled = true;
    protected final String name;
    protected final OutputType outputType;
    protected final OutputWriterType writerType;

    public OutputWriterBase(String name, OutputType type, OutputWriterType writerType, OutputFormatter formatter) {
        this.name = name;
        this.outputType = type;
        this.writerType = writerType;
        this.formatter = formatter;
    }

    @Override
    public OutputFormatter getFormatter() {
        return formatter;
    }

    @Override
    public void setFormatter(OutputFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public String getName() {
        return name;
    }

    public OutputType getOutputType() {
        return outputType;
    }

    public OutputWriterType getWriterType() {
        return writerType;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void disable() {
        enabled = false;
    }
}
