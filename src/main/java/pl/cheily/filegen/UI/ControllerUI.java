package pl.cheily.filegen.UI;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import pl.cheily.filegen.LocalData.DataManager;
import pl.cheily.filegen.LocalData.Player;
import pl.cheily.filegen.LocalData.ResourcePath;
import pl.cheily.filegen.ScoreboardApplication;
import pl.cheily.filegen.Utils.AutocompleteWrapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import static pl.cheily.filegen.LocalData.MetaKey.*;
import static pl.cheily.filegen.ScoreboardApplication.dataManager;
import static pl.cheily.filegen.Utils.Util.scrollOpt;

public class ControllerUI implements Initializable {
    public AnchorPane bg_pane;
    public TextField txt_p1_tag;
    public ComboBox<String> combo_p1_nation;
    public ImageView img_p1_flag;
    public TextField txt_p1_score;
    public TextField txt_p2_tag;
    public ComboBox<String> combo_p2_nation;
    public ImageView img_p2_flag;
    public TextField txt_p2_score;
    public TextField txt_path;
    public ComboBox<String> combo_round;
    public ComboBox<String> combo_p1_name;
    public ComboBox<String> combo_p2_name;
    public ComboBox<String> combo_comm1;
    public ComboBox<String> combo_comm2;
    public ComboBox<String> combo_host;
    public RadioButton radio_p1_W;
    public RadioButton radio_p1_L;
    public RadioButton radio_reset;
    public RadioButton radio_p2_L;
    public RadioButton radio_p2_W;
    public ToggleButton GF_toggle;
    public List<RadioButton> radio_buttons = new ArrayList<>();
    public ToggleButton scene_toggle_config;
    public ToggleButton scene_toggle_players;
    public ToggleButton scene_toggle_controller;

    private AutocompleteWrapper ac_p1_name,
            ac_p2_name,
            ac_p1_nation,
            ac_p2_nation,
            ac_round,
            ac_host,
            ac_comm1,
            ac_comm2;
    private List<AutocompleteWrapper> acWrappers;

    /**
     * Loads a hardcoded preset of round opts, attempts to load flag/nationality opts, sets the default flag as null,
     * loads radio buttons into a list and disables them all, adds scene toggles to a toggle group
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        combo_round.getItems().addAll(DataManager.DEFAULT_ROUND_SET);

        ObservableList<String> f1_opts = combo_p1_nation.getItems();
        ObservableList<String> f2_opts = combo_p2_nation.getItems();
        try ( Stream<Path> flags = Files.walk(dataManager.flagsDir) ) {
            flags.filter(path -> path.toString().endsWith(".png"))
                    .filter(path ->
                            !path.getFileName().toString().equals(ResourcePath.P1_FLAG.toString())
                                    && !path.getFileName().toString().equals(ResourcePath.P2_FLAG.toString()))
                    .map(path -> path.getFileName().toString().split("\\.")[ 0 ])
                    .forEach(path -> {
                        f1_opts.add(path.toUpperCase());
                        f2_opts.add(path.toUpperCase());
                    });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        img_p1_flag.setImage(new Image(dataManager.nullFlag.toString()));
        img_p2_flag.setImage(new Image(dataManager.nullFlag.toString()));

        radio_buttons.add(radio_reset);
        radio_buttons.add(radio_p1_W);
        radio_buttons.add(radio_p1_L);
        radio_buttons.add(radio_p2_W);
        radio_buttons.add(radio_p2_L);
        radio_buttons.forEach(r -> r.setDisable(true));

        scene_toggle_controller.setSelected(true);

        ac_p1_name = new AutocompleteWrapper(combo_p1_name);
        ac_p1_nation = new AutocompleteWrapper(combo_p1_nation);
        ac_p2_name = new AutocompleteWrapper(combo_p2_name);
        ac_p2_nation = new AutocompleteWrapper(combo_p2_nation);
        ac_round = new AutocompleteWrapper(combo_round);
        ac_host = new AutocompleteWrapper(combo_host);
        ac_comm1 = new AutocompleteWrapper(combo_comm1);
        ac_comm2 = new AutocompleteWrapper(combo_comm2);
        acWrappers = List.of(ac_p1_name, ac_p2_name, ac_p1_nation, ac_p2_nation, ac_round, ac_host, ac_comm1, ac_comm2);
    }


    /**
     * Issues the {@link ScoreboardApplication#dataManager} to save its contained data.
     * See {@link DataManager#save()}
     */
    public void on_save() {
        if ( !dataManager.isInitialized() ) {
            new Alert(Alert.AlertType.WARNING, "No path selected.", ButtonType.OK).show();
            return;
        }

        dataManager.loadMetadataFromUI(this);
        dataManager.save();

        for (AutocompleteWrapper wrapper : acWrappers) wrapper.clearSuggestions();
    }

