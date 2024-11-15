package pl.cheily.filegen.LocalData.FileManagement.Output.Formatting;

import pl.cheily.filegen.Configuration.AppConfig;
import pl.cheily.filegen.LocalData.FileManagement.Meta.Match.MatchDataKey;
import pl.cheily.filegen.LocalData.ResourcePath;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static pl.cheily.filegen.ScoreboardApplication.dataManager;

// Provides some data about the formatting function, to be displayed in the UI.
// Inputs & outputs are defined by the keys, replace this for plain strings if you're modding this.
public class FormattingUnit {
    public boolean enabled;
    // lists all required sources
    public final List<MatchDataKey> inputKeys;
    // lists possible destinations
    public final List<ResourcePath> destinations;
    public final String sampleOutput;

    // input array must be in the same order as the keys
    public final Function<String[], String> format;

    public static String oneToOnePass(String... params) {
        return params[0];
    }
    public static String findFlagFile(String... params) {
        // todo adjust this for bundled flags
        if (params[0].isEmpty()) return params[0];
        try {
            Path flag = Files.find(Path.of(dataManager.flagsDir + "/"), 2, (path, basicFileAttributes) -> path.toFile().getName().startsWith(params[0]), FileVisitOption.FOLLOW_LINKS).findFirst().get();
            return flag.getFileName().toString();
        } catch (IOException | NoSuchElementException e) {
            return params[0] + AppConfig.FLAG_EXTENSION();
        }
    }

    public FormattingUnit(List<MatchDataKey> inputKeys, List<ResourcePath> destinations, String sampleOutput, Function<String[], String> format) {
        this.enabled = true;
        this.inputKeys = inputKeys;
        this.destinations = destinations;
        this.sampleOutput = sampleOutput;
        this.format = format;
    }

}
