package pl.cheily.filegen.LocalData.FileManagement.Output.Formatting;

import pl.cheily.filegen.LocalData.FileManagement.Meta.Match.MatchDataKey;

import java.util.List;

public enum FormattingUnitMethodReference {
    ONE_TO_ONE_PASS(true),
    FIND_FLAG_FILE(true),
    CUSTOM_INTERPOLATION(true),

    DEFAULT_FORMAT_P1_NAME(false),
    DEFAULT_FORMAT_P2_NAME(false),
    DEFAULT_FORMAT_COMM_NAME(false),
    FSPR_FORMAT_PLAYER_LOSER_INDICATOR(false);

    public boolean isGuiOption;
    FormattingUnitMethodReference(boolean isGuiOption) {
        this.isGuiOption = isGuiOption;
    }

    public boolean validateInputKeys(List<MatchDataKey> keys) {
        return switch (this) {
            case ONE_TO_ONE_PASS, FIND_FLAG_FILE ->
                keys.size() == 1;
            case DEFAULT_FORMAT_P1_NAME, DEFAULT_FORMAT_P2_NAME ->
                keys.size() == 4
                && keys.get(0).isTagField()
                && keys.get(1).isNameField()
                && keys.get(2) == MatchDataKey.IS_GF
                && (keys.get(3) == MatchDataKey.IS_GF_P1_WINNER || keys.get(3) == MatchDataKey.IS_GF_P2_WINNER);
            case DEFAULT_FORMAT_COMM_NAME ->
                keys.size() == 2
                && keys.get(0).isTagField()
                && keys.get(1).isNameField();
            case FSPR_FORMAT_PLAYER_LOSER_INDICATOR ->
                keys.size() == 2
                && keys.get(0) == MatchDataKey.IS_GF
                &&( keys.get(1) == MatchDataKey.IS_GF_P1_WINNER || keys.get(1) == MatchDataKey.IS_GF_P2_WINNER);
            case CUSTOM_INTERPOLATION -> true;
        };
    }

    public static String getSampleOutput(FormattingUnitMethodReference method, List<MatchDataKey> inputKeys, String interpolationFormat, String... inputs) {
        if (method == null) {
            return "INVALID METHOD";
        }
        if (!method.validateInputKeys(inputKeys)) {
            return "INVALID INPUT KEYS";
        }
        if (inputKeys.size() != inputs.length) {
            return "MISMATCHED INPUT LENGTH";
        }

        return switch (method) {
            case ONE_TO_ONE_PASS -> FormattingUnitFactory.oneToOnePassFmt(inputs);
            case FIND_FLAG_FILE -> "<a flag file : " + inputs[0] + ">";
            case DEFAULT_FORMAT_P1_NAME -> FormattingUnitFactory.default_formatP1Name(inputs);
            case DEFAULT_FORMAT_P2_NAME -> FormattingUnitFactory.default_formatP2Name(inputs);
            case DEFAULT_FORMAT_COMM_NAME -> FormattingUnitFactory.default_formatCommName(inputs);
            case FSPR_FORMAT_PLAYER_LOSER_INDICATOR -> FormattingUnitFactory.fspr_formatPlayerLoserIndicator(inputs);
            case CUSTOM_INTERPOLATION -> FormattingUnitFactory.customInterpolateFmt(inputKeys, interpolationFormat, inputs);
        };
    }
}
