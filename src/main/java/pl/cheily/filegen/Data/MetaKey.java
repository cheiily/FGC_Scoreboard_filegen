package pl.cheily.filegen.Data;

/**
 * Set of predefined section and key names, to facilitate operating on .ini files.
 */
public enum MetaKey {
    SEC_P1("P1"), SEC_P2("P2"),
    KEY_TAG("tag"),
    KEY_NAME("name"),
    KEY_NATION("nat"),
    KEY_SEED("seed"),
    KEY_ICON("icon_url"),
    KEY_CHK_IN("checked_in"),

    SEC_ROUND("ROUND"),
    KEY_ROUND_LABEL("label"),
    KEY_SCORE_1("score_p1"),
    KEY_SCORE_2("score_p2"),
    KEY_GF("GF"),
    KEY_GF_RESET("reset"),
    KEY_GF_W1("p1_w"),

    SEC_COMMS("COMMENTARY"),
    KEY_HOST("host"),
    KEY_COMM_1("commentator_1"),
    KEY_COMM_2("commentator_2");


    private final String key;

    MetaKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}
