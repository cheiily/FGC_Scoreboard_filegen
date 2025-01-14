package pl.cheily.filegen.UI;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;
import pl.cheily.filegen.LocalData.FileManagement.Output.Formatting.OutputFormatterType;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputType;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputWriter;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputWriterType;

import java.net.URL;
import java.util.*;

import static pl.cheily.filegen.ScoreboardApplication.dataManager;

// If you're a recruiter reading this code to assess my skill, or anybody else, you're probably wondering: what the hell is this????
// Well... Essentially, I wanted to achieve a "vertical table" sort of look, which is impossible in vanilla jfx, so I had to resort to a couple... "hacks".
// Including that, I had to work around the TableView's whole value mapping system, so yes, the tableview itself pretty much only has a cosmetic role here.
// I also didn't quite want to include the whole ControlsFX just for their property sheet (which doesn't even have the look I wanted!), but in retrospect maybe that was the wrong call...
// This is quite possibly the worst piece of code in this entire project, so I'd be grateful if you looked elsewhere for assessment purposes :)
public class WriterEditPopupUI implements Initializable {
    public Stage stage;
    public TableView<Pair<String, String>> table_wrt;
    public TableColumn col_wrt_key;
    public TableColumn col_wrt_val;
    private List<Pair<String, String>> _editingWriter = new ArrayList<>();
    private String _ogName;

    private Map<String, Integer> _keyToIndex = new HashMap<>();
    private List<Object> _indexToProperty = new ArrayList<>();

    private TextField _nameTextField = new TextField();
    private ComboBox<OutputType> _outputTypeComboBox = new ComboBox<>();
    private ComboBox<OutputWriterType> _outputWriterTypeComboBox = null;
    private CheckBox _enabledCheckBox = new CheckBox();
    private TextField _formatterNameTextField = new TextField();
    private ComboBox<OutputFormatterType> _outputFormatterTypeComboBox = new ComboBox<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        col_wrt_key.setCellValueFactory(cellDataFeatures -> new ReadOnlyObjectWrapper<>(((TableColumn.CellDataFeatures<Pair<String, String>, String>)cellDataFeatures).getValue().getKey()));
        col_wrt_val.setCellValueFactory(cellDataFeatures -> {
            var key = ((TableColumn.CellDataFeatures<Pair<String, String>, String>)cellDataFeatures).getValue().getKey();
            return _indexToProperty.get(_keyToIndex.get(key));
        });

        _outputTypeComboBox = new ComboBox<>();
        _outputTypeComboBox.getItems().addAll(OutputType.values());
        _outputTypeComboBox.getSelectionModel().selectFirst();
        _outputTypeComboBox.valueProperty().addListener((observableValue, oldVal, newVal) -> {
            _outputFormatterTypeComboBox.getItems().setAll(OutputFormatterType.getSupportedTypes(newVal));
            _outputFormatterTypeComboBox.getSelectionModel().selectFirst();
            _outputWriterTypeComboBox.getItems().setAll(OutputWriterType.getSupportingType(newVal));
            _outputWriterTypeComboBox.getSelectionModel().selectFirst();
            table_wrt.getItems().set(_keyToIndex.get("Output Type"), new Pair<>("Output Type", newVal == null ? "" : newVal.toString()));
        });

        _outputWriterTypeComboBox = new ComboBox<>();
        _outputWriterTypeComboBox.getItems().addAll(OutputWriterType.values());
        _outputWriterTypeComboBox.getSelectionModel().selectFirst();
        _outputWriterTypeComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(OutputWriterType outputWriterType) {
                return outputWriterType == null ? "" : outputWriterType.toString();
            }