    /**
     * Button way of interacting with score.
     */
    public void on_p1_score_up() {
        txt_p1_score.setText(
                String.valueOf(
                        Integer.parseInt(txt_p1_score.getText()) + 1
                ));
    }

    /**
     * Button way of interacting with score.
     */
    public void on_p1_score_down() {
        txt_p1_score.setText(
                String.valueOf(
                        Integer.parseInt(txt_p1_score.getText()) - 1
                ));
    }

    /**
     * Button way of interacting with score.
     */
    public void on_p2_score_up() {
        txt_p2_score.setText(
                String.valueOf(
                        Integer.parseInt(txt_p2_score.getText()) + 1
                ));
    }

    /**
     * Button way of interacting with score.
     */
    public void on_p2_score_down() {
        txt_p2_score.setText(
                String.valueOf(
                        Integer.parseInt(txt_p2_score.getText()) - 1
                ));
    }

    /**
     * Attempts to load any data found in the directory into the app.
     */
    public void on_path_select() {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File("."));
        File dir = dc.showDialog(new Stage());

        if ( dir == null ) {
            new Alert(Alert.AlertType.WARNING, "No directory selected", ButtonType.OK).show();
            return;
        }

        combo_p1_name.getItems().clear();
        combo_p2_name.getItems().clear();
        combo_host.getItems().clear();
        combo_comm1.getItems().clear();
        combo_comm2.getItems().clear();

        dataManager.initialize(dir.toPath().toAbsolutePath());
        txt_path.setText(dir.toPath().toAbsolutePath().toString());

