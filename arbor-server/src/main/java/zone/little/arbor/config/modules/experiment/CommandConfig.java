package zone.little.arbor.config.modules.experiment;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.config.flags.HotReloadUnsupported;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.EXPERIMENT, name = "command")
public class CommandConfig implements IConfigModule {
    @ConfigInfo(name = "enable_data_command")
    @HotReloadUnsupported
    public static boolean data = false;
    @ConfigInfo(name = "enable_command_block", comments = """
            强制启用命令方块。
            注意：可能因某些线程问题导致服务器崩溃！！！
            除非你清楚自己在做什么，否则不要启用！！！
            """)
    public static boolean commandBlock = false;
    @ConfigInfo(name = "enable_waypoints_and_waypoint_command", comments = """
            启用路径点及路径点命令。
            警告：仍在测试中
            """)
    @HotReloadUnsupported
    public static boolean waypointsAndWaypointCommand = false;
    @ConfigInfo(name = "enable_tick_command", comments = """
            启用后仅允许 freeze/unfreeze/step/query 命令。
            警告：此项应在生产环境中禁用！
            """)
    public static boolean tick = false;
}
