package pl.cheily.filegen.UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import pl.cheily.filegen.LocalData.DataManager.EventProp;
import pl.cheily.filegen.LocalData.Player;
import pl.cheily.filegen.ScoreboardApplication;

import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Collections;
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
    public TableColumn<Player, String> nat_col;
    public TableColumn<Player, Boolean> chkin_col;

    private final IntegerStringConverter _ISConverter = new IntegerStringConverter();
    private final PropertyChangeListener _listener = evt -> refresh_table();
    public Button buttonDown;
    public Button buttonUp;

    {
        dataManager.subscribe(EventProp.INIT, _listener);
    }

    private void hideMoveButtons() {
        buttonUp.setVisible(false);
        buttonDown.setVisible(false);
        buttonUp.setDisable(true);
        buttonDown.setDisable(true);
    }

    private void showMoveButtons() {
        buttonUp.setVisible(true);
        buttonDown.setVisible(true);
        buttonUp.setDisable(false);
        buttonDown.setDisable(false);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scene_toggle_players.setSelected(true);
        player_table.getColumns().forEach(c -> c.setStyle("-fx-alignment: CENTER"));
        player_table.setItems(playerList);

        seed_col.setCellFactory(TextFieldTableCell.forTableColumn(_ISConverter));
        seed_col.setCellValueFactory(new PropertyValueFactory<>("seed"));
        seed_col.setSortType(TableColumn.SortType.ASCENDING);
        player_table.getSortOrder().add(seed_col);

        tag_col.setCellFactory(TextFieldTableCell.forTableColumn());
        tag_col.setCellValueFactory(new PropertyValueFactory<>("tag"));

        name_col.setCellFactory(TextFieldTableCell.forTableColumn());
        name_col.setCellValueFactory(new PropertyValueFactory<>("name"));

        nat_col.setCellFactory(playerStringTableColumn -> {
            TextFieldTableCell<Player, String> cell = new TextFieldTableCell<>() {
                @Override
                public void updateItem(String val, boolean emptyRow) {
                    super.updateItem(val, emptyRow);
                    if ( emptyRow ) return;

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

        player_table.setRowFactory(playerTableView -> {
            TableRow<Player> row = new TableRow<>();

            row.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
                if ( t1 ) {
                    buttonUp.setLayoutX(
                            player_table.getLayoutX() - buttonUp.getWidth()
                    );
                    buttonUp.setLayoutY(
                            row.localToScene(row.getBoundsInLocal()).getMinY() - 0.5 * row.getHeight()
                    );

                    buttonDown.setLayoutX(
                            player_table.getLayoutX() - buttonDown.getWidth()
                    );
                    buttonDown.setLayoutY(
                            row.localToScene(row.getBoundsInLocal()).getMinY() + 0.5 * row.getHeight()
                    );

                    showMoveButtons();
                }

            });

            return row;
        });

        player_table.setOnSort(tableViewSortEvent -> {
            if ( player_table.getSortOrder().isEmpty() || player_table.getSortOrder().get(0).equals(seed_col) )
                showMoveButtons();
            else hideMoveButtons();
        });

        player_table.setEditable(true);
    }

    public void on_challonge_import(ActionEvent actionEvent) {
    }

    public void on_save(ActionEvent actionEvent) {
        dataManager.removeAllPlayers();
        dataManager.putAllPlayers(playerList);
        if ( !dataManager.saveLists() )
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
        if ( selected == null )
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
        player_table.sort();
        hideMoveButtons();
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

    public void onButtonDown(ActionEvent actionEvent) {
        int selectedI = player_table.getSelectionModel().getSelectedIndex();
        if ( selectedI == player_table.getItems().size() - 1 ) return;

        Player selected = player_table.getItems().get(selectedI);
        Player next = player_table.getItems().get(selectedI + 1);
        int oldSeed = selected.getSeed();

        if ( selected.getSeed() == next.getSeed() ) {
            Collections.swap(playerList, selectedI, selectedI + 1);
        } else {
            selected.setSeed(next.getSeed());
            next.setSeed(oldSeed);
        }
        player_table.sort();
    }

    public void onButtonUp(ActionEvent actionEvent) {
        int selectedI = player_table.getSelectionModel().getSelectedIndex();
        if ( selectedI == 0 ) return;

        Player selected = player_table.getItems().get(selectedI);
        Player previous = player_table.getItems().get(selectedI - 1);
        int oldSeed = selected.getSeed();

        if ( selected.getSeed() == previous.getSeed() ) {
            Collections.swap(playerList, selectedI, selectedI - 1);
        } else {
            selected.setSeed(previous.getSeed());
            previous.setSeed(oldSeed);
        }
        player_table.sort();
    }
}
