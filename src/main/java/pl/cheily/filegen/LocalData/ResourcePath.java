package pl.cheily.filegen.LocalData;

import pl.cheily.filegen.Utils.Util;

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
    PLAYER_LIST("fgen/player_list.ini"),
    COMMS_LIST("fgen/comms_list.ini"),
    METADATA("fgen/metadata.ini"),
    CONFIG("fgen/config.ini");


    private final String fileName;

    ResourcePath(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return fileName;
    }

    public Path toPath() {
        return Path.of(Util.targetDir + "/" + this.fileName);
    }

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
}
