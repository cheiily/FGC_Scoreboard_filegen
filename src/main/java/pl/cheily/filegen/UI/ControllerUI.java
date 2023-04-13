package pl.cheily.filegen.UI;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import pl.cheily.filegen.ScoreboardApplication;
import pl.cheily.filegen.Utils.Player;
import pl.cheily.filegen.Utils.ResourcePath;
import pl.cheily.filegen.Utils.Util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pl.cheily.filegen.Utils.IniKey.*;
import static pl.cheily.filegen.Utils.Util.*;

public class ControllerUI implements Initializable {
    public TextField txt_p1_tag;
    public ComboBox<String> combo_p1_natio;
    public ImageView img_p1_flag;
    public TextField txt_p1_score;
    public TextField txt_p2_tag;
    public ComboBox<String> combo_p2_natio;
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

    /**
     * Loads a hardcoded preset of round opts, attempts to load flag/nationality opts, sets the default flag as null,
     * loads radio buttons into a list and disables them all, adds scene toggles to a toggle group
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> r_opts = combo_round.getItems();
        r_opts.add("Winners' R1");
        r_opts.add("Winners' R2");
        r_opts.add("Winners' R3");
        r_opts.add("Winners' R4");
        r_opts.add("Losers' R1");
        r_opts.add("Losers' R2");
        r_opts.add("Losers' R3");
        r_opts.add("Losers' R4");
        r_opts.add("Winners' Quarters");
        r_opts.add("Winners' Semis");
        r_opts.add("Winners' Finals");
        r_opts.add("Losers' Semis");
        r_opts.add("Losers' Finals");
        r_opts.add("Grand Finals");
        r_opts.add("Top 8");
        r_opts.add("Winners' Eights");
        r_opts.add("Losers' Quarters");
        r_opts.add("Losers' Eights");

        ObservableList<String> f1_opts = combo_p1_natio.getItems();
        ObservableList<String> f2_opts = combo_p2_natio.getItems();
        try (Stream<Path> flags = Files.walk(flagsDir)) {
            flags.filter(path -> path.toString().endsWith(".png"))
                    .filter(path ->
                            !path.getFileName().toString().equals(ResourcePath.P1_FLAG.toString())
                            && !path.getFileName().toString().equals(ResourcePath.P2_FLAG.toString()))
                    .map(path -> path.getFileName().toString().split("\\.")[0])
                    .forEach(path -> {
                        f1_opts.add(path.toUpperCase());
                        f2_opts.add(path.toUpperCase());
                    });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        img_p1_flag.setImage(new Image(nullFlag.toString()));
        img_p2_flag.setImage(new Image(nullFlag.toString()));

        radio_buttons.add(radio_reset);
        radio_buttons.add(radio_p1_W);
        radio_buttons.add(radio_p1_L);
        radio_buttons.add(radio_p2_W);
        radio_buttons.add(radio_p2_L);
        radio_buttons.forEach(r -> r.setDisable(true));

        scene_toggle_controller.setSelected(true);
    }


    /**
     * Attempts to save as much as possible by having try-wrapped save operations
     * (see {@link Util#saveFile(String, ResourcePath)} and {@link Util#saveImg(Path, ResourcePath)}),
     * collects failures and shows an alert if any happened.
     */
    public void on_save() {
        if (targetDir == null) {
            new Alert(Alert.AlertType.WARNING, "No path selected.", ButtonType.OK).show();
            return;
        }

        List<String> failedSaves = new ArrayList<>();



        put_meta(SEC_ROUND, KEY_ROUND_LABEL, combo_round.getValue());
        put_meta(SEC_ROUND, KEY_SCORE_1, txt_p1_score.getText());
        put_meta(SEC_ROUND, KEY_SCORE_2, txt_p2_score.getText());
        put_meta(SEC_ROUND, KEY_GF, String.valueOf(GF_toggle.isSelected()));
        put_meta(SEC_ROUND, KEY_GF_RESET, String.valueOf(radio_reset.isSelected()));
        put_meta(SEC_ROUND, KEY_GF_W1, String.valueOf(radio_p1_W.isSelected()));

        put_meta(SEC_P1, KEY_TAG, txt_p1_tag.getText());
        put_meta(SEC_P1, KEY_NAME, combo_p1_name.getValue());
        put_meta(SEC_P1, KEY_NATION, combo_p1_natio.getValue());

        put_meta(SEC_P2, KEY_TAG, txt_p2_tag.getText());
        put_meta(SEC_P2, KEY_NAME, combo_p2_name.getValue());
        put_meta(SEC_P2, KEY_NATION, combo_p2_natio.getValue());

        put_meta(SEC_COMMS, KEY_HOST, combo_host.getValue());
        put_meta(SEC_COMMS, KEY_COMM_1, combo_comm1.getValue());
        put_meta(SEC_COMMS, KEY_COMM_2, combo_comm2.getValue());

        try {
            Files.createDirectories(Path.of(targetDir + "/" + ResourcePath.METADATA.toString().split("/")[0]));
            i_metadata.store(new File(targetDir.toAbsolutePath() + "/" + ResourcePath.METADATA));
        } catch (IOException ignored) {
            failedSaves.add(ResourcePath.METADATA.toString());
        }


        failedSaves.add(saveFile(combo_round.getValue(), ResourcePath.ROUND));

        //Combine the tag with the player name split by '|'
        //Append grands' W/L tag to the name if GF toggle is on
        String temp = txt_p1_tag.getText() == null || txt_p1_tag.getText().isEmpty()
                ? combo_p1_name.getValue()
                : txt_p1_tag.getText() + " | " + combo_p1_name.getValue();
        if (GF_toggle.isSelected()) {
            if (radio_p1_W.isSelected()) temp += " [W]";
            else temp += " [L]";
        }
        if (temp == null) temp = "";
        failedSaves.add(saveFile(temp, ResourcePath.P1_NAME));

        //If no text in nationality combobox - copy the null flag, otherwise pass for saveImg to handle
        //Note: Currently if the text does not correspond to a specific flag file, saveImg will return an error, displaying a save failure alert
        temp = combo_p1_natio.getValue();
        if (temp == null || temp.isEmpty()) temp = "null.png";
        else temp = temp.toLowerCase() + ".png";
        failedSaves.add(saveImg(Path.of(temp), ResourcePath.P1_FLAG));
        failedSaves.add(saveFile(txt_p1_score.getText(), ResourcePath.P1_SCORE));

        //same as above
        temp = txt_p2_tag.getText() == null || txt_p2_tag.getText().isEmpty()
                ? combo_p2_name.getValue()
                : txt_p2_tag.getText() + " | " + combo_p2_name.getValue();
        if (GF_toggle.isSelected()) {
            if (radio_p2_W.isSelected()) temp += " [W]";
            else temp += " [L]";
        }
        failedSaves.add(saveFile(temp, ResourcePath.P2_NAME));

        temp = combo_p2_natio.getValue();
        if (temp == null || temp.isEmpty()) temp = "null.png";
        else temp = temp.toLowerCase() + ".png";
        if (temp.equals(".png")) temp = "null.png";
        failedSaves.add(saveImg(Path.of(temp), ResourcePath.P2_FLAG));
        failedSaves.add(saveFile(txt_p2_score.getText(), ResourcePath.P2_SCORE));

        //Host and comms are compiled to a single file
        temp = ""
                + (combo_host.getValue() == null ? "" : "\uD83C\uDFE0 " + combo_host.getValue() + " \uD83C\uDFE0\n")
                + (combo_comm1.getValue() == null ? "" : "\uD83C\uDF99️ " + combo_comm1.getValue() + " \uD83C\uDF99️ ")
                + (combo_comm2.getValue() == null ? "" : combo_comm2.getValue() + " \uD83C\uDF99️");
        failedSaves.add(saveFile(temp, ResourcePath.COMMS));



        //Display Alert if any errors were found
        failedSaves = failedSaves.stream().filter(Objects::nonNull).toList();
        if (!failedSaves.isEmpty())
            new Alert(Alert.AlertType.ERROR, "Failed saving files: " + failedSaves, ButtonType.OK).show();
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

        if (dir == null) {
            new Alert(Alert.AlertType.WARNING, "No directory selected", ButtonType.OK).show();
            return;
        }

        targetDir = dir.toPath();
        txt_path.setText(dir.toPath().toString());

        try_load_data(dir);
    }

