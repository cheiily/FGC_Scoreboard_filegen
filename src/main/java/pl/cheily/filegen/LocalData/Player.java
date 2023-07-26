package pl.cheily.filegen.LocalData;

import javafx.beans.property.*;

/**
 * Represents a player with all of their related local data
 */
public class Player {
    private static final Player _EMPTY = empty();
    private static final Player _EMPTY_NULL = empty_null();
    private final StringProperty tag;
    private final StringProperty name;
    private final StringProperty nationality;
    private final IntegerProperty seed;
    private final StringProperty iconUrl;
    private final BooleanProperty checkedIn;

    public Player(String tag, String name, String nationality, int seed, String iconUrl, boolean checkedIn) {
        this.tag = new SimpleStringProperty(tag);
        this.name = new SimpleStringProperty(name);
        this.nationality = new SimpleStringProperty(nationality);
        this.seed = new SimpleIntegerProperty(seed);
        this.iconUrl = new SimpleStringProperty(iconUrl);
        this.checkedIn = new SimpleBooleanProperty(checkedIn);
    }

    public Player(String tag, String name, String nationality) {
        this(tag, name, nationality, 0, null, false);
    }

    public static Player empty() {
        return new Player("", "", "");
    }

    public static Player empty_null() {
        return new Player(null, null, null);
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

    public int getSeed() {
        return seed.get();
    }

    public void setSeed(int seed) {
        this.seed.set(seed);
    }

    public IntegerProperty seedProperty() {
        return seed;
    }

    public String getIconUrl() {
        return iconUrl.get();
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl.set(iconUrl);
    }

    public StringProperty iconUrlProperty() {
        return iconUrl;
    }

    public boolean isCheckedIn() {
        return checkedIn.get();
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn.set(checkedIn);
    }

    public BooleanProperty checkedInProperty() {
        return checkedIn;
    }

    @Override
    public String toString() {
        return "Player{" +
                "tag='" + tag.get() + '\'' +
                ", name='" + name.get() + '\'' +
                ", nationality='" + nationality.get() + '\'' +
                ", seed=" + seed.get() +
                ", iconUrl=" + iconUrl.get() +
                ", checkedIn=" + checkedIn.get() +
                '}';
    }

    public boolean isEmpty() {
        return this == _EMPTY || this == _EMPTY_NULL;
    }
}