            @Override
            public OutputWriterType fromString(String s) {
                return OutputWriterType.valueOf(s);
            }
        });
        _outputWriterTypeComboBox.valueProperty().addListener((observableValue, oldVal, newVal) -> {
            table_wrt.getItems().set(_keyToIndex.get("Writer Type"), new Pair<>("Writer Type", newVal == null ? "" : newVal.toString()));
        });

        _outputFormatterTypeComboBox = new ComboBox<>();
        _outputFormatterTypeComboBox.getItems().addAll(OutputFormatterType.values());
        _outputFormatterTypeComboBox.getSelectionModel().selectFirst();
        _outputFormatterTypeComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(OutputFormatterType outputFormatterType) {
                return outputFormatterType == null ? "" : outputFormatterType.toString();
            }

            @Override
            public OutputFormatterType fromString(String s) {
                return OutputFormatterType.valueOf(s);
            }
        });
        _outputFormatterTypeComboBox.valueProperty().addListener((observableValue, oldVal, newVal) -> {
            table_wrt.getItems().set(_keyToIndex.get("Formatter Type"), new Pair<>("Formatter Type", newVal == null ? "" : newVal.toString()));
        });


        _nameTextField = new TextField();
        _nameTextField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            table_wrt.getItems().set(_keyToIndex.get("Name"), new Pair<>("Name", newVal));
        });

        _formatterNameTextField = new TextField();
        _formatterNameTextField.textProperty().addListener((observableValue, oldVal, newVal) -> {
            table_wrt.getItems().set(_keyToIndex.get("Formatter Name"), new Pair<>("Formatter Name", newVal));
        });

        _enabledCheckBox = new CheckBox();
        _enabledCheckBox.selectedProperty().addListener((observableValue, oldVal, newVal) -> {
            table_wrt.getItems().set(_keyToIndex.get("Enabled?"), new Pair<>("Enabled?", Boolean.toString(newVal)));
        });

        _keyToIndex.put("Name", 0);
        _indexToProperty.add(new SimpleObjectProperty<>(_nameTextField));
        _keyToIndex.put("Output Type", 1);
        _indexToProperty.add(new SimpleObjectProperty<>(_outputTypeComboBox));
        _keyToIndex.put("Writer Type", 2);
        _indexToProperty.add(new SimpleObjectProperty<>(_outputWriterTypeComboBox));
        _keyToIndex.put("Enabled?", 3);
        _indexToProperty.add(new SimpleObjectProperty<>(_enabledCheckBox));
        _keyToIndex.put("Formatter Name", 4);
        _indexToProperty.add(new SimpleObjectProperty<>(_formatterNameTextField));
        _keyToIndex.put("Formatter Type", 5);
        _indexToProperty.add(new SimpleObjectProperty<>(_outputFormatterTypeComboBox));
    }

    public void open(OutputWriter writer) {
        if (writer != null) {
            _editingWriter.add(new Pair<>("Name", writer.getName()));
            _editingWriter.add(new Pair<>("Output Type", writer.getOutputType().toString()));
            _editingWriter.add(new Pair<>("Writer Type", writer.getWriterType().toString()));
            _editingWriter.add(new Pair<>("Enabled?", Boolean.toString(writer.isEnabled())));
            _editingWriter.add(new Pair<>("Formatter Name", writer.getFormatter().getName()));
            _editingWriter.add(new Pair<>("Formatter Type", writer.getFormatter().getType().toString()));

            table_wrt.getItems().setAll(_editingWriter);
            _ogName = writer.getName();

            _nameTextField.setText(writer.getName());
            _outputTypeComboBox.getSelectionModel().select(writer.getOutputType());
            _outputWriterTypeComboBox.getSelectionModel().select(writer.getWriterType());
            _enabledCheckBox.setSelected(writer.isEnabled());
            _formatterNameTextField.setText(writer.getFormatter().getName());
            _outputFormatterTypeComboBox.getSelectionModel().select(writer.getFormatter().getType());
        } else {
            _editingWriter.add(new Pair<>("Name", ""));
            _editingWriter.add(new Pair<>("Output Type", ""));
            _editingWriter.add(new Pair<>("Writer Type", ""));
            _editingWriter.add(new Pair<>("Enabled?", ""));
            _editingWriter.add(new Pair<>("Formatter Name", ""));
            _editingWriter.add(new Pair<>("Formatter Type", ""));

            table_wrt.getItems().setAll(_editingWriter);
            _ogName = null;

            _nameTextField.setText("");
            _outputTypeComboBox.getSelectionModel().selectFirst();
            _outputWriterTypeComboBox.getSelectionModel().selectFirst();
            _enabledCheckBox.setSelected(false);
            _formatterNameTextField.setText("");
            _outputFormatterTypeComboBox.getSelectionModel().selectFirst();
        }
    }

    private boolean validate() {
        StringBuilder sb = new StringBuilder();
        sb.append("Current settings cannot be applied due to the following reasons:\n");
        boolean ret = true;
        for (var pair : table_wrt.getItems()) {
            if (pair.getValue() == null || pair.getValue().isEmpty()) {
                sb.append(pair.getKey()).append(" cannot be empty.").append('\n');
                ret = false;
            }
        }
        var newName = table_wrt.getItems().get(_keyToIndex.get("Name")).getValue();
        if (!table_wrt.getItems().get(_keyToIndex.get("Name")).getValue().equals(_ogName)) {
            if (dataManager.getWriter(newName) != null) {
                sb.append("Name must be unique.").append('\n');
                ret = false;
            }
        }
        if (!ret) {
            var alert = new Alert(Alert.AlertType.ERROR, sb.toString(), ButtonType.OK);
            alert.show();
        }

        return ret;
    }

    public void on_cancel() {
        stage.close();
    }

    public void on_save() {
        if (!validate())
            return;

        stage.close();
    }
}
