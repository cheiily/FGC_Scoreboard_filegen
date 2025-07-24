package pl.cheily.filegen.ResourceModules.Definition;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cheily.filegen.LocalData.LocalResourcePath;
import pl.cheily.filegen.ResourceModules.Exceptions.ResourceModuleDefinitionParseException;
import pl.cheily.filegen.ResourceModules.Installation.DownloadUtils;
import pl.cheily.filegen.ResourceModules.Exceptions.ResourceModuleDownloadException;
import pl.cheily.filegen.ResourceModules.Installation.GitHubFileDetails;
import pl.cheily.filegen.ResourceModules.Installation.ResourceModuleRequests;

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
        Path path = null;
        try {
            path = DownloadUtils.downloadTempFile(file.downloadUrl(), LocalResourcePath.RESOURCE_MODULE_DEFINITION_TEMPS.toStaticPath());
        } catch (ResourceModuleDownloadException e) {
            logger.error(e.getMessage(), e);
            return null;
        }

        if (path == null) {
            return null;
        }

        String fileContent = "";
        try {
            fileContent = new String(Files.readAllBytes(path));
            return ResourceModuleDefinitionHandlerFactory.parse(fileContent);
        } catch (IOException e) {
            logger.error("Failed to read resource module definition file: {}", path, e);
            return null;
        } catch (ResourceModuleDefinitionParseException e) {
            logger.error(e.getMessage(), e);
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