    /**
     * Tries to load as much data as possible by separating each read operation into its own try-catch block.
     * This method is VERY messy, maybe to be cleaned up in the future.
     * Currently does not set radio buttons.
     */
    private void try_load_data(File dir) {
        try {
            i_metadata.load(new File(targetDir.toAbsolutePath() + "/" + ResourcePath.METADATA));

        } catch (IOException ignored) {}

        //round data
        combo_round.setValue(get_meta(SEC_ROUND, KEY_ROUND_LABEL));
        txt_p1_score.setText(String.valueOf(get_meta(SEC_ROUND, KEY_SCORE_1, int.class)));
        txt_p2_score.setText(String.valueOf(get_meta(SEC_ROUND, KEY_SCORE_2, int.class)));

        boolean is_reset = get_meta(SEC_ROUND, KEY_GF_RESET, boolean.class);
        boolean is_p1_w = get_meta(SEC_ROUND, KEY_GF_W1, boolean.class);
        radio_buttons.forEach(r -> r.setDisable(false));

        if (is_reset && !radio_reset.isSelected())
            radio_reset.fire();
        else if (is_p1_w && !radio_p1_W.isSelected())
            radio_p1_W.fire();
        else if (!is_p1_w && radio_p1_W.isSelected())
            radio_p1_W.fire();

        radio_buttons.forEach(r -> r.setDisable(true));

        boolean is_gf = get_meta(SEC_ROUND, KEY_GF, boolean.class);
        if ( (!GF_toggle.isSelected() && is_gf)
                || (GF_toggle.isSelected() && !is_gf)
        ) GF_toggle.fire();

        //p1 data
        combo_p1_name.setValue(get_meta(SEC_P1, KEY_NAME));
        txt_p1_tag.setText(get_meta(SEC_P1, KEY_TAG));
        combo_p1_natio.setValue(get_meta(SEC_P1, KEY_NATION));
        
        //p2 data
        combo_p2_name.setValue(get_meta(SEC_P2, KEY_NAME));
        txt_p2_tag.setText(get_meta(SEC_P2, KEY_TAG));
        combo_p2_natio.setValue(get_meta(SEC_P2, KEY_NATION));

        //comms data
        combo_host.setValue(get_meta(SEC_COMMS, KEY_HOST));
        combo_comm1.setValue(get_meta(SEC_COMMS, KEY_COMM_1));
        combo_comm2.setValue(get_meta(SEC_COMMS, KEY_COMM_2));

        try {
            i_player_list.load(new File(dir.getAbsolutePath() + "/" + ResourcePath.PLAYER_LIST));
            Set<String> players = i_player_list.keySet().stream()
                    .filter(key -> !key.isEmpty())
                    .collect(Collectors.toSet());
            combo_p1_name.getItems().addAll(players);
            combo_p2_name.getItems().addAll(players);
        } catch (IOException ignored) {}

        try {
            i_comms_list.load(new File(dir.getAbsolutePath() + "/" + ResourcePath.COMMS_LIST));
            Set<String> comms = i_comms_list.keySet().stream()
                            .filter(key -> !key.isEmpty())
                            .collect(Collectors.toSet());
            combo_host.getItems().addAll(comms);
            combo_comm1.getItems().addAll(comms);
            combo_comm2.getItems().addAll(comms);
        } catch (IOException ignored) {}

    }

