package pl.cheily.filegen.UI;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import pl.cheily.filegen.ScoreboardApplication;

import java.io.IOException;

public class PlayersUI {
    public ToggleButton scene_toggle_config;
    public ToggleButton scene_toggle_players;
    public ToggleButton scene_toggle_controller;
    public ToggleGroup scene_toggles;
    public TextField txt_url;
    public TextField txt_csv_path;
    public TableView player_table;

    public void initalize() {
        scene_toggle_players.setSelected(true);
        scene_toggles = new ToggleGroup();
        scene_toggles.getToggles().add(scene_toggle_controller);
        scene_toggles.getToggles().add(scene_toggle_players);
        scene_toggles.getToggles().add(scene_toggle_config);
    }

    public void on_challonge_import(ActionEvent actionEvent) {
    }

    public void on_save(ActionEvent actionEvent) {
    }

    public void on_pull(ActionEvent actionEvent) {
    }

    public void on_push(ActionEvent actionEvent) {
    }

    public void on_add(ActionEvent actionEvent) {
    }

    public void on_remove(ActionEvent actionEvent) {
    }

    public void on_reload(ActionEvent actionEvent) {
    }

    public void on_csv_load(ActionEvent actionEvent) {
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
