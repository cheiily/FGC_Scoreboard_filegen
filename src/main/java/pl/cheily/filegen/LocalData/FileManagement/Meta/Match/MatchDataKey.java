package pl.cheily.filegen.LocalData.FileManagement.Meta.Match;

public enum MatchDataKey {
    // Players
    P1_NAME("p1_name"),
    P1_TAG("p1_tag"),
    P1_NATIONALITY("p1_nationality"),
    P1_PRONOUNS("p1_pronouns"),
    P1_HANDLE("p1_handle"),

    P2_NAME("p2_name"),
    P2_TAG("p2_tag"),
    P2_NATIONALITY("p2_nationality"),
    P2_PRONOUNS("p2_pronouns"),
    P2_HANDLE("p2_handle"),

    // Round
    ROUND_LABEL("round_label"),
    P1_SCORE("p1_score"),
    P2_SCORE("p2_score"),
    IS_GF("is_gf"),
    IS_GF_RESET("gf_reset"),
    IS_GF_P1_WINNER("gf_p1_winner"),
    IS_GF_P2_WINNER("gf_p2_winner"),


    // Commentators
    COMM_TAG_1("commentator_tag_1"),
    COMM_NAME_1("commentator_name_1"),
    COMM_PRONOUNS_1("commentator_pronouns_1"),
    COMM_NATIONALITY_1("commentator_nationallity_1"),
    COMM_HANDLE_1("commentator_handle_1"),

    COMM_TAG_2("commentator_tag_2"),
    COMM_NAME_2("commentator_name_2"),
    COMM_PRONOUNS_2("commentator_pronouns_2"),
    COMM_NATIONALITY_2("commentator_nationallity_2"),
    COMM_HANDLE_2("commentator_handle_2"),

    COMM_TAG_3("commentator_tag_3"),
    COMM_NAME_3("commentator_name_3"),
    COMM_PRONOUNS_3("commentator_pronouns_3"),
    COMM_NATIONALITY_3("commentator_nationallity_3"),
    COMM_HANDLE_3("commentator_handle_3");

    private final String key;

    MatchDataKey(String key) {
        this.key = key;
    }
    @Override
    public String toString() {
        return key;
    }
    public static MatchDataKey fromString(String key) {
        for (MatchDataKey matchDataKey : MatchDataKey.values()) {
            if (matchDataKey.key.equalsIgnoreCase(key)) {
                return matchDataKey;
            }
        }
        throw new IllegalArgumentException(
                "No enum constant with matching key value " + MatchDataKey.class.getCanonicalName() + "(" + key + ")");
    }
}
