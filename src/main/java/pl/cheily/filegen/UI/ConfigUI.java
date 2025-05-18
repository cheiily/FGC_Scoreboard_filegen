package pl.cheily.filegen.UI;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import pl.cheily.filegen.Configuration.AppConfig;
import pl.cheily.filegen.Configuration.PropKey;
import pl.cheily.filegen.LocalData.DataEventProp;
import pl.cheily.filegen.LocalData.DataManager;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputWriter;
import pl.cheily.filegen.LocalData.Player;
import pl.cheily.filegen.LocalData.ResourcePath;
import pl.cheily.filegen.ScoreboardApplication;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static pl.cheily.filegen.ScoreboardApplication.dataManager;

public class ConfigUI implements Initializable {
    public AnchorPane bg_pane;
    public ToggleButton scene_toggle_config;
    public CheckBox chk_ac_on;
    public PasswordField api_key;
    public CheckBox chk_out_raw;
    public CheckBox chk_out_html;
    public CheckBox chk_out_flags;
    public CheckBox chk_gf_radio;
    public CheckBox chk_comm3_out;
    public ImageView resultImgView;
    public TableView table_writers;
    public TableColumn col_wrt_enabled;
    public TableColumn col_wrt_name;
    public TableColumn col_wrt_outtype;
    public TableColumn col_wrt_fmt_parent;
    public TableColumn col_wrt_fmt_name;
    public TableColumn col_wrt_fmt_count;
    public TableColumn col_wrt_action;

