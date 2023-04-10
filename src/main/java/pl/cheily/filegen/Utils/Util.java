package pl.cheily.filegen.Utils;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.input.ScrollEvent;
import org.ini4j.Ini;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;

import static org.ini4j.Profile.*;
import static pl.cheily.filegen.Utils.IniKey.*;

public class Util {
    /**
     * Target directory path
     */
    public static Path targetDir;
    public static final Path flagsDir = Path.of("flags").toAbsolutePath();
    public static final Path nullFlag = Path.of(flagsDir + "/null.png");

    public static Ini i_metadata = new Ini();
    public static Ini i_player_list = new Ini();
    public static Ini i_comms_list = new Ini();

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
                        targetDir + "/" + rPath.toString().split("/")[0]
                ));
            } catch (IOException e) {
                return null;
            }
        }

        try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(targetDir.toString() + '/' + rPath.toString()), StandardCharsets.UTF_8)) {
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
     * If there is no corresponding flag file found, tries to copy the null flag (will display a warning). Will also display an error if the null flag cannot be found.
     * @param source_name name of the flag image to copy
     * @param rPath representing the file to be copied "into".
     * @return null if the operation goes through successfully, rPath.toString() if it fails.
     */
    public static String saveImg(Path source_name, ResourcePath rPath) {
        try {
            Path flagToCopy = Path.of(flagsDir + "/" + source_name);
            Path target = Path.of(targetDir + "/" + rPath);

            if (!Files.exists(flagToCopy)) {
                flagToCopy = nullFlag;
                new Alert(Alert.AlertType.WARNING, "Couldn't find corresponding flag image: " + source_name, ButtonType.OK).show();
            }

            Files.copy(flagToCopy, target,
                    StandardCopyOption.REPLACE_EXISTING);
            Files.setLastModifiedTime(Path.of(targetDir + "/" + rPath), FileTime.from(Instant.now()));
        } catch (Exception e) {
            return rPath.toString();
        }
        return null;
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
     * Looks up the player list file for records associated with the player name, via {@link Ini#get(Object)}.
     * Doesn't throw on IOException - returns null instead.
     * @param new_player_name
     * @return {@link Player} object representing the player's related fields, null if none are found.
     */
    public static Player search_player_list(String new_player_name) {

        try {
            Ini ini = new Ini(new File(Util.targetDir.toAbsolutePath() + "/" + ResourcePath.PLAYER_LIST));
            Section i_new_player = ini.get(new_player_name);
            if (i_new_player == null) return null;

            String tag, natio;
            tag = i_new_player.get(KEY_TAG.toString());
            natio = i_new_player.get(KEY_NATION.toString());

            return new Player(tag, new_player_name, natio);

        } catch (IOException ignored) {}
        return null;
    }

    public static void put_meta(IniKey section, IniKey key, String value) {
        i_metadata.put(section.toString(), key.toString(), value);
    }

    public static String get_meta(IniKey section, IniKey key) {
        String ret = i_metadata.get(section.toString(), key.toString());
        return ret == null ? "" : ret;
    }

    public static <T> T get_meta(IniKey section, IniKey key, Class<T> clazz) {
        return i_metadata.get(section.toString(), key.toString(), clazz);
    }

}
