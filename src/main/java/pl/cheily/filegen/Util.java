package pl.cheily.filegen;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.input.ScrollEvent;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class Util {
    /**
     * Target directory path
     */
    public static Path path;
    public static Path flags = Path.of("flags").toAbsolutePath();


    /**
     * Attempts to save the passed text content to a file specified by rPath.
     * If rPath is a file within a subdirectory, the function will attempt to create the necessary dirs first.
     * @param text to be written to the file.
     * @param rPath ResourcePath representing the file to be written to.
     * @return null if the operation goes through successfully, rPath.toString() if it fails.
     */
    public static String saveFile(String text, ResourcePath rPath) {
        if (rPath.toString().contains("/")) {
            try {
                Files.createDirectories(Path.of(
                        path + "/" + rPath.toString().split("/")[0]
                ));
            } catch (IOException e) {
                return null;
            }
        }

        try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(path.toString() + '/' + rPath.toString()), StandardCharsets.UTF_8)) {
            if (text == null) text = "";
            fw.write(text);

        } catch (Exception e) {
            return rPath.toString();
        }
        return null;
    }

    /**
     * Attempts to copy a flag.png file from the flag directory into the target directory, file specified by rPath.
     * Also refreshes the last modified timestamp on the rPath file to enable polled refreshes by obs.
     * @param source_name name of the flag image to copy
     * @param rPath representing the file to be copied "into".
     * @return null if the operation goes through successfully, rPath.toString() if it fails.
     */
    public static String saveImg(Path source_name, ResourcePath rPath) {
        try {
            Files.copy(Path.of(flags + "/" + source_name),
                    Path.of(path + "/" + rPath),
                    StandardCopyOption.REPLACE_EXISTING);
            Files.setLastModifiedTime(Path.of(path + "/" + rPath), FileTime.from(Instant.now()));
        } catch (Exception e) {
            return rPath.toString();
        }
        return null;
    }

    /**
     * Equivalent to {@link #makeScrollable(Node, ComboBox)} with both arguments being the same ComboBox.
     * @param comboBox
     */
    public static void makeScrollable(ComboBox<String> comboBox) {
        makeScrollable(comboBox, comboBox);
    }

    /**
     * Appends an onScroll listener based on scrollOpt to event_source, the listener will scroll items within content_to_scroll.
     * E.g. Allows scrolling on an ImageView to scroll opts on a ComboBox next to it.
     * @param event_source
     * @param content_to_scroll
     */
    public static void makeScrollable(Node event_source, ComboBox<String> content_to_scroll) {
        event_source.setOnScroll(evt -> scrollOpt(content_to_scroll, evt));
    }

    /**
     * Scrolls the selected option in content_to_scroll.
     * Utilizes {@link #wrap(int, int)} internally to normalize the index value.
     * The amount scrolled will always be 1 per event, the direction however,
     *  will be reverse to the sign of evt.getTextDeltaY() - that way a scroll up will scroll the options upwards.
     * @param content_to_scroll
     * @param evt
     */
    public static void scrollOpt(ComboBox<String> content_to_scroll, ScrollEvent evt) {
        ObservableList<String> opts = content_to_scroll.getItems();
        int old_index = opts.indexOf(content_to_scroll.getValue());
        int delta_move = -Integer.signum((int) evt.getTextDeltaY());

        if (opts.isEmpty()) {
            content_to_scroll.setValue("");
            return;
        }

        int new_index = wrap(old_index + delta_move, opts.size());
        content_to_scroll.setValue(opts.get(new_index));

    }

    /**
     * Wraps value to be between 0 and exclusive max
     * @param value
     * @param max
     * @return wrapped value
     */
    public static int wrap(int value, int max) {
        if (value > 0) value %= max;
        else while (value < 0) value = max + value;

        return value;
    }

    /**
     * Set of predefined resource paths, to be loaded onto the overlay and auto-refreshed.
     */
    public enum ResourcePath {
        ROUND("round.txt"),
        P1_NAME("p1_name.txt"),
        P1_FLAG("p1_flag.png"),
        P1_NATION("fgen/p1_nation.txt"),
        P1_SCORE("p1_score.txt"),
        P2_NAME("p2_name.txt"),
        P2_FLAG("p2_flag.png"),
        P2_NATION("fgen/p2_nation.txt"),
        P2_SCORE("p2_score.txt"),
        COMMS("comms.txt"),
        PLAYER_LIST("fgen/player_list.txt"),
        COMMS_LIST("fgen/comms_list.txt");


        private final String fileName;
        ResourcePath(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public String toString() {
            return fileName;
        }
    }


    /**
     * Represents a "player" or rather a player menu item for combo box selection,
     *  used when searching the player list for records associated with the newly input player name to auto-fill their nationality and tag as saved within the list.
     * @param tag
     * @param name
     * @param nationality
     */
    public record Player(String tag, String name, String nationality) {}

    /**
     * Browses the player list file for records associated with the player name.
     * Doesn't throw on IOException - returns null instead.
     * @param new_player_name
     * @return {@link Player} object representing the player's related fields, null if none are found.
     */
    public static Player search_player_list(String new_player_name) {
        try (BufferedReader player_list = new BufferedReader(new FileReader(Util.path.toAbsolutePath() + "/" + ResourcePath.PLAYER_LIST))) {
            String line, tag, name, natio;
            String[] words;
            while ((line = player_list.readLine()) != null) {
                words = line.split(" ");
                if (words.length > 1)
                    natio = words[1].toUpperCase();
                else natio = "";

                words = words[0].split("\\|");
                if (words.length > 1) {
                    tag = words[0];
                    name = words[1];
                } else {
                    tag = "";
                    name = words[0];
                }

                if (new_player_name.equals(name)) {
                    return new Player(tag, name, natio);
                }

            }
        } catch (IOException ignored) {}

        return null;
    }

}
