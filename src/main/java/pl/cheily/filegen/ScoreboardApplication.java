package pl.cheily.filegen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import pl.cheily.filegen.LocalData.DataManager;
import pl.cheily.filegen.LocalData.FileManagement.Output.DataWebSocket;
import pl.cheily.filegen.LocalData.FileManagement.Output.DefaultOutputFormatter;
import pl.cheily.filegen.LocalData.FileManagement.Output.RawOutputWriter;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ScoreboardApplication extends Application {
    public static Scene controllerScene,
                        playersScene,
                        configScene;


    /**
     * Application-wide local data manager. Responsible for loading and saving any meta-data and output files.
     * Supported by the passed set of OutputFormatters and OutputWriters.
     */
    public static DataManager dataManager;
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
        var rootlog = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootlog.addAppender(new AlertingAppender());

        mainStage = stage;
        dataManager = new DataManager(new RawOutputWriter("default raw-output writer #1", new DefaultOutputFormatter()));
        controllerScene = new Scene(new FXMLLoader(ScoreboardApplication.class.getResource("controller_scene.fxml")).load());
        playersScene = new Scene(new FXMLLoader(ScoreboardApplication.class.getResource("players_scene.fxml")).load());
        configScene = new Scene(new FXMLLoader(ScoreboardApplication.class.getResource("config_scene.fxml")).load());
        mainStage.setTitle("Scoreboard controller");
        mainStage.setResizable(false);
        mainStage.setScene(controllerScene);
        mainStage.show();

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