package pl.cheily.filegen.UI;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.BeanPropertyUtils;
import pl.cheily.filegen.ResourceModules.Events.ResourceModuleEventType;
import pl.cheily.filegen.ResourceModules.ResourceModule;

import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ResourceBundle;

import static pl.cheily.filegen.ScoreboardApplication.resourceModuleRegistry;

public class ResourceModuleDetailsPopupUI implements Initializable {
    private static final String PROP_DOWNLOAD = "Download";
    private static final String PROP_DELETE = "Delete";
    private static final String PROP_INSTALL = "Install";
    private static final String PROP_UNINSTALL = "Uninstall";
    private static final String PROP_ENABLE = "Enable";
    private static final String PROP_DISABLE = "Disable";
    private final static String PROP_HEADER = "Resource Module \"%s\"";

    public ResourceModule module;

    public PropertySheet property_sheet;
    public Label label_header;
    public Button btn_download;
    public Button btn_install;
    public Button btn_enable;
    public Label label_download;
    public Label label_install;
    public Label label_enable;


    private final PropertyChangeListener listener = evt -> {
        if (module == null) return;

        ResourceModule evtModule = (ResourceModule) evt.getNewValue();
        if (evtModule.getDefinition().name().equals(module.getDefinition().name())) {
            refresh();
        }
    };


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resourceModuleRegistry.eventPipeline.subscribe(ResourceModuleEventType.DOWNLOADED_MODULE, listener);
        resourceModuleRegistry.eventPipeline.subscribe(ResourceModuleEventType.REMOVED_MODULE, listener);
        resourceModuleRegistry.eventPipeline.subscribe(ResourceModuleEventType.INSTALLED_MODULE, listener);
        resourceModuleRegistry.eventPipeline.subscribe(ResourceModuleEventType.UNINSTALLED_MODULE, listener);
        resourceModuleRegistry.eventPipeline.subscribe(ResourceModuleEventType.ENABLED_MODULE, listener);
        resourceModuleRegistry.eventPipeline.subscribe(ResourceModuleEventType.DISABLED_MODULE, listener);
    }

    public void open(ResourceModule module) {
        this.module = module;
        refresh();
    }

    public void on_download(ActionEvent actionEvent) {
        if (module == null) return;
        if (module.isDownloaded())
            resourceModuleRegistry.downloadModuleAsync(module);
        else
            resourceModuleRegistry.deleteModuleAsync(module);
    }

    public void on_install(ActionEvent actionEvent) {
        if (module == null) return;
        if (module.isInstalled())
            resourceModuleRegistry.uninstallModuleAsync(module);
        else
            resourceModuleRegistry.installModuleAsync(module);
    }

    public void on_enable(ActionEvent actionEvent) {
        if (module == null) return;
        if (module.isEnabled())
            resourceModuleRegistry.disableModule(module);
        else
            resourceModuleRegistry.enableModule(module);
    }


    private void loadButtonTexts() {
        btn_download.setText(module.isDownloaded() ? PROP_DELETE : PROP_DOWNLOAD);
        btn_install.setText(module.isInstalled() ? PROP_UNINSTALL : PROP_INSTALL);
        btn_enable.setText(module.isEnabled() ? PROP_DISABLE : PROP_ENABLE);
    }

    private void setLabel(Label label, boolean value) {
        String text = value ? "YES" : "NO";
        label.setText(text);
        label.setStyle("-fx-text-fill: " + (value ? "green" : "red") + ";");
    }

    private void loadPropertySheet() {
        property_sheet.getItems().setAll(BeanPropertyUtils.getProperties(module.getDefinition()));
    }

    private void refresh() {
        label_header.setText(String.format(PROP_HEADER, module.getDefinition().name()));
        setLabel(label_download, module.isDownloaded());
        setLabel(label_install, module.isInstalled());
        setLabel(label_enable, module.isEnabled());
        loadPropertySheet();
        loadButtonTexts();
    }
}
