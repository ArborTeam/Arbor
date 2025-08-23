package me.earthme.luminol.config.modules.optimizations;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClass;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClass
public class PurpurAlternativeKeepaliveConfig implements IConfigModule {
    @ConfigInfo(baseName = "enabled")
    public static boolean useAlternateKeepAlive = false;

    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.OPTIMIZATIONS;
    }

    @Override
    public String getBaseName() {
        return "alternative_keepalive_handling";
    }
}