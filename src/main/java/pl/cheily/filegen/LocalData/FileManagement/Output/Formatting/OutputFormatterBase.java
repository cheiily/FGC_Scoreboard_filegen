package pl.cheily.filegen.LocalData.FileManagement.Output.Formatting;

import pl.cheily.filegen.Configuration.AppConfig;
import pl.cheily.filegen.Configuration.PropKey;
import pl.cheily.filegen.LocalData.FileManagement.Meta.Match.MatchDataKey;
import pl.cheily.filegen.LocalData.ResourcePath;
import pl.cheily.filegen.ScoreboardApplication;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class OutputFormatterBase implements OutputFormatter {

    protected String name;
    protected ArrayList<FormattingUnit> units;
    protected PropertyChangeListener listener;

    protected OutputFormatterBase(String name, List<FormattingUnit> units) {
        this.name = name;
        this.units = new ArrayList<>(units);
        listener = evt -> {
            var fnd = units.stream().filter(unit -> {
                for (var input : unit.inputKeys)
                    if (input.toString().startsWith("commentator_") && input.toString().endsWith("_3"))
                        return true;
                return false;
            }).toList();
            fnd.forEach(unit -> unit.enabled = (boolean) evt.getNewValue());
        };
        AppConfig.subscribe(PropKey.WRITE_COMM_3, listener);
    }

    @Override
    public String getName() {
        return "Default formatter for file-based output";
    }

    @Override
    public List<FormattingUnit> getFormats() {
        return units;
    }

    @Override
    public String getFormatted(ResourcePath resource) {
        var retunit = units.stream().filter(unit -> unit.enabled && unit.destination == resource).findFirst();
        if (retunit.isPresent()) {
            var unit = retunit.get();
            String[] params = new String[unit.inputKeys.size()];
            for (int i = 0; i < unit.inputKeys.size(); i++) {
                params[i] = ScoreboardApplication.dataManager.matchDAO.get(unit.inputKeys.get(i));
            }
            return unit.format.apply(params);
        } else {
            return "";
        }
    }

    @Override
    public Map<ResourcePath, String> getAllFormatted() {
        Map<ResourcePath, String> ret = new HashMap<>();
        for (var unit : units) {
            if (unit.enabled) {
                String[] params = new String[unit.inputKeys.size()];
                for (int i = 0; i < unit.inputKeys.size(); i++) {
                    params[i] = ScoreboardApplication.dataManager.matchDAO.get(unit.inputKeys.get(i));
                }
                String formatted = unit.format.apply(params);
                ret.put(unit.destination, formatted);
            }
        }

        return ret;
    }
}
