package pl.cheily.filegen;

public class FileSaveException extends Exception {
    private final Util.ResourcePath rPath;

    public FileSaveException(Util.ResourcePath rPath) {
        super();
        this.rPath = rPath;
    }

    public Util.ResourcePath getResourcePath() {
        return rPath;
    }
}
