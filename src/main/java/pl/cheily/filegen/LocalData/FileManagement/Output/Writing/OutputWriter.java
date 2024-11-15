package pl.cheily.filegen.LocalData.FileManagement.Output.Writing;

import pl.cheily.filegen.LocalData.FileManagement.Output.Formatting.OutputFormatter;

public interface OutputWriter {
    /**
     * By contract, the writer will return false and not execute any further writes if it is disabled.
     * For the sake of proper error-handling however, it is upon the prompter to make a preemptive check of the writer's state.
     *
     * @return success value, always false if writer is disabled
     */
    boolean writeData();

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
