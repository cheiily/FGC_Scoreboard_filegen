package pl.cheily.filegen.LocalData.FileManagement.Meta.Match;

public enum MatchDataKey {
    // Players
    P1_NAME("p1_name", "Rushdown Enjoyer"),
    P1_TAG("p1_tag", "GMRZ"),
    P1_NATIONALITY("p1_nationality", "DE"),
    P1_PRONOUNS("p1_pronouns", "he/him"),
    P1_HANDLE("p1_handle", "@rushdown-enjoyer"),

    P2_NAME("p2_name", "Zoner Enthusiast"),
    P2_TAG("p2_tag", "PRJCTL"),
    P2_NATIONALITY("p2_nationality", "UK"),
    P2_PRONOUNS("p2_pronouns", "she/her"),
    P2_HANDLE("p2_handle", "@zoner_enthusiast.bsky.social"),

    // Round
    ROUND_LABEL("round_label", "Winners Round 1"),
    P1_SCORE("p1_score", "1"),
    P2_SCORE("p2_score", "0"),
    IS_GF("is_gf", "true"),
    IS_GF_RESET("gf_reset", "false"),
    IS_GF_P1_WINNER("gf_p1_winner", "true"),
    IS_GF_P2_WINNER("gf_p2_winner", "false"),


    // Commentators
    COMM_TAG_1("commentator_tag_1", "CMMS"),
    COMM_NAME_1("commentator_name_1", "Colorful Mike"),
    COMM_PRONOUNS_1("commentator_pronouns_1", "they/them"),
    COMM_NATIONALITY_1("commentator_nationallity_1", "CH"),
    COMM_HANDLE_1("commentator_handle_1", "@rainbow-mic"),

    COMM_TAG_2("commentator_tag_2", "CMMS"),
    COMM_NAME_2("commentator_name_2", "PBP Mick"),
    COMM_PRONOUNS_2("commentator_pronouns_2", "he/him"),
    COMM_NATIONALITY_2("commentator_nationallity_2", "UA"),
    COMM_HANDLE_2("commentator_handle_2", "@playbyplay-mick.bsky.social"),

    COMM_TAG_3("commentator_tag_3", "CMMS"),
    COMM_NAME_3("commentator_name_3", "Sajam"),
    COMM_PRONOUNS_3("commentator_pronouns_3", "he/him"),
    COMM_NATIONALITY_3("commentator_nationallity_3", "US"),
    COMM_HANDLE_3("commentator_handle_3", "@him");

    private final String key;
    private final String sampleValue;

    MatchDataKey(String key, String sampleValue) {
        this.key = key;
        this.sampleValue = sampleValue;
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

    public String getSampleValue() {
        return sampleValue;
    }

    public boolean isNameField() {
        return switch(this) {
            case P1_NAME, P2_NAME, COMM_NAME_1, COMM_NAME_2, COMM_NAME_3 -> true;
            default -> false;
        };
    }

    public boolean isTagField() {
        return switch (this) {
            case P1_TAG, P2_TAG, COMM_TAG_1, COMM_TAG_2, COMM_TAG_3 -> true;
            default -> false;
        };
    }

    public boolean isNationalityField() {
            return switch (this) {
                case P1_NATIONALITY, P2_NATIONALITY, COMM_NATIONALITY_1, COMM_NATIONALITY_2, COMM_NATIONALITY_3 -> true;
                default -> false;
            };
        }

        public boolean isPronounsField() {
            return switch (this) {
                case P1_PRONOUNS, P2_PRONOUNS, COMM_PRONOUNS_1, COMM_PRONOUNS_2, COMM_PRONOUNS_3 -> true;
                default -> false;
            };
        }

        public boolean isHandleField() {
            return switch (this) {
                case P1_HANDLE, P2_HANDLE, COMM_HANDLE_1, COMM_HANDLE_2, COMM_HANDLE_3 -> true;
                default -> false;
            };
        }
}
