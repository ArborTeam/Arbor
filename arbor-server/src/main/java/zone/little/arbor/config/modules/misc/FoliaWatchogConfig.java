package zone.little.arbor.config.modules.misc;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.MISC, name = "folia_watchdog")
public class FoliaWatchogConfig implements IConfigModule {
    @ConfigInfo(name = "tick_region_time_out_ms", comments = "决定看门狗打印卡住的 tick区域 线程转储的间隔")
    public static int tickRegionTimeOutMs = 5000;
}