package pl.cheily.filegen.ResourceModules.Validation.Factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cheily.filegen.ResourceModules.Definition.ResourceModuleDefinitionHandlerFactory;
import pl.cheily.filegen.ResourceModules.Exceptions.Errors.GeneralResourceModuleErrorCode;
import pl.cheily.filegen.ResourceModules.Exceptions.Errors.PluginInstallationErrorCode;
import pl.cheily.filegen.ResourceModules.Exceptions.ResourceModuleDefinitionSPIMappingException;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.IPluginBase;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleDefinitionData;
import pl.cheily.filegen.ResourceModules.ResourceModule;
import pl.cheily.filegen.ResourceModules.ResourceModuleType;
import pl.cheily.filegen.ResourceModules.Validation.ValidationEvent;
import pl.cheily.filegen.ResourceModules.Validation.Verifier;
import pl.cheily.filegen.ScoreboardApplication;

import java.util.List;

public class InstallationVerifierFactory implements ResourceModuleVerifierFactory {
    private static final Logger logger = LoggerFactory.getLogger(InstallationVerifierFactory.class);

    @Override
    public ValidationEvent validates() {
        return ValidationEvent.INSTALLATION;
    }

    @Override
    public List<Verifier> getFor(ResourceModuleType type) {
        return switch (type) {
            case PLUGIN_JAR -> List.of(this::jar_infoMatchesDefinition);
            case STATICS_COLLECTION -> List.of(this::validateStaticsCollection);
            case STATIC_FILE -> List.of(this::validateStaticFile);
            case PROPERTIES_JSON -> List.of(this::validatePropertiesJson);
            case EXECUTABLE_COMMAND -> List.of(this::validateExecutableCommand);
            default -> List.of();
        };
    }

    @Override
    public List<Verifier> getAll() {
        return List.of(
                this::jar_infoMatchesDefinition,
                this::validateStaticsCollection,
                this::validateStaticFile,
                this::validatePropertiesJson,
                this::validateExecutableCommand
        );
    }

    private List<Error> jar_infoMatchesDefinition(ResourceModule module) {
        ResourceModuleDefinitionData definitionData;
        IPluginBase plugin;
        try {
            definitionData = ResourceModuleDefinitionHandlerFactory.spiMapping(module.getDefinition());
            plugin = ScoreboardApplication.resourceModuleRegistry.pluginRegistry.getExisting(module);
        } catch (ResourceModuleDefinitionSPIMappingException e) {
            return List.of(
                    GeneralResourceModuleErrorCode.INVALID_DEFINITION_VERSION.asError(
                            "Cannot map resource module definition to spi representation for module: \"" + module.getDefinition().qualifiedName() + "\"." +
                                    " Exception: " + e.getMessage()
                    )
            );
        }

        if (plugin == null) {
            return List.of(PluginInstallationErrorCode.NO_INSTANCE.asError());
        }

        if (!plugin.getInfo().equals(definitionData))
            return List.of(PluginInstallationErrorCode.DEFINITION_MISMATCH.asError());

        return List.of();
    }

    private List<Error> validateStaticsCollection(ResourceModule module) {
        // no-op
        return List.of();
    }

    private List<Error> validateStaticFile(ResourceModule module) {
        logger.warn("Resource module installation validation is not implemented, because this resource type has not been used before! If you see this, contact the developer. Type: {}, Module: {}", ResourceModuleType.STATIC_FILE, module);
        return List.of();
    }

    private List<Error> validatePropertiesJson(ResourceModule module) {
        logger.warn("Resource module installation validation is not implemented, because this resource type has not been used before! If you see this, contact the developer. Type: {}, Module: {}", ResourceModuleType.PROPERTIES_JSON, module);
        return List.of();
    }

    private List<Error> validateExecutableCommand(ResourceModule module) {
        logger.warn("Resource module installation validation is not implemented, because this resource type has not been used before! If you see this, contact the developer. Type: {}, Module: {}", ResourceModuleType.EXECUTABLE_COMMAND, module);
        return List.of();
    }

}
