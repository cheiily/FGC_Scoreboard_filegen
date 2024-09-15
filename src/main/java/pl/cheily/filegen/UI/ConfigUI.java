package pl.cheily.filegen.UI;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import pl.cheily.filegen.Configuration.AppConfig;
import pl.cheily.filegen.Configuration.PropKey;
import pl.cheily.filegen.LocalData.DataManager;
import pl.cheily.filegen.LocalData.ResourcePath;
import pl.cheily.filegen.ScoreboardApplication;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
    public ImageView resultImgView;


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

        //listen for resets, loads, etc.
        AppConfig.subscribeAll(configListener);
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
                && AppConfig.PUT_FLAGS(chk_out_flags.isSelected());

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
        if ( dataManager.saveConfig() ) {
            onApplyConfig();
            new Thread(displayOK).start();
        } else {
            new Thread(displayNOK).start();
            Alert a = new Alert(AlertType.WARNING, "Couldn't store configuration to " + ResourcePath.CONFIG + ". Do you want to apply changes anyway?", ButtonType.APPLY, ButtonType.NO);
            a.showAndWait();
            if ( a.getResult() == ButtonType.APPLY )
                onApplyConfig();
        }
    }

    /**
     * Attempts to reload configuration from the current path.<br>
     * Displays result.
     *
     * @see ConfigUI#displayOK
     * @see ConfigUI#displayNOK
     */
    public void onReloadConfig() {
        if ( !dataManager.loadConfig() ) {
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
}
