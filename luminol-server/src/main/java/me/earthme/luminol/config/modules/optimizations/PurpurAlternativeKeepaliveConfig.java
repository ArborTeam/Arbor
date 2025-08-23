package me.earthme.luminol.config.modules.optimizations;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClassInfo(configAttribution = EnumConfigCategory.OPTIMIZATIONS, mainName = "alternative_keepalive_handling")
public class PurpurAlternativeKeepaliveConfig implements IConfigModule {
    @ConfigInfo(baseName = "enabled")
    public static boolean useAlternateKeepAlive = false;
}