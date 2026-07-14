package zone.little.arbor.config.modules.fixes;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.FIXES, name = "fix_high_velocity_issue")
public class FoliaEntityMovingFixConfig implements IConfigModule {
    @ConfigInfo(name = "enabled", comments =
            """
                    针对 folia 上一个问题的简单修复\s
                    （有时实体会\s
                    产生一次跨越\s
                    不同 tick 区域的大幅移动，从而\s
                    导致服务器崩溃），但有时它可能不起作用""")
    public static boolean enabled = false;

    @ConfigInfo(name = "warn_on_detected")
    public static boolean warnOnDetected = false;
}