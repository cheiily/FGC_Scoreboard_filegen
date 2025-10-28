package pl.cheily.filegen.ResourceModules.Plugins.SPI;

import org.jetbrains.annotations.NotNull;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.PluginHealthData;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleDefinitionData;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleStatus;

import java.util.List;

public interface IPluginBase {

    public @NotNull ResourceModuleDefinitionData getInfo();
    public @NotNull PluginHealthData getHealthStatus();
    public void acceptRequiredModuleStatus(@NotNull List<ResourceModuleStatus> modules);
}
