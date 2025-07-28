package pl.cheily.filegen.ResourceModules.Plugins.SPI.Concrete.FlagProvider;

import org.jetbrains.annotations.NotNull;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.IPluginBase;

import java.awt.image.BufferedImage;
import java.net.URL;

//@IPluginBase.Requires(resourceModule = "[flags] Countries")

/// If a specific resource cannot be reached or the input code is invalid, the provider should always return a fallback image.
/// What exactly is the fallback image looks like is up to the implementation, but the recommended options are:
/// - a question mark on a non-transparent contrasting background,
/// - a fully transparent rectangle,
/// - a default, non-blank image of choice, like a symbol or logo.
public interface IFlagProvider extends IPluginBase {
    public @NotNull BufferedImage getFlag(@NotNull String ISO2);
    public @NotNull URL getFlagURL(@NotNull String ISO2);
    public @NotNull String getFlagBase64(@NotNull String ISO2);
}
