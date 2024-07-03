package pl.cheily.filegen.Data.Structures.MatchData;

import javafx.beans.property.*;

public class RoundData {
    public final StringProperty label = new SimpleStringProperty();
    public final IntegerProperty scoreP1 = new SimpleIntegerProperty();
    public final IntegerProperty scoreP2 = new SimpleIntegerProperty();
    public final BooleanProperty isGF = new SimpleBooleanProperty();
    public final BooleanProperty isGFReset = new SimpleBooleanProperty();
    public final BooleanProperty isGFP1Winner = new SimpleBooleanProperty();
    public final BooleanProperty isGFP2Winner = new SimpleBooleanProperty();

    public RoundData(String label, int scoreP1, int scoreP2, boolean isGF, boolean isReset, boolean isP1Winner, boolean isP2Winner) {
        this.label.set(label);
        this.scoreP1.set(scoreP1);
        this.scoreP2.set(scoreP2);
        this.isGF.set(isGF);
        this.isGFReset.set(isReset);
        this.isGFP1Winner.set(isP1Winner);
        this.isGFP2Winner.set(isP2Winner);
    }


}
