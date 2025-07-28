package pl.cheily.filegen.ResourceModules.Exceptions;

public class ResourceModuleDefinitionSerializationException extends ResourceModuleException {
    private static final String MESSAGE_TEMPLATE = "Failed serializing resource module definition. Module: %s, Content: %s, Errors: \"%s\"";


    public ResourceModuleDefinitionSerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceModuleDefinitionSerializationException(String message) {
        super(message);
    }

        public static ResourceModuleDefinitionSerializationException from(String name, String content, String errors) {
        return new ResourceModuleDefinitionSerializationException(
                String.format(
                        MESSAGE_TEMPLATE,
                        name,
                        content == null ? "" : content,
                        errors
                )
        );
    }

    public static ResourceModuleDefinitionSerializationException from(String name, String content, String errors, Throwable cause) {
        return new ResourceModuleDefinitionSerializationException(
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
