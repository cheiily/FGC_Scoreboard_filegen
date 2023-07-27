package pl.cheily.filegen.UI;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import pl.cheily.filegen.LocalData.DataManager;
import pl.cheily.filegen.LocalData.DataManager.EventProp;
import pl.cheily.filegen.LocalData.Player;
import pl.cheily.filegen.ScoreboardApplication;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

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
    public TableColumn<Player, String> nat_col;
    public TableColumn<Player, Boolean> chkin_col;

    private final IntegerStringConverter _ISConverter = new IntegerStringConverter();
    private final PropertyChangeListener _listener = evt -> refresh_table();
    {
        dataManager.subscribe(EventProp.INIT, _listener);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scene_toggle_players.setSelected(true);
        player_table.getColumns().forEach(c -> c.setStyle("-fx-alignment: CENTER"));
        player_table.setItems(playerList);

        seed_col.setCellFactory(TextFieldTableCell.forTableColumn(_ISConverter));
        seed_col.setCellValueFactory(new PropertyValueFactory<>("seed"));

//        icon_col.setCellValueFactory(playerImageCellDataFeatures -> {
//            if (playerImageCellDataFeatures.getValue().getIconUrl() != null)
//                return new ReadOnlyObjectWrapper<>(new Image(String.valueOf(playerImageCellDataFeatures.getValue().getIconUrl())));
//            else return null;
//        });

        tag_col.setCellFactory(TextFieldTableCell.forTableColumn());
        tag_col.setCellValueFactory(new PropertyValueFactory<>("tag"));

        name_col.setCellFactory(TextFieldTableCell.forTableColumn());
        name_col.setCellValueFactory(new PropertyValueFactory<>("name"));

        nat_col.setCellFactory(playerStringTableColumn -> {
//            ImageView imgView = new ImageView(dataManager.getFlag(playerImageCellDataFeatures.getValue().getNationality()));
//            imgView.preserveRatioProperty().set(true);
//            imgView.setFitHeight(20);

            TextFieldTableCell<Player, String> cell = new TextFieldTableCell<>() {
                @Override
                public void updateItem(String val, boolean emptyRow) {
                    super.updateItem(val, emptyRow);
                    if (emptyRow) return;

                    setBackground(new Background(
                            new BackgroundImage(
                                    dataManager.getFlag(val),
                                    BackgroundRepeat.NO_REPEAT,
                                    BackgroundRepeat.NO_REPEAT,
                                    BackgroundPosition.CENTER,
                                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, false))
                    ));
                }
            };

            //this is necessary
            cell.setConverter(new StringConverter<>() {
                @Override
                public String toString(String s) {
                    return s;
                }

                @Override
                public String fromString(String s) {
                    return s;
                }
            });

            cell.setStyle("-fx-alignment: center; -fx-font-weight: bold");
            return cell;
        });
        nat_col.setCellValueFactory(new PropertyValueFactory<>("nationality"));


        chkin_col.setCellFactory(CheckBoxTableCell.forTableColumn(chkin_col));
        chkin_col.setCellValueFactory(new PropertyValueFactory<>("checkedIn"));

        player_table.setEditable(true);
    }

    public void on_challonge_import(ActionEvent actionEvent) {
    }

    public void on_save(ActionEvent actionEvent) {
        dataManager.removeAllPlayers();
        dataManager.putAllPlayers(playerList);
        if (!dataManager.saveLists())
            new Alert(Alert.AlertType.ERROR, "Failed to save player/commentary list. Changes have been applied to the cached lists.", ButtonType.OK).show();
    }

    public void on_pull(ActionEvent actionEvent) {
    }

    public void on_push(ActionEvent actionEvent) {
    }

    public void on_add(ActionEvent actionEvent) {
        playerList.add(Player.empty());
        player_table.refresh();
    }

    public void on_remove(ActionEvent actionEvent) {
        Player selected = player_table.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;
        playerList.remove(selected);
        player_table.refresh();
    }

    public void on_reload(ActionEvent actionEvent) {
        dataManager.reinitialize();
        refresh_table();
    }

    private void refresh_table() {
        playerList.clear();
        playerList.addAll(dataManager.getAllPlayers());
        player_table.refresh();
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
