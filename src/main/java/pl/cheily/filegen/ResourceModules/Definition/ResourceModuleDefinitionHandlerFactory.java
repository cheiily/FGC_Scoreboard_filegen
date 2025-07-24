package pl.cheily.filegen.ResourceModules.Definition;

import org.json.JSONException;
import org.json.JSONObject;
import pl.cheily.filegen.ResourceModules.Exceptions.Errors.GeneralResourceModuleErrorCode;
import pl.cheily.filegen.ResourceModules.Exceptions.ResourceModuleDefinitionParseException;
import pl.cheily.filegen.ResourceModules.Exceptions.ResourceModuleDefinitionSerializationException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceModuleDefinitionHandlerFactory {
    private static ResourceModuleDefinitionHandlerFactoryConfig config;
    public static void loadConfig(ResourceModuleDefinitionHandlerFactoryConfig config) {
        ResourceModuleDefinitionHandlerFactory.config = config;
    }
    public static ResourceModuleDefinitionHandlerFactoryConfig getConfig() {
        return config;
    }

    static {
        config = new ResourceModuleDefinitionHandlerFactoryConfig(
                false
        );
    }

    public static ResourceModuleDefinition parse(String jsonString) throws ResourceModuleDefinitionParseException {
        JSONObject json;
        try {
            json = new JSONObject(jsonString);
            json.getString(ResourceModuleDefinition.KEY_DEFINITION_VERSION);
        } catch (JSONException e) {
            throw ResourceModuleDefinitionParseException.from(
                    "",
                    jsonString,
                    e.getMessage(),
                    e
            );
        }
        try {
            return getParser(json.getString(ResourceModuleDefinition.KEY_DEFINITION_VERSION)).parse(jsonString);
        } catch (IllegalArgumentException e) {
            throw ResourceModuleDefinitionParseException.from(
                    json.has(ResourceModuleDefinition.KEY_NAME) ? json.getString(ResourceModuleDefinition.KEY_NAME) : "",
                    jsonString,
                    e.getMessage()
            );
        }
    }

    public static String serialize(ResourceModuleDefinition definition) throws ResourceModuleDefinitionSerializationException {
        return getSerializer(definition.definitionVersion()).serialize(definition);
    }



    public static ResourceModuleDefinitionParser getParser(String definitionVersion) throws IllegalArgumentException {
        return switch (definitionVersion) {
            case ResourceModuleDefinition.V1 -> ResourceModuleDefinitionHandlerFactory::parseV1;
            default -> throw new IllegalArgumentException(
                    GeneralResourceModuleErrorCode.INVALID_DEFINITION_VERSION.asError("Value: " + definitionVersion).getMessage()
            );
        };
    }

    public static ResourceModuleDefinitionSerializer getSerializer(String definitionVersion) throws IllegalArgumentException {
        return switch (definitionVersion) {
            case ResourceModuleDefinition.V1 -> ResourceModuleDefinitionHandlerFactory::serializeV1;
            default -> throw new IllegalArgumentException(
                    GeneralResourceModuleErrorCode.INVALID_DEFINITION_VERSION.asError("Value: " + definitionVersion).getMessage()
            );
        };
    }

    public static List<String> getRequiredKeys(String definitionVersion) {
        return switch (definitionVersion) {
            case ResourceModuleDefinition.V1 -> List.of(
                ResourceModuleDefinition.KEY_DEFINITION_VERSION,
                ResourceModuleDefinition.KEY_NAME,
                ResourceModuleDefinition.KEY_INSTALL_PATH,
                ResourceModuleDefinition.KEY_DESCRIPTION,
                ResourceModuleDefinition.KEY_VERSION,
                ResourceModuleDefinition.KEY_AUTHOR,
                ResourceModuleDefinition.KEY_URL,
                ResourceModuleDefinition.KEY_EXTERNAL_URL,
                ResourceModuleDefinition.KEY_RESOURCE_TYPE
            );
            default -> throw new IllegalArgumentException(
                    GeneralResourceModuleErrorCode.INVALID_DEFINITION_VERSION.asError("Value: " + definitionVersion).getMessage()
            );
        };
    }

    private static ResourceModuleDefinition parseV1(String jsonString) throws ResourceModuleDefinitionParseException {
        JSONObject json;
        try {
            json = new JSONObject(jsonString);
        } catch (JSONException e) {
            throw ResourceModuleDefinitionParseException.from(
                    "",
                    jsonString,
                    e.getMessage(),
                    e
            );
        }

        var errors = getRequiredKeys(ResourceModuleDefinition.V1).stream()
                .filter(key -> !json.has(key))
                .collect(Collectors.joining(", "));

        if (!errors.isEmpty()) {
            throw ResourceModuleDefinitionParseException.from(
                json.has("name") ? json.getString("name") : "",
                jsonString,
                errors
            );
        }

        return new ResourceModuleDefinition(
            json.getString(ResourceModuleDefinition.KEY_DEFINITION_VERSION),
            json.getString(ResourceModuleDefinition.KEY_NAME),
            json.optString(ResourceModuleDefinition.KEY_CATEGORY, null),
            json.getString(ResourceModuleDefinition.KEY_INSTALL_PATH),
            json.getString(ResourceModuleDefinition.KEY_DESCRIPTION),
            json.getString(ResourceModuleDefinition.KEY_VERSION),
            json.optString(ResourceModuleDefinition.KEY_ISO_DATE, null),
            json.getString(ResourceModuleDefinition.KEY_AUTHOR),
            json.getString(ResourceModuleDefinition.KEY_URL),
            json.getBoolean(ResourceModuleDefinition.KEY_EXTERNAL_URL),
            json.getString(ResourceModuleDefinition.KEY_RESOURCE_TYPE),
            json.optString(ResourceModuleDefinition.KEY_ARCHIVE_TYPE, null),
            json.optBoolean(ResourceModuleDefinition.KEY_AUTOINSTALL, false),
            json.optBoolean(ResourceModuleDefinition.KEY_AUTORUN, false),
            json.optString(ResourceModuleDefinition.KEY_CHECKSUM, null)
        );
    }

    private static String serializeV1(ResourceModuleDefinition definition) throws ResourceModuleDefinitionSerializationException {
        JSONObject json = new JSONObject();

        var errors = Stream.of(
                serializeFieldV1(json, ResourceModuleDefinition.KEY_DEFINITION_VERSION, definition.definitionVersion()),
                serializeFieldV1(json, ResourceModuleDefinition.KEY_NAME, definition.name()),
                serializeFieldV1(json, ResourceModuleDefinition.KEY_CATEGORY, definition.category()),
                serializeFieldV1(json, ResourceModuleDefinition.KEY_INSTALL_PATH, definition.installPath()),
                serializeFieldV1(json, ResourceModuleDefinition.KEY_DESCRIPTION, definition.description()),
                serializeFieldV1(json, ResourceModuleDefinition.KEY_VERSION, definition.version()),
                serializeFieldV1(json, ResourceModuleDefinition.KEY_ISO_DATE, definition.isoDate()),
                serializeFieldV1(json, ResourceModuleDefinition.KEY_AUTHOR, definition.author()),
                serializeFieldV1(json, ResourceModuleDefinition.KEY_URL, definition.url()),
                serializeFieldV1(json, ResourceModuleDefinition.KEY_EXTERNAL_URL, definition.externalUrl()),
                serializeFieldV1(json, ResourceModuleDefinition.KEY_RESOURCE_TYPE, definition.resourceType()),
                serializeFieldV1(json, ResourceModuleDefinition.KEY_ARCHIVE_TYPE, definition.archiveType()),
                serializeFieldV1(json, ResourceModuleDefinition.KEY_AUTOINSTALL, definition.autoinstall()),
                serializeFieldV1(json, ResourceModuleDefinition.KEY_AUTORUN, definition.autorun()),
                serializeFieldV1(json, ResourceModuleDefinition.KEY_CHECKSUM, definition.checksum())
        ).filter(Objects::nonNull).collect(Collectors.joining(", "));

        if (!errors.isEmpty()) {
            throw ResourceModuleDefinitionSerializationException.from(
                definition.name(),
                definition.toString(),
                errors
            );
        }

        return config.pretty ? json.toString(4) : json.toString();
    }

    private static String serializeFieldV1(JSONObject json, String key, Object value) {
        try {
            json.put(key, value);
            return null;
        } catch (JSONException e) {
            return key;
        }
    }
}
