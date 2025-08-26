package me.earthme.luminol.config.modules.optimizations;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.TransformedConfig;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(configAttribution = EnumConfigCategory.OPTIMIZATIONS, mainName = "force_disable_packet_limiter_of_paper")
public class PaperPacketLimiterConfig implements IConfigModule {
    @TransformedConfig(name = "force_disable", category = {"misc", "force_disable_packet_limiter_of_paper"})
    @ConfigInfo(baseName = "force_disable")
    public static boolean forceDisable = false;
}
