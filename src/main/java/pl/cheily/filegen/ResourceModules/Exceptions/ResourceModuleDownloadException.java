package pl.cheily.filegen.ResourceModules.Exceptions;

public class ResourceModuleDownloadException extends ResourceModuleInstallationManagementException {
    private static final String MESSAGE = "Failed downloading resource module.\nURL: %s\nDownload path: %s";
    private static final String MESSAGE_WITH_DETAILS = "Failed downloading resource module.\nURL: %s\nDownload path: %s\nDetails: %s";

    private ResourceModuleDownloadException(String message) {
        super(message);
    }
    private ResourceModuleDownloadException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ResourceModuleDownloadException fromURL(String url, String downloadPath) {
        return new ResourceModuleDownloadException(String.format(MESSAGE, url, downloadPath));
    }
    public static ResourceModuleDownloadException fromURL(String url, String downloadPath, Throwable cause) {
        return new ResourceModuleDownloadException(String.format(MESSAGE, url, downloadPath), cause);
    }
    public static ResourceModuleDownloadException fromURL(String url, String downloadPath, String details, Throwable cause) {
        return new ResourceModuleDownloadException(
                String.format(MESSAGE_WITH_DETAILS, url, downloadPath, details),
                cause
        );
    }
}
