package pl.cheily.filegen;

import javafx.scene.input.ScrollEvent;
import pl.cheily.filegen.Util.ResourcePath;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class UiController {
    public TextField txt_p1_tag;
    public ComboBox<String> combo_p1_flag;
    public ImageView img_p1_flag;
    public TextField txt_p1_score;
    public TextField txt_p2_tag;
    public ComboBox<String> combo_p2_flag;
    public ImageView img_p2_flag;
    public TextField txt_p2_score;
    public TextField txt_path;
    public ComboBox<String> combo_round;
    public ComboBox<String> combo_p1_name;
    public ComboBox<String> combo_p2_name;
    public ComboBox<String> combo_comm1;
    public ComboBox<String> combo_comm2;
    public ComboBox<String> combo_host;

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

        ObservableList<String> f1_opts = combo_p1_flag.getItems();
        ObservableList<String> f2_opts = combo_p2_flag.getItems();
        try (Stream<Path> flags = Files.walk(Util.flags)) {
            flags.filter(path -> path.toString().contains("."))
                    .filter(path -> !path.getFileName().toString().equals("flags"))
                    .map(path -> path.getFileName().toString().split("\\.")[0])
                    .forEach(path -> {
                        f1_opts.add(path.toUpperCase());
                        f2_opts.add(path.toUpperCase());
                    });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }




    public void on_save(MouseEvent mouseEvent) {
        if (Util.path == null) {
            new Alert(Alert.AlertType.WARNING, "No path selected.", ButtonType.OK).show();
            return;
        }

        List<String> failedSaves = new ArrayList<>();
        try {
            Util.saveFile(combo_round.getValue(), ResourcePath.ROUND);
            
            String temp = txt_p1_tag.getText().isEmpty()
                    ? combo_p1_name.getValue()
                    : txt_p1_tag.getText() + " | " + combo_p1_name.getValue();
            Util.saveFile(temp, ResourcePath.P1_NAME);

            temp = combo_p1_flag.getValue().toLowerCase() + ".png";
            Util.saveImg(Path.of(temp), ResourcePath.P1_FLAG);
            Util.saveFile(combo_p1_flag.getValue().toLowerCase(), ResourcePath.P1_NATION);
            Util.saveFile(txt_p1_score.getText(), ResourcePath.P1_SCORE);
            
            temp = txt_p2_tag.getText().isEmpty()
                    ? combo_p2_name.getValue()
                    : txt_p2_tag.getText() + " | " + combo_p2_name.getValue();
            Util.saveFile(temp, ResourcePath.P2_NAME);

            temp = combo_p2_flag.getValue().toLowerCase() + ".png";
            Util.saveImg(Path.of(temp), ResourcePath.P2_FLAG);
            Util.saveFile(combo_p2_flag.getValue().toLowerCase(), ResourcePath.P2_NATION);
            Util.saveFile(txt_p2_score.getText(), ResourcePath.P2_SCORE);

            temp = ""
                    + (combo_host.getValue().isEmpty() ? "" : "\uD83C\uDFE0 " + combo_host.getValue() + " \uD83C\uDFE0\n")
                    + (combo_comm1.getValue().isEmpty() ? "" : "\uD83C\uDF99️ " + combo_comm1.getValue() + "  ")
                    + (combo_comm2.getValue().isEmpty() ? "" : "\uD83C\uDF99️ " + combo_comm2.getValue());
            Util.saveFile(temp, ResourcePath.COMMS);
            
        } catch (FileSaveException e) {
            failedSaves.add(e.getResourcePath().toString());
        }
        
        if (!failedSaves.isEmpty())
            new Alert(Alert.AlertType.ERROR, "Failed saving files: " + failedSaves, ButtonType.OK).show();
    }

    public void on_p1_score_up(MouseEvent mouseEvent) {
        txt_p1_score.setText(
                String.valueOf(
                        Integer.parseInt(txt_p1_score.getText()) + 1
                ));
    }

    public void on_p1_score_down(MouseEvent mouseEvent) {
        txt_p1_score.setText(
                String.valueOf(
                        Integer.parseInt(txt_p1_score.getText()) - 1
                ));
    }

    public void on_p2_score_up(MouseEvent mouseEvent) {
        txt_p2_score.setText(
                String.valueOf(
                        Integer.parseInt(txt_p2_score.getText()) + 1
                ));
    }

    public void on_p2_score_down(MouseEvent mouseEvent) {
        txt_p2_score.setText(
                String.valueOf(
                        Integer.parseInt(txt_p2_score.getText()) - 1
                ));
    }

    public void on_path_select(MouseEvent mouseEvent) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File("."));
        File dir = dc.showDialog(new Stage());

        if (dir == null) {
            new Alert(Alert.AlertType.WARNING, "No directory selected", ButtonType.OK).show();
            return;
        }

        Util.path = dir.toPath();
        txt_path.setText(dir.toPath().toString());


        try_load_data(dir);
    }


    private void try_load_data(File dir) {
        String temp;
        String[] temp_arr;

        try (BufferedReader round_file = new BufferedReader(new FileReader(dir.getAbsolutePath() + '/' + ResourcePath.ROUND))) {
            combo_round.setValue(round_file.readLine());
        } catch (IOException ignored) {}

        try (BufferedReader p1_name_file = new BufferedReader(new FileReader(dir.getAbsolutePath() + '/' + ResourcePath.P1_NAME))) {
            temp = p1_name_file.readLine();

            temp_arr = temp.split("\\|");
            if (temp_arr.length > 1) {
                txt_p1_tag.setText(temp_arr[0].trim());
                combo_p1_name.setValue(temp_arr[1].trim());
            } else
                combo_p1_name.setValue(temp);
        } catch (IOException ignored) {}

        try (BufferedReader p1_nation_file = new BufferedReader(new FileReader(dir.getAbsolutePath() + '/' + ResourcePath.P1_NATION))) {
            combo_p1_flag.setValue(p1_nation_file.readLine().toUpperCase());
        } catch (IOException ignored) {}

        try (BufferedReader p1_score_file = new BufferedReader(new FileReader(dir.getAbsolutePath() + '/' + ResourcePath.P1_SCORE))) {
            txt_p1_score.setText(p1_score_file.readLine());
        } catch (IOException ignored) {}

        try (BufferedReader p2_name_file = new BufferedReader(new FileReader(dir.getAbsolutePath() + '/' + ResourcePath.P2_NAME))) {
            temp = p2_name_file.readLine();
            temp_arr = temp.split("\\|");
            if (temp_arr.length > 1) {
                txt_p2_tag.setText(temp_arr[0].trim());
                combo_p2_name.setValue(temp_arr[1].trim());
            } else
                combo_p2_name.setValue(temp);
        } catch (IOException ignored) {}

        try (BufferedReader p2_nation_file = new BufferedReader(new FileReader(dir.getAbsolutePath() + '/' + ResourcePath.P2_NATION))) {
            combo_p2_flag.setValue(p2_nation_file.readLine().toUpperCase());
        } catch (IOException ignored) {}

        try (BufferedReader p2_score_file = new BufferedReader(new FileReader(dir.getAbsolutePath() + '/' + ResourcePath.P2_SCORE))) {
            txt_p2_score.setText(p2_score_file.readLine());
        } catch (IOException ignored) {}

        try (BufferedReader comms_file = new BufferedReader(new InputStreamReader(new FileInputStream(dir.getAbsolutePath() + '/' + ResourcePath.COMMS), StandardCharsets.UTF_8))) {
            while ((temp = comms_file.readLine()) != null) {
                if (temp.isEmpty()) continue;

                if (temp.contains("\uD83C\uDFE0")) {
                    temp = temp.split(" ")[1];
                    combo_host.setValue(temp);
                } else if (temp.contains("\uD83C\uDF99️")) {
                    temp_arr = temp.split(" +");
                    combo_comm1.setValue(temp_arr[1]);
                    if (temp_arr.length > 2) combo_comm2.setValue(temp_arr[3]);
                }
            }
        } catch (IOException ignored) {}

        try (BufferedReader player_list = new BufferedReader(new FileReader(dir.getAbsolutePath() + "/" + ResourcePath.PLAYER_LIST))) {
            ObservableList<String> p1_opts = combo_p1_name.getItems();
            ObservableList<String> p2_opts = combo_p2_name.getItems();

            while ((temp = player_list.readLine()) != null) {
                if (temp.isEmpty()) continue;

                temp_arr = temp.split(" ");
                temp_arr = temp_arr[0].split("\\|");
                if (temp_arr.length > 1) {
                    p1_opts.add(temp_arr[1]);
                    p2_opts.add(temp_arr[1]);
                } else {
                    p1_opts.add(temp_arr[0]);
                    p2_opts.add(temp_arr[0]);
                }
            }
        } catch (IOException ignored) {}

        try (BufferedReader comms_list = new BufferedReader(new FileReader(dir.getAbsolutePath() + "/" + ResourcePath.COMMS_LIST))) {
            ObservableList<String> c1_opts = combo_comm1.getItems();
            ObservableList<String> c2_opts = combo_comm2.getItems();
            ObservableList<String> h_opts = combo_host.getItems();

            while ((temp = comms_list.readLine()) != null) {
                if (temp.isEmpty()) continue;

                c1_opts.add(temp);
                c2_opts.add(temp);
                h_opts.add(temp);
            }
        } catch (IOException ignored) {}
    }

    public void on_p1_flag_selection(ActionEvent actionEvent) {
        if (combo_p1_flag.getValue() == null || combo_p1_flag.getValue() == "") {
            img_p1_flag.setImage(null);
            return;
        }
        img_p1_flag.setImage(new Image(Util.flags + "/" + combo_p1_flag.getValue().toLowerCase() + ".png"));
    }

    public void on_p2_flag_selection(ActionEvent actionEvent) {
        if (combo_p2_flag.getValue() == null || combo_p2_flag.getValue() == "") {
            img_p2_flag.setImage(null);
            return;
        }
        img_p2_flag.setImage(new Image(Util.flags + "/" + combo_p2_flag.getValue().toLowerCase() + ".png"));
    }

    public void on_p1_selection(ActionEvent actionEvent) {
        txt_p1_score.setText("0");

        if (Util.path == null) return;
        String new_player = combo_p1_name.getValue();

        Util.Player found = Util.search_player_list(new_player);
        if (found == null) {
            txt_p1_tag.setText("");
            combo_p1_flag.setValue("");
        } else {
            txt_p1_tag.setText(found.tag());
            combo_p1_flag.setValue(found.nationality());
        }

    }

    public void on_p2_selection(ActionEvent actionEvent) {
        txt_p2_score.setText("0");

        if (Util.path == null) return;
        String new_player = combo_p2_name.getValue();

        Util.Player found = Util.search_player_list(new_player);
        if (found == null) {
            txt_p2_tag.setText("");
            combo_p2_flag.setValue("");
        } else {
            txt_p2_tag.setText(found.tag());
            combo_p2_flag.setValue(found.nationality());
        }
    }

    public void on_p1_natio_scroll(ScrollEvent scrollEvent) {
        Util.scrollOpt(combo_p1_flag, scrollEvent);
    }

    public void on_p1_score_scroll(ScrollEvent scrollEvent) {
        txt_p1_score.setText(String.valueOf(
                Integer.parseInt(txt_p1_score.getText()) + Integer.signum((int) scrollEvent.getTextDeltaY())
        ));
    }

    public void on_p2_natio_scroll(ScrollEvent scrollEvent) {
        Util.scrollOpt(combo_p2_flag, scrollEvent);
    }

    public void on_p2_score_scroll(ScrollEvent scrollEvent) {
        txt_p2_score.setText(String.valueOf(
                Integer.parseInt(txt_p2_score.getText()) + Integer.signum((int) scrollEvent.getTextDeltaY())
        ));
    }

    public void on_p1_name_scroll(ScrollEvent scrollEvent) {
        Util.scrollOpt(combo_p1_name, scrollEvent);
    }

    public void on_p2_name_scroll(ScrollEvent scrollEvent) {
        Util.scrollOpt(combo_p2_name, scrollEvent);
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
        Util.scrollOpt(combo_p1_flag, scrollEvent);
    }

    public void on_p2_flag_scroll(ScrollEvent scrollEvent) {
        Util.scrollOpt(combo_p2_flag, scrollEvent);
    }

    public void on_round_scroll(ScrollEvent scrollEvent) {
        Util.scrollOpt(combo_round, scrollEvent);
    }





}