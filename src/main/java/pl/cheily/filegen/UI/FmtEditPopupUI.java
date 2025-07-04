package pl.cheily.filegen.UI;

import javafx.collections.ListChangeListener;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.HyperlinkLabel;
import org.controlsfx.control.ListSelectionView;
import pl.cheily.filegen.LocalData.FileManagement.Meta.Match.MatchDataKey;
import pl.cheily.filegen.LocalData.FileManagement.Output.Formatting.FormattingUnitBuilder;
import pl.cheily.filegen.LocalData.FileManagement.Output.Formatting.FormattingUnitMethodReference;
import pl.cheily.filegen.LocalData.ResourcePath;
import pl.cheily.filegen.ScoreboardApplication;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class FmtEditPopupUI implements Initializable {

    public Stage stage;
    public WriterEditPopupUI writerEditUI;
    public HyperlinkLabel hprlnk_fmt;
    private FormattingUnitBuilder _builder;

    public CheckBox chck_enabled;
    public ChoiceBox<ResourcePath> choice_dest;
    public ChoiceBox<FormattingUnitMethodReference> choice_func;
    public ListSelectionView<MatchDataKey> slct_keys;
    public TextArea text_req;
    public TextArea text_format;
    public TextArea text_sample;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        slct_keys.getSourceItems().setAll(MatchDataKey.values());

        ResourcePath[] outputPaths = Arrays.stream(ResourcePath.values()).filter(ResourcePath::isOutputFile).toArray(ResourcePath[]::new);
        choice_dest.getItems().setAll(outputPaths);
        choice_func.getItems().setAll(FormattingUnitMethodReference.values());

        choice_func.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal == FormattingUnitMethodReference.CUSTOM_INTERPOLATION) {
                text_format.setDisable(false);
            } else {
                text_format.setDisable(true);
            }
        });
        choice_func.getSelectionModel().selectedItemProperty().addListener((observable , oldVal, newVal) -> {
            if (newVal != null) {
                List<String> hintList = newVal.getValidInputKeyHint();
                StringBuilder hint = new StringBuilder("");
                for (int i = 0; i < hintList.size(); i++) {
                    hint.append(i).append(": ").append(hintList.get(i));
                    if (i < hintList.size() - 1) hint.append("\t");
                }
                text_req.setText(hint.toString());
            }
        });

        javafx.beans.value.ChangeListener updateSampleListener = (observable, oldValue, newValue) -> {
            if (newValue == null) {
                text_sample.setText("");
                return;
            }
            text_sample.setText(FormattingUnitMethodReference.getSampleOutput(
                    choice_func.getValue(),
                    slct_keys.getTargetItems(),
                    text_format.getText(),
                    slct_keys.getTargetItems().stream().map(MatchDataKey::getSampleValue).toArray(String[]::new)
            ));
        };

        slct_keys.targetItemsProperty().get().addListener(
                (ListChangeListener<? super MatchDataKey>) change -> updateSampleListener.changed(null, null, change));
        choice_func.getSelectionModel().selectedItemProperty().addListener(updateSampleListener);
        text_format.textProperty().addListener(updateSampleListener);

        hprlnk_fmt.setOnAction(actionEvent -> {
            ScoreboardApplication.instance.getHostServices().showDocument("https://cheiily.github.io/FGC_Scoreboard_filegen/articles/formatting-unit/#custom-interpolation-formatting");
        });
    }

    public void open(FormattingUnitBuilder builder) {
        _builder = builder;
        slct_keys.getSourceItems().setAll(MatchDataKey.values());
        slct_keys.getSourceItems().removeAll(builder.inputKeys.get());
        slct_keys.getTargetItems().setAll(builder.inputKeys.get());

        chck_enabled.setSelected(builder.enabled.get());
        choice_func.getSelectionModel().select(builder.formatType.get());
        choice_dest.getSelectionModel().select(builder.destination.get());

        text_format.setText(builder.customInterpolationFormat.get());
        text_sample.setText(builder.sampleOutput.get());
    }

    public void on_save() {
        _builder.enabled.set(chck_enabled.isSelected());
        _builder.destination.set(choice_dest.getValue());
        _builder.formatType.set(choice_func.getValue());
        _builder.inputKeys.set(slct_keys.getTargetItems());
        _builder.customInterpolationFormat.set(text_format.getText());
        _builder.sampleOutput.set(text_sample.getText());
        writerEditUI.accept_fmt_changes(_builder);
        stage.close();
    }

    public void on_cancel() {
        stage.close();
    }
}
