package pl.cheily.filegen.ResourceModules.Exceptions;

public class ArchiveFormatNotSupportedException extends UnarchivingException {
    private static final String MESSAGE = "Archive format not supported: %s";

    private ArchiveFormatNotSupportedException(String message) {
        super(message);
    }
    private ArchiveFormatNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ArchiveFormatNotSupportedException fromFormat(String format) {
        return new ArchiveFormatNotSupportedException(String.format(MESSAGE, format));
    }

    public static ArchiveFormatNotSupportedException fromFormat(String format, Throwable cause) {
        return new ArchiveFormatNotSupportedException(String.format(MESSAGE, format), cause);
    }
    public static ArchiveFormatNotSupportedException fromFormat(String format, String details, Throwable cause) {
        return new ArchiveFormatNotSupportedException(
                String.format("%s. Details: %s", String.format(MESSAGE, format), details),
                cause
        );
    }
}
