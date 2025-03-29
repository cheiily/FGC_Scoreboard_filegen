package pl.cheily.filegen.LocalData.FileManagement.Output.Formatting;

import pl.cheily.filegen.LocalData.FileManagement.Meta.Match.MatchDataKey;
import pl.cheily.filegen.LocalData.ResourcePath;

import java.util.ArrayList;
import java.util.List;

public class DefaultOutputFormatter extends OutputFormatterBase {
    public static final String defaultName = "Default formatter for file-based output";
    static final String tagSeparator = " | ";
    static final String loserMarker = " [L] ";

    @Override
    public OutputFormatterType getType() {
        return OutputFormatterType.DEFAULT;
    }

    public static List<FormattingUnit> getPreset() {
        ArrayList<FormattingUnit> ret = new ArrayList<>();

        // todo add divs
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.ROUND_LABEL), ResourcePath.ROUND, "Winners Round 1"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P1_SCORE), ResourcePath.P1_SCORE, "3"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P2_SCORE), ResourcePath.P2_SCORE, "2"));

        ret.add(FormattingUnitFactory.default_P1Name(List.of(MatchDataKey.P1_TAG, MatchDataKey.P1_NAME, MatchDataKey.IS_GF, MatchDataKey.IS_GF_P1_WINNER), ResourcePath.P1_NAME, "GMRZ | Zoner Enthusiast"));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.P1_NATIONALITY), ResourcePath.P1_FLAG, "<a flag image>"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P1_PRONOUNS), ResourcePath.P1_PRONOUNS, "he/him"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P1_HANDLE), ResourcePath.P1_HANDLE, "@cooluser.bsky.social"));

        ret.add(FormattingUnitFactory.default_P2Name(List.of(MatchDataKey.P2_TAG, MatchDataKey.P2_NAME, MatchDataKey.IS_GF, MatchDataKey.IS_GF_P2_WINNER), ResourcePath.P2_NAME, "GMRZ | Rushdown Enjoyer"));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.P2_NATIONALITY), ResourcePath.P2_FLAG, "<a flag image>"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P2_PRONOUNS), ResourcePath.P2_PRONOUNS, "they/them"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.P2_HANDLE), ResourcePath.P2_HANDLE, "@cooler_user27"));

        ret.add(FormattingUnitFactory.default_commName(List.of(MatchDataKey.COMM_TAG_1, MatchDataKey.COMM_NAME_1), ResourcePath.C1_NAME, "CMMS | Nerd"));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.COMM_NATIONALITY_1), ResourcePath.C1_FLAG, "<a flag image>"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_PRONOUNS_1), ResourcePath.C1_PRONOUNS, "he/him"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_HANDLE_1), ResourcePath.C1_HANDLE, "@nerdman"));

        ret.add(FormattingUnitFactory.default_commName(List.of(MatchDataKey.COMM_TAG_2, MatchDataKey.COMM_NAME_2), ResourcePath.C2_NAME, "CMMS | Geek"));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.COMM_NATIONALITY_2), ResourcePath.C2_FLAG, "<a flag image>"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_PRONOUNS_2), ResourcePath.C2_PRONOUNS, "she/her"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_HANDLE_2), ResourcePath.C2_HANDLE, "@geekgirl"));

        ret.add(FormattingUnitFactory.default_commName(List.of(MatchDataKey.COMM_TAG_3, MatchDataKey.COMM_NAME_3), ResourcePath.C3_NAME, "CMMS | FrameWizard"));
        ret.add(FormattingUnitFactory.findFlagFile(List.of(MatchDataKey.COMM_NATIONALITY_3), ResourcePath.C3_FLAG, "<a flag image>"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_PRONOUNS_3), ResourcePath.C3_PRONOUNS, "they/them"));
        ret.add(FormattingUnitFactory.oneToOnePass(List.of(MatchDataKey.COMM_HANDLE_3), ResourcePath.C3_HANDLE, "@frame_wizard"));

        return ret;
    }

    public DefaultOutputFormatter() {
        this(defaultName, getPreset());
    }

    public DefaultOutputFormatter(String name, List<FormattingUnit> units) {
        super(name, units);
    }

    public static DefaultOutputFormatter deserialize(OutputFormatterDeserializerParams params) {
        return new DefaultOutputFormatter(params.name, params.units);
    }
}
