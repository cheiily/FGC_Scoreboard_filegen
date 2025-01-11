package pl.cheily.filegen.LocalData.FileManagement.Output.Formatting;

import pl.cheily.filegen.LocalData.FileManagement.Meta.Match.MatchDataKey;
import pl.cheily.filegen.LocalData.ResourcePath;

import java.util.ArrayList;
import java.util.List;

public class FullySeparatingOutputFormatter extends OutputFormatterBase {
    public static final String defaultName = "Fully-separating formatter for file-based output";

    @Override
    public OutputFormatterType getType() {
        return OutputFormatterType.FULLY_SEPARATING;
    }

    public static List<FormattingUnit> getPreset() {
        ArrayList<FormattingUnit> ret = new ArrayList<>();

        // todo add divs
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.ROUND_LABEL), ResourcePath.ROUND, "Winners Round 1"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P1_SCORE), ResourcePath.P1_SCORE, "3"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P2_SCORE), ResourcePath.P2_SCORE, "2"));

        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P1_TAG), ResourcePath.P1_TAG, "GMRZ"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P1_NAME), ResourcePath.P1_NAME, "Zoner Enthusiast"));
        ret.add(FormattingUnitFactory.fspr_playerLoserIndicator(List.of(MatchDataKey.IS_GF, MatchDataKey.IS_GF_P1_WINNER), ResourcePath.P1_LOSER_INDICATOR, DefaultOutputFormatter.loserMarker));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.P1_NATIONALITY), ResourcePath.P1_FLAG, "<a flag image>"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P1_PRONOUNS), ResourcePath.P1_PRONOUNS, "he/him"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P1_HANDLE), ResourcePath.P1_HANDLE, "@cooluser.bsky.social"));

        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P2_TAG), ResourcePath.P2_TAG, "GMRZ"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P2_NAME), ResourcePath.P2_NAME, "Rushdown Enjoyer"));
        ret.add(FormattingUnitFactory.fspr_playerLoserIndicator(List.of(MatchDataKey.IS_GF, MatchDataKey.IS_GF_P2_WINNER), ResourcePath.P2_LOSER_INDICATOR, DefaultOutputFormatter.loserMarker));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.P2_NATIONALITY), ResourcePath.P2_FLAG, "<a flag image>"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P2_PRONOUNS), ResourcePath.P2_PRONOUNS, "they/them"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P2_HANDLE), ResourcePath.P2_HANDLE, "@cooler_user27"));

        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_TAG_1), ResourcePath.C1_TAG, "CMMS"));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.COMM_NAME_1), ResourcePath.C1_NAME, "Nerd"));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.COMM_NATIONALITY_1), ResourcePath.C1_FLAG, "<a flag image>"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_PRONOUNS_1), ResourcePath.C1_PRONOUNS, "he/him"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_HANDLE_1), ResourcePath.C1_HANDLE, "@nerdman"));

        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.COMM_TAG_2), ResourcePath.C2_TAG, "CMMS"));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.COMM_NAME_2), ResourcePath.C2_NAME, "Geek"));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.COMM_NATIONALITY_2), ResourcePath.C2_FLAG, "<a flag image>"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_PRONOUNS_2), ResourcePath.C2_PRONOUNS, "she/her"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_HANDLE_2), ResourcePath.C2_HANDLE, "@geekgirl"));

        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_TAG_3), ResourcePath.C3_TAG, "CMMS"));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.COMM_NAME_3), ResourcePath.C3_NAME, "FrameWizard"));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.COMM_NATIONALITY_3), ResourcePath.C3_FLAG, "<a flag image>"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_PRONOUNS_3), ResourcePath.C3_PRONOUNS, "they/them"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_HANDLE_3), ResourcePath.C3_HANDLE, "@frame_wizard"));

        return ret;
    }

    public FullySeparatingOutputFormatter() {
        super("Fully-separating formatter for file-based output", getPreset());
    }

    private FullySeparatingOutputFormatter(String name, List<FormattingUnit> units) {
        super(name, units);
    }

    public static FullySeparatingOutputFormatter deserialize(OutputFormatterDeserializerParams params) {
        return new FullySeparatingOutputFormatter(params.name, params.units);
    }
}
