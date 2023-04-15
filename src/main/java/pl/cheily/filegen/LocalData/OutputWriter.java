package pl.cheily.filegen.LocalData;

public interface OutputWriter {
    /**
     * By contract, the writer will return false and not execute any further writes if it is disabled.
     * For the sake of proper error-handling however, it is upon the prompter to make a preemptive check of the writer's state.
     *
     * @param resourceName name of the target file to write to
     * @param data         data to save, due to the nature of flag-file handling, the data string should contain the name of the source flag image.
     * @return success value, always false if writer is disabled
     * @apiNote When attempting to copy a flag image, the source flag name should be contained within the <code>data</code>.
     */
    boolean writeData(String resourceName, String... data);

    OutputFormatter getFormatter();

    void setFormatter(OutputFormatter formatter);

    /**
     * @return Per-instance unique name, to distinguish among other writers of the same type.
     */
    public String getName();

    public boolean isEnabled();

    public void enable();

    public void disable();

}
