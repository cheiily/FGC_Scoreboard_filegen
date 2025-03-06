package pl.cheily.filegen.UI;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;
import pl.cheily.filegen.LocalData.FileManagement.Meta.Match.MatchDataKey;
import pl.cheily.filegen.LocalData.FileManagement.Output.Formatting.FormattingUnitBuilder;
import pl.cheily.filegen.LocalData.FileManagement.Output.Formatting.FormattingUnitMethodReference;
import pl.cheily.filegen.LocalData.FileManagement.Output.Formatting.OutputFormatterType;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputType;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputWriter;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputWriterType;
import pl.cheily.filegen.LocalData.ResourcePath;
import pl.cheily.filegen.ScoreboardApplication;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static javafx.scene.control.cell.TextFieldTableCell.forTableColumn;
import static pl.cheily.filegen.ScoreboardApplication.dataManager;

// If you're a recruiter reading this code to assess my skill, or anybody else, you're probably wondering: what the hell is this????
// Well... Essentially, I wanted to achieve a "vertical table" sort of look, which is impossible in vanilla jfx, so I had to resort to a couple... "hacks".
// Including that, I had to work around the TableView's whole value mapping system, so yes, the tableview itself pretty much only has a cosmetic role here.
// I also didn't quite want to include the whole ControlsFX just for their property sheet (which doesn't even have the look I wanted!), but in retrospect maybe that was the wrong call...
// This is quite possibly the worst piece of code in this entire project, so I'd be grateful if you looked elsewhere for assessment purposes :)
public class WriterEditPopupUI implements Initializable {
    private static WriterEditPopupUI _instance;
    public Stage stage;
    /******************WRITER********************/
    private String _ogName;

    public TextField txt_name;
    public ChoiceBox<OutputType> choice_output;
    public ChoiceBox<OutputWriterType> choice_wtype;
    public CheckBox chck_enabled;
    public TextField txt_fmtname;
    public ChoiceBox<OutputFormatterType> choice_ftype;
    public Button btn_fmt_reload;

    /******************FORMATS********************/
    private int _editingFmtIndex = -1;
    public List<FormattingUnitBuilder> list_fmt;
    public TableView<FormattingUnitBuilder> table_fmt;
    public TableColumn<FormattingUnitBuilder, Boolean> col_fmt_on;
    public TableColumn<FormattingUnitBuilder, List<MatchDataKey>> col_fmt_in;
    public TableColumn<FormattingUnitBuilder, Object> col_fmt_func;
    public TableColumn<FormattingUnitBuilder, String> col_fmt_temp;
    public TableColumn<FormattingUnitBuilder, String> col_fmt_sample;
    public TableColumn<FormattingUnitBuilder, ResourcePath> col_fmt_dest;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        _instance = this;
        choice_output.getItems().setAll(OutputType.values());
        choice_output.valueProperty().addListener((observableValue, oldVal, newVal) -> {
            choice_ftype.getItems().setAll(OutputFormatterType.getSupportedTypes(newVal));
            choice_ftype.getSelectionModel().selectFirst();
            choice_wtype.getItems().setAll(OutputWriterType.getSupportingType(newVal));
            choice_wtype.getSelectionModel().selectFirst();
        });

        choice_wtype.getItems().setAll(OutputWriterType.values());

        choice_ftype.getItems().setAll(OutputFormatterType.values());

        btn_fmt_reload.disableProperty().bind(choice_ftype.valueProperty().isNull());

        chck_enabled.textProperty().bind(chck_enabled.selectedProperty().map(b -> b ? "ON" : "OFF"));

        list_fmt = new ArrayList<>();
        table_fmt.setItems(FXCollections.observableList(list_fmt));




