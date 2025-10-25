package pl.cheily.filegen.ResourceModules.Exceptions.Errors;

public enum PluginInstallationErrorCode implements ErrorCode {
    NO_INSTANCE("No plugin instance could not be found, despite previous installation attempt."),
    DEFINITION_MISMATCH("The provided plugin definition does not match the predefined definition."),
    ;

    private final String message;

    PluginInstallationErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
