package me.earthme.luminol.config.modules.experiment;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClassInfo(configAttribution = EnumConfigCategory.EXPERIMENT, mainName = "disable_async_catchers")
public class DisableAsyncCatcherConfig implements IConfigModule {
    @ConfigInfo(baseName = "enabled")
    public static boolean enabled = false;
}