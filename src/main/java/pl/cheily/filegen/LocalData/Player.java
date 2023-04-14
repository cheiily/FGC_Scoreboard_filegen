package pl.cheily.filegen.LocalData;

/**
 * Represents a "player" or rather a player menu item for combo box selection,
 * used when searching the player list for records associated with the newly input player name to auto-fill their nationality and tag as saved within the list.
 *
 * @param tag
 * @param name
 * @param nationality
 */
public record Player(String tag, String name, String nationality) {
}
