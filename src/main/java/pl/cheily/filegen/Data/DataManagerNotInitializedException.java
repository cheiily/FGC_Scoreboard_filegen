package pl.cheily.filegen.Data;

/**
 * Indicates that an operation has been issued, where a valid path is required, while the {@link DataManager} has not yet been initialized with such.
 */
public class DataManagerNotInitializedException extends Exception {
    private static final String MESSAGE = "DataManager has not been initialized with a valid path! Valid output path for output might not have been selected.";

    public DataManagerNotInitializedException() {
        super(MESSAGE);
    }

    public DataManagerNotInitializedException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
