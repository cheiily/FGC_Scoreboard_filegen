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

public class Util {
    public static Path path;
    public static Path flags = Path.of("flags").toAbsolutePath();


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

    public static String saveImg(Path source_name, ResourcePath rPath) {
        try {
            Files.copy(Path.of(flags + "/" + source_name),
                    Path.of(path + "/" + rPath),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            return rPath.toString();
        }
        return null;
    }

    public static void makeScrollable(ComboBox<String> comboBox) {
        makeScrollable(comboBox, comboBox);
    }

    public static void makeScrollable(Node event_source, ComboBox<String> content_to_scroll) {
        event_source.setOnScroll(evt -> scrollOpt(content_to_scroll, evt));
    }

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

    public static int wrap(int value, int max) {
        while (value < 0) {
            value = max + value;
        }
        while (value > max) {
            value = max - value;
        }
        if (value == max) value = 0;

        return value;
    }

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


    public record Player(String tag, String name, String nationality) {}

    public static Player search_player_list(String new_player) {
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

                if (new_player.equals(name)) {
                    return new Player(tag, name, natio);
                }

            }
        } catch (IOException ignored) {}

        return null;
    }

}
