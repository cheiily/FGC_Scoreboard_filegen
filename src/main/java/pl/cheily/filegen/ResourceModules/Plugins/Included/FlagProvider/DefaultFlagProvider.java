package pl.cheily.filegen.ResourceModules.Plugins.Included.FlagProvider;

import javafx.scene.image.Image;
import pl.cheily.filegen.LocalData.DataManagerNotInitializedException;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Concrete.FlagProvider.IFlagProvider;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.IPluginBase;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.PluginData;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.PluginHealthData;
import pl.cheily.filegen.ResourceModules.ResourceModule;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

import static pl.cheily.filegen.ResourceModules.Plugins.SPI.PluginHealthData.HealthStatus.*;

public class DefaultFlagProvider implements IFlagProvider {
    private final PluginData pluginData = new PluginData(
        "Included Flag Provider",
        "Default provider intended for handling the basic flag modules featured on SSC_Resources.",
        "1.0.0",
        "2025-07-18T23:44:59+02:00",
        "cheily"
    );
    private ResourceModule cachedModule = null;
    private boolean cachedCanReach = false;

    public DefaultFlagProvider() {}

    @Override
    public PluginData getInfo() {
        return pluginData;
    }

    @Override
    public PluginHealthData getHealthStatus() {
        PluginHealthData healthData = new PluginHealthData();
        healthData.healthRecords.add(
            new PluginHealthData.HealthRecord(
                "getFlag",
                    cachedCanReach ? READY : NOT_READY,
                    cachedCanReach ? "" : "Extracted flag files unreadable."
            )
        );
        healthData.healthRecords.add(
            new PluginHealthData.HealthRecord(
                "getFlagURL",
                    cachedCanReach ? READY : NOT_READY,
                    cachedCanReach ? "" : "Extracted flag files unreadable."
            )
        );
        healthData.healthRecords.add(
            new PluginHealthData.HealthRecord(
                "getFlagBase64",
                    cachedCanReach ? READY : NOT_READY,
                    cachedCanReach ? "" : "Extracted flag files unreadable."
            )
        );

        return healthData;
    }

    @Override
    public void acceptRequiredModuleStatus(List<ResourceModule> modules) {
        cachedModule = modules.get(0);

        File[] files = cachedModule.getDefinition().getInstallDirPath().toFile().listFiles();
        cachedCanReach = files != null && files.length > 0;
        for (File file : files) {
            cachedCanReach &= Files.isReadable(file.toPath());
        }
    }

    @Override
    public Image getFlag(String ISO2) {
        return null;
    }

    @Override
    public URL getFlagURL(String ISO2) {
        return null;
    }

    @Override
    public String getFlagBase64(String ISO2) {
        return "";
    }
}
