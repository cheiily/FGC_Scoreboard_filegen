package pl.cheily.filegen.LocalData.FileManagement.Output.Formatting;

import pl.cheily.filegen.LocalData.FileManagement.Meta.Match.MatchDataKey;
import pl.cheily.filegen.LocalData.ResourcePath;

import java.util.List;
import java.util.function.Function;

// Provides some data about the formatting function, to be displayed in the UI.
// Inputs & outputs are defined by the keys, replace this for plain strings if you're modding this.
public class FormattingUnit {
    public boolean enabled;
    public List<MatchDataKey> inputKeys;
    public ResourcePath destination;
    public String sampleOutput;

    // input array must be in the same order as the keys
    public Function<String[], String> format;
    public FormattingUnitMethodReference formatType;
    public String customInterpolationFormat;

    public static FormattingUnit deserialize(boolean enabled, List<MatchDataKey> inputKeys, ResourcePath destination, String sampleOutput, String customInterpolationFormat, FormattingUnitMethodReference type) {
        var ret = switch (type) {
            case ONE_TO_ONE_PASS -> FormattingUnitFactory.oneToOnePass(inputKeys, destination, sampleOutput);
            case FIND_FLAG_FILE -> FormattingUnitFactory.findFlagFile(inputKeys, destination, sampleOutput);
            case CUSTOM_INTERPOLATION -> FormattingUnitFactory.customInterpolate(inputKeys, destination, sampleOutput, customInterpolationFormat);
            case DEFAULT_FORMAT_P1_NAME -> FormattingUnitFactory.default_P1Name(inputKeys, destination, sampleOutput);
            case DEFAULT_FORMAT_P2_NAME -> FormattingUnitFactory.default_P2Name(inputKeys, destination, sampleOutput);
            case DEFAULT_FORMAT_COMM_NAME -> FormattingUnitFactory.default_commName(inputKeys, destination, sampleOutput);
            case FSPR_FORMAT_PLAYER_LOSER_INDICATOR -> FormattingUnitFactory.fspr_playerLoserIndicator(inputKeys, destination, sampleOutput);
        };
        ret.enabled = enabled;
        return ret;
    }

    FormattingUnit(boolean enabled, List<MatchDataKey> inputKeys, ResourcePath destination, String sampleOutput, Function<String[], String> format, String customInterpolationFormat, FormattingUnitMethodReference type) {
        this.enabled = enabled;
        this.inputKeys = inputKeys;
        this.destination = destination;
        this.sampleOutput = sampleOutput;
        this.format = format;
        this.customInterpolationFormat = customInterpolationFormat;
        this.formatType = type;
    }
}
