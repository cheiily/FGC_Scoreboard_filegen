package pl.cheily.filegen.ResourceModules;

public class ResourceModule {
    ResourceModuleDefinition definition;

    public boolean isDownloaded;
    public boolean isInstalled;
    public boolean isEnabled;

    public String execEntryPoint;

    public ResourceModule(ResourceModuleDefinition definition) {
        this.definition = definition;
        this.isDownloaded = false;
        this.isInstalled = false;
        this.isEnabled = false;
        this.execEntryPoint = null;
    }
}
