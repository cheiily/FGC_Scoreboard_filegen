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
        if ( !formats.containsKey(ResourcePath.of(resourceName)) ) return null;

        return formats.get(ResourcePath.of(resourceName)).apply(data);
    }

    private static final String emojiHouse = "\uD83C\uDFE0";
    private static final String emojiMic = "\uD83C\uDF99️";

    private static final Map<ResourcePath, Function<String[], String>> formats = new HashMap<>();

    //initializer block
    static {
        formats.put(ResourcePath.P1_NAME, DefaultOutputFormatter::playerName);
        formats.put(ResourcePath.P2_NAME, DefaultOutputFormatter::playerName);
        formats.put(ResourcePath.COMMS, DefaultOutputFormatter::comms);
    }

    private static String playerName(String... data) {
        if ( data.length < 2 )
            return data[ 0 ];

        if ( Boolean.parseBoolean(data[ 1 ]) )
            return data[ 0 ] + " [W]";
        else return data[ 0 ] + " [L]";
    }

    private static String comms(String... data) {
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
