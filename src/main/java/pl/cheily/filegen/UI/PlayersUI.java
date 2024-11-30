package pl.cheily.filegen.UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import pl.cheily.filegen.LocalData.DataEventProp;
import pl.cheily.filegen.LocalData.Player;
import pl.cheily.filegen.ScoreboardApplication;
import pl.cheily.filegen.Utils.PlayerTableUtil;

import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import static pl.cheily.filegen.ScoreboardApplication.dataManager;

public class PlayersUI implements Initializable {
    enum EditMode {
        PLAYERS,
        COMMENTATORS
    }
    EditMode mode = EditMode.PLAYERS;

    public AnchorPane bg_pane;
    public ToggleButton scene_toggle_players;
    public TextField txt_url;
    public TextField txt_csv_path;
    public TableView<Player> player_table;
    private final ObservableList<Player> playerList = FXCollections.observableList(new ArrayList<>());
    private final ObservableList<Player> commsList = FXCollections.observableList(new ArrayList<>());
    public TableColumn<Player, Integer> seed_col;
    public TableColumn<Player, String> tag_col;
    public TableColumn<Player, String> name_col;
    public TableColumn<Player, String> nat_col;
    public TableColumn<Player, String> pron_col;
    public TableColumn<Player, String> sns_col;



    private final IntegerStringConverter _ISConverter = new IntegerStringConverter();
    private final PropertyChangeListener _listener = evt -> reload_table();
    public Button buttonDown;
    public Button buttonUp;
    public Button buttonCog;
    public Button btn_switch_table;

    {
        dataManager.subscribe(DataEventProp.INIT, _listener);
    }

    /**
     * Hides & disables {@link #buttonDown}, {@link #buttonUp}, {@link #buttonCog}
     */
    private void hideMoveButtons() {
        buttonUp.setVisible(false);
        buttonDown.setVisible(false);
        buttonCog.setVisible(false);
        buttonCog.setDisable(true);
        buttonUp.setDisable(true);
        buttonDown.setDisable(true);
    }

    /**
     * Shows & enables {@link #buttonDown}, {@link #buttonUp}, {@link #buttonCog}
     */
    private void showMoveButtons() {
        buttonUp.setVisible(true);
        buttonDown.setVisible(true);
        buttonCog.setVisible(true);
        buttonCog.setDisable(false);
        buttonUp.setDisable(false);
        buttonDown.setDisable(false);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scene_toggle_players.setSelected(true);
        player_table.getColumns().forEach(c -> c.setStyle("-fx-alignment: CENTER"));
        player_table.setItems(playerList);

        seed_col.setCellFactory(TextFieldTableCell.forTableColumn(_ISConverter));
        seed_col.setCellValueFactory(new PropertyValueFactory<>("remoteSeed"));
        seed_col.setSortType(TableColumn.SortType.ASCENDING);
        player_table.getSortOrder().add(seed_col);
        seed_col.setOnEditCommit(
                seedChangeEvent -> {
                    seedChangeEvent.getRowValue().setRemoteSeed(seedChangeEvent.getNewValue());
                    List<Player> list = switch (mode) {
                        case PLAYERS -> playerList;
                        case COMMENTATORS -> commsList;
                    };
                    PlayerTableUtil.adjustSeeds(list, seedChangeEvent.getRowValue());
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

        pron_col.setCellFactory(TextFieldTableCell.forTableColumn());
        pron_col.setCellValueFactory(new PropertyValueFactory<>("pronouns"));

        sns_col.setCellFactory(TextFieldTableCell.forTableColumn());
        sns_col.setCellValueFactory(new PropertyValueFactory<>("snsHandle"));

        player_table.setRowFactory(playerTableView -> {
            TableRow<Player> row = new TableRow<>();

            row.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
                if ( t1 ) {
                    buttonUp.setLayoutX(
                            player_table.getLayoutX() - buttonUp.getWidth()
                    );
                    buttonUp.setLayoutY(
                            row.localToScene(row.getBoundsInLocal()).getMinY() -  row.getHeight()
                    );

                    buttonCog.setLayoutX(
                            player_table.getLayoutX() - buttonCog.getWidth()
                    );
                    buttonCog.setLayoutY(
                            row.localToScene(row.getBoundsInLocal()).getMinY()
                    );

                    buttonDown.setLayoutX(
                            player_table.getLayoutX() - buttonDown.getWidth()
                    );
                    buttonDown.setLayoutY(
                            row.localToScene(row.getBoundsInLocal()).getMinY() + row.getHeight()
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
        if ( !dataManager.isInitialized() ) {
            new Alert(Alert.AlertType.ERROR, "Working directory is not selected! Cannot save player list.").show();
            return;
        }
        switch (mode) {
            case PLAYERS -> {
                dataManager.playersDAO.deleteAll();
                dataManager.playersDAO.setAll(playerList.stream().map(Player::getUuidStr).toList(), playerList);
            }
            case COMMENTATORS -> {
                dataManager.commentaryDAO.deleteAll();
                dataManager.commentaryDAO.setAll(commsList.stream().map(Player::getUuidStr).toList(), commsList);
            }
        }
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
        List<Player> list = switch (mode) {
            case PLAYERS -> playerList;
            case COMMENTATORS -> commsList;
        };

        int newSeed = list.stream()
                .max(Comparator.comparingInt(Player::getRemoteSeed))
                .orElseGet(Player::getInvalid)
                .getRemoteSeed() + 1;
        Player newPlayer = Player.newEmpty();
        newPlayer.setRemoteSeed(newSeed);
        list.add(newPlayer);

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

        switch (mode) {
            case PLAYERS -> playerList.remove(selected);
            case COMMENTATORS -> commsList.remove(selected);
        }

        player_table.refresh();
        hideMoveButtons();
    }

    /**
     * Re-initializes the {@link ScoreboardApplication#dataManager} and refreshes the table.
     * @param actionEvent
     */
    public void on_reload(ActionEvent actionEvent) {
        if ( !dataManager.isInitialized() ) {
            new Alert(Alert.AlertType.ERROR, "No working directory selected - cannot reload lists!").show();
            return;
        }

        dataManager.playersDAO.refresh();
        dataManager.commentaryDAO.refresh();
        reload_table();
    }

    /**
     * Clears and reloads the table data, refreshes and sorts the table.
     */
    private void reload_table() {
        playerList.clear();
        playerList.addAll(dataManager.playersDAO.getAll());
        commsList.clear();
        commsList.addAll(dataManager.commentaryDAO.getAll());
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
        hideMoveButtons();
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

    public void onButtonCog(ActionEvent actionEvent) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Player Details");

        FXMLLoader loader = new FXMLLoader(ScoreboardApplication.class.getResource("player_edit_popup.fxml"));
        try {
            Parent root = loader.load();
            PlayerEditPopupUI controller = loader.getController();
            controller.open(player_table.getSelectionModel().getSelectedItem());
            controller.stage = popupStage;
            Scene scene = new Scene(root);
            popupStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }

        popupStage.show();
    }

    public void on_switch_table(ActionEvent actionEvent) {
        if (mode == EditMode.PLAYERS)
            mode = EditMode.COMMENTATORS;
        else mode = EditMode.PLAYERS;

        switch (mode) {
            case PLAYERS -> {
                player_table.setItems(playerList);
                seed_col.setEditable(true);
                btn_switch_table.setText("Switch to Comms");
            }
            case COMMENTATORS -> {
                player_table.setItems(commsList);
                seed_col.setEditable(false);
                btn_switch_table.setText("Switch to Players");
            }
        }
        hideMoveButtons();
    }
}
