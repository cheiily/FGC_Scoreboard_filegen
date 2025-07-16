package pl.cheily.filegen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.cheily.filegen.LocalData.DataManager;
import pl.cheily.filegen.LocalData.FileManagement.Output.Writing.DataWebSocket;
import pl.cheily.filegen.Notifications.NotificationAPIChecker;
import pl.cheily.filegen.Notifications.VersionChecker;
import pl.cheily.filegen.ResourceModules.ResourceModuleDefinitionFetcher;
import pl.cheily.filegen.ResourceModules.ResourceModuleRegistry;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ScoreboardApplication extends Application {
    public static ScoreboardApplication instance;

    public static Scene controllerScene,
                        playersScene,
                        configScene;


    /**
     * Application-wide local data manager. Responsible for loading and saving any meta-data and output files.
     * Supported by the passed set of OutputFormatters and OutputWriters.
     */
    public static DataManager dataManager;
    public static ResourceModuleRegistry resourceModuleRegistry;
    private static Stage mainStage;

    /**
     * An WebSocket server initialised at runtime, can be connected to within OBS to dynamically update a browser source on metadata save.
     * Will be listening on 127.0.0.1:52086 by default.
     * 
     * TODO: Avoid using final in order to add config to change address to listen to.
     */
    public static final DataWebSocket dataWebSocket = new DataWebSocket(new InetSocketAddress("127.0.0.1", 52086));

    @Override
    public void start(Stage stage) throws IOException {
        instance = this;

        var rootlog = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        var appender = new AlertingAppender();
        appender.setContext(rootlog.getLoggerContext());
//        appender.start();//?
        rootlog.addAppender(appender);

        mainStage = stage;
        dataManager = new DataManager();
        resourceModuleRegistry = new ResourceModuleRegistry();
        controllerScene = new Scene(new FXMLLoader(ScoreboardApplication.class.getResource("controller_scene.fxml")).load());
        playersScene = new Scene(new FXMLLoader(ScoreboardApplication.class.getResource("players_scene.fxml")).load());
        configScene = new Scene(new FXMLLoader(ScoreboardApplication.class.getResource("config_scene.fxml")).load());
        mainStage.setTitle("Scoreboard controller");
        mainStage.setResizable(false);
        mainStage.setScene(controllerScene);
        mainStage.show();

        VersionChecker.queueUpdateCheck();
        // todo load persistent config on init & only check for updates if allowed
        NotificationAPIChecker.queueNotificationChecks();
        resourceModuleRegistry.queueInitialize();
        System.out.println("here");
//        dataWebSocket.start();
//
//        // Destruct the server on app closure.
//        // Otherwise the server thread will run indefinitely in the background.
//        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent event) {
//                try {
//                    dataWebSocket.stop();
//                } catch (InterruptedException e) {};
//
//            }
//        });
    }

    public static void setControllerScene() {
        mainStage.setScene(controllerScene);
    }

    public static void setPlayersScene() {
        mainStage.setScene(playersScene);
    }

    public static void setConfigScene() {
        mainStage.setScene(configScene);
    }

    public static void main(String[] args) {
        launch();
    }
}