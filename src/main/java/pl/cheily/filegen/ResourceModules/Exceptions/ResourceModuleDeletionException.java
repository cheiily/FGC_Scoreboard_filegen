package pl.cheily.filegen.ResourceModules.Exceptions;

public class ResourceModuleDeletionException extends ResourceModuleInstallationManagementException {
    private final static String MESSAGE = "Failed to delete resource module \"%s\" at path \"%s\".";

    ResourceModuleDeletionException(String message) {
        super(message);
    }

    ResourceModuleDeletionException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ResourceModuleDeletionException fromPath(String moduleName, String path) {
        return new ResourceModuleDeletionException(String.format(MESSAGE, moduleName, path));
    }
    public static ResourceModuleDeletionException fromPath(String moduleName, String path, Throwable cause) {
        return new ResourceModuleDeletionException(String.format(MESSAGE, moduleName, path), cause);
    }
    public static ResourceModuleDeletionException fromPath(String moduleName, String path, String details, Throwable cause) {
        return new ResourceModuleDeletionException(String.format(MESSAGE + " Details: %s", moduleName, path, details), cause);
    }
}
