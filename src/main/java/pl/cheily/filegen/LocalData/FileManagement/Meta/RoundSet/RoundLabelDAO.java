package pl.cheily.filegen.LocalData.FileManagement.Meta.RoundSet;

import pl.cheily.filegen.LocalData.FileManagement.Meta.DAO;

import java.util.Comparator;
import java.util.List;

public interface RoundLabelDAO extends DAO<String> {
    public List<String> getAllSorted(Comparator<String> sorter);
    public List<String> getAllSorted();
    @Override
    public void setAll(List<String> labels, List<String> unused);
    public void setAll(List<String> labels);

    public static List<String> getDefault() {
        return List.of(
                //w rounds
                "Winners' R1", "Winners' R2", "Winners' R3", "Winners' R4",
                //l rounds
                "Losers' R1", "Losers' R2", "Losers' R3", "Losers' R4",
                //w top 8
                "Winners' Semis", "Winners' Finals",
                //l top 8
                "Losers' Eights", "Losers' Quarters", "Losers' Semis", "Losers' Finals",
                //gf
                "Grand Finals",
                //Extra
                "Top 8", "Winners' top 8", "Losers' top 8", "Losers' top 6", "Losers' top 4",
                "Winners' Eights", "Winners' Quarters", "Pools"
        );
    };
}