    /**
     * Tries to load a related flag file into the coupled {@link ImageView}, loads the null flag if no related file is found.
     */
    public void on_p1_natio_selection() {
        try {
            img_p1_flag.setImage(new Image(flagsDir + "/" + combo_p1_natio.getValue().toLowerCase() + ".png"));
        } catch (Exception ex) {
            img_p1_flag.setImage(new Image(nullFlag.toString()));
        }
    }

    /**
     * Tries to load a related flag file into the coupled {@link ImageView}, loads the null flag if no related file is found.
     */
    public void on_p2_natio_selection() {
        try {
            img_p2_flag.setImage(new Image(flagsDir + "/" + combo_p2_natio.getValue().toLowerCase() + ".png"));
        } catch (Exception ex) {
            img_p2_flag.setImage(new Image(nullFlag.toString()));
        }
    }

    /**
     * Clears score, sets related fields via {@link Util#search_player_list(String)}
     */
    public void on_p1_selection() {
        txt_p1_score.setText("0");

        if (targetDir == null) return;
        String new_player = combo_p1_name.getValue();

        Player found = search_player_list(new_player);
        if (found == null) {
            txt_p1_tag.setText("");
            combo_p1_natio.setValue("");
        } else {
            txt_p1_tag.setText(found.tag());
            combo_p1_natio.setValue(found.nationality());
        }

    }

