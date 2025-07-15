package pl.cheily.filegen.LocalData.FileManagement.Output.Formatting;

import pl.cheily.filegen.LocalData.FileManagement.Meta.Match.MatchDataKey;
import pl.cheily.filegen.LocalData.LocalResourcePath;

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
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.ROUND_LABEL), LocalResourcePath.ROUND, "Winners Round 1"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P1_SCORE), LocalResourcePath.P1_SCORE, "3"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P2_SCORE), LocalResourcePath.P2_SCORE, "2"));

        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P1_TAG), LocalResourcePath.P1_TAG, "GMRZ"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P1_NAME), LocalResourcePath.P1_NAME, "Zoner Enthusiast"));
        ret.add(FormattingUnitFactory.fspr_playerLoserIndicator(List.of(MatchDataKey.IS_GF, MatchDataKey.IS_GF_P1_WINNER), LocalResourcePath.P1_LOSER_INDICATOR, DefaultOutputFormatter.loserMarker));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.P1_NATIONALITY), LocalResourcePath.P1_FLAG, "<a flag image>"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P1_PRONOUNS), LocalResourcePath.P1_PRONOUNS, "he/him"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P1_HANDLE), LocalResourcePath.P1_HANDLE, "@cooluser.bsky.social"));

        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P2_TAG), LocalResourcePath.P2_TAG, "GMRZ"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P2_NAME), LocalResourcePath.P2_NAME, "Rushdown Enjoyer"));
        ret.add(FormattingUnitFactory.fspr_playerLoserIndicator(List.of(MatchDataKey.IS_GF, MatchDataKey.IS_GF_P2_WINNER), LocalResourcePath.P2_LOSER_INDICATOR, DefaultOutputFormatter.loserMarker));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.P2_NATIONALITY), LocalResourcePath.P2_FLAG, "<a flag image>"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P2_PRONOUNS), LocalResourcePath.P2_PRONOUNS, "they/them"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P2_HANDLE), LocalResourcePath.P2_HANDLE, "@cooler_user27"));

        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_TAG_1), LocalResourcePath.C1_TAG, "CMMS"));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.COMM_NAME_1), LocalResourcePath.C1_NAME, "Nerd"));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.COMM_NATIONALITY_1), LocalResourcePath.C1_FLAG, "<a flag image>"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_PRONOUNS_1), LocalResourcePath.C1_PRONOUNS, "he/him"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_HANDLE_1), LocalResourcePath.C1_HANDLE, "@nerdman"));

        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.COMM_TAG_2), LocalResourcePath.C2_TAG, "CMMS"));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.COMM_NAME_2), LocalResourcePath.C2_NAME, "Geek"));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.COMM_NATIONALITY_2), LocalResourcePath.C2_FLAG, "<a flag image>"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_PRONOUNS_2), LocalResourcePath.C2_PRONOUNS, "she/her"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_HANDLE_2), LocalResourcePath.C2_HANDLE, "@geekgirl"));

        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_TAG_3), LocalResourcePath.C3_TAG, "CMMS"));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.COMM_NAME_3), LocalResourcePath.C3_NAME, "FrameWizard"));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.COMM_NATIONALITY_3), LocalResourcePath.C3_FLAG, "<a flag image>"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_PRONOUNS_3), LocalResourcePath.C3_PRONOUNS, "they/them"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_HANDLE_3), LocalResourcePath.C3_HANDLE, "@frame_wizard"));

        return ret;
    }

    public FullySeparatingOutputFormatter() {
        super("Fully-separating formatter for file-based output", getPreset());
    }

    public FullySeparatingOutputFormatter(String name, List<FormattingUnit> units) {
        super(name, units);
    }

    public static FullySeparatingOutputFormatter deserialize(OutputFormatterDeserializerParams params) {
        return new FullySeparatingOutputFormatter(params.name, params.units);
    }
}
