package pl.cheily.filegen.ResourceModules.Exceptions;

public abstract class ResourceModuleException extends Exception {
    public ResourceModuleException(String message) {
        super(message);
    }
    public ResourceModuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
