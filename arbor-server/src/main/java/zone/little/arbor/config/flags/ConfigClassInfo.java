package zone.little.arbor.config.flags;

import zone.little.arbor.enums.EnumConfigCategory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigClassInfo {
    EnumConfigCategory category();

    String name();

    String[] directory() default {};

    String comments() default "";
}
