package pl.cheily.filegen.ResourceModules.Plugins.SPI;

import pl.cheily.filegen.ResourceModules.ResourceModule;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public interface IPluginBase {
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Requires {
        public String resourceModule() default "";
    }
    public static @interface RequiresCategory {
        public String resourceModuleCategory() default "";
    }

    public PluginData getInfo();
    public PluginHealthData getHealthStatus();
    public void acceptRequiredModuleStatus(List<ResourceModule> modules);
}
