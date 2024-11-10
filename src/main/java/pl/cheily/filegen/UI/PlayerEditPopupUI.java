package pl.cheily.filegen.UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import pl.cheily.filegen.LocalData.Player;

import javafx.scene.control.ListView;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PlayerEditPopupUI implements Initializable {
    ObservableList<String> heads;
    public ListView<String> list_heads;
    ObservableList<String> vals;
    public ListView<String> list_vals;

    public ListView<String> list_uuid_head;
    ObservableList<String> uuid_val;
    public ListView<String> list_uuid_val;

    private Player _editingPlayer;
    public Stage stage;

    public void open(Player player) {
        _editingPlayer = player;

        uuid_val.clear();
        uuid_val.add(player.getUuid().toString());

        vals.clear();
        vals.add(player.getTag());
        vals.add(player.getName());
        vals.add(player.getNationality());
        vals.add(player.getPronouns());
        vals.add(player.getSnsHandle());
        vals.add(Long.toString(player.getRemoteId()));
        vals.add(Long.toString(player.getRemoteSeed()));
        vals.add(player.getRemoteName());
        vals.add(player.getRemoteIconUrl());

        list_heads.setItems(heads);
        list_vals.setItems(vals);
        list_uuid_val.setItems(uuid_val);
    }

    public void on_save() {
        _editingPlayer.setTag(vals.get(0));
        _editingPlayer.setName(vals.get(1));
        _editingPlayer.setNationality(vals.get(2));
        _editingPlayer.setPronouns(vals.get(3));
        _editingPlayer.setSnsHandle(vals.get(4));
        _editingPlayer.setRemoteId(Long.parseLong(vals.get(5)));
        _editingPlayer.setRemoteSeed(Integer.parseInt(vals.get(6)));
        _editingPlayer.setRemoteName(vals.get(7));
        _editingPlayer.setRemoteIconUrl(vals.get(8));

        stage.close();
    }

    public void on_cancel() {
        stage.close();
    }

    public void on_val_commit(ListView.EditEvent<String> event) {
        vals.set(event.getIndex(), event.getNewValue());
        list_vals.getSelectionModel().selectNext();
        list_vals.edit(list_vals.getSelectionModel().getSelectedIndex());
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        list_uuid_head.getItems().setAll("UUID");
        heads = FXCollections.observableArrayList("Tag", "Name", "Nationality", "Pronouns", "SNS Handle", "Remote ID", "Remote Seed", "Remote Name", "Remote Icon URL");
        vals = FXCollections.observableArrayList();
        uuid_val = FXCollections.observableArrayList();
        list_vals.setCellFactory(TextFieldListCell.forListView());

        list_uuid_head.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        list_heads.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        list_uuid_val.setStyle("-fx-alignment: CENTER;");
        list_vals.setStyle("-fx-alignment: CENTER;");
    }

}
