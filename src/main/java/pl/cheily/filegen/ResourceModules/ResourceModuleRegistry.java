package pl.cheily.filegen.ResourceModules;

import javafx.application.Platform;
import org.json.JSONObject;
import pl.cheily.filegen.LocalData.DataManagerNotInitializedException;
import pl.cheily.filegen.LocalData.LocalResourcePath;
import pl.cheily.filegen.ResourceModules.Events.ResourceModuleEventPipeline;
import pl.cheily.filegen.ResourceModules.Events.ResourceModuleEventType;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResourceModuleRegistry {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ResourceModuleRegistry.class);

    public ResourceModuleEventPipeline eventPipeline;
    public List<ResourceModule> modules;

    public ResourceModuleRegistry() {
        eventPipeline = new ResourceModuleEventPipeline();
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

    public void initializeAsync() {
        loadInstallationsAsync();
        loadRemoteAsync();
        doAutoInstall();
    }

    public void initialize() {
        loadInstallations();
        loadRemote();
        doAutoInstall();
    }

    public void queueInitialize() {
        Platform.runLater(this::initialize);
    }

    public void loadInstallationsAsync() {
        new Thread(this::loadInstallations).start();
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
                    .map(ResourceModule::scannedLocal)
                    .toList();

            modules.addAll(installedModules);
            eventPipeline.push(ResourceModuleEventType.LOADED_INSTALLATIONS, null);
        } catch (IOException | DataManagerNotInitializedException e) {
            logger.error("Failed to load installed resource modules.", e);
        }
    }

    public void loadRemoteAsync() {
        new Thread(this::loadRemote).start();
    }

    public void loadRemote() {
        List<GitHubFileDetails> files = ResourceModuleDefinitionFetcher.fetchRemoteFiles();
        for (GitHubFileDetails file : files) {
            ResourceModuleDefinition definition = ResourceModuleDefinitionFetcher.fetchResourceModuleDefinition(file);
            if (definition != null) {
                var module = ResourceModule.scannedRemote(definition);
                var isAlreadyInstalled = modules.stream().anyMatch(
                        installed -> installed.definition.equals(module.definition)
                );
                if (!isAlreadyInstalled)
                    register(module);
            }
        }
        eventPipeline.push(ResourceModuleEventType.FETCHED_DEFINITIONS, null);
    }

    public void doAutoinstallAsync() {
        new Thread(this::doAutoInstall).start();
    }

    public void doAutoInstall() {
        for (ResourceModule module : modules) {
            if (!module.isDownloaded() && module.definition.autoinstall()) {
                ResourceModuleInstallationManager.downloadAndInstallModule(module.definition);
                eventPipeline.push(ResourceModuleEventType.DOWNLOADED_MODULE, module);
                if (module.isInstalled())
                    eventPipeline.push(ResourceModuleEventType.INSTALLED_MODULE, module);
            }
        }
    }


    public void downloadModuleAsync(ResourceModule module) {
        new Thread(() -> downloadModule(module)).start();
    }

    public void downloadModule(ResourceModule module) {
        if (module.isDownloaded()) {
            logger.warn("Resource module {} is already downloaded.", module.definition.installName());
            return;
        }
        var result = ResourceModuleInstallationManager.downloadAndInstallModule(module.definition);
        if (result != null) {
            module.setDownloaded(true);
            module.setInstalled(result.isInstalled());
            module.setEnabled(result.isEnabled());
        }
        eventPipeline.push(ResourceModuleEventType.DOWNLOADED_MODULE, module);
    }

    public void deleteModuleAsync(ResourceModule module) {
        new Thread(() -> deleteModule(module)).start();
    }

    public void deleteModule(ResourceModule module) {
        if (module.isEnabled())
            disableModule(module);
        if (module.isInstalled())
            uninstallModule(module);

        ResourceModuleInstallationManager.deleteModule(module);
        eventPipeline.push(ResourceModuleEventType.REMOVED_MODULE, module);
    }

    public void installModuleAsync(ResourceModule module) {
        new Thread(() -> installModule(module)).start();
    }

    public void installModule(ResourceModule module) {
        if (!module.isDownloaded()) {
            logger.warn("Resource module {} is not downloaded, cannot install.", module.definition.installName());
            return;
        }
        logger.info("Installing resource modules is a WIP feature, intended for JAR plugins.");
        module.setInstalled(true);
        eventPipeline.push(ResourceModuleEventType.INSTALLED_MODULE, module);
    }

    public void uninstallModuleAsync(ResourceModule module) {
        new Thread(() -> uninstallModule(module)).start();
    }

    public void uninstallModule(ResourceModule module) {
        logger.info("Uninstalling resource modules is a WIP feature, intended for JAR plugins.");
        module.setInstalled(false);
        eventPipeline.push(ResourceModuleEventType.UNINSTALLED_MODULE, module);
    }

    public void enableModule(ResourceModule module) {
        if (!module.isInstalled()) {
            logger.warn("Resource module {} is not installed, cannot enable.", module.definition.installName());
            return;
        }
        module.setEnabled(true);
        eventPipeline.push(ResourceModuleEventType.ENABLED_MODULE, module);
    }

    public void disableModule(ResourceModule module) {
        module.setEnabled(false);
        eventPipeline.push(ResourceModuleEventType.DISABLED_MODULE, module);
    }
}
