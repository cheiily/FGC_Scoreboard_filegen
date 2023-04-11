package pl.cheily.filegen.UI;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import pl.cheily.filegen.ScoreboardApplication;

import java.io.IOException;

public class ConfigUI {
    public ToggleButton scene_toggle_config;
    public ToggleButton scene_toggle_players;
    public ToggleButton scene_toggle_controller;
    public ToggleGroup scene_toggles;
    public CheckBox chk_ignore_case;
    public PasswordField api_key;

    public void initalize() {
        scene_toggle_config.setSelected(true);
        scene_toggles = new ToggleGroup();
        scene_toggles.getToggles().add(scene_toggle_controller);
        scene_toggles.getToggles().add(scene_toggle_players);
        scene_toggles.getToggles().add(scene_toggle_config);
    }

    public void on_chk_ignore_case(ActionEvent actionEvent) {
    }

    public void on_api_key_input(ActionEvent actionEvent) {
    }

    public void on_scene_toggle_config(ActionEvent actionEvent) {
        ScoreboardApplication.setConfigScene();
    }

    public void on_scene_toggle_players(ActionEvent actionEvent) {
        ScoreboardApplication.setPlayersScene();
    }

    public void on_scene_toggle_controller(ActionEvent actionEvent) {
        ScoreboardApplication.setControllerScene();
    }
}
