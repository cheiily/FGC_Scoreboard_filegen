package pl.cheily.filegen.ResourceModules.Plugins.SPI;

import org.jetbrains.annotations.NotNull;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.PluginHealthData;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleDefinitionData;
import pl.cheily.filegen.ResourceModules.Plugins.SPI.Status.ResourceModuleStatus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public interface IPluginBase {
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Requires {
        public String resourceModule() default "";
    }
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface RequiresCategory {
        public String resourceModuleCategory() default "";
    }

    public @NotNull ResourceModuleDefinitionData getInfo();
    public @NotNull PluginHealthData getHealthStatus();
    public void acceptRequiredModuleStatus(@NotNull List<ResourceModuleStatus> modules);
}