        col_fmt_in.setCellFactory(column -> new TableCell<>(){
            private HBox hbox = new HBox();
            private Label label = new Label();
            private Button button = new Button("Edit");

            {
                button.setOnAction(e -> {
                    Stage popup = new Stage();
                    popup.setTitle("Edit Formatting Unit");

                    FXMLLoader loader = new FXMLLoader(ScoreboardApplication.class.getResource("formatting_unit_input_keys_edit_popup.fxml"));
                    try {
                        Parent root = loader.load();
                        FmtInKeysEditPopupUI controller = loader.getController();
                        controller.open(getTableView().getItems().get(getIndex()));
                        controller.writerEditUI = _instance;
                        _editingFmtIndex = getIndex();
                        Scene scene = new Scene(root);
                        popup.setScene(scene);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    popup.show();
                });

                if (getIndex() != -1) {
                    label.textProperty().bind(getTableView().getItems().get(getIndex()).inputKeysProperty().map(keys -> keys.size() + "|"));
                } else
                    label.setText("ER|");
                hbox.getChildren().addAll(label, button);
                setGraphic(hbox);
            }
        });

//        col_fmt_in.setCellFactory(column -> new TableCell<>(){
//            private final MenuButton button = new MenuButton();
//
//            {
//                for (MatchDataKey key : MatchDataKey.values()) {
//                    CheckMenuItem checkMenuItem = new CheckMenuItem(key.toString());
//                    checkMenuItem.setOnAction(e -> {
//                        FormattingUnitBuilder fmt = getTableView().getItems().get(getIndex());
//                        if (checkMenuItem.isSelected()) {
//                            fmt.getInputKeys().add(key);
//                        } else {
//                            fmt.getInputKeys().remove(key);
//                        }
//                        button.setText(fmt.getInputKeys().stream().map(MatchDataKey::toString).collect(Collectors.joining(", ")));
//                    });
//                    button.getItems().add(checkMenuItem);
//                }
//
//                setGraphic(button);
//            }
//
//            //
////            @Override
////            protected void updateItem(Menu item, boolean empty) {
////                super.updateItem(item, empty);
////                if (empty || item == null) {
////                    setGraphic(null);
////                    setText(null);
////                } else {
////                    setGraphic(button);
////                }
////
//////                item.setText(item.getItems().stream().filter((MenuItem it) -> ((CheckMenuItem)it).isSelected()).map(MenuItem::getText).collect(Collectors.joining(", ")));
//////                setText(getTableView().getItems().get(getIndex()).getInputKeys().stream().map(MatchDataKey::toString).collect(Collectors.joining(", ")));
////            }
//        });
//        col_fmt_in.setCellValueFactory(param -> {
//            return new SimpleObjectProperty<>(param.getValue().getInputKeys());
//        });
//        col_fmt_in.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue()));
//        col_fmt_in.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().inputKeys.stream().collect(Collectors.toSet())));

        col_fmt_on.setCellFactory(javafx.scene.control.cell.CheckBoxTableCell.forTableColumn(col_fmt_on));
        col_fmt_on.setCellValueFactory(new PropertyValueFactory<>("enabled"));
//        col_fmt_on.setCellValueFactory(cellDataFeatures -> {
//            try {
//                return JavaBeanBooleanPropertyBuilder.create().bean(cellDataFeatures.getValue()).name("enabled").build();
//            } catch (NoSuchMethodException e) {
//                return null;
//            }
//        });
//        col_fmt_in.setCellFactory(javafx.scene.control.cell.TableCell);
//        col_fmt_in.setCellValueFactory(cellDataFeatures -> new SimpleListProperty<>(cellDataFeatures.getValue().getInputKeys()));
//        col_fmt_in.setCellFactory(formattingUnitBuilderMenuTableColumn -> {
//            var cell = javafx.scene.control.cell.ComboBoxTableCell.<FormattingUnitBuilder, FormattingUnitMethodReference>forTableColumn().call(formattingUnitBuilderMenuTableColumn);
//            cell.setConverter(FormattingUnitBuilder.methodReferenceStringConverter);
//        });
//        col_fmt_in.setCellValueFactory(formattingUnitBuilderComboBoxCellDataFeatures -> {
//            return new SimpleStringProperty(formattingUnitBuilderComboBoxCellDataFeatures.getValue().inputKeys.get(0).toString());
//        });
//        col_fmt_func.setCellFactory(javafx.scene.control.cell.ComboBoxTableCell.forTableColumn(FXCollections.observableList(new ArrayList<>(List.of(FormattingUnitMethodReference.values())))));
//        col_fmt_func.setCellFactory(column -> {
//            var cell = new ComboBoxTableCell<FormattingUnitBuilder, Object>();
////            cell.setConverter(new StringConverter<>() {
////                @Override
////                public String toString(FormattingUnitMethodReference object) {
////                    return object == null ? "" : object.toString();
////                }
////
////                @Override
////                public FormattingUnitMethodReference fromString(String s) {
////                    return FormattingUnitMethodReference.valueOf(s);
////                }
////            });
//            cell.getItems().setAll(FormattingUnitMethodReference.values());
//            return cell;
//        });

        col_fmt_func.setCellFactory(ComboBoxTableCell.forTableColumn(FormattingUnitMethodReference.values()));
        col_fmt_func.setCellValueFactory(new PropertyValueFactory<>("formatType"));
//        col_fmt_func.setCellValueFactory(celldata -> celldata.getValue().formatTypeProperty().map(ref -> (Object) ref));
//        col_fmt_func.setCellValueFactory(cellDataFeatures -> {
//            try {
//                return JavaBeanObjectPropertyBuilder.create().bean(cellDataFeatures.getValue()).name("formatType").build();
//            } catch (NoSuchMethodException e) {
//                throw new RuntimeException(e);
//            }
//        });

        var tftc = TextFieldTableCell.<FormattingUnitBuilder, String>forTableColumn(new DefaultStringConverter());
        col_fmt_temp.setCellFactory(TextFieldTableCell.forTableColumn());
        col_fmt_temp.setCellValueFactory(new PropertyValueFactory<>("customInterpolationFormat"));


        col_fmt_sample.setCellFactory(javafx.scene.control.cell.TextFieldTableCell.forTableColumn());
        col_fmt_sample.setCellValueFactory(new PropertyValueFactory<>("sampleOutput"));

        col_fmt_dest.setCellFactory(javafx.scene.control.cell.ChoiceBoxTableCell.forTableColumn(ResourcePath.values()));
        col_fmt_dest.setCellValueFactory(new PropertyValueFactory<>("destination"));
    }

