package pl.cheily.filegen.LocalData.FileManagement.Meta.RoundSet;

import pl.cheily.filegen.LocalData.FileManagement.Meta.DAO;

import java.util.Comparator;
import java.util.List;

public interface RoundLabelDAO extends DAO<String> {
    public List<String> getAllSorted(Comparator<String> sorter);
    public List<String> getAllSorted();
    public List<String> getDefault();
    @Override
    public void setAll(List<String> labels, List<String> unused);
    public void setAll(List<String> labels);
}
