package pl.cheily.filegen.LocalData.FileManagement.Meta.WriterConfig;

public enum OutputWriterPropKey {
    //reocurring
    ENABLED("enabled"),

    WRITER_TYPE("type"),

    FORMATTER_NAME("formatter"),
    FORMATTER_TYPE("formatterType"),

    FMT_UNIT("fmt"),
    FMT_UNIT_INPUT("in"),
    FMT_UNIT_DESTINATION("out"),
    FMT_UNIT_SAMPLE_OUTPUT("sample"),
    FMT_UNIT_TEMPLATE("template"),
    FMT_UNIT_METHOD("method")
    ;

    private final String key;

    OutputWriterPropKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }

    public static OutputWriterPropKey fromString(String key) {
        for (OutputWriterPropKey value : values()) {
            if (value.key.equals(key)) {
                return value;
            }
        }
        throw new IllegalArgumentException("No enum constant " + OutputWriterPropKey.class.getCanonicalName() + "." + key);
    }
}
