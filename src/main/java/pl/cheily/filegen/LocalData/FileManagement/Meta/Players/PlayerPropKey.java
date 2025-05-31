package pl.cheily.filegen.LocalData.FileManagement.Meta.Players;

public enum PlayerPropKey {
    //todo this is closely tied to Player's structure and DB layout, maybe put this in Player instead? idk
    ID("id"),
    NAME("name"),
    TAG("tag"),
    NATIONALITY("nationality"),
    PRONOUNS("pronouns"),
    SNS_HANDLE("sns_handle"),
    REMOTE_ID("remote_id"),
    REMOTE_NAME("remote_name"),
    REMOTE_SEED("remote_seed"),
    REMOTE_ICON_URL("remote_icon_url");


    final String key;
    PlayerPropKey(String key) {
        this.key = key;
    }
    @Override
    public String toString() {
        return key;
    }
}
