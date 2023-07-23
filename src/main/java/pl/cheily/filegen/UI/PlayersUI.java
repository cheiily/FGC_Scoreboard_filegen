package pl.cheily.filegen.UI;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import pl.cheily.filegen.LocalData.Player;
import pl.cheily.filegen.ScoreboardApplication;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static pl.cheily.filegen.ScoreboardApplication.dataManager;

public class PlayersUI implements Initializable {
    public AnchorPane bg_pane;
    public ToggleButton scene_toggle_players;
    public TextField txt_url;
    public TextField txt_csv_path;
    public TableView<Player> player_table;
    private final ObservableList<Player> playerList = FXCollections.observableList(dataManager.getAllPlayers());
    public TableColumn<Player, Integer> seed_col;
    public TableColumn<Player, Image> icon_col;
    public TableColumn<Player, String> tag_col;
    public TableColumn<Player, String> name_col;
    public TableColumn<Player, ImageView> nat_col;
    public TableColumn<Player, CheckBox> chkin_col;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scene_toggle_players.setSelected(true);
        player_table.getColumns().forEach(c -> c.setStyle("-fx-alignment: CENTER"));
        player_table.setItems(playerList);

        seed_col.setCellValueFactory(playerIntegerCellDataFeatures -> new ReadOnlyObjectWrapper<>(playerIntegerCellDataFeatures.getValue().seed()));
        icon_col.setCellValueFactory(playerImageCellDataFeatures -> {
            if (playerImageCellDataFeatures.getValue().icon_url() != null)
                return new ReadOnlyObjectWrapper<>(new Image(String.valueOf(playerImageCellDataFeatures.getValue().icon_url())));
            else return null;
        });
        tag_col.setCellValueFactory(playerStringCellDataFeatures -> new ReadOnlyObjectWrapper<>(playerStringCellDataFeatures.getValue().tag()));
        name_col.setCellValueFactory(playerStringCellDataFeatures -> new ReadOnlyObjectWrapper<>(playerStringCellDataFeatures.getValue().name()));
        nat_col.setCellValueFactory(playerImageCellDataFeatures -> {
            ImageView imgView = new ImageView(dataManager.getFlag(playerImageCellDataFeatures.getValue().nationality()));
            imgView.preserveRatioProperty().set(true);
            imgView.setFitHeight(20);
            return new ReadOnlyObjectWrapper<>(imgView);
        });
        chkin_col.setCellValueFactory(playerBooleanCellDataFeatures -> {
            CheckBox cbox = new CheckBox();
            cbox.setSelected(playerBooleanCellDataFeatures.getValue().chk_in());
            cbox.setDisable(true);
            return new ReadOnlyObjectWrapper<>(cbox);
        });

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
        playerList.clear();
        playerList.addAll(dataManager.getAllPlayers());
    }

    public void on_csv_load(ActionEvent actionEvent) {
    }

    public void on_csv_export(ActionEvent actionEvent) {
    }

    /**
     * Prompts the {@link ScoreboardApplication} to display the corresponding scene.
     */
    public void on_scene_set_config() {
        ScoreboardApplication.setConfigScene();
    }

    public void on_scene_set_players() {
        scene_toggle_players.setSelected(true);
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
