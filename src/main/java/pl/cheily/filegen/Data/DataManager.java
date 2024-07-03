package pl.cheily.filegen.Data;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import org.ini4j.Ini;
import org.ini4j.Profile;
import pl.cheily.filegen.Configuration.AppConfig;
import pl.cheily.filegen.Data.Structures.MatchData.Player;
import pl.cheily.filegen.UI.ControllerUI;
import pl.cheily.filegen.Utils.Util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static pl.cheily.filegen.Data.DataManager.EventProp.*;
import static pl.cheily.filegen.Data.MetaKey.*;
import static pl.cheily.filegen.ScoreboardApplication.dataWebSocket;

public class DataManager {
    /**
     * Target directory path
     */
    public Path targetDir;
    public final Path flagsDir = Path.of("flags").toAbsolutePath();
    //TODO hook this up to AppConfig#Flagsdir via listener
    public final Path nullFlag = Path.of(flagsDir + "/null.png");
    public final static Set<String> DEFAULT_ROUND_SET = Set.of(
            //w rounds
            "Winners' R1", "Winners' R2", "Winners' R3", "Winners' R4",
            //l rounds
            "Losers' R1", "Losers' R2", "Losers' R3", "Losers' R4",
            //w top 8
            "Winners' Semis", "Winners' Finals",
            //l top 8
            "Losers' Eights", "Losers' Quarters", "Losers' Semis", "Losers' Finals",
            //gf
            "Grand Finals",
            //Extra
            "Top 8", "Winners' top 8", "Losers' top 8", "Losers' top 6", "Losers' top 4",
            "Winners' Eights", "Winners' Quarters", "Pools"
    );
    public enum EventProp {
        INIT("init"),
        SAVE("save"),
        SAVE_CONFIG("save-config"),
        SAVE_LISTS("save-lists"),
        PLAYER_LIST("lists-players"),
        METADATA("metadata"),
        OUTPUT_WRITERS("writers-output");

        private final String prop;

        @Override
        public String toString() {
            return prop;
        }
        
        public static EventProp of(String property) {
            for (EventProp evtProp : EventProp.values()) {
                if (evtProp.prop.equals(property))
                    return evtProp;
            }
            return null;
        }

        EventProp(String prop) {
            this.prop = prop;
        }
    }

    private final List<OutputWriter> writers = new ArrayList<>(2);

    private final Ini metadata = new Ini();
    private final Ini playerList = new Ini();
    private final Set<String> commsList = new HashSet<>();
    private final Set<String> roundSet = new HashSet<>();
    private boolean initialized;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

