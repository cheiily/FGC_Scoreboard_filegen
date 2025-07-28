package pl.cheily.filegen.ResourceModules.Plugins.HealthCheckDecorators.Concrete;

import org.jetbrains.annotations.NotNull;
import pl.cheily.filegen.ResourceModules.Plugins.HealthCheckDecorators.PluginCommandHealthCheckDecorator;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Concrete.FlagProvider.IFlagProvider;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleStatus;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.PluginData;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.PluginHealthData;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

public class FlagProviderHealthCheckDecorator implements IFlagProvider {
    public IFlagProvider flagProvider;
    private PluginCommandHealthCheckDecorator commandHealthCheckDecorator;

    public FlagProviderHealthCheckDecorator(IFlagProvider flagProvider) {
        this.flagProvider = flagProvider;
        this.commandHealthCheckDecorator = new PluginCommandHealthCheckDecorator(flagProvider);
    }

    @NotNull
    @Override
    public BufferedImage getFlag(@NotNull String ISO2) {
        return commandHealthCheckDecorator.invoke(() -> flagProvider.getFlag(ISO2));
    }

    @NotNull
    @Override
    public URL getFlagURL(@NotNull String ISO2) {
        return commandHealthCheckDecorator.invoke(() -> flagProvider.getFlagURL(ISO2));
    }

    @Override
    public @NotNull String getFlagBase64(@NotNull String ISO2) {
        return commandHealthCheckDecorator.invoke(() -> flagProvider.getFlagBase64(ISO2));
    }

    @NotNull
    @Override
    public PluginData getInfo() {
        return flagProvider.getInfo();
    }

    @NotNull
    @Override
    public PluginHealthData getHealthStatus() {
        return flagProvider.getHealthStatus();
    }

    @Override
    public void acceptRequiredModuleStatus(@NotNull List<ResourceModuleStatus> modules) {

    }
}
