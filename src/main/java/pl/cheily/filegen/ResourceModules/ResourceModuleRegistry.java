package pl.cheily.filegen.ResourceModules;

import javafx.application.Platform;
import org.json.JSONObject;
import pl.cheily.filegen.LocalData.DataManagerNotInitializedException;
import pl.cheily.filegen.LocalData.LocalResourcePath;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResourceModuleRegistry {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ResourceModuleRegistry.class);

    public List<ResourceModule> modules;

    public ResourceModuleRegistry() {
        modules = new ArrayList<>();
    }

    public void register(ResourceModule module) {
        modules.add(module);
    }

    public void unregister(ResourceModule module) {
        modules.remove(module);
    }

    public void clearRemote() {
        modules.removeIf(module -> !module.isDownloaded());
    }


    public void initialize() {
        loadInstallations();
        loadRemote();
        autoinstall();
    }

    public void queueInitialize() {
        Platform.runLater(this::initialize);
    }

    public void loadInstallations() {
        try {
            var installedModules = Files.find(LocalResourcePath.RESOURCE_MODULE_INSTALL.toPath(), 5,
                (path, basicFileAttributes) ->
                        basicFileAttributes.isRegularFile() && path.toString().endsWith(ResourceModuleDefinition.EXTENSION)
                ).map(path -> {
                    String content;
                    try {
                        content = new String(Files.readAllBytes(path));
                    } catch (IOException e) {
                        return null;
                    }
                    JSONObject json = new JSONObject(content);
                    return ResourceModuleDefinition.fromJson(json);
                }).filter(Objects::nonNull)
                    .map(ResourceModule::installed)
                    .toList();

            modules.addAll(installedModules);
        } catch (IOException | DataManagerNotInitializedException e) {
            logger.error("Failed to load installed resource modules.", e);
        }
    }

    public void loadRemote() {
        List<GitHubFileDetails> files = ResourceModuleDefinitionFetcher.fetchRemoteFiles();
        for (GitHubFileDetails file : files) {
            ResourceModuleDefinition definition = ResourceModuleDefinitionFetcher.fetchResourceModuleDefinition(file);
            if (definition != null) {
                var module = ResourceModule.remote(definition);
                var isAlreadyInstalled = modules.stream().anyMatch(
                        installed -> installed.definition.equals(module.definition)
                );
                if (!isAlreadyInstalled)
                    register(module);
            }
        }
    }

    public void autoinstall() {
        for (ResourceModule module : modules) {
            if (!module.isDownloaded() && module.definition.autoinstall()) {
                ResourceModuleFetcher.fetchModule(module.definition);
            }
        }
    }
}
