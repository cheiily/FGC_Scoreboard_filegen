package pl.cheily.filegen.LocalData;

import javafx.beans.property.*;

import java.util.UUID;

/**
 * Represents a player with all of their related local data
 */
// todo socials handle
public class Player {
    private static final Player _EMPTY = getInvalid();

    private final UUID uuid;
    private final ReadOnlyStringProperty uuidStr;
    private final StringProperty tag;
    private final StringProperty name;
    private final StringProperty nationality;
    private final StringProperty pronouns;
    private final StringProperty snsHandle;
    private final LongProperty remoteId;
    private final IntegerProperty remoteSeed;
    private final StringProperty remoteName;
    private final StringProperty remoteIconUrl;

    public Player(String tag, String name, String nationality, String pronouns, String snsHandle, long remoteId, int remoteSeed, String remoteName, String iconUrl) {
        this(UUID.randomUUID(), tag, name, nationality, pronouns, snsHandle, remoteId, remoteSeed, remoteName, iconUrl);
    }

    private Player(UUID uuid, String tag, String name, String nationality, String pronouns, String snsHandle, long remoteId, int remoteSeed, String remoteName, String iconUrl) {
        this.uuid = uuid;
        this.uuidStr = new SimpleStringProperty(uuid.toString());
        this.tag = new SimpleStringProperty(tag);
        this.name = new SimpleStringProperty(name);
        this.nationality = new SimpleStringProperty(nationality);
        this.pronouns = new SimpleStringProperty(pronouns);
        this.snsHandle = new SimpleStringProperty(snsHandle);
        this.remoteId = new SimpleLongProperty(remoteId);
        this.remoteSeed = new SimpleIntegerProperty(remoteSeed);
        this.remoteName = new SimpleStringProperty(remoteName);
        this.remoteIconUrl = new SimpleStringProperty(iconUrl);
    }

    public static Player deserialize(String uuid, String tag, String name, String nationality, String snsHandle, String pronouns, long remoteId, int remoteSeed, String remoteName, String iconUrl) {
        return new Player(UUID.fromString(uuid), tag, name, nationality, pronouns, snsHandle, remoteId, remoteSeed, remoteName, iconUrl);
    }

    public Player(String tag, String name, String nationality, String pronouns, String snsHandle) {
        this(tag, name, nationality, pronouns, snsHandle, 0, 0, null, "");
    }

    public static Player getInvalid() {
        return new Player(new UUID(0, 0), "", "", "", "", "", 0, 0, "", "");
    }
    public static Player newEmpty() {
        return new Player("", "", "", "", "");
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUuidStr() {
        return uuidStr.get();
    }

    public ReadOnlyStringProperty uuidStrProperty() {
        return uuidStr;
    }

    public String getTag() {
        return tag.get();
    }

    public void setTag(String tag) {
        this.tag.set(tag);
    }

    public StringProperty tagProperty() {
        return tag;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getNationality() {
        return nationality.get();
    }

    public void setNationality(String nationality) {
        this.nationality.set(nationality);
    }

    public StringProperty nationalityProperty() {
        return nationality;
    }

    public String getPronouns() {
        return pronouns.get();
    }

    public void setPronouns(String pronouns) {
        this.pronouns.set(pronouns);
    }

    public StringProperty pronounsProperty() {
        return pronouns;
    }

    public long getRemoteId() {
        return remoteId.get();
    }

    public void setRemoteId(long remoteId) {
        this.remoteId.set(remoteId);
    }

    public LongProperty remoteIdProperty() {
        return remoteId;
    }

    public int getRemoteSeed() {
        return remoteSeed.get();
    }

    public void setRemoteSeed(int seed) {
        this.remoteSeed.set(seed);
    }

    public IntegerProperty remoteSeedProperty() {
        return remoteSeed;
    }

    public String getRemoteName() {
        return remoteName.get();
    }

    public void setRemoteName(String remoteName) {
        this.remoteName.set(remoteName);
    }

    public StringProperty remoteNameProperty() {
        return remoteName;
    }

    public String getRemoteIconUrl() {
        return remoteIconUrl.get();
    }

    public void setRemoteIconUrl(String iconUrl) {
        this.remoteIconUrl.set(iconUrl);
    }

    public StringProperty remoteIconUrlProperty() {
        return remoteIconUrl;
    }



    @Override
    public String toString() {
        return "Player{" +
                "uuid=" + uuid +
                ", tag='" + tag.get() + '\'' +
                ", name='" + name.get() + '\'' +
                ", nationality='" + nationality.get() + '\'' +
                ", pronouns='" + pronouns.get() + '\'' +
                ", remoteId=" + remoteId.get() +
                ", remoteSeed=" + remoteSeed.get() +
                ", remoteName='" + remoteName.get() + '\'' +
                ", remoteIconUrl='" + remoteIconUrl.get() + '\'' +
                '}';
    }

    public boolean isEmpty() {
        return this == _EMPTY || (
                // dodge the uuid check
                this.tag == _EMPTY.tag
                && this.name == _EMPTY.name
                && this.pronouns == _EMPTY.pronouns
                && this.nationality == _EMPTY.nationality
                && this.remoteId == _EMPTY.remoteId
                && this.remoteSeed == _EMPTY.remoteSeed
                && this.remoteName == _EMPTY.remoteName
                && this.remoteIconUrl == _EMPTY.remoteIconUrl
        );
    }
}
