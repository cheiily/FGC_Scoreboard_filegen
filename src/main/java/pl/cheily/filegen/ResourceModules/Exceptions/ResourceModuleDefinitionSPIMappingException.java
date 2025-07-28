package pl.cheily.filegen.ResourceModules.Exceptions;

public class ResourceModuleDefinitionSPIMappingException extends ResourceModuleException {
        private static final String MESSAGE_TEMPLATE = "Failed serializing resource module definition to appropriate SPI data-mapping. Module: %s, Content: %s, Errors: \"%s\"";


    public ResourceModuleDefinitionSPIMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceModuleDefinitionSPIMappingException(String message) {
        super(message);
    }

        public static ResourceModuleDefinitionSPIMappingException from(String name, String content, String errors) {
        return new ResourceModuleDefinitionSPIMappingException(
                String.format(
                        MESSAGE_TEMPLATE,
                        name,
                        content == null ? "" : content,
                        errors
                )
        );
    }

    public static ResourceModuleDefinitionSPIMappingException from(String name, String content, String errors, Throwable cause) {
        return new ResourceModuleDefinitionSPIMappingException(
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
