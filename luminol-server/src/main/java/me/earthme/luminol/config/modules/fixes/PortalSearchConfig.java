package me.earthme.luminol.config.modules.fixes;

import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClassInfo(name = "portal_search_config", category = me.earthme.luminol.enums.EnumConfigCategory.FIXES)
public class PortalSearchConfig {
    @ConfigInfo(name = "delay_1_task_if_failed_first_check", comments = "If true, when the first portal search fails, it will delay 1 task to search again or create. Would fix duplicated portals but may have side effects.")
    public static boolean delay1TaskIfFailedFirstCheck = true;
}
