package pl.cheily.filegen.ResourceModules.Exceptions;

public class UnarchivingException extends ResourceModuleInstallationManagementException {
    private final static String MESSAGE = "Failed unarchiving resource module. Archive path: %s; Destination path: %s";

    UnarchivingException(String message) {
        super(message);
    }
    UnarchivingException(String message, Throwable cause) {
        super(message, cause);
    }

    public static UnarchivingException fromArchiveAndDestination(String archivePath, String destinationPath) {
        return new UnarchivingException(String.format(MESSAGE, archivePath, destinationPath));
    }
    public static UnarchivingException fromArchiveAndDestination(String archivePath, String destinationPath, Throwable cause) {
        return new UnarchivingException(String.format(MESSAGE, archivePath, destinationPath), cause);
    }
    public static UnarchivingException fromArchiveAndDestination(String archivePath, String destinationPath, String details, Throwable cause) {
        return new UnarchivingException(
                String.format("%s. Details: %s", String.format(MESSAGE, archivePath, destinationPath), details),
                cause
        );
    }

}
