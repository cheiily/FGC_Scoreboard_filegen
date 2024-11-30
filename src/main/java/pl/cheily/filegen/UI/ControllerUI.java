package pl.cheily.filegen.UI;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import pl.cheily.filegen.Configuration.AppConfig;
import pl.cheily.filegen.LocalData.DataEventProp;
import pl.cheily.filegen.LocalData.DataManager;
import pl.cheily.filegen.LocalData.FileManagement.Meta.Match.MatchDataKey;
import pl.cheily.filegen.LocalData.FileManagement.Meta.RoundSet.RoundLabelDAO;
import pl.cheily.filegen.LocalData.Player;
import pl.cheily.filegen.LocalData.ResourcePath;
import pl.cheily.filegen.ScoreboardApplication;
import pl.cheily.filegen.Utils.AutocompleteWrapper;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import static pl.cheily.filegen.ScoreboardApplication.dataManager;
import static pl.cheily.filegen.Utils.Util.scrollOpt;

public class ControllerUI implements Initializable {
    public AnchorPane bg_pane;
    public TextField txt_p1_tag;
    public ComboBox<String> combo_p1_nation;
    public ImageView img_p1_flag;
    public TextField txt_p1_score;
    public TextField txt_p2_tag;
    public ComboBox<String> combo_p2_nation;
    public ImageView img_p2_flag;
    public TextField txt_p2_score;
    public TextField txt_path;
    public ComboBox<String> combo_round;
    public ComboBox<String> combo_p1_name;
    public ComboBox<String> combo_p2_name;
    public ComboBox<String> combo_comm1;
    public ComboBox<String> combo_comm2;
    public ComboBox<String> combo_comm3;
    public RadioButton radio_p1_W;
    public RadioButton radio_p1_L;
    public RadioButton radio_reset;
    public RadioButton radio_p2_L;
    public RadioButton radio_p2_W;
    public ToggleButton GF_toggle;
    public List<RadioButton> radio_buttons = new ArrayList<>();
    public ToggleButton scene_toggle_controller;
    public TextField txt_p1_handle;
    public TextField txt_p1_pronouns;
    public TextField txt_p2_pronouns;
    public TextField txt_p2_handle;
    public TextField txt_comm1_tag;
    public TextField txt_comm1_pronouns;
    public TextField txt_comm1_handle;
    public TextField txt_comm2_tag;
    public TextField txt_comm2_pronouns;
    public TextField txt_comm2_handle;
    public TextField txt_comm3_tag;
    public TextField txt_comm3_pronouns;
    public TextField txt_comm3_handle;
    public ComboBox<String> combo_comm1_nat;
    public ComboBox<String> combo_comm2_nat;
    public ComboBox<String> combo_comm3_nat;
    public AnchorPane pane_comm3;
    public AnchorPane pane_comm2;
    public AnchorPane pane_comm1;
    public Button btn_expand;
    public Text label_comm1_header;
    public Text label_comm2_header;
    boolean expanded = false;

