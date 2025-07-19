package pl.cheily.filegen.ResourceModules.Plugins.SPI.Concrete.FlagProvider;

import javafx.scene.image.Image;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.IPluginBase;

import java.net.URL;

@IPluginBase.Requires(resourceModule = "[flags] Countries")
public interface IFlagProvider extends IPluginBase {
    public Image getFlag(String ISO2);
    public URL getFlagURL(String ISO2);
    public String getFlagBase64(String ISO2);
}
