package pl.cheily.filegen.LocalData;

import java.net.URL;

/**
 * Represents a player with all of their related local data
 *
 * @param tag
 * @param name
 * @param nationality
 * @param seed
 * @param icon_url
 * @param chk_in
 */
public record Player(
        String tag,
        String name,
        String nationality,
        int seed,
        URL icon_url,
        boolean chk_in
) {

    public Player(String tag, String name, String nationality) {
        this(tag, name, nationality, 0, null, false);
    }

    public static Player EMPTY = new Player("", "", "");
    public static Player EMPTY_NULL = new Player(null, null, null);

}
