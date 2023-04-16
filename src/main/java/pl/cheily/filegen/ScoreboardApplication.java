package pl.cheily.filegen;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pl.cheily.filegen.LocalData.DataHttpServer;
import pl.cheily.filegen.LocalData.DataManager;
import pl.cheily.filegen.LocalData.DefaultOutputFormatter;
import pl.cheily.filegen.LocalData.RawOutputWriter;

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
     * An HTTP server initialised at runtime, can be connected to within OBS to dynamically update a browser source on metadata save.
     * Will be listening on 127.0.0.1:52086 by default. <br/>
     * 
     * TODO: Avoid using final in order to add config to change address to listen to.
     */
    public static final DataHttpServer dataHttpServer = new DataHttpServer();

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        dataManager = new DataManager(new RawOutputWriter("default raw-output writer #1", new DefaultOutputFormatter()));
        controllerScene = new Scene(new FXMLLoader(ScoreboardApplication.class.getResource("controller_scene.fxml")).load());
        playersScene = new Scene(new FXMLLoader(ScoreboardApplication.class.getResource("players_scene.fxml")).load());
        configScene = new Scene(new FXMLLoader(ScoreboardApplication.class.getResource("config_scene.fxml")).load());
        mainStage.setTitle("Scoreboard controller");
        mainStage.setResizable(false);
        mainStage.setScene(controllerScene);
        mainStage.show();

        dataHttpServer.start(new InetSocketAddress("127.0.0.1", 52086));

        // Destruct the server on app closure.
        // Otherwise the server thread will run indefinitely in the background.
        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                dataHttpServer.close();
            }
        });
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