    private final PropertyChangeListener listener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            table_writers.getItems().clear();
            table_writers.getItems().addAll(dataManager.getWriters());
        }
    };
    {
        dataManager.subscribe(DataEventProp.INIT, listener);
        dataManager.subscribe(DataEventProp.CHANGED_OUTPUT_WRITERS, listener);
    }


    private final static Object _resDispLock = new Object();
    private final List<Runnable> resultDisplayQ = new ArrayList<>(1);
    /**
     * Displays a check mark in {@code resultImgView}.
     * <p>
     * Specifically:<ol>
     * <li>adds itself to {@code resultDisplayQ}</li>
     * <li>clears the current image</li>
     * <li>waits 10ms for a visible "refresh" effect as a reaction to a click</li>
     * <li>sets the image as check mark</li>
     * <li>waits 1000ms (1sec)</li>
     * <li>removes itself from the queue</li>
     * <li>checks the queue and clears the image only if it is empty</li>
     * </ol>
     * Synchronizes on {@link ConfigUI#_resDispLock} before steps 2., 4. and 6. and 7. together
     */
    private final Runnable displayOK = new Runnable() {
        @Override
        public void run() {
            resultDisplayQ.add(this);

            synchronized (_resDispLock) {
                resultImgView.setImage(null);
            }
            try {
                Thread.sleep(10);

                synchronized (_resDispLock) {
                    resultImgView.setImage(
                            new Image(DataManager.class.getResource("OtherImages/check-mark.png").toString())
                    );
                }

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                synchronized (_resDispLock) {
                    resultDisplayQ.remove(this);
                    if ( resultDisplayQ.isEmpty() )
                        resultImgView.setImage(null);
                }
            }
        }
    };

    /**
     * Displays a cross mark in {@code resultImgView}.
     * <p>
     * Specifically:<ol>
     * <li>adds itself to {@code resultDisplayQ}</li>
     * <li>clears the current image</li>
     * <li>waits 10ms for a visible "refresh" effect as a reaction to a click</li>
     * <li>sets the image as cross mark</li>
     * <li>waits 1000ms (1sec)</li>
     * <li>removes itself from the queue</li>
     * <li>checks the queue and clears the image only if it is empty</li>
     * </ol>
     * Synchronizes on {@link ConfigUI#_resDispLock} before steps 2., 4. and 6. and 7. together
     */
    private final Runnable displayNOK = new Runnable() {
        @Override
        public void run() {
            resultDisplayQ.add(this);

            synchronized (_resDispLock) {
                resultImgView.setImage(null);
            }
            try {
                Thread.sleep(10);

                synchronized (_resDispLock) {
                    resultImgView.setImage(
                            new Image(DataManager.class.getResource("OtherImages/cross-mark.png").toString())
                    );
                }

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                synchronized (_resDispLock) {
                    resultDisplayQ.remove(this);
                    if ( resultDisplayQ.isEmpty() )
                        resultImgView.setImage(null);
                }
            }
        }
    };

    /**
     * Listener to automatically update the UI on loads, resets, etc.
     */
    PropertyChangeListener configListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            PropKey prop = PropKey.of(evt.getPropertyName());
            if ( prop == null ) return;

            switch (prop) {
                default -> {
                }
                case CHALLONGE_API -> api_key.setText((String) evt.getNewValue());
                case AUTOCOMPLETE_ON -> chk_ac_on.setSelected((Boolean) evt.getNewValue());
                case MAKE_RAW_OUTPUT -> chk_out_raw.setSelected((Boolean) evt.getNewValue());
                case MAKE_HTML_OUTPUT -> chk_out_html.setSelected((Boolean) evt.getNewValue());
                case GF_RADIO_ON_LABEL_MATCH -> chk_gf_radio.setSelected((Boolean) evt.getNewValue());
                case PUT_FLAGS -> chk_out_flags.setSelected((Boolean) evt.getNewValue());
                case WRITE_COMM_3 -> chk_comm3_out.setSelected((Boolean) evt.getNewValue());
            }
        }
    };

    /**
     * Subscribes to {@link AppConfig} for all relevant updates.
     * Initializes UI controls with current configuration state.
     *
     * @param url unused
     * @param resourceBundle unused
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scene_toggle_config.setSelected(true);

        //load initial cfg
        api_key.setText(AppConfig.CHALLONGE_API());
        chk_ac_on.setSelected(AppConfig.AUTOCOMPLETE_ON());
        chk_out_raw.setSelected(AppConfig.MAKE_RAW_OUTPUT());
        chk_out_html.setSelected(AppConfig.MAKE_HTML_OUTPUT());
        chk_gf_radio.setSelected(AppConfig.GF_RADIO_ON_LABEL_MATCH());
        chk_out_flags.setSelected(AppConfig.PUT_FLAGS());
        chk_comm3_out.setSelected(AppConfig.WRITE_COMM_3());

        //listen for resets, loads, etc.
        AppConfig.subscribeAll(configListener);

        col_wrt_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        col_wrt_enabled.setCellValueFactory(cellDataFeatures -> {
            CheckBox chk = new CheckBox();
            chk.setSelected( ((TableColumn.CellDataFeatures<OutputWriter, CheckBox>)cellDataFeatures).getValue().isEnabled() );
            chk.setDisable(true);
            chk.setStyle("-fx-opacity: 1");
            return new ReadOnlyObjectWrapper<>(chk);
        });
        col_wrt_outtype.setCellValueFactory(new PropertyValueFactory<>("outputType"));
        col_wrt_fmt_name.setCellValueFactory(cellDataFeatures ->
                new ReadOnlyObjectWrapper<>(((TableColumn.CellDataFeatures<OutputWriter, String>)cellDataFeatures).getValue().getFormatter().getName())
        );
        col_wrt_fmt_count.setCellValueFactory(cellDataFeatures ->
                new ReadOnlyObjectWrapper<>(((TableColumn.CellDataFeatures<OutputWriter, Integer>)cellDataFeatures).
                        getValue().getFormatter().getFormats().stream().filter(unit -> unit.enabled).toList().size())
        );
        col_wrt_action.setCellValueFactory(cellDataFeatures -> {
            Button btn = new Button("⚙");
            btn.setOnAction(e -> {
                Stage popupStage = new Stage();
                popupStage.setTitle("Output Writer Editor");

                FXMLLoader loader = new FXMLLoader(ScoreboardApplication.class.getResource("writer_edit_popup.fxml"));
                try {
                    Parent root = loader.load();
                    WriterEditPopupUI controller = loader.getController();
                    controller.open(((TableColumn.CellDataFeatures<OutputWriter, Button>)cellDataFeatures).getValue());
                    controller.config_ui_table = table_writers;
                    controller.stage = popupStage;
                    Scene scene = new Scene(root);
                    popupStage.setScene(scene);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                popupStage.show();
            });
            return new ReadOnlyObjectWrapper<>(btn);
        });
        col_wrt_action.setGraphic(new Button("+"));
        table_writers.getItems().addAll(dataManager.getWriters());
    }

    /**
     * Attempts to save relevant configuration fields one by one, will save only the valid updates.
     * Displays result.
     *
     * @see ConfigUI#displayOK
     * @see ConfigUI#displayNOK
     */
    public void onApplyConfig() {
        boolean success = AppConfig.CHALLONGE_API(api_key.getText())
                && AppConfig.AUTOCOMPLETE_ON(chk_ac_on.isSelected())
                && AppConfig.MAKE_RAW_OUTPUT(chk_out_raw.isSelected())
                && AppConfig.MAKE_HTML_OUTPUT(chk_out_html.isSelected())
                && AppConfig.GF_RADIO_ON_LABEL_MATCH(chk_gf_radio.isSelected())
                && AppConfig.PUT_FLAGS(chk_out_flags.isSelected())
                && AppConfig.WRITE_COMM_3(chk_comm3_out.isSelected());

        if ( !success ) {
            new Thread(displayNOK).start();
            new Alert(AlertType.ERROR, "Some settings were invalid and could not be applied.", ButtonType.OK).show();
        } else
            new Thread(displayOK).start();
    }

    /**
     * Attempts to store settings to {@link ResourcePath#CONFIG}.
     * Applies them on success, asks where they should be applied on failure.<br>
     * Displays result.
     *
     * @see ConfigUI#onApplyConfig()
     * @see ConfigUI#displayOK
     * @see ConfigUI#displayNOK
     */
    public void onSaveConfig() {
        if ( !dataManager.isInitialized() ) {
            new Thread(displayNOK).start();
            onApplyConfig();
            new Alert(AlertType.WARNING, "No working directory selected - cannot save config to file! Changes were applied to the running configuration.").show();
            return;
        }

        if ( dataManager.configDAO.saveAll() ) {
            onApplyConfig();
            new Thread(displayOK).start();
        } else {
            new Thread(displayNOK).start();
            new Alert(AlertType.WARNING, "Couldn't store configuration to " + ResourcePath.CONFIG + "! Changes were applied.").show();
            onApplyConfig();
        }


//        var names = dataManager.getWriters().stream().map(w -> w.getName()).toList();
//        dataManager.outputWriterDAO.setAll(names, dataManager.getWriters());
    }

    /**
     * Attempts to reload configuration from the current path.<br>
     * Displays result.
     *
     * @see ConfigUI#displayOK
     * @see ConfigUI#displayNOK
     */
    public void onReloadConfig() {
        if ( !dataManager.isInitialized() ) {
            new Alert(AlertType.ERROR, "No working directory selected - cannot reload configuration!").show();
            return;
        }

        if ( !dataManager.configDAO.loadAll() ) {
            new Thread(displayNOK).start();
            new Alert(AlertType.WARNING, "Couldn't load configuration from " + ResourcePath.CONFIG + ".").show();
        } else
            new Thread(displayOK).start();
    }

    /**
     * Resets the configuration to default state.
     * Displays a confirmation.
     *
     * @see AppConfig#reset()
     * @see ConfigUI#displayOK
     */
    public void onResetConfig() {
        AppConfig.reset();
        api_key.setText(AppConfig.CHALLONGE_API());
        chk_ac_on.setSelected(AppConfig.AUTOCOMPLETE_ON());
        chk_out_raw.setSelected(AppConfig.MAKE_RAW_OUTPUT());
        chk_out_html.setSelected(AppConfig.MAKE_HTML_OUTPUT());
        chk_gf_radio.setSelected(AppConfig.GF_RADIO_ON_LABEL_MATCH());
        chk_out_flags.setSelected(AppConfig.PUT_FLAGS());

        new Thread(displayOK).start();
    }

    /**
     * Prompts the {@link ScoreboardApplication} to display the corresponding scene.
     */
    public void on_scene_set_config() {
        scene_toggle_config.setSelected(true);
        ScoreboardApplication.setConfigScene();
    }

    /**
     * Prompts the {@link ScoreboardApplication} to display the corresponding scene.
     */
    public void on_scene_set_players() {
        ScoreboardApplication.setPlayersScene();
    }

    /**
     * Prompts the {@link ScoreboardApplication} to display the corresponding scene.
     */
    public void on_scene_set_controller() {
        ScoreboardApplication.setControllerScene();
    }

    /**
     * Resets the focus to avoid "sticky" controls.
     */
    public void on_bg_click() {
        bg_pane.requestFocus();
    }

    public void on_restore_writers(ActionEvent actionEvent) {
        if ( !dataManager.isInitialized() ) {
            new Alert(AlertType.ERROR, "No working directory selected - cannot restore default writers!").show();
            new Thread(displayNOK).start();
            return;
        }

        var result = new Alert(AlertType.CONFIRMATION, "Are you sure you want to restore default output settings?", ButtonType.APPLY, ButtonType.CANCEL).showAndWait();
        if ( result.isPresent() && result.get() == ButtonType.APPLY ) {
            dataManager.removeAllWriters();
            DataManager.defaultWriters().forEach(writer -> dataManager.addWriter(writer));
            new Thread(displayOK).start();
        }
    }
}
