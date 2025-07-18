package pl.cheily.filegen.Utils;

import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import pl.cheily.filegen.ResourceModules.ResourceModule;
import pl.cheily.filegen.ScoreboardApplication;
import pl.cheily.filegen.UI.PlayerEditPopupUI;
import pl.cheily.filegen.UI.ResourceModuleDetailsPopupUI;

import java.util.List;

import static pl.cheily.filegen.ScoreboardApplication.resourceModuleRegistry;

public class ResourceModuleActionBundleFactory {
    private static final String PROP_DOWNLOAD = "Download";
    private static final String PROP_DELETE = "Delete";
    private static final String PROP_INSTALL = "Install";
    private static final String PROP_UNINSTALL = "Uninstall";
    private static final String PROP_ENABLE = "Enable";
    private static final String PROP_DISABLE = "Disable";
    private static final String PROP_MORE = "More";

    public static FlowPane getPane(ResourceModule module) {
        FlowPane pane = new FlowPane();
        pane.setPrefHeight(50);
        pane.setAlignment(Pos.CENTER);
        pane.getChildren().addAll(getButtons(module));
        return pane;
    }

    private static List<Button> getButtons(ResourceModule module) {
        Button downloadButton = new Button(module.isDownloaded() ? PROP_DELETE : PROP_DOWNLOAD);
        downloadButton.setOnAction(evt -> {
            if (module.isDownloaded()) {
                resourceModuleRegistry.deleteModuleAsync(module);
            } else {
                resourceModuleRegistry.downloadModuleAsync(module);
            }
        });

        Button installButton = new Button(module.isInstalled() ? PROP_UNINSTALL : PROP_INSTALL);
        installButton.setOnAction(evt -> {
            if (module.isInstalled()) {
                resourceModuleRegistry.uninstallModuleAsync(module);
            } else {
                resourceModuleRegistry.installModuleAsync(module);
            }
        });
        installButton.setDisable(!module.isDownloaded());

        Button enableButton = new Button(module.isEnabled() ? PROP_DISABLE : PROP_ENABLE);
        enableButton.setOnAction(evt -> {
            if (module.isEnabled()) {
                resourceModuleRegistry.disableModule(module);
            } else {
                resourceModuleRegistry.enableModule(module);
            }
        });
        enableButton.setDisable(!module.isInstalled());

        Button moreButton = new Button(PROP_MORE);
        moreButton.setOnAction(evt -> {
            Stage popupStage = new Stage();
            popupStage.setTitle("Resource Module Details");

            FXMLLoader loader = new FXMLLoader(ScoreboardApplication.class.getResource("resource_module_details_popup.fxml"));
            try {
                Parent root = loader.load();
                ResourceModuleDetailsPopupUI controller = loader.getController();
                controller.open(module);
                Scene scene = new Scene(root);
                popupStage.setScene(scene);
            } catch (Exception e) {
                e.printStackTrace();
            }

            popupStage.show();
        });

        return List.of(
                downloadButton,
                installButton,
                enableButton,
                moreButton
        );
    }
}
