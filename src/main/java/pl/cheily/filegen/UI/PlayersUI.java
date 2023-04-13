package pl.cheily.filegen.UI;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import pl.cheily.filegen.ScoreboardApplication;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PlayersUI implements Initializable {
    public ToggleButton scene_toggle_config;
    public ToggleButton scene_toggle_players;
    public ToggleButton scene_toggle_controller;
    public TextField txt_url;
    public TextField txt_csv_path;
    public TableView player_table;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scene_toggle_players.setSelected(true);
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
        scene_toggle_config.setSelected(false);
        ScoreboardApplication.setConfigScene();
    }

    public void on_scene_toggle_players(ActionEvent actionEvent) {
        scene_toggle_players.setSelected(true);
        ScoreboardApplication.setPlayersScene();
    }

    public void on_scene_toggle_controller(ActionEvent actionEvent) {
        scene_toggle_controller.setSelected(false);
        ScoreboardApplication.setControllerScene();
    }
}
