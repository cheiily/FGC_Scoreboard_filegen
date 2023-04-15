package pl.cheily.filegen.LocalData;

import pl.cheily.filegen.ScoreboardApplication;

import java.nio.file.Path;

/**
 * Set of predefined resource paths, to be loaded onto the overlay and auto-refreshed.
 */
public enum ResourcePath {

    ROUND("round.txt"),
    P1_NAME("p1_name.txt"),
    P1_FLAG("p1_flag.png"),
    P1_SCORE("p1_score.txt"),
    P2_NAME("p2_name.txt"),
    P2_FLAG("p2_flag.png"),
    P2_SCORE("p2_score.txt"),
    COMMS("comms.txt"),
    CUSTOM_PLAYER_LIST("lists/player_list.csv"),
    CUSTOM_COMMS_LIST("lists/comms_list.csv"),
    PLAYER_LIST("meta/player_list.ini"),
    COMMS_LIST("meta/comms_list.csv"),
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
     */
    public Path toPath() {
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

    /**
     * @return true if the resource is an overlay resource
     */
    public boolean isOutputResource() {
        return !this.fileName.startsWith("lists")
                && !this.fileName.startsWith("meta");
    }
}