   /**
     * @param toProp   property to subscribe to
     * @param listener listener to add as a subscriber
     */
    public void subscribe(EventProp toProp, PropertyChangeListener listener) {
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
    public void unsubscribe(EventProp fromProp, PropertyChangeListener listener) {
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
        roundSet.addAll(DEFAULT_ROUND_SET);
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
     * Additionally, the Manager will try to read a {@link ResourcePath#CUSTOM_ROUND_LIST}. If there is no such file found, the {@link DataManager#DEFAULT_ROUND_SET} is loaded instead.
     * As there is not much additional information to ever store about the round labels, the Manager does not store such lists in its "meta" files, but rather holds it in memory while active.
     *
     * @param targetDir {@link Path} representation of the target directory
     * @see DataManager#loadMetadata()
     * @see DataManager#loadInternalLists()
     * @see DataManager#loadCustomLists()
     * @see DataManager#saveLists()
     * @see DataManager#loadRoundsCSV()
     */
    public void initialize(Path targetDir) {
        initialized = true;
        this.targetDir = targetDir;

        metadata.clear();
        playerList.clear();
        commsList.clear();
        roundSet.clear();

        loadMetadata();
        loadInternalLists();
        loadCustomLists();
        saveLists();
        createCustomListsDir();
        loadConfig();

        loadRoundsCSV();
        if ( roundSet.isEmpty() ) roundSet.addAll(DEFAULT_ROUND_SET);
        pcs.firePropertyChange(INIT.toString(), null, null);
    }

    public void reinitialize() {
        initialize(this.targetDir);
    }

    /**
     * Prepares the folder for custom lists.
     * @return success value
     */
    private boolean createCustomListsDir() {
        try {
            Files.createDirectory(Path.of(targetDir + "/lists"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    /**
     * Writes the stored data to files.<br/>
     * Note that the manager will only write to the {@link ResourcePath#METADATA} and output files.
     * The manager will NOT write any of the data contained within {@link DataManager#playerList} or {@link DataManager#commsList}.<br>
     * In case any of the save operations failed, an {@link Alert} will be displayed, listing the filenames.
     * <p>
     * See also: {@link DataManager#saveMeta()}, {@link DataManager#saveOutput()}
     */
    public void save() {
        if ( !saveMeta() )
            new Alert(Alert.AlertType.ERROR, "Couldn't save metadata.", ButtonType.OK).show();

        //Update the Websocket before we return.
        dataWebSocket.updateMetadata();

        List<ResourcePath> failedResourceSaves = saveOutput();
        if ( failedResourceSaves.isEmpty() ) return;

        StringBuilder sb = new StringBuilder();
        sb.append("Couldn't save the following resources: ");
        sb.append(failedResourceSaves);

        new Alert(Alert.AlertType.ERROR, sb.toString(), ButtonType.OK).show();
        pcs.firePropertyChange(SAVE.toString(), null, null);
    }

    /**
     * Writes the overlay output data to the corresponding files.
     * <p>
     * As there is not yet implemented any easy method of mapping {@link ResourcePath}s and {@link MetaKey}s to one another, it is difficult to automate this task.
     * Hence, in case of extension, if the modifier wishes to save any additional data or change the target locations, filenames, extensions, etc.
     * - it is recommended to use {@link OutputWriter#writeData(String, String...)} directly with the desired target path,
     * rather than attempting to extend the ResourcePath and/or MetaKey enums for usage with {@link DataManager#saveResource(ResourcePath, String...)}.
     *
     * @return List of {@link ResourcePath}s, which failed to be saved.
     * @see DataManager#saveResource(ResourcePath, String...)
     * @see DataManager#getMeta(MetaKey, MetaKey)
     */
    private List<ResourcePath> saveOutput() {
        List<ResourcePath> failedSaves = new ArrayList<>();

        if ( !saveResource(ResourcePath.ROUND, getMeta(SEC_ROUND, KEY_ROUND_LABEL)) )
            failedSaves.add(ResourcePath.ROUND);
        if ( !saveResource(ResourcePath.P1_SCORE, getMeta(SEC_ROUND, KEY_SCORE_1)) )
            failedSaves.add(ResourcePath.P1_SCORE);
        if ( !saveResource(ResourcePath.P2_SCORE, getMeta(SEC_ROUND, KEY_SCORE_2)) )
            failedSaves.add(ResourcePath.P2_SCORE);
        if ( AppConfig.PUT_FLAGS() ) {
            if ( !saveResource(ResourcePath.P1_FLAG, (getMeta(SEC_P1, KEY_NATION) + AppConfig.FLAG_EXTENSION()).toLowerCase()) )
                failedSaves.add(ResourcePath.P1_FLAG);
            if ( !saveResource(ResourcePath.P2_FLAG, (getMeta(SEC_P2, KEY_NATION) + AppConfig.FLAG_EXTENSION()).toLowerCase()) )
                failedSaves.add(ResourcePath.P2_FLAG);
        }

        if ( !saveResource(
                ResourcePath.COMMS,
                getMeta(SEC_COMMS, KEY_HOST),
                getMeta(SEC_COMMS, KEY_COMM_1),
                getMeta(SEC_COMMS, KEY_COMM_2)
        ) ) failedSaves.add(ResourcePath.COMMS);

        boolean isGF = getMeta(SEC_ROUND, KEY_GF, boolean.class);
        boolean isReset = getMeta(SEC_ROUND, KEY_GF_RESET, boolean.class);
        boolean winnerSide = isReset ? false
                : getMeta(SEC_ROUND, KEY_GF_W1, boolean.class);

        if ( !saveResource(
                ResourcePath.P1_NAME,
                getMeta(SEC_P1, KEY_TAG),
                getMeta(SEC_P1, KEY_NAME),
                isGF ? Boolean.toString(winnerSide) : null
        ) ) failedSaves.add(ResourcePath.P1_NAME);

        winnerSide = !winnerSide;

        if ( !saveResource(
                ResourcePath.P2_NAME,
                getMeta(SEC_P2, KEY_TAG),
                getMeta(SEC_P2, KEY_NAME),
                isGF ? Boolean.toString(winnerSide) : null
        ) ) failedSaves.add(ResourcePath.P1_NAME);

        return failedSaves;
    }

    /**
     * Utility method. Iterates over the assigned {@link OutputWriter}s and, if they're enabled, instructs them to save the passed data
     *
     * @param rp   {@link ResourcePath} representing the target file, passed via {@link ResourcePath#toString()}
     * @param data vararg package of data to be formatted
     * @return success value
     * @see OutputWriter#writeData(String, String...)
     */
    private boolean saveResource(ResourcePath rp, String... data) {
        boolean success = true;
        for (OutputWriter writer : writers)
            if ( writer.isEnabled() ) success &= writer.writeData(rp.toString(), data);

        return success;
    }

    /**
     * Attempts to store the metadata.
     *
     * @return success value
     * @see Ini#store(File)
     * @see ResourcePath#toPath()
     */
    private boolean saveMeta() {
        try {
            if ( !Files.exists(ResourcePath.METADATA.toPath().getParent()) )
                Files.createDirectories(ResourcePath.METADATA.toPath().getParent());

            metadata.store(ResourcePath.METADATA.toPath().toFile());
            return true;
        } catch (IOException | DataManagerNotInitializedException e) {
            return false;
        }
    }

    /**
     * Attempts to save the currently applied app configuration to {@link ResourcePath#CONFIG}
     * @return success value
     * @see AppConfig#getAsIni()
     */
    public boolean saveConfig() {
        try {
            if ( !Files.exists(ResourcePath.CONFIG.toPath().getParent()) )
                Files.createDirectories(ResourcePath.CONFIG.toPath().getParent());

            AppConfig.getAsIni().store(ResourcePath.CONFIG.toPath().toFile());

            pcs.firePropertyChange(SAVE_CONFIG.toString(), null, null);
            return true;
        } catch (IOException | DataManagerNotInitializedException e) {
            return false;
        }
    }

    /**
     * Attempts to load the app configuration from {@link ResourcePath#CONFIG}
     * @return success value
     * @see AppConfig#loadFromIni(Ini ini)
     */
    public boolean loadConfig() {
        try {
            Ini cfg = new Ini(ResourcePath.CONFIG.toPath().toFile());
            return AppConfig.loadFromIni(cfg);
        } catch (IOException | DataManagerNotInitializedException e) {
            return false;
        }
    }

    /**
     * Attempts to write the internal data into "meta" files.
     * Note that {@link OutputWriter} by contract is only designed to be able to save the overlay output data and not any other, undefined files.
     * For that reason, this method must operate on such files manually.
     *
     * @return success value
     * @see Ini#store(File)
     * @see BufferedWriter
     */
    public boolean saveLists() {
        boolean success = true;

        try {
            if ( !Files.exists(ResourcePath.PLAYER_LIST.toPath().getParent()) )
                Files.createDirectories(ResourcePath.PLAYER_LIST.toPath().getParent());
        } catch (IOException | DataManagerNotInitializedException e) {
            return false;
        }
        try {
            playerList.store(ResourcePath.PLAYER_LIST.toPath().toFile());
        } catch (IOException | DataManagerNotInitializedException e) {
            success = false;
        }
        try ( BufferedWriter bw = Files.newBufferedWriter(ResourcePath.COMMS_LIST.toPath()) ) {
            StringBuilder sb = new StringBuilder();
            commsList.stream()
                    .map(s -> s + '\n')
                    .forEach(sb::append);

            bw.write(sb.toString().trim());
        } catch (IOException | DataManagerNotInitializedException e) {
            success = false;
        }

        pcs.firePropertyChange(SAVE_LISTS.toString(), null, null);
        return success;
    }

    /**
     * Attempts to load {@link ResourcePath#METADATA}
     *
     * @return success value
     * @see Ini#load(File)
     */
    private boolean loadMetadata() {
        try {
            metadata.load(ResourcePath.METADATA.toPath().toFile());
            return true;
        } catch (IOException | DataManagerNotInitializedException e) {
            return false;
        }
    }

    /**
     * Attempts to load the custom lists, excluding the round list.
     *
     * @return propagated success value
     * @see DataManager#loadCommsCSV(ResourcePath)
     * @see DataManager#loadPlayersCSV()
     * @see DataManager#loadRoundsCSV()
     */
    private boolean loadCustomLists() {
        return loadCommsCSV(ResourcePath.CUSTOM_COMMS_LIST) && loadPlayersCSV();
    }

    /**
     * Uses {@link CSVReader} to load the custom player list regardless of its CSV-style.
     * Any such loaded line will be saved into {@link DataManager#playerList} via the {@link DataManager#putPlayer(Player)} method.
     * If the line does not have 3 separate columns - the operation will except and return failure.
     *
     * @return success value, false if any read line is not correctly formatted
     */
    private boolean loadPlayersCSV() {
        try ( CSVReader csvReader = new CSVReader(Files.newBufferedReader(ResourcePath.CUSTOM_PLAYER_LIST.toPath())) ) {
            String[] line;
            while ( (line = csvReader.readNext()) != null )
                putPlayer(new Player(line[ 0 ], line[ 1 ], line[ 2 ]));

            return true;
        } catch (IOException | CsvValidationException | DataManagerNotInitializedException e) {
            return false;
        }
    }

    /**
     * As both the custom and internal commentary lists are stored in the exact same way, this method has been adapted to operate on both.
     * Note that {@link OutputWriter} by contract is only designed to be able to save the overlay output data and not any other, undefined files.
     * For that reason, this method must operate on such files manually.
     *
     * @param rPath {@link ResourcePath} specifying which commentary list to load
     * @return success value
     * @see BufferedReader
     */
    private boolean loadCommsCSV(ResourcePath rPath) {
        try ( BufferedReader bReader = Files.newBufferedReader(rPath.toPath()) ) {
            String line;
            while ( (line = bReader.readLine()) != null )
                commsList.add(line);

            return true;
        } catch (IOException | DataManagerNotInitializedException e) {
            return false;
        }
    }

    /**
     * Attempts to read the custom round list.
     * <p>
     * As this is a more commonly failure-returning operation, it is not included with other lists in {@link DataManager#loadCustomLists()}
     *
     * @return success value
     */
    private boolean loadRoundsCSV() {
        try ( BufferedReader bReader = Files.newBufferedReader(ResourcePath.CUSTOM_ROUND_LIST.toPath()) ) {
            String line;
            while ( (line = bReader.readLine()) != null ) {
                roundSet.add(line);
            }

            return true;
        } catch (IOException | DataManagerNotInitializedException e) {
            return false;
        }
    }

    /**
     * Attempts to load the internal lists.
     *
     * @return success value
     * @see Ini#load(File)
     * @see DataManager#loadCommsCSV(ResourcePath)
     */
    private boolean loadInternalLists() {
        try {
            playerList.load(ResourcePath.PLAYER_LIST.toPath().toFile());
            loadCommsCSV(ResourcePath.COMMS_LIST);
            return true;
        } catch (IOException | DataManagerNotInitializedException e) {
            return false;
        }
    }

    /**
     * Utility method. As the stored metadata is only updated on-save, the most common scenario will be reading all of the related values.
     * This method wraps that operation in order to improve code clarity within the controller class, at the cost of exposing its members to the DataManager.
     *
     * @param ui {@link ControllerUI} to load data from its UI elements
     * @see DataManager#putMeta(MetaKey, MetaKey, String)
     */
    public void loadMetadataFromUI(ControllerUI ui) {
        putMeta(SEC_ROUND, KEY_ROUND_LABEL, ui.combo_round.getValue());
        putMeta(SEC_ROUND, KEY_SCORE_1, ui.txt_p1_score.getText());
        putMeta(SEC_ROUND, KEY_SCORE_2, ui.txt_p2_score.getText());
        putMeta(SEC_ROUND, KEY_GF, String.valueOf(ui.GF_toggle.isSelected()));
        putMeta(SEC_ROUND, KEY_GF_RESET, String.valueOf(ui.radio_reset.isSelected()));
        putMeta(SEC_ROUND, KEY_GF_W1, String.valueOf(ui.radio_p1_W.isSelected()));

        putMeta(SEC_P1, KEY_TAG, ui.txt_p1_tag.getText());
        putMeta(SEC_P1, KEY_NAME, ui.combo_p1_name.getValue());
        putMeta(SEC_P1, KEY_NATION, ui.combo_p1_nation.getValue());

        putMeta(SEC_P2, KEY_TAG, ui.txt_p2_tag.getText());
        putMeta(SEC_P2, KEY_NAME, ui.combo_p2_name.getValue());
        putMeta(SEC_P2, KEY_NATION, ui.combo_p2_nation.getValue());

        putMeta(SEC_COMMS, KEY_HOST, ui.combo_host.getValue());
        putMeta(SEC_COMMS, KEY_COMM_1, ui.combo_comm1.getValue());
        putMeta(SEC_COMMS, KEY_COMM_2, ui.combo_comm2.getValue());
    }

    /**
     * Utility method. Wraps the action of storing all the Player-related data in one clean function call.
     * Also, naturally ensures data correctness or predictability by utilizing the {@link Player} record.
     *
     * @param player to store within the {@link DataManager#playerList}
     */
    public void putPlayer(Player player) {
        putPlayer(player, true);
    }

    private void putPlayer(Player player, boolean fireEvent) {
        playerList.put(player.getName(), KEY_TAG.toString(), player.getTag());
        playerList.put(player.getName(), KEY_NATION.toString(), player.getNationality());
        playerList.put(player.getName(), KEY_SEED.toString(), player.getSeed());
        playerList.put(player.getName(), KEY_ICON.toString(), player.getIconUrl());
        playerList.put(player.getName(), KEY_CHK_IN.toString(), player.isCheckedIn());

        if (fireEvent) pcs.firePropertyChange(PLAYER_LIST.toString(), null, null);
    }

    public void putAllPlayers(List<Player> players) {
        for (Player player : players) {
            putPlayer(player, false);
        }

        pcs.firePropertyChange(PLAYER_LIST.toString(), null, null);
    }

    public void removePlayer(Player player) {
        playerList.remove(player.getName());

        pcs.firePropertyChange(PLAYER_LIST.toString(), null, null);
    }

    public void removeAllPlayers() {
        playerList.clear();

        pcs.firePropertyChange(PLAYER_LIST.toString(), null, null);
    }

    /**
     * Returns an optional value, depending on if such a {@link Player} is contained within the {@link DataManager#playerList}.
     *
     * @param playerName name of the player to search for
     * @return valid {@link Player} if present, {@link Optional#empty()} if not.
     * @see Optional
     */
    public Optional<Player> getPlayer(String playerName) {
        Profile.Section sec_player = playerList.get(playerName);
        if ( sec_player == null ) return Optional.empty();

        return Optional.of(new Player(
                sec_player.get(KEY_TAG.toString()),
                sec_player.getName(),
                sec_player.get(KEY_NATION.toString()),
                sec_player.get(KEY_SEED.toString(), int.class),
                sec_player.get(KEY_ICON.toString(), String.class),
                sec_player.get(KEY_CHK_IN.toString(), boolean.class)
        ));
    }

    /**
     * Returns a list of all the {@link Player}s contained within the {@link DataManager#playerList}.
     *
     * @return list of players
     */
    public List<Player> getAllPlayers() {
        List<Player> ret = new ArrayList<>();
        for (String playerName : playerList.keySet()) {
            Profile.Section sec_player = playerList.get(playerName);

            ret.add(new Player(
                    sec_player.get(KEY_TAG.toString()),
                    sec_player.getName(),
                    sec_player.get(KEY_NATION.toString()),
                    sec_player.get(KEY_SEED.toString(), int.class),
                    sec_player.get(KEY_ICON.toString(), String.class),
                    sec_player.get(KEY_CHK_IN.toString(), boolean.class)
            ));
        }
        return ret;
    }

    /**
     * For utility and performance, this method returns names of all the stored players in an efficient way.
     *
     * @return list of player names
     * @see DataManager#playerList
     */
    public List<String> getAllPlayerNames() {
        return playerList.keySet().stream().toList();
    }

    /**
     * @return list of commentators
     * @see DataManager#commsList
     */
    public List<String> getAllCommentatorNames() {
        return commsList.stream().toList();
    }

    /**
     * @param playerName
     * @return presence value
     * @see Ini#containsKey(Object)
     */
    public boolean hasPlayer(String playerName) {
        return playerList.containsKey(playerName);
    }

    /**
     * Utility method, used for loading the UI elements. Copying of the actual files happens in other ways rather than with the help of this method.<br/>
     *
     * @param ISO2_code by standard but in actuality - an extension-less string representing the name of the related file within {@link DataManager#flagsDir}.
     * @return {@link Image} with the loaded flag, {@link DataManager#nullFlag} if the corresponding file cannot be found.
     * @see DataManager#saveResource(ResourcePath, String...)
     * @see OutputWriter#writeData(String, String...)
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
     * @see DataManager#saveResource(ResourcePath, String...)
     * @see OutputWriter#writeData(String, String...)
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
     * @return current round labels as immutable set
     */
    public Set<String> getRoundLabels() {
        return Collections.unmodifiableSet(roundSet);
    }

    /**
     * @return list of the current round labels, sorted with {@link Util#roundComparator}
     */
    public List<String> getRoundLabelsSorted() {
        return getRoundLabelsSorted(Util.roundComparator);
    }

    /**
     * @param comparator to apply
     * @return list of the current round labels, sorted with the passed comparator
     */
    public List<String> getRoundLabelsSorted(Comparator<String> comparator) {
        List<String> ret = new ArrayList<>(roundSet);
        ret.sort(comparator);
        return ret;
    }

    /**
     * Puts a metadata entry within {@link DataManager#metadata}
     *
     * @param section
     * @param key
     * @param value
     * @see Ini#put(String, String, Object)
     */
    public void putMeta(MetaKey section, MetaKey key, String value) {
        metadata.put(section.toString(), key.toString(), value);

        pcs.firePropertyChange(METADATA.toString(), null, null);
    }

    /**
     * Wraps null values with an empty string literal.
     *
     * @param section
     * @param key
     * @return an entry from {@link DataManager#metadata}
     * @see Ini#get(Object, Object)
     */
    public String getMeta(MetaKey section, MetaKey key) {
        String ret = metadata.get(section.toString(), key.toString());
        return ret == null ? "" : ret;
    }

    /**
     * @param section
     * @param key
     * @param clazz
     * @param <T>
     * @return an entry from {@link DataManager#metadata} cast to the desired type
     * @see Ini#get(Object, Object, Class)
     */
    public <T> T getMeta(MetaKey section, MetaKey key, Class<T> clazz) {
        return metadata.get(section.toString(), key.toString(), clazz);
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

        pcs.firePropertyChange(OUTPUT_WRITERS.toString(), null, null);
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

        pcs.firePropertyChange(OUTPUT_WRITERS.toString(), null, null);
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

        pcs.firePropertyChange(OUTPUT_WRITERS.toString(), null, null);
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

        pcs.firePropertyChange(OUTPUT_WRITERS.toString(), null, null);
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

        pcs.firePropertyChange(OUTPUT_WRITERS.toString(), null, null);
    }

}
