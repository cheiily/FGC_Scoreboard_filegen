package pl.cheily.filegen.LocalData;

import net.harawata.appdirs.AppDirsFactory;
import pl.cheily.filegen.ScoreboardApplication;

import java.nio.file.Path;

/**
 * Set of predefined resource paths, to be loaded onto the overlay and auto-refreshed.
 */
public enum LocalResourcePath {

    //output
    //todo rename to FILE_*
    ROUND("round.txt"),
    P1_SCORE("p1_score.txt"),
    P2_SCORE("p2_score.txt"),


    P1_NAME("p1_name.txt"),
    P1_FLAG("p1_flag.png"),
    P1_PRONOUNS("p1_pronouns.txt"),
    P1_HANDLE("p1_handle.txt"),

    P2_NAME("p2_name.txt"),
    P2_FLAG("p2_flag.png"),
    P2_PRONOUNS("p2_pronouns.txt"),
    P2_HANDLE("p2_handle.txt"),

    C1_NAME("comm1_name.txt"),
    C1_FLAG("comm1_flag.png"),
    C1_PRONOUNS("comm1_pronouns.txt"),
    C1_HANDLE("comm1_handle.txt"),

    C2_NAME("comm2_name.txt"),
    C2_FLAG("comm2_flag.png"),
    C2_PRONOUNS("comm2_pronouns.txt"),
    C2_HANDLE("comm2_handle.txt"),

    C3_NAME("comm3_name.txt"),
    C3_FLAG("comm3_flag.png"),
    C3_PRONOUNS("comm3_pronouns.txt"),
    C3_HANDLE("comm3_handle.txt"),

    // extras for the FullySeparatingOutputFormatter
    P1_TAG("p1_tag.txt"),
    P1_LOSER_INDICATOR("p1_loser_indicator.txt"),
    P2_TAG("p2_tag.txt"),
    P2_LOSER_INDICATOR("p2_loser_indicator.txt"),
    C1_TAG("comm1_tag.txt"),
    C2_TAG("comm2_tag.txt"),
    C3_TAG("comm3_tag.txt"),


    DIV_ROUND("html/round"),

    //lists todo remove in favor of UI loading csvs
    CUSTOM_PLAYER_LIST("lists/player_list.csv"),
    CUSTOM_COMMS_LIST("lists/comms_list.csv"),
    CUSTOM_ROUND_LIST("lists/round_list.csv"),

    //meta
    PLAYER_LIST("meta/player_list.ini"),
    COMMS_LIST("meta/comms_list.ini"),
    ROUND_LIST("meta/round_list.ini"),
    MATCH_DATA("meta/match_data.ini"), // formerly METADATA(meta/metadata.ini)
    CONFIG("meta/config.ini"),
    WRITER_CONFIG("meta/writer_config.ini"),

    SHARED_NOTIFICATION_CACHE("<persistent>/notification_cache.ini"),
    REPEATING_NOTIFICATION_MEMORY("<persistent>/notification_memory.ini"),
    RESOURCE_MODULE_DEFINITION_TEMPS("<persistent>/modules/definition_temps"),
    RESOURCE_MODULE_INSTALL("<persistent>/modules");


    private final String fileName;

    LocalResourcePath(String fileName) {
        this.fileName = fileName;
    }

    private static int _persistentDirOffset = 13;
    private static String _persistentDataDir = null;
    public static String persistentDataDir() {
        if ( _persistentDataDir == null ) {
            _persistentDataDir = Path.of(AppDirsFactory.getInstance().getUserDataDir(
                    "SimpleScoreboardController",
                    "_",
                    "_cheily"
            ) + "/").toString();
        }
        return _persistentDataDir;
    }

    /**
     * @return just the contained value, NOT an absolute path
     */
    @Override
    public String toString() {
        return fileName;
    }
    public static LocalResourcePath fromString(String path) {
        for (LocalResourcePath rPath : LocalResourcePath.values()) {
            if ( rPath.fileName.equals(path) ) return rPath;
        }
        return null;
    }

    private Path persistentPath() {
        return Path.of(persistentDataDir() + fileName.substring(_persistentDirOffset));
    }

    /**
     * @return valid absolute {@link Path}
     * @throws DataManagerNotInitializedException if {@link DataManager#isInitialized()} returns false
     */
    public Path toPath() throws DataManagerNotInitializedException {
        if ( this.isPersistentDataPath() )
            return this.persistentPath();

        // todo if key starts with DIV_, throw new exception
        if ( !ScoreboardApplication.dataManager.isInitialized() ) throw new DataManagerNotInitializedException();

        return Path.of(ScoreboardApplication.dataManager.targetDir + "/" + this.fileName);
    }

    /**
     * Finds the enum with the desired value.
     *
     * @param path either full path as contained by the enum, or just its last segment (i.e. filename)
     * @return null if not found, valid enum otherwise
     */
    public static LocalResourcePath of(String path) {
        String[] arr;
        for (LocalResourcePath rPath : LocalResourcePath.values()) {
            if ( rPath.fileName.equals(path) ) return rPath;
            if ( (arr = rPath.fileName.split("/")).length > 1
                    && arr[ arr.length - 1 ].equals(path)
            ) return rPath;

        }
        return null;
    }

    public boolean isHTML() {
        return this.fileName.startsWith("html/");
    }

    public boolean isOutputFile() {
        return !this.fileName.contains("/");
    }

    public boolean isPersistentDataPath() {
        return this.fileName.startsWith("<persistent>/");
    }

    // todo .format() -> .ini(), .csv(), .db() | .folder() -> .local() .appdata()
    // todo load config on boot from appdata
}
