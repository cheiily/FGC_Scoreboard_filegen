package pl.cheily.filegen.LocalData;

import com.opencsv.CSVReader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cheily.filegen.Configuration.AppConfig;
import pl.cheily.filegen.LocalData.FileManagement.Meta.Config.ConfigDAO;
import pl.cheily.filegen.LocalData.FileManagement.Meta.Config.ConfigDAOIni;
import pl.cheily.filegen.LocalData.FileManagement.Meta.EventfulCachedIniDAOWrapper;
import pl.cheily.filegen.LocalData.FileManagement.Meta.Match.MatchDAO;
import pl.cheily.filegen.LocalData.FileManagement.Meta.Match.MatchDAOIni;
import pl.cheily.filegen.LocalData.FileManagement.Meta.Match.MatchDataKey;
import pl.cheily.filegen.LocalData.FileManagement.Meta.Players.PlayersDAO;
import pl.cheily.filegen.LocalData.FileManagement.Meta.Players.PlayersDAOIni;
import pl.cheily.filegen.LocalData.FileManagement.Meta.RoundSet.RoundLabelDAO;
import pl.cheily.filegen.LocalData.FileManagement.Meta.RoundSet.RoundLabelDAOIni;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.OutputWriter;
import pl.cheily.filegen.UI.ControllerUI;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static pl.cheily.filegen.LocalData.DataEventProp.*;
import static pl.cheily.filegen.LocalData.ResourcePath.COMMS_LIST;
import static pl.cheily.filegen.LocalData.ResourcePath.ROUND_LIST;
import static pl.cheily.filegen.ScoreboardApplication.dataWebSocket;

public class DataManager {
    private final static Logger logger = LoggerFactory.getLogger(DataManager.class);

    /**
     * Target directory path
     */
    public Path targetDir;
    public final Path flagsDir = Path.of("flags").toAbsolutePath();
    //TODO hook this up to AppConfig#Flagsdir via listener
    // todo move nullflag to resources, make flag module entirely optional
    public final Path nullFlag = Path.of(flagsDir + "/null.png");

    private final List<OutputWriter> writers = new ArrayList<>(2);

    public ConfigDAO configDAO;
    public MatchDAO matchDAO;
    public PlayersDAO playersDAO;
    public PlayersDAO commentaryDAO;
    public RoundLabelDAO roundLabelDAO;

