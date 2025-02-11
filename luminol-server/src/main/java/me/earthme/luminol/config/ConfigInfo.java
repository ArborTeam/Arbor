package me.earthme.luminol.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigInfo {
    String baseName();

    String comments() default "";
}