    /**
     * Clears score, sets related fields via {@link Util#search_player_list(String)}
     */
    public void on_p2_selection() {
        txt_p2_score.setText("0");

        if (targetDir == null) return;
        String new_player = combo_p2_name.getValue();

        Player found = search_player_list(new_player);
        if (found == null) {
            txt_p2_tag.setText("");
            combo_p2_natio.setValue("");
        } else {
            txt_p2_tag.setText(found.tag());
            combo_p2_natio.setValue(found.nationality());
        }
    }


    /**
     * Score text area scroll listener - increments the value by one on scroll up, decrements by one on scroll down.
     * @param scrollEvent
     */
    public void on_p1_score_scroll(ScrollEvent scrollEvent) {
        txt_p1_score.setText(String.valueOf(
                Integer.parseInt(txt_p1_score.getText()) + Integer.signum((int) scrollEvent.getTextDeltaY())
        ));
    }


    /**
     * Score text area scroll listener - increments the value on scroll up, decrements on scroll down.
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
        scrollOpt(combo_p1_natio, scrollEvent);
    }

    public void on_p2_natio_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_p2_natio, scrollEvent);
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
        scrollOpt(combo_p1_natio, scrollEvent);
    }

    public void on_p2_flag_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_p2_natio, scrollEvent);
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
     * @param actionEvent
     */
    public void on_player_swap(ActionEvent actionEvent) {
        String p1_name = combo_p1_name.getValue();
        String p1_tag = txt_p1_tag.getText();
        String p1_nat = combo_p1_natio.getValue();
        String p1_score = txt_p1_score.getText();
        String p2_name = combo_p2_name.getValue();
        String p2_tag = txt_p2_tag.getText();
        String p2_nat = combo_p2_natio.getValue();
        String p2_score = txt_p2_score.getText();

        combo_p1_name.setValue(p2_name);
        txt_p1_tag.setText(p2_tag);
        combo_p1_natio.setValue(p2_nat);
        txt_p1_score.setText(p2_score);
        combo_p2_name.setValue(p1_name);
        txt_p2_tag.setText(p1_tag);
        combo_p2_natio.setValue(p1_nat);
        txt_p2_score.setText(p1_score);

        if (GF_toggle.isSelected() && !radio_reset.isSelected()) {
            boolean p1w = radio_p1_W.isSelected();
            radio_buttons.forEach(r -> r.setSelected(false));
            if (p1w) {
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
    public void on_GF_toggle(ActionEvent actionEvent) {
        boolean turn_off = !GF_toggle.isSelected();
        radio_buttons.forEach(r -> r.setDisable(turn_off));
    }

    public void on_scene_toggle_config(ActionEvent actionEvent) {
        scene_toggle_config.setSelected(false);
        ScoreboardApplication.setConfigScene();
    }

    public void on_scene_toggle_players(ActionEvent actionEvent) {
        scene_toggle_players.setSelected(false);
        ScoreboardApplication.setPlayersScene();
    }

    public void on_scene_toggle_controller(ActionEvent actionEvent) {
        scene_toggle_controller.setSelected(true);
        ScoreboardApplication.setControllerScene();
    }

}