package pl.cheily.filegen.UI;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import pl.cheily.filegen.ScoreboardApplication;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfigUI implements Initializable {
    public ToggleButton scene_toggle_config;
    public ToggleButton scene_toggle_players;
    public ToggleButton scene_toggle_controller;
    public CheckBox chk_ignore_case;
    public PasswordField api_key;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scene_toggle_config.setSelected(true);
    }

    public void on_chk_ignore_case(ActionEvent actionEvent) {
    }

    public void on_api_key_input(ActionEvent actionEvent) {
    }

    public void on_scene_toggle_config(ActionEvent actionEvent) {
        scene_toggle_config.setSelected(true);
        ScoreboardApplication.setConfigScene();
    }

    public void on_scene_toggle_players(ActionEvent actionEvent) {
        scene_toggle_players.setSelected(false);
        ScoreboardApplication.setPlayersScene();
    }

    public void on_scene_toggle_controller(ActionEvent actionEvent) {
        scene_toggle_controller.setSelected(false);
        ScoreboardApplication.setControllerScene();
    }

    public void onResetConfig(ActionEvent actionEvent) {
    }
}