    private float expandedX = 228;
    private float expandedWidth = 184;
    private float collapsedX = 320;
    private float collapsedWidth = 276;
    private AutocompleteWrapper ac_p1_name,
            ac_p2_name,
            ac_p1_nation,
            ac_p2_nation,
            ac_round,
            ac_comm1,
            ac_comm2,
            ac_comm3,
            ac_comm1_nat,
            ac_comm2_nat,
            ac_comm3_nat;
    private List<AutocompleteWrapper> acWrappers;
    private final PropertyChangeListener listener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName() == DataEventProp.CHANGED_PLAYER_LIST.toString()) {
                List<String> pNames = dataManager.playersDAO.getAll().stream().map(Player::getName).toList();
                ac_p1_name.loadOriginList(pNames);
                ac_p2_name.loadOriginList(pNames);
                ac_p1_name.clearSuggestions();
                ac_p2_name.clearSuggestions();
            } else if (evt.getPropertyName() == DataEventProp.INIT.toString()) {
                if (AppConfig.WRITE_COMM_3())
                    expand();
                else collapse();
            }
        }
    };
    {
        dataManager.subscribe(DataEventProp.CHANGED_PLAYER_LIST, listener);
    }


    /**
     * Loads a hardcoded preset of round opts, attempts to load flag/nationality opts, sets the default flag as null,
     * loads radio buttons into a list and disables them all, adds scene toggles to a toggle group
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ComboBoxListViewSkin<String> comboBoxListViewSkin = new ComboBoxListViewSkin<String>(combo_round) {
            private Button buttonAdd = new Button("+Add");
            private Button buttonReload = new Button("↻Reload");

            private TextField textField = new TextField();
            private VBox pane = new VBox();
            {
                buttonAdd.setDisable(true);
                buttonAdd.setOnAction(event -> {
                    String text = textField.getText();
                    if (text.isBlank()) return;
                    if (dataManager.isInitialized()) dataManager.roundLabelDAO.set(text, text);
                    combo_round.getItems().add(text);
                    textField.clear();
                    buttonAdd.setDisable(true);
                });
                textField.setOnKeyTyped(event -> {
                    if (textField.getText().isBlank())
                        buttonAdd.setDisable(true);
                    else buttonAdd.setDisable(false);
                });

                textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                    ac_round.propertyChange(new PropertyChangeEvent(this, "text", null, !newValue));
                });
                textField.setOnAction(event -> buttonAdd.fire());

                buttonReload.setOnAction(event -> {
                    combo_round.getItems().clear();
                    if (!dataManager.isInitialized()) {
                        new Alert(Alert.AlertType.WARNING, "No working path selected - reloading default label set.", ButtonType.OK).show();
                        combo_round.getItems().addAll(RoundLabelDAO.getDefault());
                    } else
                        combo_round.getItems().addAll(dataManager.roundLabelDAO.getAllSorted());
                });
                pane.setStyle("-fx-background-color: -fx-control-inner-background; -fx-border-color: -fx-box-border; -fx-border-width: 1;");
            }

            @Override
            public Node getPopupContent() {
                Node defaultContent = super.getPopupContent();
                HBox hBox = new HBox();
                hBox.getChildren().addAll(buttonAdd, textField, buttonReload);
                HBox.setHgrow(textField, Priority.ALWAYS);
                defaultContent.setManaged(true);
                pane.getChildren().setAll(defaultContent, hBox);
                return pane;
            }
        };
        combo_round.setSkin(comboBoxListViewSkin);


        combo_round.setCellFactory(param -> new ListCell<>() {
            private Button button = new Button("-");
            private Label label = new Label();
            private HBox graphic;
            private Hyperlink link = new Hyperlink("X");

            {
                label.textProperty().bind(itemProperty());
                label.setMaxWidth(Double.POSITIVE_INFINITY);
                label.setOnMouseClicked(event -> combo_round.hide());
                link.setVisited(true);
                link.setStyle("-fx-underline: false; -fx-font-weight: bold;");
                link.setOnMouseReleased(event -> {
                    String item = getItem();
                    if (dataManager.isInitialized()) dataManager.roundLabelDAO.delete(item);
                    combo_round.getItems().remove(item);
                    combo_round.show();
                });
                button.setFont(new Font(button.getFont().getFamily(), 9));
                button.setTextAlignment(TextAlignment.CENTER);
                button.setAlignment(Pos.CENTER);
                button.setMaxSize(16, 16);
                button.setOnMouseReleased(event -> {
                    String item = getItem();
                    if (dataManager.isInitialized()) dataManager.roundLabelDAO.delete(item);
                    combo_round.getItems().remove(item);
                    combo_round.show();
                });

                graphic = new HBox(label, link);
                HBox.setHgrow(label, Priority.ALWAYS);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(graphic);
                }
            }
        });

        if (!dataManager.isInitialized())
            combo_round.getItems().addAll(RoundLabelDAO.getDefault());
        else combo_round.getItems().addAll(dataManager.roundLabelDAO.getAllSorted());

        ObservableList<String> f1_opts = combo_p1_nation.getItems();
        ObservableList<String> f2_opts = combo_p2_nation.getItems();
        ObservableList<String> cf1_opts = combo_comm1_nat.getItems();
        ObservableList<String> cf2_opts = combo_comm2_nat.getItems();
        ObservableList<String> cf3_opts = combo_comm3_nat.getItems();
        try ( Stream<Path> flags = Files.walk(dataManager.flagsDir) ) {
            flags.filter(path -> path.toString().endsWith(".png"))
                    .filter(path ->
                            !path.getFileName().toString().equals(ResourcePath.P1_FLAG.toString())
                                    && !path.getFileName().toString().equals(ResourcePath.P2_FLAG.toString()))
                    .map(path -> path.getFileName().toString().split("\\.")[ 0 ])
                    .forEach(path -> {
                        f1_opts.add(path.toUpperCase());
                        f2_opts.add(path.toUpperCase());
                        cf1_opts.add(path.toUpperCase());
                        cf2_opts.add(path.toUpperCase());
                        cf3_opts.add(path.toUpperCase());
                    });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        img_p1_flag.setImage(new Image(dataManager.nullFlag.toString()));
        img_p2_flag.setImage(new Image(dataManager.nullFlag.toString()));

        radio_buttons.add(radio_reset);
        radio_buttons.add(radio_p1_W);
        radio_buttons.add(radio_p1_L);
        radio_buttons.add(radio_p2_W);
        radio_buttons.add(radio_p2_L);
        radio_buttons.forEach(r -> r.setDisable(true));

        scene_toggle_controller.setSelected(true);

        ac_p1_name = new AutocompleteWrapper(combo_p1_name);
        ac_p1_nation = new AutocompleteWrapper(combo_p1_nation);
        ac_p2_name = new AutocompleteWrapper(combo_p2_name);
        ac_p2_nation = new AutocompleteWrapper(combo_p2_nation);
        ac_round = new AutocompleteWrapper(combo_round);
        ac_comm1 = new AutocompleteWrapper(combo_comm1);
        ac_comm2 = new AutocompleteWrapper(combo_comm2);
        ac_comm3 = new AutocompleteWrapper(combo_comm3);
        ac_comm1_nat = new AutocompleteWrapper(combo_comm1_nat);
        ac_comm2_nat = new AutocompleteWrapper(combo_comm2_nat);
        ac_comm3_nat = new AutocompleteWrapper(combo_comm3_nat);
        acWrappers = List.of(ac_p1_name, ac_p2_name, ac_p1_nation, ac_p2_nation, ac_round, ac_comm1, ac_comm2, ac_comm3, ac_comm1_nat, ac_comm2_nat, ac_comm3_nat);
    }


    /**
     * Issues the {@link ScoreboardApplication#dataManager} to save its contained data.
     * See {@link DataManager#save()}
     */
    public void on_save() {
        if ( !dataManager.isInitialized() ) {
            new Alert(Alert.AlertType.WARNING, "No path selected.", ButtonType.OK).show();
            return;
        }

        dataManager.loadMetadataFromUI(this);
        dataManager.save();

        Player p1 = new Player(
                txt_p1_tag.getText(),
                combo_p1_name.getValue(),
                combo_p1_nation.getValue(),
                txt_p1_pronouns.getText(),
                txt_p1_handle.getText()
        );
        Player p2 = new Player(
                txt_p2_tag.getText(),
                combo_p2_name.getValue(),
                combo_p2_nation.getValue(),
                txt_p2_pronouns.getText(),
                txt_p2_handle.getText()
        );
        String score_1 = txt_p1_score.getText();
        String score_2 = txt_p2_score.getText();
        Player c1 = new Player(
                txt_comm1_tag.getText(),
                combo_comm1.getValue(),
                combo_comm1_nat.getValue(),
                txt_comm1_pronouns.getText(),
                txt_comm1_handle.getText()
        );
        Player c2 = new Player(
                txt_comm2_tag.getText(),
                combo_comm2.getValue(),
                combo_comm2_nat.getValue(),
                txt_comm2_pronouns.getText(),
                txt_comm2_handle.getText()
        );
        Player c3 = new Player(
                txt_comm3_tag.getText(),
                combo_comm3.getValue(),
                combo_comm3_nat.getValue(),
                txt_comm3_pronouns.getText(),
                txt_comm3_handle.getText()
        );

        for (AutocompleteWrapper wrapper : acWrappers) wrapper.clearSuggestions();
        combo_p1_name.setValue(p1.getName());
        txt_p1_tag.setText(p1.getTag());
        combo_p1_nation.setValue(p1.getNationality());
        txt_p1_pronouns.setText(p1.getPronouns());
        txt_p1_handle.setText(p1.getSnsHandle());
        combo_p2_name.setValue(p2.getName());
        txt_p2_tag.setText(p2.getTag());
        combo_p2_nation.setValue(p2.getNationality());
        txt_p2_pronouns.setText(p2.getPronouns());
        txt_p2_handle.setText(p2.getSnsHandle());

        txt_p1_score.setText(score_1);
        txt_p2_score.setText(score_2);

        combo_comm1.setValue(c1.getName());
        txt_comm1_tag.setText(c1.getTag());
        combo_comm1_nat.setValue(c1.getNationality());
        txt_comm1_pronouns.setText(c1.getPronouns());
        txt_comm1_handle.setText(c1.getSnsHandle());
        combo_comm2.setValue(c2.getName());
        txt_comm2_tag.setText(c2.getTag());
        combo_comm2_nat.setValue(c2.getNationality());
        txt_comm2_pronouns.setText(c2.getPronouns());
        txt_comm2_handle.setText(c2.getSnsHandle());
        combo_comm3.setValue(c3.getName());
        txt_comm3_tag.setText(c3.getTag());
        combo_comm3_nat.setValue(c3.getNationality());
        txt_comm3_pronouns.setText(c3.getPronouns());
        txt_comm3_handle.setText(c3.getSnsHandle());
    }

    /**
     * Button way of interacting with score.
     */
    public void on_p1_score_up() {
        txt_p1_score.setText(
                String.valueOf(
                        Integer.parseInt(txt_p1_score.getText()) + 1
                ));
    }

    /**
     * Button way of interacting with score.
     */
    public void on_p1_score_down() {
        txt_p1_score.setText(
                String.valueOf(
                        Integer.parseInt(txt_p1_score.getText()) - 1
                ));
    }

    /**
     * Button way of interacting with score.
     */
    public void on_p2_score_up() {
        txt_p2_score.setText(
                String.valueOf(
                        Integer.parseInt(txt_p2_score.getText()) + 1
                ));
    }

    /**
     * Button way of interacting with score.
     */
    public void on_p2_score_down() {
        txt_p2_score.setText(
                String.valueOf(
                        Integer.parseInt(txt_p2_score.getText()) - 1
                ));
    }

    /**
     * Attempts to load any data found in the directory into the app.
     */
    public void on_path_select() {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File("."));
        File dir = dc.showDialog(new Stage());

        if ( dir == null ) {
            new Alert(Alert.AlertType.WARNING, "No directory selected", ButtonType.OK).show();
            return;
        }

        combo_p1_name.getItems().clear();
        combo_p2_name.getItems().clear();
        combo_comm1.getItems().clear();
        combo_comm2.getItems().clear();
        combo_comm3.getItems().clear();

        dataManager.initialize(dir.toPath().toAbsolutePath());
        txt_path.setText(dir.toPath().toAbsolutePath().toString());

        tryLoadData();
    }

    /**
     * Loads data from the initialized {@link ScoreboardApplication#dataManager} into ui components
     */
    private void tryLoadData() {

        combo_round.getItems().clear();
        List<String> rnds = dataManager.roundLabelDAO.getAllSorted();
        if (rnds.isEmpty()) rnds = RoundLabelDAO.getDefault();
        combo_round.getItems().addAll(rnds);
        ac_round.loadOriginList(rnds);

        List<String> allPlayers = dataManager.playersDAO.getAll().stream().map(Player::getName).toList();
        List<String> allComms = dataManager.commentaryDAO.getAll().stream().map(Player::getName).toList();
        combo_p1_name.getItems().addAll(allPlayers);
        combo_p2_name.getItems().addAll(allPlayers);
        combo_comm3.getItems().addAll(allComms);
        combo_comm1.getItems().addAll(allComms);
        combo_comm2.getItems().addAll(allComms);

        ac_p1_name.loadOriginList(allPlayers);
        ac_p2_name.loadOriginList(allPlayers);
        ac_comm1.loadOriginList(allComms);
        ac_comm2.loadOriginList(allComms);
        ac_comm3.loadOriginList(allComms);

        for (AutocompleteWrapper acWrapper : acWrappers) {
            acWrapper.clearSuggestions();
        }

        //round data
        combo_round.setValue(dataManager.matchDAO.get(MatchDataKey.ROUND_LABEL));
        txt_p1_score.setText(String.valueOf(dataManager.matchDAO.get(MatchDataKey.P1_SCORE)));
        txt_p2_score.setText(String.valueOf(dataManager.matchDAO.get(MatchDataKey.P2_SCORE)));

//        boolean is_reset = dataManager.getMeta(SEC_ROUND, KEY_GF_RESET, boolean.class);
//        boolean is_p1_w = dataManager.getMeta(SEC_ROUND, KEY_GF_W1, boolean.class);
        //enable radio to allow "chained" setting
        radio_buttons.forEach(r -> r.setDisable(false));

        if ( Boolean.parseBoolean(dataManager.matchDAO.get(MatchDataKey.IS_GF_RESET)) && !radio_reset.isSelected() )
            radio_reset.fire();
        else if ( Boolean.parseBoolean(dataManager.matchDAO.get(MatchDataKey.IS_GF_P1_WINNER)) && !radio_p1_W.isSelected() )
            radio_p1_W.fire();
        else if ( Boolean.parseBoolean(dataManager.matchDAO.get(MatchDataKey.IS_GF_P2_WINNER)) && radio_p1_W.isSelected() )
            radio_p1_W.fire();

        //disable radio
        GF_toggle.setSelected(false);

        //finally set radio in the proper state
        boolean is_gf = Boolean.parseBoolean(dataManager.matchDAO.get(MatchDataKey.IS_GF));
        GF_toggle.setSelected(is_gf);

        //p1 data
        combo_p1_name.setValue(dataManager.matchDAO.get(MatchDataKey.P1_NAME));
        txt_p1_tag.setText(dataManager.matchDAO.get(MatchDataKey.P1_TAG));
        combo_p1_nation.setValue(dataManager.matchDAO.get(MatchDataKey.P1_NATIONALITY));
        txt_p1_pronouns.setText(dataManager.matchDAO.get(MatchDataKey.P1_PRONOUNS));
        txt_p1_handle.setText(dataManager.matchDAO.get(MatchDataKey.P1_HANDLE));

        //p2 data
        combo_p2_name.setValue(dataManager.matchDAO.get(MatchDataKey.P2_NAME));
        txt_p2_tag.setText(dataManager.matchDAO.get(MatchDataKey.P2_TAG));
        combo_p2_nation.setValue(dataManager.matchDAO.get(MatchDataKey.P2_NATIONALITY));
        txt_p2_pronouns.setText(dataManager.matchDAO.get(MatchDataKey.P2_PRONOUNS));
        txt_p2_handle.setText(dataManager.matchDAO.get(MatchDataKey.P2_HANDLE));

        //comms data
        combo_comm1.setValue(dataManager.matchDAO.get(MatchDataKey.COMM_NAME_1));
        txt_comm1_tag.setText(dataManager.matchDAO.get(MatchDataKey.COMM_TAG_1));
        combo_comm1_nat.setValue(dataManager.matchDAO.get(MatchDataKey.COMM_NATIONALITY_1));
        txt_comm1_pronouns.setText(dataManager.matchDAO.get(MatchDataKey.COMM_PRONOUNS_1));
        txt_comm1_handle.setText(dataManager.matchDAO.get(MatchDataKey.COMM_HANDLE_1));

        combo_comm2.setValue(dataManager.matchDAO.get(MatchDataKey.COMM_NAME_2));
        txt_comm2_tag.setText(dataManager.matchDAO.get(MatchDataKey.COMM_TAG_2));
        combo_comm2_nat.setValue(dataManager.matchDAO.get(MatchDataKey.COMM_NATIONALITY_2));
        txt_comm2_pronouns.setText(dataManager.matchDAO.get(MatchDataKey.COMM_PRONOUNS_2));
        txt_comm2_handle.setText(dataManager.matchDAO.get(MatchDataKey.COMM_HANDLE_2));

        combo_comm3.setValue(dataManager.matchDAO.get(MatchDataKey.COMM_NAME_3));
        txt_comm3_tag.setText(dataManager.matchDAO.get(MatchDataKey.COMM_TAG_3));
        combo_comm3_nat.setValue(dataManager.matchDAO.get(MatchDataKey.COMM_NATIONALITY_3));
        txt_comm3_pronouns.setText(dataManager.matchDAO.get(MatchDataKey.COMM_PRONOUNS_3));
        txt_comm3_handle.setText(dataManager.matchDAO.get(MatchDataKey.COMM_HANDLE_3));

    }

    /**
     * Tries to load a related flag file into the coupled {@link ImageView}, loads the null flag if no related file is found.
     * See {@link pl.cheily.filegen.LocalData.DataManager#getFlag(String)}
     */
    public void on_p1_nation_selection() {
        img_p1_flag.setImage(dataManager.getFlag(combo_p1_nation.getValue()));
    }

    /**
     * Tries to load a related flag file into the coupled {@link ImageView}, loads the null flag if no related file is found.
     * See {@link pl.cheily.filegen.LocalData.DataManager#getFlag(String)}
     */
    public void on_p2_nation_selection() {
        img_p2_flag.setImage(dataManager.getFlag(combo_p2_nation.getValue()));
    }

    /**
     * Searches for the selected player via .
     * If no such player is found within the defined set (i.e. equal to {@link Player#getInvalid()}),
     * the related fields are not cleared, so as not to overwrite any previously entered data
     * that might be related to the undefined player.
     */
    public void on_p1_selection() {
        txt_p1_score.setText("0");

        Player selected;
        if ( dataManager.isInitialized() ) {
            var found = dataManager.playersDAO.findByName(combo_p1_name.getValue());
            if (found.isEmpty()) selected = Player.getInvalid();
            else selected = found.get(0);
        } else {
            selected = Player.getInvalid();
        }
//        Player selected = dataManager.getPlayer(combo_p1_name.getValue())
//                .orElse(Player.empty());

        if ( selected != null && selected != Player.getInvalid() ) {
            txt_p1_tag.setText(selected.getTag());
            combo_p1_nation.setValue(selected.getNationality());
            txt_p1_pronouns.setText(selected.getPronouns());
            txt_p1_handle.setText(selected.getSnsHandle());
        }
    }

    /**
     * Searches for the selected player via .
     * If no such player is found within the defined set (i.e. equal to {@link Player#getInvalid()}),
     * the related fields are not cleared, so as not to overwrite any previously entered data
     * that might be related to the undefined player.
     */
    public void on_p2_selection() {
        txt_p2_score.setText("0");

        Player selected;
        if ( dataManager.isInitialized() ) {
            var found = dataManager.playersDAO.findByName(combo_p2_name.getValue());
            if (found.isEmpty()) selected = Player.getInvalid();
            else selected = found.get(0);
        } else {
            selected = Player.getInvalid();
        }
//        Player selected = dataManager.getPlayer(combo_p2_name.getValue())
//                .orElse(Player.empty());

        if ( selected != null && selected != Player.getInvalid() ) {
            txt_p2_tag.setText(selected.getTag());
            combo_p2_nation.setValue(selected.getNationality());
            txt_p2_pronouns.setText(selected.getPronouns());
            txt_p2_handle.setText(selected.getSnsHandle());
        }
    }


    /**
     * Score text area scroll listener - increments the value by one on scroll up, decrements by one on scroll down.
     *
     * @param scrollEvent
     */
    public void on_p1_score_scroll(ScrollEvent scrollEvent) {
        txt_p1_score.setText(String.valueOf(
                Integer.parseInt(txt_p1_score.getText()) + Integer.signum((int) scrollEvent.getTextDeltaY())
        ));
    }


    /**
     * Score text area scroll listener - increments the value on scroll up, decrements on scroll down.
     *
     * @param scrollEvent
     */
    public void on_p2_score_scroll(ScrollEvent scrollEvent) {
        txt_p2_score.setText(String.valueOf(
                Integer.parseInt(txt_p2_score.getText()) + Integer.signum((int) scrollEvent.getTextDeltaY())
        ));
    }


    //adding scroll listeners in a prettier way than via Util.makeScrollable
    public void on_p1_name_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_p1_name, scrollEvent);
    }

    public void on_p2_name_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_p2_name, scrollEvent);
    }

    public void on_p1_natio_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_p1_nation, scrollEvent);
    }

    public void on_p2_natio_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_p2_nation, scrollEvent);
    }

    public void on_comm1_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_comm1, scrollEvent);
    }

    public void on_comm2_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_comm2, scrollEvent);
    }

    public void on_comm3_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_comm3, scrollEvent);
    }

    public void on_p1_flag_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_p1_nation, scrollEvent);
    }

    public void on_p2_flag_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_p2_nation, scrollEvent);
    }

    public void on_round_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_round, scrollEvent);
    }

    /**
     * If the selected round contains "gran" (e.g. Grand Finals), the radio buttons become enabled.
     * Otherwise - they all become disabled.
     */
    public void on_round_select() {
        if (AppConfig.GF_RADIO_ON_LABEL_MATCH()) {
            String temp = combo_round.getValue() == null ? "" : combo_round.getValue();
            if ((temp.toLowerCase().contains("gran") && !GF_toggle.isSelected())
                    || (!temp.toLowerCase().contains("gran") && GF_toggle.isSelected())
            ) GF_toggle.fire();
        }
    }


    /**
     * Radio buttons work in the following way:
     * <ul>
     *     <li>either player's W radio sets the other player's L radio and unsets all other buttons</li>
     *     <li>either player's L radio sets the other player's W radio and unsets all other buttons</li>
     *     <li>reset radio sets both players' L radio and unsets their W buttons</li>
     * </ul>
     */
    public void on_radio_W1() {
        radio_buttons.forEach(r -> r.setSelected(false));
        radio_p1_W.setSelected(true);
        radio_p2_L.setSelected(true);
    }

    /**
     * See {@link #on_radio_W1()}.
     */
    public void on_radio_L1() {
        radio_buttons.forEach(r -> r.setSelected(false));
        radio_p1_L.setSelected(true);
        radio_p2_W.setSelected(true);
    }

    /**
     * See {@link #on_radio_W1()}.
     */
    public void on_radio_reset() {
        radio_buttons.forEach(r -> r.setSelected(false));
        radio_p1_L.setSelected(true);
        radio_p2_L.setSelected(true);
        radio_reset.setSelected(true);
    }

    /**
     * See {@link #on_radio_W1()}.
     */
    public void on_radio_L2() {
        radio_buttons.forEach(r -> r.setSelected(false));
        radio_p1_W.setSelected(true);
        radio_p2_L.setSelected(true);
    }

    /**
     * See {@link #on_radio_W1()}.
     */
    public void on_radio_W2() {
        radio_buttons.forEach(r -> r.setSelected(false));
        radio_p1_L.setSelected(true);
        radio_p2_W.setSelected(true);
    }

    /**
     * Swaps the player data around.
     *
     * @param actionEvent
     */
    public void on_player_swap(ActionEvent actionEvent) {
        String p1_name = combo_p1_name.getValue();
        String p1_tag = txt_p1_tag.getText();
        String p1_nat = combo_p1_nation.getValue();
        String p1_pronouns = txt_p1_pronouns.getText();
        String p1_handle = txt_p1_handle.getText();
        String p1_score = txt_p1_score.getText();
        String p2_name = combo_p2_name.getValue();
        String p2_tag = txt_p2_tag.getText();
        String p2_nat = combo_p2_nation.getValue();
        String p2_pronouns = txt_p2_pronouns.getText();
        String p2_handle = txt_p2_handle.getText();
        String p2_score = txt_p2_score.getText();

        combo_p1_name.setValue(p2_name);
        txt_p1_tag.setText(p2_tag);
        combo_p1_nation.setValue(p2_nat);
        txt_p1_score.setText(p2_score);
        txt_p1_pronouns.setText(p2_pronouns);
        txt_p1_handle.setText(p2_handle);
        combo_p2_name.setValue(p1_name);
        txt_p2_tag.setText(p1_tag);
        combo_p2_nation.setValue(p1_nat);
        txt_p2_score.setText(p1_score);
        txt_p2_pronouns.setText(p1_pronouns);
        txt_p2_handle.setText(p1_handle);

        if ( GF_toggle.isSelected() && !radio_reset.isSelected() ) {
            boolean p1w = radio_p1_W.isSelected();
            radio_buttons.forEach(r -> r.setSelected(false));
            if ( p1w ) {
                radio_p1_L.setSelected(true);
                radio_p2_W.setSelected(true);
            } else {
                radio_p2_L.setSelected(true);
                radio_p1_W.setSelected(true);
            }
        }
    }

    /**
     * Disables or enables the radio buttons.
     */
    public void on_GF_toggle() {
        boolean turn_off = !GF_toggle.isSelected();
        radio_buttons.forEach(r -> r.setDisable(turn_off));
    }

    /**
     * Prompts the {@link ScoreboardApplication} to display the corresponding scene.
     */
    public void on_scene_set_config() {
        ScoreboardApplication.setConfigScene();
    }

    public void on_scene_set_players() {
        ScoreboardApplication.setPlayersScene();
    }

    public void on_scene_set_controller() {
        scene_toggle_controller.setSelected(true);
        ScoreboardApplication.setControllerScene();
    }

    /**
     * Resets the focus to avoid "sticky" controls.
     */
    public void on_bg_click() {
        bg_pane.requestFocus();
    }

    public void on_comm1_nat_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_comm1_nat, scrollEvent);
    }

    public void on_comm2_nat_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_comm2_nat, scrollEvent);
    }

    public void on_comm3_nat_scroll(ScrollEvent scrollEvent) {
        scrollOpt(combo_comm3_nat, scrollEvent);
    }

    public void on_button_expand(ActionEvent actionEvent) {
        expanded = !expanded;
        if (expanded) expand();
        else collapse();
        AppConfig.WRITE_COMM_3(expanded);
    }

    private void expand() {
        expanded = true;
        btn_expand.setText("-");
        pane_comm3.setVisible(true);
        pane_comm1.setPrefWidth(expandedWidth);
        pane_comm2.relocate(expandedX, pane_comm2.getLayoutY());
        pane_comm2.setPrefWidth(expandedWidth);
        label_comm1_header.setLayoutX(51);
        label_comm2_header.setLayoutX(51);
    }

    private void collapse() {
        expanded = false;
        btn_expand.setText("+");
        pane_comm3.setVisible(false);
        pane_comm1.setPrefWidth(collapsedWidth);
        pane_comm2.relocate(collapsedX, pane_comm2.getLayoutY());
        pane_comm2.setPrefWidth(collapsedWidth);
        label_comm1_header.setLayoutX(96);
        label_comm2_header.setLayoutX(96);
    }
}