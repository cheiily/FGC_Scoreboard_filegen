package pl.cheily.filegen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.cheily.filegen.LocalData.DataManager;
import pl.cheily.filegen.LocalData.DefaultOutputFormatter;
import pl.cheily.filegen.LocalData.DefaultOutputWriter;

import java.io.IOException;

public class ScoreboardApplication extends Application {
    public static Scene controllerScene,
                        playersScene,
                        configScene;


    public static DataManager dataManager;
    private static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(ScoreboardApplication.class.getResource("controller_scene.fxml"));
        controllerScene = new Scene(fxmlLoader.load());
        mainStage.setTitle("Scoreboard controller");
        mainStage.setResizable(false);
        mainStage.setScene(controllerScene);
        mainStage.show();
    }

    public static void setControllerScene() {
        mainStage.setScene(controllerScene);
    }
    
    public static void setPlayersScene() {
        if (playersScene == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ScoreboardApplication.class.getResource("players_scene.fxml"));
                playersScene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        mainStage.setScene(playersScene);
    }
    
    public static void setConfigScene() {
        if (configScene == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(ScoreboardApplication.class.getResource("config_scene.fxml"));
                configScene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        mainStage.setScene(configScene);
    }

    public static void main(String[] args) {
        launch();
    }
}