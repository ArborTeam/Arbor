package me.earthme.luminol.config.modules.misc;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClassInfo(configAttribution = EnumConfigCategory.MISC, mainName = "force_disable_packet_limiter_of_paper")
public class PaperPacketLimiterConfig implements IConfigModule {
    @ConfigInfo(baseName = "force_disable")
    public static boolean forceDisable = false;
}
