package me.earthme.luminol.config.modules.misc;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigInfo;

public class PaperPacketLimiterConfig implements IConfigModule {
    @ConfigInfo(baseName = "force_disable")
    public static boolean forceDisable = false;

    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.MISC;
    }

    @Override
    public String getBaseName() {
        return "force_disable_packet_limiter_of_paper";
    }
}
