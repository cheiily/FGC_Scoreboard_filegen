package pl.cheily.filegen.UI;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import pl.cheily.filegen.ScoreboardApplication;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfigUI implements Initializable {
    public AnchorPane bg_pane;
    public ToggleButton scene_toggle_config;
    public ToggleButton scene_toggle_players;
    public ToggleButton scene_toggle_controller;
    public CheckBox chk_ac_on;
    public PasswordField api_key;
    public CheckBox chk_out_raw;
    public CheckBox chk_out_html;
    public CheckBox chk_out_flags;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scene_toggle_config.setSelected(true);
    }

    public void onSaveConfig(ActionEvent actionEvent) {
    }

    public void onReloadConfig(ActionEvent actionEvent) {
    }

    public void onSelectConfig(ActionEvent actionEvent) {
    }

    public void onResetConfig(ActionEvent actionEvent) {
    }

    /**
     * Prompts the {@link ScoreboardApplication} to display the corresponding scene.
     */
    public void on_scene_toggle_config() {
        scene_toggle_config.setSelected(false);
        ScoreboardApplication.setConfigScene();
    }

    public void on_scene_toggle_players() {
        scene_toggle_players.setSelected(false);
        ScoreboardApplication.setPlayersScene();
    }

    public void on_scene_toggle_controller() {
        scene_toggle_controller.setSelected(true);
        ScoreboardApplication.setControllerScene();
    }

    /**
     * Resets the focus to avoid "sticky" controls.
     */
    public void on_bg_click() {
        bg_pane.requestFocus();
    }
}
