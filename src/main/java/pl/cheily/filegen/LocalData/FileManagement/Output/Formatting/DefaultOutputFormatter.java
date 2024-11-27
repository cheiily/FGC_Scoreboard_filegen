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
import java.util.function.Function;

public class DefaultOutputFormatter implements OutputFormatter {

    protected static final String tagSeparator = " | ";
    protected static final String loserMarker = " [L] ";
    private ArrayList<FormattingUnit> units;
    private PropertyChangeListener listener;

    public static List<FormattingUnit> getPreset() {
        ArrayList<FormattingUnit> ret = new ArrayList<>();

        Function<String[], String> format_p1_name = (String... params) -> {
            // 0 - tag, 1 - name, 2 - isGF, 3 - isGFP1Winner
            String name = params[0].isEmpty() ? params[1] : params[0] + tagSeparator + params[1];
            if (Boolean.parseBoolean(params[2]) && !Boolean.parseBoolean(params[3]))
                name = loserMarker + name;
            name = name.trim();
            return name;
        };
        Function<String[], String> format_p2_name = (String... params) -> {
            String name = params[0].isEmpty() ? params[1] : params[0] + tagSeparator + params[1];
            if (Boolean.parseBoolean(params[2]) && !Boolean.parseBoolean(params[3]))
                name = name + loserMarker;
            name = name.trim();
            return name;
        };
        Function<String[], String> format_comm_name = (String... params) -> {
            String name = params[0].isEmpty() ? params[1] : params[0] + tagSeparator + params[1];
            name = name.trim();
            return name;
        };

        // todo add divs
        ret.add(new FormattingUnit(List.of(MatchDataKey.ROUND_LABEL), List.of(ResourcePath.ROUND), "Winners Round 1", FormattingUnit::oneToOnePass));
        ret.add(new FormattingUnit(List.of(MatchDataKey.P1_SCORE), List.of(ResourcePath.P1_SCORE), "3", FormattingUnit::oneToOnePass));
        ret.add(new FormattingUnit(List.of(MatchDataKey.P2_SCORE), List.of(ResourcePath.P2_SCORE), "2", FormattingUnit::oneToOnePass));

        ret.add(new FormattingUnit(List.of(MatchDataKey.P1_TAG, MatchDataKey.P1_NAME, MatchDataKey.IS_GF, MatchDataKey.IS_GF_P1_WINNER), List.of(ResourcePath.P1_NAME), "GMRZ | Zoner Enthusiast", format_p1_name));
        ret.add(new FormattingUnit(List.of(MatchDataKey.P1_NATIONALITY), List.of(ResourcePath.P1_FLAG), "<a flag image>", FormattingUnit::findFlagFile));
        ret.add(new FormattingUnit(List.of(MatchDataKey.P1_PRONOUNS), List.of(ResourcePath.P1_PRONOUNS), "he/him", FormattingUnit::oneToOnePass));
        ret.add(new FormattingUnit(List.of(MatchDataKey.P1_HANDLE), List.of(ResourcePath.P1_HANDLE), "@cooluser.bsky.social", FormattingUnit::oneToOnePass));

        ret.add(new FormattingUnit(List.of(MatchDataKey.P2_TAG, MatchDataKey.P2_NAME, MatchDataKey.IS_GF, MatchDataKey.IS_GF_P2_WINNER), List.of(ResourcePath.P2_NAME), "GMRZ | Rushdown Enjoyer", format_p2_name));
        ret.add(new FormattingUnit(List.of(MatchDataKey.P2_NATIONALITY), List.of(ResourcePath.P2_FLAG), "<a flag image>", FormattingUnit::findFlagFile));
        ret.add(new FormattingUnit(List.of(MatchDataKey.P2_PRONOUNS), List.of(ResourcePath.P2_PRONOUNS), "they/them", FormattingUnit::oneToOnePass));
        ret.add(new FormattingUnit(List.of(MatchDataKey.P2_HANDLE), List.of(ResourcePath.P2_HANDLE), "@cooler_user27", FormattingUnit::oneToOnePass));

        ret.add(new FormattingUnit(List.of(MatchDataKey.COMM_TAG_1, MatchDataKey.COMM_NAME_1), List.of(ResourcePath.C1_NAME), "CMMS | Nerd", format_comm_name));
        ret.add(new FormattingUnit(List.of(MatchDataKey.COMM_NATIONALITY_1), List.of(ResourcePath.C1_FLAG), "<a flag image>", FormattingUnit::findFlagFile));
        ret.add(new FormattingUnit(List.of(MatchDataKey.COMM_PRONOUNS_1), List.of(ResourcePath.C1_PRONOUNS), "he/him", FormattingUnit::oneToOnePass));
        ret.add(new FormattingUnit(List.of(MatchDataKey.COMM_HANDLE_1), List.of(ResourcePath.C1_HANDLE), "@nerdman", FormattingUnit::oneToOnePass));

        ret.add(new FormattingUnit(List.of(MatchDataKey.COMM_TAG_2, MatchDataKey.COMM_NAME_2), List.of(ResourcePath.C2_NAME), "CMMS | Geek", format_comm_name));
        ret.add(new FormattingUnit(List.of(MatchDataKey.COMM_NATIONALITY_2), List.of(ResourcePath.C2_FLAG), "<a flag image>", FormattingUnit::findFlagFile));
        ret.add(new FormattingUnit(List.of(MatchDataKey.COMM_PRONOUNS_2), List.of(ResourcePath.C2_PRONOUNS), "she/her", FormattingUnit::oneToOnePass));
        ret.add(new FormattingUnit(List.of(MatchDataKey.COMM_HANDLE_2), List.of(ResourcePath.C2_HANDLE), "@geekgirl", FormattingUnit::oneToOnePass));

        ret.add(new FormattingUnit(List.of(MatchDataKey.COMM_TAG_3, MatchDataKey.COMM_NAME_3), List.of(ResourcePath.C3_NAME), "CMMS | FrameWizard", format_comm_name));
        ret.add(new FormattingUnit(List.of(MatchDataKey.COMM_NATIONALITY_3), List.of(ResourcePath.C3_FLAG), "<a flag image>", FormattingUnit::findFlagFile));
        ret.add(new FormattingUnit(List.of(MatchDataKey.COMM_PRONOUNS_3), List.of(ResourcePath.C3_PRONOUNS), "they/them", FormattingUnit::oneToOnePass));
        ret.add(new FormattingUnit(List.of(MatchDataKey.COMM_HANDLE_3), List.of(ResourcePath.C3_HANDLE), "@frame_wizard", FormattingUnit::oneToOnePass));

        return ret;
    }

    public DefaultOutputFormatter() {
        units = new ArrayList<>(getPreset());
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

    //todo per-writer config of used formatter and per-formatter config of disabled units
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
        var retunit = units.stream().filter(unit -> unit.enabled && unit.destinations.contains(resource)).findFirst();
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
                for (var dest : unit.destinations) {
                    ret.put(dest, formatted);
                }
            }
        }

        return ret;
    }
}