    public void open(OutputWriter writer) {
        if (writer != null) {
            _ogName = writer.getName();

            txt_name.setText(writer.getName());
            choice_output.getSelectionModel().select(writer.getOutputType());
            choice_wtype.getSelectionModel().select(writer.getWriterType());
            chck_enabled.setSelected(writer.isEnabled());
            txt_fmtname.setText(writer.getFormatter().getName());
            choice_ftype.getSelectionModel().select(writer.getFormatter().getType());

            list_fmt.clear();
            list_fmt.addAll(writer.getFormatter().getFormats().stream().map(FormattingUnitBuilder::from).toList());
        } else {
            _ogName = null;

            txt_name.setText("");
            choice_output.getSelectionModel().selectFirst();
            choice_wtype.getSelectionModel().selectFirst();
            chck_enabled.setSelected(false);
            txt_fmtname.setText("");
            choice_ftype.getSelectionModel().selectFirst();

            list_fmt.clear();
//            table_fmt.getItems().clear();
        }
    }

    private boolean validate() {
        StringBuilder sb = new StringBuilder();
        sb.append("Current settings cannot be applied due to the following reasons:\n");
        boolean ret = true;
        boolean tmp = true;

        tmp = choice_ftype.getValue().supportedWriterTypes.contains(choice_output.getValue());
        if (!tmp) sb.append("• formatter type must support the output type.").append('\n');
        ret &= tmp;

        tmp = choice_wtype.getValue().supportedWriterType == choice_output.getValue();
        if (!tmp) sb.append("• writer type must support the output type.").append('\n');
        ret &= tmp;

        tmp = !txt_name.getText().isEmpty();
        if (!tmp) sb.append("• name cannot be empty.").append('\n');
        ret &= tmp;

        tmp = !txt_fmtname.getText().isEmpty();
        if (!tmp) sb.append("• formatter name cannot be empty.").append('\n');
        ret &= tmp;

        if (!txt_name.getText().equals(_ogName)) {
            tmp = dataManager.getWriter(txt_name.getText()) == null;
            if (!tmp) sb.append("• name must be unique (there is already a writer with this name).").append('\n');
            ret &= tmp;
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

    public void accept_fmt_changes(List<MatchDataKey> keys) {
        if (_editingFmtIndex != -1) {
            list_fmt.get(_editingFmtIndex).setInputKeys(keys);
            table_fmt.refresh();
        }
    }
}
