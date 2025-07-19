package pl.cheily.filegen.ResourceModules.Plugins.HealthCheckDecorators.Concrete;

import javafx.scene.image.Image;
import pl.cheily.filegen.ResourceModules.Plugins.HealthCheckDecorators.PluginCommandHealthCheckDecorator;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Concrete.FlagProvider.IFlagProvider;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.PluginData;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.PluginHealthData;
import pl.cheily.filegen.ResourceModules.ResourceModule;

import java.net.URL;
import java.util.List;

public class FlagProviderHealthCheckDecorator implements IFlagProvider {
    public IFlagProvider flagProvider;
    private PluginCommandHealthCheckDecorator commandHealthCheckDecorator;

    public FlagProviderHealthCheckDecorator(IFlagProvider flagProvider) {
        this.flagProvider = flagProvider;
        this.commandHealthCheckDecorator = new PluginCommandHealthCheckDecorator(flagProvider);
    }

    @Override
    public Image getFlag(String ISO2) {
        return commandHealthCheckDecorator.invoke(() -> flagProvider.getFlag(ISO2));
    }

    @Override
    public URL getFlagURL(String ISO2) {
        return commandHealthCheckDecorator.invoke(() -> flagProvider.getFlagURL(ISO2));
    }

    @Override
    public String getFlagBase64(String ISO2) {
        return commandHealthCheckDecorator.invoke(() -> flagProvider.getFlagBase64(ISO2));
    }

    @Override
    public PluginData getInfo() {
        return flagProvider.getInfo();
    }

    @Override
    public PluginHealthData getHealthStatus() {
        return flagProvider.getHealthStatus();
    }

    @Override
    public void acceptRequiredModuleStatus(List<ResourceModule> modules) {

    }
}
