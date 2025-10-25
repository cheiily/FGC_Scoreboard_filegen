package pl.cheily.filegen.ResourceModules.Exceptions;

public class ResourceModuleDefinitionSPIUnmappingException extends ResourceModuleException {
    private static final String MESSAGE_TEMPLATE =
            "Failed deserializing SPI mapping to appropriate resource module definition . Module: %s, Content: %s, Errors: \"%s\"";


    public ResourceModuleDefinitionSPIUnmappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceModuleDefinitionSPIUnmappingException(String message) {
        super(message);
    }

        public static ResourceModuleDefinitionSPIUnmappingException from(String name, String content, String errors) {
        return new ResourceModuleDefinitionSPIUnmappingException(
                String.format(
                        MESSAGE_TEMPLATE,
                        name,
                        content == null ? "" : content,
                        errors
                )
        );
    }

    public static ResourceModuleDefinitionSPIUnmappingException from(String name, String content, String errors, Throwable cause) {
        return new ResourceModuleDefinitionSPIUnmappingException(
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
