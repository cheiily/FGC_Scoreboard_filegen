package pl.cheily.filegen.LocalData.FileManagement.Output.Formatting;

import java.util.List;

public class OutputFormatterDeserializerParams {
    public String name;
    public List<FormattingUnit> units;

    public OutputFormatterDeserializerParams(String name, List<FormattingUnit> units) {
        this.name = name;
        this.units = units;
    }
}
