package pl.cheily.filegen.LocalData;

public interface OutputWriter {
    /**
     * When attempting to copy a flag image, the source flag name should be contained within the <code>data</code>
     *
     * @param resourceName name of the target file to write to
     * @param data         data to save
     * @return success value
     */
    boolean writeData(String resourceName, String data);
}
