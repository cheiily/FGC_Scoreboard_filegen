package pl.cheily.filegen.UI;

import javafx.collections.ListChangeListener;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pl.cheily.filegen.LocalData.FileManagement.Meta.Match.MatchDataKey;
import pl.cheily.filegen.LocalData.FileManagement.Output.Formatting.FormattingUnitBuilder;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class FmtInKeysEditPopupUI implements Initializable {

    public Stage stage;

    public MenuButton menu_keys;
    public ListView<MatchDataKey> lst_keys;
    public Button btn_save;
    public WriterEditPopupUI writerEditUI;

    private FormattingUnitBuilder _builder;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        menu_keys.getItems().clear();
        menu_keys.getItems().addAll(Arrays.stream(MatchDataKey.values())
                .map(key -> {
                    CheckMenuItem item = new CheckMenuItem(key.toString());
                    item.setOnAction(event -> {
                        if (item.isSelected()) {
                            lst_keys.getItems().add(key);
                        } else {
                            lst_keys.getItems().remove(key);
                        }
                    });
                    return item;
                }).toList());
    }

    public void open(FormattingUnitBuilder builder) {
        _builder = builder;
        menu_keys.getItems().forEach(item -> {
            if (item instanceof CheckMenuItem) {
                ((CheckMenuItem) item).setSelected(builder.getInputKeys().contains(MatchDataKey.fromString(item.getText())));
            }
        });

        lst_keys.getItems().setAll(builder.getInputKeys());
    }

    public void on_save() {
        _builder.setInputKeys(lst_keys.getItems());
//        writerEditUI.accept_fmt_changes(lst_keys.getItems());
        stage.close();
    }
}
