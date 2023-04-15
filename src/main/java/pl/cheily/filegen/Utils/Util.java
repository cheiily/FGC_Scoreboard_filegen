package pl.cheily.filegen.Utils;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.input.ScrollEvent;

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

}
