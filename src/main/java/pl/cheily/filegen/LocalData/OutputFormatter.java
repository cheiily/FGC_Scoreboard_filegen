package pl.cheily.filegen.LocalData;

public interface OutputFormatter {
    /**
     * Formats a resource to the desired layout.
     *
     * @param resourceName identifier of the resource, to decide how the data should be formatted
     * @param data         to format, may consist of multiple separate parts to merge
     * @return formatted data
     */
    String format(String resourceName, String... data);
}
