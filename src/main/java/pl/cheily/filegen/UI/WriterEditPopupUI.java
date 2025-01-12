package pl.cheily.filegen.UI;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Stage;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputWriter;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputWriterType;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class WriterEditPopupUI implements Initializable {
    public Stage stage;
    public TableView table_wrt;
    public TableColumn col_wrt_key;
    public TableColumn col_wrt_val;
    private Map<String, String> _editingWriter = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        col_wrt_key.setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(((TableColumn.CellDataFeatures<String, String>)cellDataFeatures).getValue()));
        col_wrt_val.setCellValueFactory(cellDataFeatures -> {
            var str = ((TableColumn.CellDataFeatures<String, String>)cellDataFeatures).getValue();
            try {
                OutputWriterType type = OutputWriterType.valueOf(str);
                ComboBox<OutputWriterType> comboBox = new ComboBox<>();
                comboBox.getItems().addAll(OutputWriterType.values());
                comboBox.setValue(type);
                return new ReadOnlyObjectWrapper<>(comboBox);
            } catch (Exception e) {

            }
            return new ReadOnlyObjectWrapper<>(str);


        });
        col_wrt_key.setCellValueFactory(new MapValueFactory<>("Key"));
        col_wrt_val.setCellValueFactory(new MapValueFactory<>("Value"));
    }

    public void open(OutputWriter writer) {
        _editingWriter.put("Name", writer.getName());
        _editingWriter.put("Type", writer.getWriterType().name());

        table_wrt.getItems().addAll(_editingWriter.entrySet());
    }

    public void on_cancel() {
        stage.close();
    }

    public void on_save() {
        stage.close();
    }
}
