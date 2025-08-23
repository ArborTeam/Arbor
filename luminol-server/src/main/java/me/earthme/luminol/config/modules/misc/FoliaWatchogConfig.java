package me.earthme.luminol.config.modules.misc;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClass;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClass
public class FoliaWatchogConfig implements IConfigModule {
    @ConfigInfo(baseName = "tick_region_time_out_ms")
    public static int tickRegionTimeOutMs = 5000;

    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.MISC;
    }

    @Override
    public String getBaseName() {
        return "folia_watchdog";
    }
}