package pl.cheily.filegen.UI;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import pl.cheily.filegen.LocalData.FileManagement.Output.Formatting.FormattingUnit;
import pl.cheily.filegen.LocalData.FileManagement.Output.Formatting.FormattingUnitBuilder;
import pl.cheily.filegen.LocalData.FileManagement.Output.Formatting.FormattingUnitMethodReference;
import pl.cheily.filegen.LocalData.FileManagement.Output.Formatting.OutputFormatterType;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputType;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputWriter;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputWriterType;
import pl.cheily.filegen.LocalData.LocalResourcePath;
import pl.cheily.filegen.ScoreboardApplication;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static pl.cheily.filegen.ScoreboardApplication.dataManager;

public class WriterEditPopupUI implements Initializable {
    private static WriterEditPopupUI _instance;
    public TableView<OutputWriter> config_ui_table;
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
    public TableColumn<FormattingUnitBuilder, String> col_fmt_in;
    public TableColumn<FormattingUnitBuilder, FormattingUnitMethodReference> col_fmt_func;
    public TableColumn<FormattingUnitBuilder, String> col_fmt_temp;
    public TableColumn<FormattingUnitBuilder, String> col_fmt_sample;
    public TableColumn<FormattingUnitBuilder, LocalResourcePath> col_fmt_dest;
    public TableColumn<FormattingUnitBuilder, Object> col_fmt_edit;

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


        col_fmt_edit.setCellFactory(column -> new TableCell<>(){
            private HBox hbox = new HBox();
            private Button button = new Button("⚙");

            {
                button.setOnAction(e -> {
                    Stage popup = new Stage();
                    popup.setTitle("Edit Formatting Unit");

                    FXMLLoader loader = new FXMLLoader(ScoreboardApplication.class.getResource("formatting_unit_edit_popup.fxml"));
                    try {
                        Parent root = loader.load();
                        FmtEditPopupUI controller = loader.getController();
                        controller.open(getTableView().getItems().get(getIndex()));
                        controller.writerEditUI = _instance;
                        controller.stage = popup;
                        _editingFmtIndex = getIndex();
                        Scene scene = new Scene(root);
                        popup.setScene(scene);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    popup.show();
                });

                hbox.getChildren().addAll(button);
                hbox.setAlignment(javafx.geometry.Pos.CENTER);
                setGraphic(hbox);
            }
        });

//        col_fmt_in.setCellFactory(TextFieldTableCell.forTableColumn());
        col_fmt_in.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().inputKeys.size())));

        col_fmt_on.setCellFactory(CheckBoxTableCell.forTableColumn(col_fmt_on));
        col_fmt_on.setCellValueFactory(new PropertyValueFactory<>("enabled"));

        col_fmt_func.setCellFactory(ComboBoxTableCell.forTableColumn(FormattingUnitMethodReference.values()));
        col_fmt_func.setCellValueFactory(new PropertyValueFactory<>("formatType"));
//        col_fmt_func.setOnEditCommit(event -> {
//            if (event.getNewValue() == FormattingUnitMethodReference.CUSTOM_INTERPOLATION) {
//                System.out.println("here");
//            }
//        });


        col_fmt_temp.setCellFactory(TextFieldTableCell.forTableColumn());
        col_fmt_temp.setCellValueFactory(new PropertyValueFactory<>("customInterpolationFormat"));

        col_fmt_sample.setCellFactory(TextFieldTableCell.forTableColumn());
        col_fmt_sample.setCellValueFactory(new PropertyValueFactory<>("sampleOutput"));

        LocalResourcePath[] outputPaths = Arrays.stream(LocalResourcePath.values()).filter(LocalResourcePath::isOutputFile).toArray(LocalResourcePath[]::new);
        col_fmt_dest.setCellFactory(ComboBoxTableCell.forTableColumn(outputPaths));
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

        dataManager.removeWriter(dataManager.getWriter(_ogName));

        List<FormattingUnitBuilder> failed = list_fmt.stream().filter(fu -> !fu.validate()).toList();
        if (!failed.isEmpty()) {
            var alert = new Alert(Alert.AlertType.WARNING, failed.size() + " formatting units are invalid:\n" +
                    failed.stream().map(FormattingUnitBuilder::toString).collect(Collectors.joining())
                    + ".\n\nOnly applying valid changes.", ButtonType.OK);
            alert.show();
        }

        ArrayList<FormattingUnit> formats = list_fmt.stream().filter(FormattingUnitBuilder::validate).map(FormattingUnitBuilder::build).collect(Collectors.toCollection(ArrayList::new));
        formats.addAll(failed.stream().map(fu -> fu.fallbackUnit).toList());

        var newWriter = choice_wtype.getValue().ctor.apply(
            txt_name.getText(),
            choice_ftype.getValue().paramCtor.apply(
                    txt_fmtname.getText(),
                    formats
            )
        );
        if (!chck_enabled.isSelected())
            newWriter.disable();

        dataManager.addWriter(newWriter);

        config_ui_table.getItems().setAll(dataManager.getWriters());
        config_ui_table.refresh();

        if (failed.isEmpty()) stage.close();
    }

    public void accept_fmt_changes(FormattingUnitBuilder newFmt) {
        if (_editingFmtIndex != -1) {
            list_fmt.set(_editingFmtIndex, newFmt);
            table_fmt.refresh();
        }
    }

    public void on_reload_fmt_preset(ActionEvent actionEvent) {
        var cont = new Alert(Alert.AlertType.WARNING, "This will reset any changes made to the formatting unit list below. Are you sure you want to proceed?", ButtonType.YES, ButtonType.CANCEL).showAndWait();
        if (cont.isEmpty() || cont.get() != ButtonType.YES)
            return;

        list_fmt.clear();
        list_fmt.addAll(choice_ftype.getValue().presetSupplier.get().stream().map(FormattingUnitBuilder::from).toList());
        table_fmt.refresh();
    }

    public void on_add_unit(ActionEvent actionEvent) {
        list_fmt.add(new FormattingUnitBuilder());
        table_fmt.refresh();
        table_fmt.scrollTo(list_fmt.size() - 1);
    }

    public void on_delete(ActionEvent actionEvent) {
        if (_ogName == null) {
            stage.close();
        }

        Alert alert = new Alert(Alert.AlertType.WARNING, "Are you sure you want to delete this writer? This action cannot be undone.", ButtonType.YES, ButtonType.CANCEL);
        alert.setTitle("Delete Writer");
        var result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            dataManager.removeWriter(dataManager.getWriter(_ogName));
            config_ui_table.getItems().setAll(dataManager.getWriters());
            config_ui_table.refresh();
            stage.close();
        }
    }
}
