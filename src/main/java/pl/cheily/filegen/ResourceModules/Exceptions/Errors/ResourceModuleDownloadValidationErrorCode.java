package pl.cheily.filegen.ResourceModules.Exceptions.Errors;

public enum ResourceModuleDownloadValidationErrorCode implements ErrorCode {
    NOT_A_DIRECTORY("The extracted path is not a directory."),
    NO_FILES_FOUND("No files found in the directory."),
    CANNOT_READ_FILE("The file is not readable or does not exist."),
    ;

    private final String message;

    ResourceModuleDownloadValidationErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
