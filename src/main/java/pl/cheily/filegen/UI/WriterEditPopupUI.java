package pl.cheily.filegen.UI;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Pair;
import pl.cheily.filegen.LocalData.FileManagement.Output.Formatting.OutputFormatterType;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputType;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputWriter;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputWriterType;

import java.net.URL;
import java.util.*;

public class WriterEditPopupUI implements Initializable {
    public Stage stage;
    public TableView<Pair<String, String>> table_wrt;
    public TableColumn col_wrt_key;
    public TableColumn col_wrt_val;
    private List<Pair<String, String>> _editingWriter = new ArrayList<>();
    private ComboBox<OutputFormatterType> _outputFormatterTypeComboBox = new ComboBox<>();
    private ComboBox<OutputWriterType> _outputWriterTypeComboBox = null;

    private <E extends Enum> Optional<ComboBox<? extends Enum>> comboForEnum(Class<E> clazz, String defaultValue) {
        try {
            ComboBox<E> comboBox = new ComboBox<>();
            comboBox.getItems().addAll(clazz.getEnumConstants());
            comboBox.setValue((E)(
                    E.valueOf(clazz, defaultValue)
            ));

            if (clazz.equals(OutputType.class))
                comboBox.valueProperty().addListener((observableValue, oldVal, newVal) -> {
                    System.out.println("OutputType changed to " + newVal);
                    _outputFormatterTypeComboBox.getItems().setAll(OutputFormatterType.getSupportedTypes((OutputType) newVal));
                    _outputFormatterTypeComboBox.getSelectionModel().selectFirst();
                    _outputWriterTypeComboBox.getItems().setAll(OutputWriterType.getSupportingType((OutputType) newVal));
                });
            else if (clazz.equals(OutputFormatterType.class))
                _outputFormatterTypeComboBox = (ComboBox<OutputFormatterType>) comboBox;
            else if (clazz.equals(OutputWriterType.class))
                _outputWriterTypeComboBox = (ComboBox<OutputWriterType>) comboBox;

            return Optional.of(comboBox);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        col_wrt_key.setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(((TableColumn.CellDataFeatures<Pair<String, String>, String>)cellDataFeatures).getValue().getKey()));
        col_wrt_val.setCellValueFactory(cellDataFeatures -> {
            var key = ((TableColumn.CellDataFeatures<Pair<String, String>, String>)cellDataFeatures).getValue().getKey();
            var val = ((TableColumn.CellDataFeatures<Pair<String, String>, String>)cellDataFeatures).getValue().getValue();
            var ret = comboForEnum(OutputType.class, val)
                    .or(() -> comboForEnum(OutputWriterType.class, val))
                    .or(() -> comboForEnum(OutputFormatterType.class, val));

            if (ret.isPresent())
                return new ReadOnlyObjectWrapper<>(ret.get());

            if (key.equals("Enabled?")) {
                CheckBox checkBox = new CheckBox();
                checkBox.setSelected(Boolean.parseBoolean(val));
                return new ReadOnlyObjectWrapper<>(checkBox);
            }

            return new SimpleStringProperty(val);
        });
    }

    public void open(OutputWriter writer) {
        _editingWriter.add(new Pair<>("Name", writer.getName()));
        _editingWriter.add(new Pair<>("Output Type", writer.getOutputType().toString()));
        _editingWriter.add(new Pair<>("Writer Type", writer.getWriterType().toString()));
        _editingWriter.add(new Pair<>("Enabled?", Boolean.toString(writer.isEnabled())));
        _editingWriter.add(new Pair<>("Formatter Name", writer.getFormatter().getName()));
        _editingWriter.add(new Pair<>("Formatter Type", writer.getFormatter().getType().toString()));

        table_wrt.getItems().setAll(_editingWriter);
    }

    public void on_cancel() {
        stage.close();
    }

    public void on_save() {
        stage.close();
    }
}
