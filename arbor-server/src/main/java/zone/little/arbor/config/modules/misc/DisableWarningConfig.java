package zone.little.arbor.config.modules.misc;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.MISC, name = "disable_warning")
public class DisableWarningConfig implements IConfigModule {
    @ConfigInfo(name = "disable_heightmap_warning", comments =
            """
                    禁用高度图检测的警告""")
    public static boolean disableHeightmapWarning = false;
    @ConfigInfo(name = "disable_offline_mode_warning", comments = "禁用服务器启动时在日志中弹出的离线模式警告")
    public static boolean disableOfflineModeWarning = false;
    @ConfigInfo(name = "disable_moved_wrongly_threshold_warning", comments = "禁用错误移动的警告和检测")
    public static boolean disableMovedWronglyThresholdWarning = false;
}
