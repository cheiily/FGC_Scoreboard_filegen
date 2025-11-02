package pl.cheily.filegen.ResourceModules.Plugins.Decorators;

import pl.cheily.filegen.ResourceModules.Plugins.SPI.IPluginBase;

public interface IPluginDecorator {
    IPluginBase getUnderlying();
}
