package pl.cheily.filegen.ResourceModules.Facades;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import pl.cheily.filegen.ResourceModules.Plugins.PluginHandle;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Concrete.FlagProvider.IFlagProvider;

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
        return SwingFXUtils.toFXImage(handle.get().getFlag(ISO2_code), null);
    }

    public static Set<String> getAvailableFlags() {
        return handle.get().getAvailableFlags();
    }
}
