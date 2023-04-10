package pl.cheily.filegen.Utils;

/**
 * Set of predefined resource paths, to be loaded onto the overlay and auto-refreshed.
 */
public enum ResourcePath {
    ROUND("round.txt"),
    P1_NAME("p1_name.txt"),
    P1_FLAG("p1_flag.png"),
    P1_NATION("fgen/p1_nation.txt"),
    P1_SCORE("p1_score.txt"),
    P2_NAME("p2_name.txt"),
    P2_FLAG("p2_flag.png"),
    P2_NATION("fgen/p2_nation.txt"),
    P2_SCORE("p2_score.txt"),
    COMMS("comms.txt"),
    PLAYER_LIST("fgen/player_list.ini"),
    COMMS_LIST("fgen/comms_list.ini"),
    METADATA("fgen/metadata.ini");


    private final String fileName;

    ResourcePath(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return fileName;
    }
}
