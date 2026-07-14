package zone.little.arbor.config.modules.fixes;

import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.FIXES, name = "long_command_support")
public class LongCommandSupportConfig {
    @ConfigInfo(name = "enabled", comments = """
            某些长命令可以通过 dialog 命令来运行，
            但 paper 禁止了这一行为。
            启用此项以修复该问题。""")
    public static boolean enabled = true;
}
