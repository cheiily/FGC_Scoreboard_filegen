package pl.cheily.filegen.LocalData.FileManagement.Output.Formatting;

import pl.cheily.filegen.Configuration.AppConfig;
import pl.cheily.filegen.LocalData.FileManagement.Meta.Match.MatchDataKey;
import pl.cheily.filegen.LocalData.LocalResourcePath;
import pl.cheily.filegen.Utils.Pair;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import static pl.cheily.filegen.ScoreboardApplication.dataManager;

public class FormattingUnitFactory {
    static final Pattern customInterpolationPattern = Pattern.compile("\\{.+?\\}");
    static final Pattern customInterpolationTernaryPattern = Pattern.compile("\\{.+?\\?.+?:.+?\\}");
    static final Pattern customInterpolationIndexPattern = Pattern.compile("\\$\\d+");
    static final char customInterpolationGroupOpenDelimiter = '{';
    static final char customInterpolationGroupCloseDelimiter = '}';
    static final char customInterpolationTernaryIf = '?';
    static final char customInterpolationTernaryElse = ':';

    public static void updateFormatter(FormattingUnit unit) {
        switch (unit.formatType) {
            case ONE_TO_ONE_PASS -> unit.format = FormattingUnitFactory::oneToOnePassFmt;
            case FIND_FLAG_FILE -> unit.format = FormattingUnitFactory::findFlagFileFmt;
            case CUSTOM_INTERPOLATION -> unit.format = (String... params) -> FormattingUnitFactory.customInterpolateFmt(unit.inputKeys, unit.customInterpolationFormat, params);
            case DEFAULT_FORMAT_P1_NAME -> unit.format = FormattingUnitFactory::default_formatP1Name;
            case DEFAULT_FORMAT_P2_NAME -> unit.format = FormattingUnitFactory::default_formatP2Name;
            case DEFAULT_FORMAT_COMM_NAME -> unit.format = FormattingUnitFactory::default_formatCommName;
            case FSPR_FORMAT_PLAYER_LOSER_INDICATOR -> unit.format = FormattingUnitFactory::fspr_formatPlayerLoserIndicator;
        };
    }

    //-------------------Preset formatter method (required for deserialization) & factory methods-------------------
    public static FormattingUnit oneToOnePass(List<MatchDataKey> inputKeys, LocalResourcePath destination, String sampleOutput) {
        return new FormattingUnit(true, inputKeys, destination, sampleOutput, FormattingUnitFactory::oneToOnePassFmt, null, FormattingUnitMethodReference.ONE_TO_ONE_PASS);
    }
    public static String oneToOnePassFmt(String... params) {
        return params[0];
    }


