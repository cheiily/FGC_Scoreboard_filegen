package pl.cheily.filegen.ResourceModules.Plugins.SPI;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Requires {
    public String[] resourceModules() default {};

    public String[] resourceModuleCategories() default {};
}
