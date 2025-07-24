package pl.cheily.filegen.ResourceModules.Exceptions;

public class ResourceModuleDefinitionParseException extends ResourceModuleException {
    private static final String MESSAGE_TEMPLATE = "Failed parsing resource module definition. Module: %s, Content: %s, Errors: \"%s\"";


    public ResourceModuleDefinitionParseException(String message) {
        super(message);
    }

    public ResourceModuleDefinitionParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ResourceModuleDefinitionParseException from(String name, String content, String errors) {
        return new ResourceModuleDefinitionParseException(
                String.format(
                        MESSAGE_TEMPLATE,
                        name,
                        content == null ? "" : content,
                        errors
                )
        );
    }

    public static ResourceModuleDefinitionParseException from(String name, String content, String errors, Throwable cause) {
        return new ResourceModuleDefinitionParseException(
                String.format(
                        MESSAGE_TEMPLATE,
                        name,
                        content == null ? "" : content,
                        errors
                ),
                cause
        );
    }
}
