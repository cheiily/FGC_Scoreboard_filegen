package pl.cheily.filegen;

import javafx.scene.input.ScrollEvent;
import org.ini4j.Config;
import org.ini4j.Ini;
import pl.cheily.filegen.Util.ResourcePath;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UiController {
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
    private Ini i_metadata = new Ini();
    private Ini i_player_list = new Ini();
    private Ini i_comms_list = new Ini();

    /**
     * Loads a hardcoded preset of round opts, attempts to load flag/nationality opts, sets the default flag as null,
     * loads radio buttons into a list and disables them all
     */
    public void initialize() {
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
        try (Stream<Path> flags = Files.walk(Util.flagsDir)) {
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

        img_p1_flag.setImage(new Image(Util.flagsDir + "/null.png"));
        img_p2_flag.setImage(new Image(Util.flagsDir + "/null.png"));

        radio_buttons.add(radio_reset);
        radio_buttons.add(radio_p1_W);
        radio_buttons.add(radio_p1_L);
        radio_buttons.add(radio_p2_W);
        radio_buttons.add(radio_p2_L);
        radio_buttons.forEach(r -> r.setDisable(true));

        try {
            i_metadata.load(new File(ResourcePath.METADATA.toString()));
        } catch (IOException ignored) {
            //file is created automatically via File ctor
        }
    }


    /**
     * Attempts to save as much as possible by having try-wrapped save operations
     * (see {@link Util#saveFile(String, ResourcePath)} and {@link Util#saveImg(Path, ResourcePath)}),
     * collects failures and shows an alert if any happened.
     */
    public void on_save() {
        if (Util.targetDir == null) {
            new Alert(Alert.AlertType.WARNING, "No path selected.", ButtonType.OK).show();
            return;
        }

        List<String> failedSaves = new ArrayList<>();

        failedSaves.add(Util.saveFile(combo_round.getValue(), ResourcePath.ROUND));

        //Combine the tag with the player name split by '|'
        //Append grands' W/L tag to the name if GF toggle is on
        String temp = txt_p1_tag.getText().isEmpty()
                ? combo_p1_name.getValue()
                : txt_p1_tag.getText() + " | " + combo_p1_name.getValue();
        if (GF_toggle.isSelected()) {
            if (radio_p1_W.isSelected()) temp += " [W]";
            else temp += " [L]";
        }
        if (temp == null) temp = "";
        failedSaves.add(Util.saveFile(temp, ResourcePath.P1_NAME));

        //If no text in nationality combobox - copy the null flag, otherwise pass for saveImg to handle
        //Note: Currently if the text does not correspond to a specific flag file, saveImg will return an error, displaying a save failure alert
        temp = combo_p1_natio.getValue();
        if (temp == null || temp.isEmpty()) temp = "null.png";
        else temp = temp.toLowerCase() + ".png";
        failedSaves.add(Util.saveImg(Path.of(temp), ResourcePath.P1_FLAG));

        temp = combo_p1_natio.getValue() == null ? "" : combo_p1_natio.getValue();
        failedSaves.add(Util.saveFile(temp.toLowerCase(), ResourcePath.P1_NATION));
        failedSaves.add(Util.saveFile(txt_p1_score.getText(), ResourcePath.P1_SCORE));

        //same as above
        temp = txt_p2_tag.getText().isEmpty()
                ? combo_p2_name.getValue()
                : txt_p2_tag.getText() + " | " + combo_p2_name.getValue();
        if (GF_toggle.isSelected()) {
            if (radio_p2_W.isSelected()) temp += " [W]";
            else temp += " [L]";
        }
        failedSaves.add(Util.saveFile(temp, ResourcePath.P2_NAME));

        temp = combo_p2_natio.getValue();
        if (temp == null || temp.isEmpty()) temp = "null.png";
        else temp = temp.toLowerCase() + ".png";
        if (temp.equals(".png")) temp = "null.png";
        failedSaves.add(Util.saveImg(Path.of(temp), ResourcePath.P2_FLAG));
        temp = combo_p2_natio.getValue() == null ? "" : combo_p2_natio.getValue();
        failedSaves.add(Util.saveFile(temp.toLowerCase(), ResourcePath.P2_NATION));
        failedSaves.add(Util.saveFile(txt_p2_score.getText(), ResourcePath.P2_SCORE));

        //Host and comms are compiled to a single file
        temp = ""
                + (combo_host.getValue() == null ? "" : "\uD83C\uDFE0 " + combo_host.getValue() + " \uD83C\uDFE0\n")
                + (combo_comm1.getValue() == null ? "" : "\uD83C\uDF99️ " + combo_comm1.getValue() + " \uD83C\uDF99️ ")
                + (combo_comm2.getValue() == null ? "" : combo_comm2.getValue() + " \uD83C\uDF99️");
        failedSaves.add(Util.saveFile(temp, ResourcePath.COMMS));

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

        Util.targetDir = dir.toPath();
        txt_path.setText(dir.toPath().toString());

        try_load_data(dir);
    }

    /**
     * Tries to load as much data as possible by separating each read operation into its own try-catch block.
     * This method is VERY messy, maybe to be cleaned up in the future.
     * Currently does not set radio buttons.
     */
    private void try_load_data(File dir) {
        String temp;
        String[] temp_arr;

        //radio load aids
        boolean p1L = false,
                p2L = false;


        try (BufferedReader round_file = new BufferedReader(new FileReader(dir.getAbsolutePath() + '/' + ResourcePath.ROUND))) {
            combo_round.setValue(round_file.readLine());
        } catch (IOException | NullPointerException ignored) {}

        try (BufferedReader p1_name_file = new BufferedReader(new FileReader(dir.getAbsolutePath() + '/' + ResourcePath.P1_NAME))) {
            temp = p1_name_file.readLine();

            //Tries to split by the tag-name separator '|', if none is present there will only be one word.
            //Does not load nationality - lets the combo listener handle that
            temp_arr = temp.split("\\|");
            if (temp_arr.length > 1) {
                txt_p1_tag.setText(temp_arr[0].trim());
                //additional split to filter out grand finals tag
                combo_p1_name.setValue(temp_arr[1].trim().split(" ")[0]);
            } else
                combo_p1_name.setValue(temp.split(" ")[0]);

            //load w/l radio
            if (temp.endsWith("[L]")) {
                p1L = true;
            }
        } catch (IOException | NullPointerException ignored) {}

        try (BufferedReader p1_nation_file = new BufferedReader(new FileReader(dir.getAbsolutePath() + '/' + ResourcePath.P1_NATION))) {
            combo_p1_natio.setValue(p1_nation_file.readLine().toUpperCase());
        } catch (IOException | NullPointerException ignored) {}

        try (BufferedReader p1_score_file = new BufferedReader(new FileReader(dir.getAbsolutePath() + '/' + ResourcePath.P1_SCORE))) {
            txt_p1_score.setText(p1_score_file.readLine());
        } catch (IOException | NullPointerException ignored) {}

        //same as above
        try (BufferedReader p2_name_file = new BufferedReader(new FileReader(dir.getAbsolutePath() + '/' + ResourcePath.P2_NAME))) {
            temp = p2_name_file.readLine();
            temp_arr = temp.split("\\|");
            if (temp_arr.length > 1) {
                txt_p2_tag.setText(temp_arr[0].trim());
                combo_p2_name.setValue(temp_arr[1].trim().split(" ")[0]);
            } else
                combo_p2_name.setValue(temp.split(" ")[0]);

            if (temp.endsWith("[L]")) {
                p2L = true;
            }
        } catch (IOException | NullPointerException ignored) {}

        try (BufferedReader p2_nation_file = new BufferedReader(new FileReader(dir.getAbsolutePath() + '/' + ResourcePath.P2_NATION))) {
            combo_p2_natio.setValue(p2_nation_file.readLine().toUpperCase());
        } catch (IOException | NullPointerException ignored) {}

        try (BufferedReader p2_score_file = new BufferedReader(new FileReader(dir.getAbsolutePath() + '/' + ResourcePath.P2_SCORE))) {
            txt_p2_score.setText(p2_score_file.readLine());
        } catch (IOException | NullPointerException ignored) {}

        //set the radio
        //expect toggle to be on after round-name load
        if (GF_toggle.isSelected()) {
            if (p1L && p2L) radio_reset.fire();
            else if (p1L) radio_p1_L.fire();
            else if (p2L) radio_p2_L.fire();
        }

        //As the comms file is all mashed up together, some parsing is necessary
        try (BufferedReader comms_file = new BufferedReader(new InputStreamReader(new FileInputStream(dir.getAbsolutePath() + '/' + ResourcePath.COMMS), StandardCharsets.UTF_8))) {
            while ((temp = comms_file.readLine()) != null) {
                if (temp.isEmpty()) continue;

                //host's line will contain the house emoji
                if (temp.contains("\uD83C\uDFE0")) {
                    temp = temp.split(" ")[1];
                    combo_host.setValue(temp);
                //comms' line will contain the microphone emoji
                } else if (temp.contains("\uD83C\uDF99️")) {
                    temp_arr = temp.split(" +");
                    combo_comm1.setValue(temp_arr[1]);
                    //there may be one or two commentators
                    if (temp_arr.length > 3) combo_comm2.setValue(temp_arr[3]);
                }
            }
        } catch (IOException | NullPointerException ignored) {}


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
            img_p1_flag.setImage(new Image(Util.flagsDir + "/" + combo_p1_natio.getValue().toLowerCase() + ".png"));
        } catch (Exception ex) {
            img_p1_flag.setImage(new Image(Util.flagsDir + "/null.png"));
        }
    }

    /**
     * Tries to load a related flag file into the coupled {@link ImageView}, loads the null flag if no related file is found.
     */
    public void on_p2_natio_selection() {
        try {
            img_p2_flag.setImage(new Image(Util.flagsDir + "/" + combo_p2_natio.getValue().toLowerCase() + ".png"));
        } catch (Exception ex) {
            img_p2_flag.setImage(new Image(Util.flagsDir + "/null.png"));
        }
    }

    /**
     * Clears score, sets related fields via {@link Util#search_player_list(String)}
     */
    public void on_p1_selection() {
        txt_p1_score.setText("0");

        if (Util.targetDir == null) return;
        String new_player = combo_p1_name.getValue();

        Util.Player found = Util.search_player_list(new_player);
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

        if (Util.targetDir == null) return;
        String new_player = combo_p2_name.getValue();

        Util.Player found = Util.search_player_list(new_player);
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
        Util.scrollOpt(combo_p1_name, scrollEvent);
    }

    public void on_p2_name_scroll(ScrollEvent scrollEvent) {
        Util.scrollOpt(combo_p2_name, scrollEvent);
    }
    public void on_p1_natio_scroll(ScrollEvent scrollEvent) {
        Util.scrollOpt(combo_p1_natio, scrollEvent);
    }

    public void on_p2_natio_scroll(ScrollEvent scrollEvent) {
        Util.scrollOpt(combo_p2_natio, scrollEvent);
    }

    public void on_comm1_scroll(ScrollEvent scrollEvent) {
        Util.scrollOpt(combo_comm1, scrollEvent);
    }

    public void on_comm2_scroll(ScrollEvent scrollEvent) {
        Util.scrollOpt(combo_comm2, scrollEvent);
    }

    public void on_host_scroll(ScrollEvent scrollEvent) {
        Util.scrollOpt(combo_host, scrollEvent);
    }

    public void on_p1_flag_scroll(ScrollEvent scrollEvent) {
        Util.scrollOpt(combo_p1_natio, scrollEvent);
    }

    public void on_p2_flag_scroll(ScrollEvent scrollEvent) {
        Util.scrollOpt(combo_p2_natio, scrollEvent);
    }

    public void on_round_scroll(ScrollEvent scrollEvent) {
        Util.scrollOpt(combo_round, scrollEvent);
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

    public void on_player_swap(ActionEvent actionEvent) {
        String p1_score = txt_p1_score.getText();
        String p2_score = txt_p2_score.getText();
        String p1_name = combo_p1_name.getValue();
        String p2_name = combo_p2_name.getValue();

        combo_p1_name.setValue(p2_name);
        combo_p2_name.setValue(p1_name);
        txt_p1_score.setText(p2_score);
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

    public void on_GF_toggle(ActionEvent actionEvent) {
        boolean turn_off = !GF_toggle.isSelected();
        radio_buttons.forEach(r -> r.setDisable(turn_off));
    }
}