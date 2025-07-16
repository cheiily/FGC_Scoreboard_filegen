package pl.cheily.filegen.ResourceModules;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cheily.filegen.LocalData.DataManagerNotInitializedException;
import pl.cheily.filegen.LocalData.LocalResourcePath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResourceModuleDefinitionFetcher {
    private static final Logger logger = LoggerFactory.getLogger(ResourceModuleDefinitionFetcher.class);

    public static List<ResourceModuleDefinition> fetchResourceModuleDefinitions() {
        return fetchRemoteFiles()
                .stream().filter(Objects::nonNull)
                .map(ResourceModuleDefinitionFetcher::fetchResourceModuleDefinition)
                .filter(Objects::nonNull)
                .toList();

    }

    public static ResourceModuleDefinition fetchResourceModuleDefinition(GitHubFileDetails file) {
        Path path;
        try {
            path = DownloadUtils.downloadTempFile(file.downloadUrl(), LocalResourcePath.RESOURCE_MODULE_DEFINITION_TEMPS.toPath());
        } catch (DataManagerNotInitializedException e) { // not happening
            logger.error("Data manager is not initialized, cannot fetch resource module definition.", e);
            return null;
        }

        if (path == null) {
            return null;
        }

        try {
            var json = new JSONObject(new String(Files.readAllBytes(path)));
            return ResourceModuleDefinition.fromJson(json);
        } catch (IOException e) {
            logger.error("Failed to read resource module definition file: {}", path, e);
            return null;
        }
    }

    public static List<GitHubFileDetails> fetchRemoteFiles() {
        List<GitHubFileDetails> files = extractFilesRecursively("");
        return files.stream().filter(Objects::nonNull).toList();
    }

    private static List<GitHubFileDetails> extractFilesRecursively(String path) {
        ArrayList<GitHubFileDetails> allFiles = new ArrayList<>(ResourceModuleRequests.githubContents(path));
        ArrayList<GitHubFileDetails> files = new ArrayList<>(allFiles.stream()
                .filter(file -> file.type().equals(GitHubFileDetails.TYPE_FILE))
                .filter(file -> file.path().endsWith(ResourceModuleDefinition.EXTENSION))
                .toList());
        List<GitHubFileDetails> directories = allFiles.stream()
                .filter(file -> file.type().equals(GitHubFileDetails.TYPE_DIR))
                .toList();

        for (GitHubFileDetails dir : directories) {
            List<GitHubFileDetails> subFiles = extractFilesRecursively(dir.path());
            files.addAll(subFiles);
        }

        return files;
    }


}
