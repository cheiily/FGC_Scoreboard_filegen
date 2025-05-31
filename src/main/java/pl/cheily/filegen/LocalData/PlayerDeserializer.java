package pl.cheily.filegen.LocalData;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;

import java.util.UUID;

public class PlayerDeserializer {
    private String uuid;
    private String tag;
    private String name;
    private String nationality;
    private String pronouns;
    private String snsHandle;
    private long remoteId;
    private int remoteSeed;
    private String remoteName;
    private String remoteIconUrl;

    // make builder methods
    public static PlayerDeserializer fromUuid(String uuid) {
        PlayerDeserializer player = new PlayerDeserializer();
        player.uuid = uuid;
        return player;
    }

    public PlayerDeserializer withTag(String tag) {
        this.tag = tag;
        return this;
    }

    public PlayerDeserializer withName(String name) {
        this.name = name;
        return this;
    }

    public PlayerDeserializer withNationality(String nationality) {
        this.nationality = nationality;
        return this;
    }

    public PlayerDeserializer withPronouns(String pronouns) {
        this.pronouns = pronouns;
        return this;
    }

    public PlayerDeserializer withSnsHandle(String snsHandle) {
        this.snsHandle = snsHandle;
        return this;
    }

    public PlayerDeserializer withRemoteId(long remoteId) {
        this.remoteId = remoteId;
        return this;
    }

    public PlayerDeserializer withRemoteSeed(int remoteSeed) {
        this.remoteSeed = remoteSeed;
        return this;
    }

    public PlayerDeserializer withRemoteName(String remoteName) {
        this.remoteName = remoteName;
        return this;
    }

    public PlayerDeserializer withRemoteIconUrl(String remoteIconUrl) {
        this.remoteIconUrl = remoteIconUrl;
        return this;
    }

    public Player deserialize() {
        return Player.deserialize(
                uuid,
                tag,
                name,
                nationality,
                snsHandle,
                pronouns,
                remoteId,
                remoteSeed,
                remoteName,
                remoteIconUrl
        );
    }
}
