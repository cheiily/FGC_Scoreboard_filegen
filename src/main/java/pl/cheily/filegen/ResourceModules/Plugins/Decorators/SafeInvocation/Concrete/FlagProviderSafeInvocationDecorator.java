package pl.cheily.filegen.ResourceModules.Plugins.Decorators.SafeInvocation.Concrete;

import org.jetbrains.annotations.NotNull;
import pl.cheily.filegen.ResourceModules.Exceptions.Plugins.PluginCommandInvocationRuntimeException;
import pl.cheily.filegen.ResourceModules.Plugins.Decorators.SafeInvocation.PluginCommandSafeInvocationDecorator;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Concrete.FlagProvider.IFlagProvider;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.PluginHealthData;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleDefinitionData;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleStatus;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

public class FlagProviderSafeInvocationDecorator implements IFlagProvider {
    public IFlagProvider flagProvider;
    private final PluginCommandSafeInvocationDecorator commandSafeInvocationDecorator;

    public FlagProviderSafeInvocationDecorator(IFlagProvider flagProvider) {
        this.flagProvider = flagProvider;
        this.commandSafeInvocationDecorator = new PluginCommandSafeInvocationDecorator(flagProvider);
    }

    @NotNull
    @Override
    public BufferedImage getFlag(@NotNull String ISO2) throws PluginCommandInvocationRuntimeException {
        return commandSafeInvocationDecorator.invoke(() -> flagProvider.getFlag(ISO2), "getFlag");
    }

    @NotNull
    @Override
    public URL getFlagURL(@NotNull String ISO2) throws PluginCommandInvocationRuntimeException {
        return commandSafeInvocationDecorator.invoke(() -> flagProvider.getFlagURL(ISO2), "getFlagURL");
    }

    @Override
    public @NotNull String getFlagBase64(@NotNull String ISO2) throws PluginCommandInvocationRuntimeException {
        return commandSafeInvocationDecorator.invoke(() -> flagProvider.getFlagBase64(ISO2), "getFlagBase64");
    }

    @NotNull
    @Override
    public ResourceModuleDefinitionData getInfo() throws PluginCommandInvocationRuntimeException {
        return commandSafeInvocationDecorator.invoke(() -> flagProvider.getInfo(), "getInfo");
    }

    @NotNull
    @Override
    public PluginHealthData getHealthStatus() throws PluginCommandInvocationRuntimeException {
        return commandSafeInvocationDecorator.invoke(() -> flagProvider.getHealthStatus(), "getHealthStatus");
    }

    @Override
    public void acceptRequiredModuleStatus(@NotNull List<ResourceModuleStatus> modules) throws PluginCommandInvocationRuntimeException {
        commandSafeInvocationDecorator.invoke(() -> {
            flagProvider.acceptRequiredModuleStatus(modules);
            return null;
        }, "acceptRequiredModuleStatus");
    }
}
