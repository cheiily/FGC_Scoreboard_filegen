package pl.cheily.filegen.LocalData;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
        if ( !formats.containsKey(ResourcePath.of(resourceName)) ) return data [ 0 ];

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


    protected static String playerName(String... data) {
        String ret;
        if ( data[ 0 ] != null )
            ret = data[ 0 ] + tagSeparator + data[ 1 ];
        else ret = data[ 1 ];

        if ( data[ 2 ] != null )
            ret += Boolean.parseBoolean(data[ 2 ]) ? winnerMarker : loserMarker;

        return ret;
    }

    protected static String comms(String... data) {
        StringBuilder ret = new StringBuilder();

        if ( !data[ 0 ].isEmpty() )
            ret.append(emojiHouse).append(data[ 0 ]).append(emojiHouse).append('\n');

        if ( data.length == 2 ) {
            ret.append(emojiMic).append(data[ 1 ]).append(emojiMic);
            return ret.toString();
        }

        for (int i = 1; i < data.length - 1; ++i) {
            ret.append(data[ i ]).append(emojiMic);
        }
        ret.append(data[ data.length - 1 ]);
        return ret.toString();
    }

}
