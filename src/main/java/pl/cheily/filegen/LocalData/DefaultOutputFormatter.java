package pl.cheily.filegen.LocalData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultOutputFormatter implements OutputFormatter {

    /**
     * Formats a resource to the desired layout
     *
     * @param resourceName identifier of the resource, to decide how the data should be formatted
     * @param data         to format, may consist of multiple separate parts to merge
     * @return formatted data
     * @implNote The default implementation checks if the passed resourceName is a valid ResourcePath-registered path.
     * In case of extension, one would want to omit that safety check and save directly to the passed file.
     */
    @Override
    public String format(String resourceName, String... data) {
        if ( !formats.containsKey(ResourcePath.of(resourceName)) ) return data[ 0 ];

        return formats.get(ResourcePath.of(resourceName)).apply(data);
    }

    protected static final String tagSeparator = " | ";
    protected static final String winnerMarker = " [W]";
    protected static final String loserMarker = " [L]";
    protected static final String emojiHouse = "\uD83C\uDFE0";
    protected static final String emojiMic = "\uD83C\uDF99️";

    protected static final Map<ResourcePath, Function<String[], String>> formats = new HashMap<>();

    //initializer block
    static {
        formats.put(ResourcePath.P1_NAME, DefaultOutputFormatter::playerName);
        formats.put(ResourcePath.P2_NAME, DefaultOutputFormatter::playerName);
        formats.put(ResourcePath.COMMS, DefaultOutputFormatter::comms);
    }


    /**
     * The default way of formatting player names.
     *
     * @param data See {@link OutputFormatter#format(String, String...)} for details on how the player data is passed by default.
     * @return Formatted player name output.
     */
    protected static String playerName(String... data) {
        String ret;
        if ( !data[ 0 ].isEmpty() )
            ret = data[ 0 ] + tagSeparator + data[ 1 ];
        else ret = data[ 1 ];

        if ( data[ 2 ] != null )
            ret += Boolean.parseBoolean(data[ 2 ]) ? winnerMarker : loserMarker;

        return ret;
    }

    /**
     * The default way of formatting commentary output.
     * <p>
     * This method facilitates passing an arbitrary number of commentators, while filtering the list for only non-empty entries.
     * It also accounts for the exceptional case of just one commentator being present.
     *
     * @param data See {@link OutputFormatter#format(String, String...)} for details on how the commentary data is passed by default.
     * @return Formatted string of commentary data, merging the host and comms into a single file.
     */
    protected static String comms(String... data) {
        StringBuilder ret = new StringBuilder();

        if ( !data[ 0 ].isEmpty() )
            ret.append(emojiHouse).append(data[ 0 ]).append(emojiHouse).append('\n');

        data[ 0 ] = "";
        int count = (int) Arrays.stream(data).filter(s -> !s.isEmpty()).count();
        List<String> notEmpty = Arrays.stream(data).filter(s -> !s.isEmpty()).toList();

        if ( count == 1 ) {
            ret.append(emojiMic)
                    .append(notEmpty.get(0))
                    .append(emojiMic);

            return ret.toString();
        }

        for (int i = 0; i < count - 1; ++i)
            ret.append(notEmpty.get(i)).append(emojiMic);
        if (count > 0) ret.append(notEmpty.get(count - 1));
        return ret.toString();
    }

}
