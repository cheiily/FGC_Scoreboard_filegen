package pl.cheily.filegen.Utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import pl.cheily.filegen.Configuration.AppConfig;
import pl.cheily.filegen.Configuration.PropKey;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class AutocompleteWrapper implements PropertyChangeListener {
    private final ComboBox<String> box;
    private List<String> originalList;
    private boolean enabled;

    public AutocompleteWrapper(ComboBox<String> comboBox) {
        this(comboBox, true);
    }

    public AutocompleteWrapper(ComboBox<String> comboBox, boolean initialEnable) {
        this.box = comboBox;
        this.originalList = comboBox.getItems();
        this.enabled = initialEnable;
        setupAutocomplete(box);
        AppConfig.subscribe(PropKey.AUTOCOMPLETE_ON, this);
    }


    public void toggleAutocomplete() {
        enabled = !enabled;
    }

    public boolean isAutocompleteEnabled() {
        return enabled;
    }

    public void loadOriginList(List<String> list) {
        originalList = new ArrayList<>(list);
    }

    public void clearSuggestions() {
        box.setItems(FXCollections.observableList(originalList));
    }

    /**
     * @return the contained {@link ComboBox}
     */
    public ComboBox<String> getComboBox() {
        return box;
    }


    private void setupAutocomplete(ComboBox<String> comboBox) {

        comboBox.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, t -> {
            if ( !enabled ) return;
            comboBox.hide();
        });

        comboBox.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

            private boolean moveCaretToPos = false;
            private int caretPos;

            @Override
            public void handle(KeyEvent event) {
                if ( !enabled ) return;

                if ( event.getCode() == KeyCode.UP ) {
                    caretPos = -1;
                    if ( comboBox.getEditor().getText() != null ) {
                        moveCaret(comboBox.getEditor().getText().length());
                    }
                    return;
                } else if ( event.getCode() == KeyCode.DOWN ) {
                    if ( !comboBox.isShowing() ) {
                        comboBox.show();
                    }
                    caretPos = -1;
                    if ( comboBox.getEditor().getText() != null ) {
                        moveCaret(comboBox.getEditor().getText().length());
                    }
                    return;
                } else if ( event.getCode() == KeyCode.BACK_SPACE ) {
                    if ( comboBox.getEditor().getText() != null ) {
                        moveCaretToPos = true;
                        caretPos = comboBox.getEditor().getCaretPosition();
                    }
                } else if ( event.getCode() == KeyCode.DELETE ) {
                    if ( comboBox.getEditor().getText() != null ) {
                        moveCaretToPos = true;
                        caretPos = comboBox.getEditor().getCaretPosition();
                    }
                } else if ( event.getCode() == KeyCode.ENTER ) {
                    return;
                }

                if ( event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT || event.getCode().equals(KeyCode.SHIFT) || event.getCode().equals(KeyCode.CONTROL)
                        || event.isControlDown() || event.getCode() == KeyCode.HOME
                        || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB ) {
                    return;
                }

                ObservableList<String> list = FXCollections.observableArrayList();
                for (String aData : originalList) {
                    if ( aData != null && comboBox.getEditor().getText() != null && aData.toLowerCase().contains(comboBox.getEditor().getText().toLowerCase()) ) {
                        list.add(aData);
                    }
                }

                String t = "";
                if ( comboBox.getEditor().getText() != null ) {
                    t = comboBox.getEditor().getText();
                }

                comboBox.setItems(list);
                comboBox.getEditor().setText(t);
                if ( !moveCaretToPos ) {
                    caretPos = -1;
                }
                moveCaret(t.length());
                if ( !list.isEmpty() ) {
                    comboBox.show();
                }
            }

            private void moveCaret(int textLength) {
                if ( caretPos == -1 ) {
                    comboBox.getEditor().positionCaret(textLength);
                } else {
                    comboBox.getEditor().positionCaret(caretPos);
                }
                moveCaretToPos = false;
            }
        });
    }

    /**
     * Listens to changes on {@link AppConfig#AUTOCOMPLETE_ON()}.
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        this.enabled = (boolean) evt.getNewValue();
    }
}
