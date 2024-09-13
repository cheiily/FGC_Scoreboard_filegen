package pl.cheily.filegen.LocalData.FileManagement.Meta.Match;

public enum MatchDataKey {
    P1_NAME("p1_name"),
    P1_TAG("p1_tag"),
    P1_NATIONALITY("p1_nationality"),
    P1_PRONOUNS("p1_pronouns"),

    P2_NAME("p2_name"),
    P2_TAG("p2_tag"),
    P2_NATIONALITY("p2_nationality"),
    P2_PRONOUNS("p2_pronouns"),

    ROUND_LABEL("round_label"),
    P1_SCORE("p1_score"),
    P2_SCORE("p2_score"),
    IS_GF("is_gf"),
    GF_RESET("gf_reset"),
    GF_P1_WINNER("gf_p1_winner"),
    GF_P2_WINNER("gf_p2_winner"),

    COMM_TAG_TEMPLATE("commentator_tag_"),
    COMM_NAME_TEMPLATE("commentator_name_"),
    COMM_PRONOUNS_TEMPLATE("commentator_pronouns_"),
    COMM_NATIONALITY_TEMPLATE("commentator_nationallity_");

    private final String key;

    MatchDataKey(String key) {
        this.key = key;
    }
    @Override
    public String toString() {
        return key;
    }
}
