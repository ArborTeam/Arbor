package me.earthme.luminol.config.modules.misc;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClassInfo(configAttribution = EnumConfigCategory.MISC, mainName = "folia_watchdog")
public class FoliaWatchogConfig implements IConfigModule {
    @ConfigInfo(baseName = "tick_region_time_out_ms")
    public static int tickRegionTimeOutMs = 5000;
}