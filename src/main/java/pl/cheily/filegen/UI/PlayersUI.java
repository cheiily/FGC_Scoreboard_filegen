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
import pl.cheily.filegen.LocalData.DataEventProp;
import pl.cheily.filegen.LocalData.Player;
import pl.cheily.filegen.ScoreboardApplication;
import pl.cheily.filegen.Utils.PlayerTableUtil;

import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import static pl.cheily.filegen.ScoreboardApplication.dataManager;

public class PlayersUI implements Initializable {
    public AnchorPane bg_pane;
    public ToggleButton scene_toggle_players;
    public TextField txt_url;
    public TextField txt_csv_path;
    public TableView<Player> player_table;
    private final ObservableList<Player> playerList = FXCollections.observableList(List.of());
    public TableColumn<Player, Integer> seed_col;
    public TableColumn<Player, Image> icon_col;
    public TableColumn<Player, String> tag_col;
    public TableColumn<Player, String> name_col;
    public TableColumn<Player, String> nat_col;
    public TableColumn<Player, Boolean> chkin_col;


    private final IntegerStringConverter _ISConverter = new IntegerStringConverter();
    private final PropertyChangeListener _listener = evt -> reload_table();
    public Button buttonDown;
    public Button buttonUp;

    {
        dataManager.subscribe(DataEventProp.INIT, _listener);
    }

    /**
     * Hides & disables {@link #buttonDown}, {@link #buttonUp}
     */
    private void hideMoveButtons() {
        buttonUp.setVisible(false);
        buttonDown.setVisible(false);
        buttonUp.setDisable(true);
        buttonDown.setDisable(true);
    }

    /**
     * Shows & enables {@link #buttonDown}, {@link #buttonUp}
     */
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
        seed_col.setOnEditCommit(
                seedChangeEvent -> {
                    seedChangeEvent.getRowValue().setRemoteSeed(seedChangeEvent.getNewValue());
                    PlayerTableUtil.adjustSeeds(playerList, seedChangeEvent.getRowValue());
                    player_table.sort();
                }
        );

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

    /**
     * Submits changes by completely overwriting {@link ScoreboardApplication#dataManager}'s player list with the new list.
     * Prompts the manager to save the new list to a resource file and displays an alert if the operation fails.
     * @param actionEvent
     */
    public void on_save(ActionEvent actionEvent) {
        dataManager.playersDAO.deleteAll();
        dataManager.playersDAO.setAll(playerList.stream().map(Player::getUuidStr).toList(), playerList);
//        if ( !dataManager.saveLists() )
//            new Alert(Alert.AlertType.ERROR, "Failed to save player/commentary list. Changes have been applied to the cached lists.", ButtonType.OK).show();
    }

    public void on_pull(ActionEvent actionEvent) {
    }

    public void on_push(ActionEvent actionEvent) {
    }

    /**
     * Appends a new empty player to the table. Sets their initial seed as the highest present seed + 1.
     * @param actionEvent
     */
    public void on_add(ActionEvent actionEvent) {
        int newSeed = playerList.stream()
                .max(Comparator.comparingInt(Player::getRemoteSeed))
                .orElseGet(Player::empty)
                .getRemoteSeed() + 1;
        Player newPlayer = Player.empty();
        newPlayer.setRemoteSeed(newSeed);
        playerList.add(newPlayer);

        player_table.refresh();
        hideMoveButtons();
    }

    /**
     * Removes the selected player.
     * @param actionEvent
     */
    public void on_remove(ActionEvent actionEvent) {
        Player selected = player_table.getSelectionModel().getSelectedItem();
        if ( selected == null )
            return;
        playerList.remove(selected);

        player_table.refresh();
        hideMoveButtons();
    }

    /**
     * todo investigate this
     * Re-initializes the {@link ScoreboardApplication#dataManager} and refreshes the table.
     * @param actionEvent
     */
    public void on_reload(ActionEvent actionEvent) {
        dataManager.initialize(dataManager.targetDir);
        reload_table();
    }

    /**
     * Clears and reloads the table data, refreshes and sorts the table.
     */
    private void reload_table() {
        playerList.clear();
        playerList.addAll(dataManager.playersDAO.getAll());
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

    /**
     * A single press of the up/down button will adjust the player's seed by one step up/down depending on the current sorting method.
     * In case there is overlap, adjusts the other player's seed to cause a functional swap.
     *
     * @param actionEvent
     */
    public void onButtonDown(ActionEvent actionEvent) {
        if (seed_col.getSortType() == TableColumn.SortType.ASCENDING)
            PlayerTableUtil.incrementSelectedSeedAndSwap(player_table);
        else PlayerTableUtil.decrementSelectedSeedAndSwap(player_table);
    }

    /**
     * A single press of the up/down button will adjust the player's seed by one step up/down depending on the current sorting method.
     * In case there is overlap, adjusts the other player's seed to cause a functional swap.
     *
     * @param actionEvent
     */
    public void onButtonUp(ActionEvent actionEvent) {
        if (seed_col.getSortType() == TableColumn.SortType.ASCENDING)
            PlayerTableUtil.decrementSelectedSeedAndSwap(player_table);
        else PlayerTableUtil.incrementSelectedSeedAndSwap(player_table);
    }
}
