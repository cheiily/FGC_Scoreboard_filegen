package pl.cheily.filegen.UI;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import pl.cheily.filegen.Configuration.AppConfig;
import pl.cheily.filegen.Configuration.PropKey;
import pl.cheily.filegen.ScoreboardApplication;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfigUI implements Initializable {
    public AnchorPane bg_pane;
    public ToggleButton scene_toggle_config;
    public CheckBox chk_ac_on;
    public PasswordField api_key;
    public CheckBox chk_out_raw;
    public CheckBox chk_out_html;
    public CheckBox chk_out_flags;

    /**
     * Listener to automatically update the UI on loads, resets, etc.
     */
    PropertyChangeListener configListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            PropKey prop = PropKey.of(evt.getPropertyName());
            if (prop ==null) return;

            switch (prop) {
                default: return;
                case CHALLONGE_API: api_key.setText(AppConfig.CHALLONGE_API());
                case AUTOCOMPLETE_ON: chk_ac_on.setSelected(AppConfig.AUTOCOMPLETE_ON());
                case MAKE_RAW_OUTPUT: chk_out_raw.setSelected(AppConfig.MAKE_RAW_OUTPUT());
                case MAKE_HTML_OUTPUT: chk_out_html.setSelected(AppConfig.MAKE_HTML_OUTPUT());
                case PUT_FLAGS: chk_out_flags.setSelected(AppConfig.PUT_FLAGS());
            }
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scene_toggle_config.setSelected(true);

        //load initial cfg
        api_key.setText(AppConfig.CHALLONGE_API());
        chk_ac_on.setSelected(AppConfig.AUTOCOMPLETE_ON());
        chk_out_raw.setSelected(AppConfig.MAKE_RAW_OUTPUT());
        chk_out_html.setSelected(AppConfig.MAKE_HTML_OUTPUT());
        chk_out_flags.setSelected(AppConfig.PUT_FLAGS());

        //listen for resets, loads, etc.
        AppConfig.subscribeAll(configListener);
    }

    public void onSaveConfig(ActionEvent actionEvent) {
        AppConfig.CHALLONGE_API(api_key.getText());
        AppConfig.AUTOCOMPLETE_ON(chk_ac_on.isSelected());
        AppConfig.MAKE_RAW_OUTPUT(chk_out_raw.isSelected());
        AppConfig.MAKE_HTML_OUTPUT(chk_out_html.isSelected());
        AppConfig.PUT_FLAGS(chk_out_flags.isSelected());
    }

    public void onReloadConfig(ActionEvent actionEvent) {
    }

    public void onSelectConfig(ActionEvent actionEvent) {
    }

    public void onResetConfig(ActionEvent actionEvent) {
        AppConfig.reset();
    }

    /**
     * Prompts the {@link ScoreboardApplication} to display the corresponding scene.
     */
    public void on_scene_set_config() {
        scene_toggle_config.setSelected(true);
        ScoreboardApplication.setConfigScene();
    }

    public void on_scene_set_players() {
        ScoreboardApplication.setPlayersScene();
    }

    public void on_scene_set_controller() {
        ScoreboardApplication.setControllerScene();
    }

    /**
     * Resets the focus to avoid "sticky" controls.
     */
    public void on_bg_click() {
        bg_pane.requestFocus();
    }
}