        tryLoadData();
    }

    /**
     * Loads data from the initialized {@link ScoreboardApplication#dataManager} into ui components
     */
    private void tryLoadData() {

        combo_round.getItems().clear();
        combo_round.getItems().addAll(dataManager.getRoundLabels());
        ac_round.loadOriginList(dataManager.getRoundLabels().stream().toList());

        //round data
        combo_round.setValue(dataManager.getMeta(SEC_ROUND, KEY_ROUND_LABEL));
        txt_p1_score.setText(String.valueOf(dataManager.getMeta(SEC_ROUND, KEY_SCORE_1, int.class)));
        txt_p2_score.setText(String.valueOf(dataManager.getMeta(SEC_ROUND, KEY_SCORE_2, int.class)));

        boolean is_reset = dataManager.getMeta(SEC_ROUND, KEY_GF_RESET, boolean.class);
        boolean is_p1_w = dataManager.getMeta(SEC_ROUND, KEY_GF_W1, boolean.class);
        radio_buttons.forEach(r -> r.setDisable(false));

        if ( is_reset && !radio_reset.isSelected() )
            radio_reset.fire();
        else if ( is_p1_w && !radio_p1_W.isSelected() )
            radio_p1_W.fire();
        else if ( !is_p1_w && radio_p1_W.isSelected() )
            radio_p1_W.fire();

        radio_buttons.forEach(r -> r.setDisable(true));

        boolean is_gf = dataManager.getMeta(SEC_ROUND, KEY_GF, boolean.class);
        if ( (!GF_toggle.isSelected() && is_gf)
                || (GF_toggle.isSelected() && !is_gf)
        ) GF_toggle.fire();

        //p1 data
        combo_p1_name.setValue(dataManager.getMeta(SEC_P1, KEY_NAME));
        txt_p1_tag.setText(dataManager.getMeta(SEC_P1, KEY_TAG));
        combo_p1_nation.setValue(dataManager.getMeta(SEC_P1, KEY_NATION));

        //p2 data
        combo_p2_name.setValue(dataManager.getMeta(SEC_P2, KEY_NAME));
        txt_p2_tag.setText(dataManager.getMeta(SEC_P2, KEY_TAG));
        combo_p2_nation.setValue(dataManager.getMeta(SEC_P2, KEY_NATION));

        //comms data
        combo_host.setValue(dataManager.getMeta(SEC_COMMS, KEY_HOST));
        combo_comm1.setValue(dataManager.getMeta(SEC_COMMS, KEY_COMM_1));
        combo_comm2.setValue(dataManager.getMeta(SEC_COMMS, KEY_COMM_2));

        List<String> allPlayers = dataManager.getAllPlayerNames();
        List<String> allComms = dataManager.getAllCommentatorNames();
        combo_p1_name.getItems().addAll(allPlayers);
        combo_p2_name.getItems().addAll(allPlayers);
        combo_host.getItems().addAll(allComms);
        combo_comm1.getItems().addAll(allComms);
        combo_comm2.getItems().addAll(allComms);

        ac_p1_name.loadOriginList(allPlayers);
        ac_p2_name.loadOriginList(allPlayers);
        ac_host.loadOriginList(allComms);
        ac_comm1.loadOriginList(allComms);
        ac_comm2.loadOriginList(allComms);

        for (AutocompleteWrapper acWrapper : acWrappers) {
            acWrapper.clearSuggestions();
        }
    }

    /**
     * Tries to load a related flag file into the coupled {@link ImageView}, loads the null flag if no related file is found.
     * See {@link pl.cheily.filegen.LocalData.DataManager#getFlag(String)}
     */
    public void on_p1_nation_selection() {
        img_p1_flag.setImage(dataManager.getFlag(combo_p1_nation.getValue()));
    }

    /**
     * Tries to load a related flag file into the coupled {@link ImageView}, loads the null flag if no related file is found.
     * See {@link pl.cheily.filegen.LocalData.DataManager#getFlag(String)}
     */
    public void on_p2_nation_selection() {
        img_p2_flag.setImage(dataManager.getFlag(combo_p2_nation.getValue()));
    }

    /**
     * Searches for the selected player via {@link pl.cheily.filegen.LocalData.DataManager#getPlayer(String)}.
     * If no such player is found within the defined set (i.e. equal to {@link Player#EMPTY}),
     * the related fields are not cleared, so as not to overwrite any previously entered data
     * that might be related to the undefined player.
     */
    public void on_p1_selection() {
        txt_p1_score.setText("0");

        Player selected = dataManager.getPlayer(combo_p1_name.getValue())
                .orElse(Player.EMPTY);

        if ( selected != Player.EMPTY ) {
            txt_p1_tag.setText(selected.tag());
            combo_p1_nation.setValue(selected.nationality());
        }
    }

    /**
     * Searches for the selected player via {@link pl.cheily.filegen.LocalData.DataManager#getPlayer(String)}.
     * If no such player is found within the defined set (i.e. equal to {@link Player#EMPTY}),
     * the related fields are not cleared, so as not to overwrite any previously entered data
     * that might be related to the undefined player.
     */
    public void on_p2_selection() {
        txt_p2_score.setText("0");

        Player selected = dataManager.getPlayer(combo_p2_name.getValue())
                .orElse(Player.EMPTY);

        if ( selected != Player.EMPTY ) {
            txt_p2_tag.setText(selected.tag());
            combo_p2_nation.setValue(selected.nationality());
        }
    }


    /**
     * Score text area scroll listener - increments the value by one on scroll up, decrements by one on scroll down.
     *
     * @param scrollEvent
     */
    public void on_p1_score_scroll(ScrollEvent scrollEvent) {
        txt_p1_score.setText(String.valueOf(
                Integer.parseInt(txt_p1_score.getText()) + Integer.signum((int) scrollEvent.getTextDeltaY())
        ));
    }


    /**
     * Score text area scroll listener - increments the value on scroll up, decrements on scroll down.
     *
     * @param scrollEvent
     */
    public void on_p2_score_scroll(ScrollEvent scrollEvent) {
        txt_p2_score.setText(String.valueOf(
                Integer.parseInt(txt_p2_score.getText()) + Integer.signum((int) scrollEvent.getTextDeltaY())
        ));
    }


    //adding scroll listeners in a prettier way than via Util.makeScrollable
    public void on_p1_name_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_p1_name, scrollEvent);
    }

    public void on_p2_name_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_p2_name, scrollEvent);
    }

    public void on_p1_natio_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_p1_nation, scrollEvent);
    }

    public void on_p2_natio_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_p2_nation, scrollEvent);
    }

    public void on_comm1_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_comm1, scrollEvent);
    }

    public void on_comm2_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_comm2, scrollEvent);
    }

    public void on_host_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_host, scrollEvent);
    }

    public void on_p1_flag_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_p1_nation, scrollEvent);
    }

    public void on_p2_flag_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_p2_nation, scrollEvent);
    }

    public void on_round_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_round, scrollEvent);
    }

    /**
     * If the selected round contains "gran" (e.g. Grand Finals), the radio buttons become enabled.
     * Otherwise - they all become disabled.
     */
    public void on_round_select() {
        String temp = combo_round.getValue() == null ? "" : combo_round.getValue();
        if ( (temp.toLowerCase().contains("gran") && !GF_toggle.isSelected())
                || (!temp.toLowerCase().contains("gran") && GF_toggle.isSelected())
        ) GF_toggle.fire();
    }


    /**
     * Radio buttons work in the following way:
     * <ul>
     *     <li>either player's W radio sets the other player's L radio and unsets all other buttons</li>
     *     <li>either player's L radio sets the other player's W radio and unsets all other buttons</li>
     *     <li>reset radio sets both players' L radio and unsets their W buttons</li>
     * </ul>
     */
    public void on_radio_W1() {
        radio_buttons.forEach(r -> r.setSelected(false));
        radio_p1_W.setSelected(true);
        radio_p2_L.setSelected(true);
    }

    /**
     * See {@link #on_radio_W1()}.
     */
    public void on_radio_L1() {
        radio_buttons.forEach(r -> r.setSelected(false));
        radio_p1_L.setSelected(true);
        radio_p2_W.setSelected(true);
    }

    /**
     * See {@link #on_radio_W1()}.
     */
    public void on_radio_reset() {
        radio_buttons.forEach(r -> r.setSelected(false));
        radio_p1_L.setSelected(true);
        radio_p2_L.setSelected(true);
        radio_reset.setSelected(true);
    }

    /**
     * See {@link #on_radio_W1()}.
     */
    public void on_radio_L2() {
        radio_buttons.forEach(r -> r.setSelected(false));
        radio_p1_W.setSelected(true);
        radio_p2_L.setSelected(true);
    }

    /**
     * See {@link #on_radio_W1()}.
     */
    public void on_radio_W2() {
        radio_buttons.forEach(r -> r.setSelected(false));
        radio_p1_L.setSelected(true);
        radio_p2_W.setSelected(true);
    }

    /**
     * Swaps the player data around.
     *
     * @param actionEvent
     */
    public void on_player_swap(ActionEvent actionEvent) {
        String p1_name = combo_p1_name.getValue();
        String p1_tag = txt_p1_tag.getText();
        String p1_nat = combo_p1_nation.getValue();
        String p1_score = txt_p1_score.getText();
        String p2_name = combo_p2_name.getValue();
        String p2_tag = txt_p2_tag.getText();
        String p2_nat = combo_p2_nation.getValue();
        String p2_score = txt_p2_score.getText();

        combo_p1_name.setValue(p2_name);
        txt_p1_tag.setText(p2_tag);
        combo_p1_nation.setValue(p2_nat);
        txt_p1_score.setText(p2_score);
        combo_p2_name.setValue(p1_name);
        txt_p2_tag.setText(p1_tag);
        combo_p2_nation.setValue(p1_nat);
        txt_p2_score.setText(p1_score);

        if ( GF_toggle.isSelected() && !radio_reset.isSelected() ) {
            boolean p1w = radio_p1_W.isSelected();
            radio_buttons.forEach(r -> r.setSelected(false));
            if ( p1w ) {
                radio_p1_L.setSelected(true);
                radio_p2_W.setSelected(true);
            } else {
                radio_p2_L.setSelected(true);
                radio_p1_W.setSelected(true);
            }
        }
    }

    /**
     * Disables or enables the radio buttons.
     */
    public void on_GF_toggle() {
        boolean turn_off = !GF_toggle.isSelected();
        radio_buttons.forEach(r -> r.setDisable(turn_off));
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