package pl.cheily.filegen.ResourceModules.Exceptions.Errors;

public enum GeneralResourceModuleErrorCode implements ErrorCode {
    INVALID_MODULE_TYPE("Invalid module type."),
    INVALID_DEFINITION_VERSION("Invalid definition version."),
    ;

    private final String message;

    GeneralResourceModuleErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
