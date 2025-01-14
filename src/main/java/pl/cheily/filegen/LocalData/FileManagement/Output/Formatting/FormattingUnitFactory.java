package pl.cheily.filegen.LocalData.FileManagement.Output.Formatting;

import pl.cheily.filegen.Configuration.AppConfig;
import pl.cheily.filegen.LocalData.FileManagement.Meta.Match.MatchDataKey;
import pl.cheily.filegen.LocalData.ResourcePath;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pl.cheily.filegen.ScoreboardApplication.dataManager;

public class FormattingUnitFactory {
    private static final Pattern customInterpolationPattern = Pattern.compile("\\{.+\\}");

    public static void updateFormatter(FormattingUnit unit) {
        switch (unit.formatType) {
            case ONE_TO_ONE_PASS -> unit.format = FormattingUnitFactory::oneToOnePassFmt;
            case FIND_FLAG_FILE -> unit.format = FormattingUnitFactory::findFlagFileFmt;
            case CUSTOM_INTERPOLATION -> unit.format = (String... params) -> FormattingUnitFactory.customInterpolateFmt(unit, params);
            case DEFAULT_FORMAT_P1_NAME -> unit.format = FormattingUnitFactory::default_formatP1Name;
            case DEFAULT_FORMAT_P2_NAME -> unit.format = FormattingUnitFactory::default_formatP2Name;
            case DEFAULT_FORMAT_COMM_NAME -> unit.format = FormattingUnitFactory::default_formatCommName;
            case FSPR_FORMAT_PLAYER_LOSER_INDICATOR -> unit.format = FormattingUnitFactory::fspr_formatPlayerLoserIndicator;
        };
    }

    //-------------------Preset formatter method (required for deserialization) & factory methods-------------------
    public static FormattingUnit oneToOnePass(List<MatchDataKey> inputKeys, ResourcePath destination, String sampleOutput) {
        return new FormattingUnit(true, inputKeys, destination, sampleOutput, FormattingUnitFactory::oneToOnePassFmt, null, FormattingUnitMethodReference.ONE_TO_ONE_PASS);
    }
    public static String oneToOnePassFmt(String... params) {
        return params[0];
    }


    public static FormattingUnit findFlagFile(List<MatchDataKey> inputKeys, ResourcePath destination, String sampleOutput) {
        return new FormattingUnit(true, inputKeys, destination, sampleOutput, FormattingUnitFactory::findFlagFileFmt, null, FormattingUnitMethodReference.FIND_FLAG_FILE);
    }

    public static String findFlagFileFmt(String... params) {
        // todo adjust this for bundled flags
        if (params[0].isEmpty()) return params[0];
        try {
            Path flag = Files.find(Path.of(dataManager.flagsDir + "/"), 2, (path, basicFileAttributes) -> path.toFile().getName().startsWith(params[0]), FileVisitOption.FOLLOW_LINKS).findFirst().get();
            return flag.getFileName().toString();
        } catch (IOException | NoSuchElementException e) {
            return params[0] + AppConfig.FLAG_EXTENSION();
        }
    }


    public static FormattingUnit customInterpolate(List<MatchDataKey> inputKeys, ResourcePath destination, String sampleOutput, String customInterpolationFormat) {
        var fmt = new FormattingUnit(true, inputKeys, destination, sampleOutput, null, customInterpolationFormat, FormattingUnitMethodReference.CUSTOM_INTERPOLATION);
        fmt.format = (String... params) -> FormattingUnitFactory.customInterpolateFmt(fmt, params);
        return fmt;
    }

    public static String customInterpolateFmt(FormattingUnit unit, String... params) {
        Matcher matcher = customInterpolationPattern.matcher(unit.customInterpolationFormat);
        return matcher.replaceAll(ptrn -> {
            String s = ptrn.group();
            try {
                MatchDataKey val = MatchDataKey.valueOf(s.substring(1, s.length() - 1));
                return params[(unit.inputKeys.indexOf(val))];
            } catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
                return s;
            }
        });
    }


    //-------------------Default formatter methods-------------------
    public static FormattingUnit default_P1Name(List<MatchDataKey> inputKeys, ResourcePath destination, String sampleOutput) {
        return new FormattingUnit(true, inputKeys, destination, sampleOutput, FormattingUnitFactory::default_formatP1Name, null, FormattingUnitMethodReference.DEFAULT_FORMAT_P1_NAME);
    }
    public static String default_formatP1Name(String... params) {
        // 0 - tag, 1 - name, 2 - isGF, 3 - isGFP1Winner
        String name = params[0].isEmpty() ? params[1] : params[0] + DefaultOutputFormatter.tagSeparator + params[1];
        if (Boolean.parseBoolean(params[2]) && !Boolean.parseBoolean(params[3]))
            name = DefaultOutputFormatter.loserMarker + name;
        name = name.trim();
        return name;
    };


    public static FormattingUnit default_P2Name(List<MatchDataKey> inputKeys, ResourcePath destination, String sampleOutput) {
        return new FormattingUnit(true, inputKeys, destination, sampleOutput, FormattingUnitFactory::default_formatP2Name, null, FormattingUnitMethodReference.DEFAULT_FORMAT_P2_NAME);
    }
    public static String default_formatP2Name(String... params) {
        // 0 - tag, 1 - name, 2 - isGF, 3 - isGFP2Winner
        String name = params[0].isEmpty() ? params[1] : params[0] + DefaultOutputFormatter.tagSeparator + params[1];
        if (Boolean.parseBoolean(params[2]) && !Boolean.parseBoolean(params[3]))
            name = DefaultOutputFormatter.loserMarker + name;
        name = name.trim();
        return name;
    };


    public static FormattingUnit default_commName(List<MatchDataKey> inputKeys, ResourcePath destination, String sampleOutput) {
        return new FormattingUnit(true, inputKeys, destination, sampleOutput, FormattingUnitFactory::default_formatCommName, null, FormattingUnitMethodReference.DEFAULT_FORMAT_COMM_NAME);
    }
    public static String default_formatCommName(String... params) {
        // 0 - tag, 1 - name
        String name = params[0].isEmpty() ? params[1] : params[0] + DefaultOutputFormatter.tagSeparator + params[1];
        name = name.trim();
        return name;
    };


    public static FormattingUnit fspr_playerLoserIndicator(List<MatchDataKey> inputKeys, ResourcePath destination, String sampleOutput) {
        return new FormattingUnit(true, inputKeys, destination, sampleOutput, FormattingUnitFactory::fspr_formatPlayerLoserIndicator, null, FormattingUnitMethodReference.FSPR_FORMAT_PLAYER_LOSER_INDICATOR);
    }
    public static String fspr_formatPlayerLoserIndicator(String... params) {
        // 0 - isGF, 1 - isWinner
        if (Boolean.parseBoolean(params[0]) && !Boolean.parseBoolean(params[1]))
            return DefaultOutputFormatter.loserMarker;
        return "";
    }
}
