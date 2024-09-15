package pl.cheily.filegen.LocalData;

public enum DataEventProp {
    //todo change this back to have fields
    INIT,
    SAVE,
    CHANGED_CONFIG,
    CHANGED_PLAYER_LIST,
    CHANGED_COMMENTARY_LIST,
    CHANGED_OUTPUT_WRITERS,
    CHANGED_MATCH_DATA,
    CHANGED_ROUND_LABELS
    ;


    public static DataEventProp of(String property) {
        for (DataEventProp evtProp : DataEventProp.values()) {
            if (evtProp.name().equals(property))
                return evtProp;
        }
        return null;
    }
}
