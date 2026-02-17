package pl.cheily.filegen.ResourceModules.Facades;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import pl.cheily.filegen.ResourceModules.Plugins.PluginHandle;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Concrete.FlagProvider.IFlagProvider;
import pl.cheily.filegen.ScoreboardApplication;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

public class FlagModuleFacade {

    private static final PluginHandle<IFlagProvider> handle = PluginHandle.ofType(IFlagProvider.class);

    public static PluginHandle<IFlagProvider> getHandle() {
        return handle;
    }

    public static boolean isAvailable() {
        return handle.isEnabled();
    }

    public static Image getFlag(String ISO2_code) {
        if (ISO2_code == null || ISO2_code.isBlank() || !isAvailable())
            return new Image(ScoreboardApplication.dataManager.nullFlag.toString());

        return SwingFXUtils.toFXImage(handle.get().getFlag(ISO2_code), null);
    }

    public static URL getFlagURL(String ISO2_code) {
        if (ISO2_code == null || ISO2_code.isBlank() || !isAvailable())
            try {
                // should not happen as long as the default is bundled properly
                return ScoreboardApplication.dataManager.nullFlag.toUri().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

        return handle.get().getFlagURL(ISO2_code);
    }

    public static Set<String> getAvailableFlags() {
        return handle.get().getAvailableFlags();
    }

    public static void onInit(Runnable runnable) {
        handle.onInit(runnable);
    }

    public static void onEnable(Runnable runnable) {
        handle.onEnable(runnable);
    }

    public static void onDisable(Runnable runnable) {
        handle.onDisable(runnable);
    }
}