    public static FormattingUnit findFlagFile(List<MatchDataKey> inputKeys, LocalResourcePath destination, String sampleOutput) {
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


    public static FormattingUnit customInterpolate(List<MatchDataKey> inputKeys, LocalResourcePath destination, String sampleOutput, String customInterpolationFormat) {
        var fmt = new FormattingUnit(true, inputKeys, destination, sampleOutput, null, customInterpolationFormat, FormattingUnitMethodReference.CUSTOM_INTERPOLATION);
        fmt.format = (String... params) -> FormattingUnitFactory.customInterpolateFmt(inputKeys, customInterpolationFormat, params);
        return fmt;
    }

    public static String customInterpolateFmt(List<MatchDataKey> inputKeys, String customInterpolationFormat, String... params) {
        int minlen = Math.min(inputKeys.size(), params.length);
        HashMap<MatchDataKey, String> inputs = new HashMap<>(minlen);
        for (int i = 0; i < minlen; i++)
            inputs.put(inputKeys.get(i), params[i]);

        return evaluate_CIFormat(inputs, customInterpolationFormat);

//        Matcher matcher = customInterpolationPattern.matcher(customInterpolationFormat);
//        return matcher.replaceAll(ptrn -> {
//            String s = ptrn.group();
//            try {
//                MatchDataKey val = MatchDataKey.fromString(s.substring(1, s.length() - 1));
//                return params[(inputKeys.indexOf(val))];
//            } catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
//                return s;
//            }
//        });
    }

    private static String evaluate_CIFormat(HashMap<MatchDataKey, String> inputs, String expression) {
        if (expression == null || expression.isEmpty()) {
            return "";
        }

        StringBuilder eval = new StringBuilder();
        String expr = expression;
        int iSubExpr = -1;
        do {
            iSubExpr = expr.indexOf(customInterpolationGroupOpenDelimiter);
            if (iSubExpr == -1) {
                eval.append(expr);
                break;
            } else {
                String pre = expr.substring(0, iSubExpr);
                eval.append(pre);
                expr = expr.substring(iSubExpr + 1);

                var subExpr = evaluate_subCIExpression(inputs, expr);
                eval.append(subExpr.first());
                expr = expr.substring(subExpr.second());
            }
        } while (!expr.isEmpty());

        return eval.toString();
    }

    private static Pair<String, Integer> evaluate_subCIExpression(HashMap<MatchDataKey, String> inputs, String expression) {
        StringBuilder eval = new StringBuilder();
        StringBuilder buffer = new StringBuilder();
        String ternaryCond = null, ternaryPositive = null, ternaryNegative = null;
        int iTernaryIf = -1, iTernaryElse = -1;
        int exitPoint = -1;
        boolean metExit = false;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == customInterpolationGroupOpenDelimiter) {
                var val = evaluate_subCIExpression(inputs, expression.substring(i + 1));
                buffer.append(val.first());
                i += val.second();
                continue;
            }

            if (c == customInterpolationTernaryIf) {
                ternaryCond = buffer.toString();
                buffer = new StringBuilder();
                iTernaryIf = i;
//                i++;
                continue;
            }

            if (c == customInterpolationTernaryElse) {
                ternaryPositive = buffer.toString();
                buffer = new StringBuilder();
                iTernaryElse = i;
//                i++;
                continue;
            }

            if (c == customInterpolationGroupCloseDelimiter) {
                metExit = true;
                if (iTernaryIf != -1 && iTernaryElse != -1) { // ternary
                    ternaryNegative = buffer.toString();
                    buffer = new StringBuilder();

                    if (ternaryCond != null && !ternaryCond.isEmpty())
                        // (cond ? cond : other) shortform
                        if (iTernaryElse == iTernaryIf + 1)
                            eval.append(ternaryCond);
                        else eval.append(ternaryPositive);
                    else
                        eval.append(ternaryNegative);
                } else { // not ternary, just a key replacement
                    try {
                        MatchDataKey key = MatchDataKey.fromString(buffer.toString());
                        if (!inputs.containsKey(key))
                            throw new IllegalArgumentException();

                        eval.append(inputs.get(key));
                        buffer = new StringBuilder();
                    } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                        // do not clear the buffer -> it will get passed as is
                    }
                }

                eval.append(buffer);
                buffer = new StringBuilder();
                exitPoint = i + 1;
                break;
            }

            // if it's just a regular character
            buffer.append(c);
        }

        // not met group close for some reason - buffer has not been dumped
        eval.append(buffer);

        if (exitPoint == -1) {
            exitPoint = expression.length();
        }
        return new Pair<>((metExit ? "" : customInterpolationGroupOpenDelimiter) + eval.toString(), exitPoint);
    }

    //-------------------Default formatter methods-------------------
    public static FormattingUnit default_P1Name(List<MatchDataKey> inputKeys, LocalResourcePath destination, String sampleOutput) {
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


    public static FormattingUnit default_P2Name(List<MatchDataKey> inputKeys, LocalResourcePath destination, String sampleOutput) {
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


    public static FormattingUnit default_commName(List<MatchDataKey> inputKeys, LocalResourcePath destination, String sampleOutput) {
        return new FormattingUnit(true, inputKeys, destination, sampleOutput, FormattingUnitFactory::default_formatCommName, null, FormattingUnitMethodReference.DEFAULT_FORMAT_COMM_NAME);
    }
    public static String default_formatCommName(String... params) {
        // 0 - tag, 1 - name
        String name = params[0].isEmpty() ? params[1] : params[0] + DefaultOutputFormatter.tagSeparator + params[1];
        name = name.trim();
        return name;
    };


    public static FormattingUnit fspr_playerLoserIndicator(List<MatchDataKey> inputKeys, LocalResourcePath destination, String sampleOutput) {
        return new FormattingUnit(true, inputKeys, destination, sampleOutput, FormattingUnitFactory::fspr_formatPlayerLoserIndicator, null, FormattingUnitMethodReference.FSPR_FORMAT_PLAYER_LOSER_INDICATOR);
    }
    public static String fspr_formatPlayerLoserIndicator(String... params) {
        // 0 - isGF, 1 - isWinner
        if (Boolean.parseBoolean(params[0]) && !Boolean.parseBoolean(params[1]))
            return DefaultOutputFormatter.loserMarker;
        return "";
    }
}
