package pl.cheily.filegen.Utils;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.input.ScrollEvent;

import java.util.Comparator;
import java.util.regex.Pattern;

public class Util {
    /**
     * Scrolls the selected option in content_to_scroll.
     * Utilizes {@link #wrap(int, int)} internally to normalize the index value.
     * The amount scrolled will always be 1 per event, the direction however,
     * will be reverse to the sign of evt.getTextDeltaY() - that way a scroll up will scroll the options upwards.
     *
     * @param content_to_scroll
     * @param evt
     */
    public static void scrollOpt(ComboBox<String> content_to_scroll, ScrollEvent evt) {
        ObservableList<String> opts = content_to_scroll.getItems();
        int old_index = opts.indexOf(content_to_scroll.getValue());
        int delta_move = -Integer.signum((int) evt.getTextDeltaY());

        if ( opts.isEmpty() ) {
            content_to_scroll.setValue("");
            return;
        }

        int new_index = wrap(old_index + delta_move, opts.size());
        content_to_scroll.setValue(opts.get(new_index));
    }

    /**
     * Wraps value to be between 0 and exclusive max
     *
     * @param value
     * @param max
     * @return wrapped value
     */
    public static int wrap(int value, int max) {
        if ( value > 0 ) value %= max;
        else while ( value < 0 ) value = max + value;

        return value;
    }

    public static Comparator<String> roundComparator = new Comparator<String>() {
        final Pattern rPattern = Pattern.compile("r\\d");

        private int getOrderIndex(String rLabel) {
            rLabel = rLabel.toLowerCase();
            if (rLabel.contains("gran")) return 0;
            if (rLabel.contains("loser")) {
                if (rLabel.contains("final")) return 1;
                if (rLabel.contains("semi")) return 2;

                if (rLabel.contains("quarter") || rLabel.contains("eight")) return 5;

                if (rLabel.contains("r1")) return 10;
                if (rLabel.contains("r2")) return 11;
                if (rLabel.contains("r3")) return 12;
                if (rLabel.contains("r4")) return 13;
            }
            if (rLabel.contains("winner")) {
                if (rLabel.contains("final")) return 3;
                if (rLabel.contains("semi")) return 4;

                if (rLabel.contains("r1")) return 6;
                if (rLabel.contains("r2")) return 7;
                if (rLabel.contains("r3")) return 8;
                if (rLabel.contains("r4")) return 9;
            }
            if (rPattern.matcher(rLabel).find()) return 14;
            return 15;
        }

        @Override
        public int compare(String o1, String o2) {
            int diff = getOrderIndex(o1) - getOrderIndex(o2);
            return diff == 15 ? o1.compareTo(o2) : diff;
        }
    };

}
