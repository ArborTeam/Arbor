package me.earthme.luminol.config.modules.optimizations;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.TransformedConfig;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.OPTIMIZATIONS, name = "force_disable_packet_limiter_of_paper")
public class PaperPacketLimiterConfig implements IConfigModule {
    @TransformedConfig(name = "force_disable", directory = {"misc", "force_disable_packet_limiter_of_paper"})
    @ConfigInfo(name = "force_disable")
    public static boolean forceDisable = false;
}
