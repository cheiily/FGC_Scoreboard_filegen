package pl.cheily.filegen.UI;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import org.controlsfx.control.PropertySheet;
import pl.cheily.filegen.ResourceModules.Events.ResourceModuleEventType;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Requires;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.PluginHealthData;
import pl.cheily.filegen.ResourceModules.ResourceModule;
import pl.cheily.filegen.ResourceModules.ResourceModuleType;
import pl.cheily.filegen.ScoreboardApplication;
import pl.cheily.filegen.Utils.Pair;
import pl.cheily.filegen.Utils.SafeInvocationUtil;

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

    public ResourceModule module;

    public PropertySheet property_sheet;
    public GridPane prop_grid;
    public Label label_header;
    public Button btn_download;
    public Button btn_install;
    public Button btn_enable;
    public Label label_download;
    public Label label_install;
    public Label label_enable;

    public ListView<String> list_req;
    public TableView<PluginHealthData.HealthRecord> table_health;
    public TableColumn<PluginHealthData.HealthRecord, String> col_method;
    public TableColumn<PluginHealthData.HealthRecord, PluginHealthData.HealthStatus> col_status;
    public TableColumn<PluginHealthData.HealthRecord, String> col_reason;
    public AnchorPane anchor_req;
    public AnchorPane anchor_health;
    public AnchorPane anchor_def;
    private final Pair<Integer, Integer> anchorDefSizes = new Pair<>(300, 523);

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
        resourceModuleRegistry.eventPipeline.subscribe(ResourceModuleEventType.UPDATED_PLUGIN_HEALTH_STATUS, listener);

        col_method.setCellValueFactory(record -> new SimpleStringProperty(record.getValue().methodName()));
        col_status.setCellValueFactory(record -> new SimpleObjectProperty<>(record.getValue().status()));
        col_status.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(PluginHealthData.HealthStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status.name());
                    if (status == PluginHealthData.HealthStatus.READY) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    } else {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    }
                }
            }
        });
        col_reason.setCellValueFactory(record -> new SimpleStringProperty(record.getValue().message()));
        col_method.setStyle("-fx-alignment: CENTER;");
        col_status.setStyle("-fx-alignment: CENTER;");
        col_reason.setStyle("-fx-alignment: CENTER;");
        list_req.setStyle("-fx-alignment: CENTER;");
    }

    public void open(ResourceModule module) {
        this.module = module;
        initWithModule();
        refresh();
    }

    public void on_download(ActionEvent actionEvent) {
        if (module == null) return;
        if (module.isDownloaded())
            resourceModuleRegistry.deleteModuleAsync(module);
        else
            resourceModuleRegistry.downloadModuleAsync(module);
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
        prop_grid.getChildren().clear();

        Label propLabel = new Label("Property");
        propLabel.setTextAlignment(TextAlignment.CENTER);
        propLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        Label valueLabel = new Label("Value");
        valueLabel.setTextAlignment(TextAlignment.CENTER);
        valueLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");

        prop_grid.addRow(0, propLabel, valueLabel);
        var rowCount = new Object() {
            int val = 1;
        };
        module.getDefinition().getProperties().forEach( property -> {
            Label propNameLabel = new Label(property.name());
            propNameLabel.setMinHeight(30);
            propNameLabel.setTextAlignment(TextAlignment.LEFT);

            Text valueText = new Text();
            if (property.type() == String.class)
                valueText.setText((String) property.value());
            else if (property.type() == Boolean.class) {
                valueText.setText((boolean) property.value() ? "YES" : "NO");
                valueText.setFill(Paint.valueOf((boolean) property.value() ? "green" : "red"));
                var font = valueText.getFont();
                valueText.setFont(Font.font(font.getName(), FontWeight.BOLD, font.getSize()));
            } else
                valueText.setText("Parsing error - unsupported type: " + property.type().getSimpleName());

            valueText.wrappingWidthProperty().set(prop_grid.getColumnConstraints().get(1).getPrefWidth());

            if (property.name().equals("url")) {
                valueText.setOnMouseClicked(event -> {
                    ScoreboardApplication.instance.getHostServices().showDocument((String)property.value());
                });
                valueText.setStyle("-fx-cursor: hand; -fx-text-decoration: solid underline; -fx-fill: blue;");
            }
            prop_grid.addRow(rowCount.val, propNameLabel, valueText);
            rowCount.val += 1;
        });
        prop_grid.getColumnConstraints().forEach(constraint -> {
            constraint.setHalignment(HPos.CENTER);
        });
    }

    private void setButtonsEnableState() {
        btn_install.setDisable(!module.isDownloaded());
        btn_enable.setDisable(!module.isInstalled());
    }

    private void loadHealthGrid() {
        table_health.getItems().clear();
        if (module == null || !module.isInstalled()) return;

        var plugin = SafeInvocationUtil.getOrNull(() -> resourceModuleRegistry.pluginRegistry.getExisting(module));
        if (plugin == null) return;

        PluginHealthData healthData = plugin.getHealthStatus();
        table_health.getItems().addAll(healthData.healthRecords());
    }

    private void loadRequirementsList() {
        list_req.getItems().clear();
        if (module == null) return;

        var plugin = SafeInvocationUtil.getOrNull(() -> resourceModuleRegistry.pluginRegistry.getRaw(module));
        if (plugin == null) return;

        Requires req = plugin.getClass().getAnnotation(Requires.class);
        if (req == null) return;

        for (String mod : req.resourceModules()) {
            list_req.getItems().add("Module: " + mod);
        }

        for (String cat : req.resourceModuleCategories()) {
            list_req.getItems().add("Category: " + cat);
        }
    }

    private void refresh() {
        label_header.setText(String.format(module.getDefinition().qualifiedName(), module.getDefinition().name()));
        setLabel(label_download, module.isDownloaded());
        setLabel(label_install, module.isInstalled());
        setLabel(label_enable, module.isEnabled());
        loadButtonTexts();
        setButtonsEnableState();
        loadHealthGrid();
    }

    private void initWithModule() {
        loadPropertySheet();
        loadRequirementsList();
        var isPlugin = SafeInvocationUtil.getOrNull(module::getModuleType) == ResourceModuleType.PLUGIN_JAR;
        anchor_def.setPrefHeight(
                isPlugin
                        ? anchorDefSizes.first()
                        : anchorDefSizes.second()
        );
        anchor_health.setDisable(!isPlugin);
        anchor_health.setVisible(isPlugin);
        anchor_req.setDisable(!isPlugin);
        anchor_req.setVisible(isPlugin);
    }
}
