package pl.cheily.filegen.LocalData;

import pl.cheily.filegen.ScoreboardApplication;

import java.nio.file.Path;

/**
 * Set of predefined resource paths, to be loaded onto the overlay and auto-refreshed.
 */
public enum ResourcePath {

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

    DIV_ROUND("html/round"),

    //lists todo remove in favor of UI loading csvs
    CUSTOM_PLAYER_LIST("lists/player_list.csv"),
    CUSTOM_COMMS_LIST("lists/comms_list.csv"),
    CUSTOM_ROUND_LIST("lists/round_list.csv"),

    //meta
    PLAYER_LIST("meta/player_list.ini"),
    COMMS_LIST("meta/comms_list.ini"),
    ROUND_LIST("meta/round_list.ini"),
    METADATA("meta/metadata.ini"),
    CONFIG("meta/config.ini");


    private final String fileName;

    ResourcePath(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return just the contained value, NOT an absolute path
     */
    @Override
    public String toString() {
        return fileName;
    }

    /**
     * @return valid absolute {@link Path}
     * @throws DataManagerNotInitializedException if {@link DataManager#isInitialized()} returns false
     */
    public Path toPath() throws DataManagerNotInitializedException {
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
    public static ResourcePath of(String path) {
        String[] arr;
        for (ResourcePath rPath : ResourcePath.values()) {
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

    // todo .format() -> .ini(), .csv(), .db() | .folder() -> .local() .appdata()
    // todo load config on boot from appdata
}
