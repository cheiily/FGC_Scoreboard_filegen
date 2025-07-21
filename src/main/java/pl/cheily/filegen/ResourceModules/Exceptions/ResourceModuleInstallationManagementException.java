package pl.cheily.filegen.ResourceModules.Exceptions;

public abstract class ResourceModuleInstallationManagementException extends ResourceModuleException {
    public ResourceModuleInstallationManagementException(String message) {
        super(message);
    }
    public ResourceModuleInstallationManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}
