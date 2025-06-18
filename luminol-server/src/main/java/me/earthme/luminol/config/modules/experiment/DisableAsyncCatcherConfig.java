package me.earthme.luminol.config.modules.experiment;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigInfo;

public class DisableAsyncCatcherConfig implements IConfigModule {
    @ConfigInfo(baseName = "enabled")
    public static boolean enabled = false;

    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.EXPERIMENT;
    }

    @Override
    public String getBaseName() {
        return "disable_async_catchers";
    }
}