    private boolean initialized;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final PropertyChangeListener propagator = evt -> pcs.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());

   /**
     * @param toProp   property to subscribe to
     * @param listener listener to add as a subscriber
     */
    public void subscribe(DataEventProp toProp, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(toProp.toString(), listener);
    }

    /**
     * Subscribes to all properties.
     *
     * @param listener listener to add as a subscriber
     */
    public void subscribeAll(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * @param fromProp property to unsubscribe from
     * @param listener listener to remove from subscribers
     */
    public void unsubscribe(DataEventProp fromProp, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(fromProp.toString(), listener);
    }

    /**
     * Unsubscribes from all properties.
     *
     * @param listener listener to remove from subscribers
     */
    public void unsubscribeAll(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }


    /**
     * Constructs a DataWriter with the assigned OutputWriters.
     *
     * @param writers vararg package of {@link OutputWriter}s
     * @throws IllegalArgumentException if no writers were passed
     */
    public DataManager(OutputWriter... writers) throws IllegalArgumentException {
        if ( writers.length == 0 )
            throw new IllegalArgumentException("DataManager cannot be created without any writers");

        this.writers.addAll(List.of(writers));
    }

    /**
     * Initializes the Manager with the specified target directory.
     * The Manager will clear any of its stored (unsaved) data and attempt to load data from files contained within the directory.
     * The loading proceeds in the following order: <ol>
     * <li>{@link ResourcePath#METADATA}</li>
     * <li>Data stored within other internal files, i.e. in the "meta" directory in order to restore the previous or imported configuration.</li>
     * <li>Data stored within custom lists, i.e. in the "lists" directory - in order to complement the lists with any updates or missing entries.</li>
     * </ol>
     * The Manager will then attempt to save the loaded data into the internal files so as to store the updated configuration.<br/>
     * Afterwards, the directory for custom lists will be prepared.<br/>
     * Additionally, the Manager will try to read a //custom round list//. If there is no such file found, the  is loaded instead.
     * As there is not much additional information to ever store about the round labels, the Manager does not store such lists in its "meta" files, but rather holds it in memory while active.
     *
     * @param targetDir {@link Path} representation of the target directory
     */
    public void initialize(Path targetDir) {
        if (targetDir == null) {
            logger.warn("Attempted to initialize DataManager with a null target directory. Cancelling.");
        }

        initialized = true;
        this.targetDir = targetDir;

        // todo change resourcepaths to ini
        var configDAOWrapper = new EventfulCachedIniDAOWrapper<>(new ConfigDAOIni(ResourcePath.CONFIG), ConfigDAO.class, CHANGED_CONFIG.name());
        var matchDAOWrapper = new EventfulCachedIniDAOWrapper<>(new MatchDAOIni(ResourcePath.METADATA), MatchDAO.class, CHANGED_MATCH_DATA.name());
        var playersDAOWrapper = new EventfulCachedIniDAOWrapper<>(new PlayersDAOIni(ResourcePath.PLAYER_LIST), PlayersDAO.class, CHANGED_PLAYER_LIST.name());
        var commentaryDAOWrapper = new EventfulCachedIniDAOWrapper<>(new PlayersDAOIni(COMMS_LIST), PlayersDAO.class, CHANGED_COMMENTARY_LIST.name());
        var roundLabelDAOWrapper = new EventfulCachedIniDAOWrapper<>(new RoundLabelDAOIni(ROUND_LIST), RoundLabelDAO.class, CHANGED_ROUND_LABELS.name());

        configDAOWrapper.subscribe(propagator);
        matchDAOWrapper.subscribe(propagator);
        playersDAOWrapper.subscribe(propagator);
        commentaryDAOWrapper.subscribe(propagator);
        roundLabelDAOWrapper.subscribe(propagator);

        configDAO = configDAOWrapper.getDAO();
        matchDAO = matchDAOWrapper.getDAO();
        playersDAO = playersDAOWrapper.getDAO();
        commentaryDAO = commentaryDAOWrapper.getDAO();
        roundLabelDAO = roundLabelDAOWrapper.getDAO();
        // todo retain manually edited player list

        pcs.firePropertyChange(INIT.toString(), null, null);
    }

    /**
     * Writes the stored data to files.<br/>
     * Note that the manager will only write to the {@link ResourcePath#METADATA} and output files.
     * The manager will NOT write any of the data contained within  or .<br>
     * In case any of the save operations failed, an {@link Alert} will be displayed, listing the filenames.
     * <p>
     */
    public void save() {
        //Update the Websocket before we return.
        // todo make it a writer
//        dataWebSocket.updateMetadata();
        // todo onsave check if player/comm list was manually edited and alert

        for (OutputWriter writer : writers) {
            writer.writeData();
        }
        pcs.firePropertyChange(SAVE.toString(), null, null);
    }

    /**
     * Uses {@link CSVReader} to load the custom player list regardless of its CSV-style.
     *
     * @return success value, false if any read line is not correctly formatted
     */
//    private boolean loadPlayersFromChallongeCSV() { // todo move this into an integration module
//        try ( CSVReader csvReader = new CSVReader(Files.newBufferedReader(ResourcePath.CUSTOM_PLAYER_LIST.toPath())) ) {
//            String[] line;
//            while ( (line = csvReader.readNext()) != null ) {
//                // todo try determine column indices by header
//                Player player = new Player(line[ 0 ], line[ 1 ], line[ 2 ], "", "");
//                playersDAO.set(player.getUuidStr(), player);
//            }
//
//            return true;
//        } catch (IOException | CsvValidationException | DataManagerNotInitializedException e) {
//            return false;
//        }
//    }

    /**
     * Utility method. As the stored metadata is only updated on-save, the most common scenario will be reading all of the related values.
     * This method wraps that operation in order to improve code clarity within the controller class, at the cost of exposing its members to the DataManager.
     *
     * @param ui {@link ControllerUI} to load data from its UI elements
     */
    public void loadMetadataFromUI(ControllerUI ui) {
        matchDAO.set(MatchDataKey.ROUND_LABEL.toString(), ui.combo_round.getValue());
        matchDAO.set(MatchDataKey.P1_SCORE.toString(), ui.txt_p1_score.getText());
        matchDAO.set(MatchDataKey.P2_SCORE.toString(), ui.txt_p2_score.getText());
        matchDAO.set(MatchDataKey.IS_GF.toString(), Boolean.toString(ui.GF_toggle.isSelected()));
        matchDAO.set(MatchDataKey.IS_GF_RESET.toString(), Boolean.toString(ui.radio_reset.isSelected()));
        matchDAO.set(MatchDataKey.IS_GF_P1_WINNER.toString(), Boolean.toString(ui.radio_p1_W.isSelected()));
        matchDAO.set(MatchDataKey.IS_GF_P2_WINNER.toString(), Boolean.toString(ui.radio_p2_W.isSelected()));

        matchDAO.set(MatchDataKey.P1_TAG.toString(), ui.txt_p1_tag.getText());
        matchDAO.set(MatchDataKey.P1_NAME.toString(), ui.combo_p1_name.getValue());
        matchDAO.set(MatchDataKey.P1_NATIONALITY.toString(), ui.combo_p1_nation.getValue());
        matchDAO.set(MatchDataKey.P1_PRONOUNS.toString(), ui.txt_p1_pronouns.getText());
        matchDAO.set(MatchDataKey.P1_HANDLE.toString(), ui.txt_p1_handle.getText());

        matchDAO.set(MatchDataKey.P2_TAG.toString(), ui.txt_p2_tag.getText());
        matchDAO.set(MatchDataKey.P2_NAME.toString(), ui.combo_p2_name.getValue());
        matchDAO.set(MatchDataKey.P2_NATIONALITY.toString(), ui.combo_p2_nation.getValue());
        matchDAO.set(MatchDataKey.P2_PRONOUNS.toString(), ui.txt_p2_pronouns.getText());
        matchDAO.set(MatchDataKey.P2_HANDLE.toString(), ui.txt_p2_handle.getText());

        matchDAO.set(MatchDataKey.COMM_NAME_1, ui.combo_comm1.getValue());
        matchDAO.set(MatchDataKey.COMM_TAG_1, ui.txt_comm1_tag.getText());
        matchDAO.set(MatchDataKey.COMM_NATIONALITY_1, ui.combo_comm1_nat.getValue());
        matchDAO.set(MatchDataKey.COMM_PRONOUNS_1, ui.txt_comm1_pronouns.getText());
        matchDAO.set(MatchDataKey.COMM_HANDLE_1, ui.txt_comm1_handle.getText());

        matchDAO.set(MatchDataKey.COMM_NAME_2, ui.combo_comm2.getValue());
        matchDAO.set(MatchDataKey.COMM_TAG_2, ui.txt_comm2_tag.getText());
        matchDAO.set(MatchDataKey.COMM_NATIONALITY_2, ui.combo_comm2_nat.getValue());
        matchDAO.set(MatchDataKey.COMM_PRONOUNS_2, ui.txt_comm2_pronouns.getText());
        matchDAO.set(MatchDataKey.COMM_HANDLE_2, ui.txt_comm2_handle.getText());

        matchDAO.set(MatchDataKey.COMM_NAME_3, ui.combo_comm3.getValue());
        matchDAO.set(MatchDataKey.COMM_TAG_3, ui.txt_comm3_tag.getText());
        matchDAO.set(MatchDataKey.COMM_NATIONALITY_3, ui.combo_comm3_nat.getValue());
        matchDAO.set(MatchDataKey.COMM_PRONOUNS_3, ui.txt_comm3_pronouns.getText());
        matchDAO.set(MatchDataKey.COMM_HANDLE_3, ui.txt_comm3_handle.getText());
    }

    // todo move to OPTIONAL flag module
    /**
     * Utility method, used for loading the UI elements. Copying of the actual files happens in other ways.<br/>
     *
     * @param ISO2_code by standard but in actuality - an extension-less string representing the name of the related file within {@link DataManager#flagsDir}.
     * @return {@link Image} with the loaded flag, {@link DataManager#nullFlag} if the corresponding file cannot be found.
     */
    public Image getFlag(String ISO2_code) {
        if ( ISO2_code == null ) return new Image(nullFlag.toString());

        ISO2_code = ISO2_code.toLowerCase();
        ISO2_code += AppConfig.FLAG_EXTENSION();
        if ( !Files.exists(Path.of(flagsDir + "/" + ISO2_code)) )
            return new Image(nullFlag.toString());
        return new Image(flagsDir + "/" + ISO2_code);
    }

    /**
     * A modified version of {@link DataManager#getFlag} that returns a Base64 String instead of an Image.
     *
     * @param ISO2_code by standard but in actuality - an extension-less string representing the name of the related file within {@link DataManager#flagsDir}.
     * @return Base64 String representation of the loaded flag, {@link DataManager#nullFlag} if the corresponding file cannot be found.
     * @see DataManager#getFlag(String)
     */
    public String getFlagBase64String(String ISO2_code) throws IOException {

        byte[] raw_image;

        if (ISO2_code == null) {
            raw_image = Files.readAllBytes(Path.of(nullFlag.toString()));
        } else {
            ISO2_code = ISO2_code.toLowerCase();
            ISO2_code += AppConfig.FLAG_EXTENSION();

            if (!Files.exists(Path.of(flagsDir + "/" + ISO2_code))) {
                raw_image = Files.readAllBytes(Path.of(nullFlag.toString()));
            } else {
                raw_image = Files.readAllBytes(Path.of(flagsDir + "/" + ISO2_code));
            }
        }

        return Base64.getEncoder().encodeToString(raw_image);
    }

    /**
     * @return the initialization state of the Manager.
     * @see DataManager#initialize(Path)
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Appends an {@link OutputWriter} to the internal writer list.<br/>
     * Note that there is no way to remove writers from the list, they can only be disabled.
     *
     * @param writer
     * @see DataManager#disableWriter(String)
     * @see DataManager#disableWriters(Class)
     * @see OutputWriter#disable()
     */
    public void addWriter(OutputWriter writer) {
        writers.add(writer);

        pcs.firePropertyChange(CHANGED_OUTPUT_WRITERS.toString(), null, null);
    }

    /**
     * Enables the writer selected with its name.<br/>
     * In case there are multiple writers with the same name (which is a breach of the {@link OutputWriter} contract), only the first such writer is enabled.
     * If there are none such writers, nothing is changed.
     *
     * @param byName
     * @see OutputWriter#enable()
     */
    public void enableWriter(String byName) {
        writers.stream().filter(w -> w.getName().equals(byName)).findFirst().ifPresent(OutputWriter::enable);

        pcs.firePropertyChange(CHANGED_OUTPUT_WRITERS.toString(), null, null);
    }

    /**
     * Enables all writers of the desired class. Useful if there are multiple output writers of the same type to be enabled.
     *
     * @param ofClass
     * @param <T>
     * @see OutputWriter#enable()
     */
    public <T extends OutputWriter> void enableWriters(Class<T> ofClass) {
        writers.stream().filter(w -> w.getClass().equals(ofClass)).forEach(OutputWriter::enable);

        pcs.firePropertyChange(CHANGED_OUTPUT_WRITERS.toString(), null, null);
    }

    /**
     * Disables the writer selected with its name.<br/>
     * In case there are multiple writers with the same name (which is a breach of the {@link OutputWriter} contract), only the first such writer is disabled.
     * If there are none such writers, nothing is changed.
     *
     * @param byName
     * @see OutputWriter#disable()
     */
    public void disableWriter(String byName) {
        writers.stream().filter(w -> w.getName().equals(byName)).findFirst().ifPresent(OutputWriter::disable);

        pcs.firePropertyChange(CHANGED_OUTPUT_WRITERS.toString(), null, null);
    }

    /**
     * Disables all writers of the desired class. Useful if there are multiple output writers of the same type to be disabled.
     *
     * @param ofClass
     * @param <T>
     * @see OutputWriter#disable()
     */
    public <T extends OutputWriter> void disableWriters(Class<T> ofClass) {
        writers.stream().filter(w -> w.getClass().equals(ofClass)).forEach(OutputWriter::disable);

        pcs.firePropertyChange(CHANGED_OUTPUT_WRITERS.toString(), null, null);
    }

}
