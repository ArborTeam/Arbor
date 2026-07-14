package zone.little.arbor.config.modules.fixes;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.FIXES, name = "allow_unsafe_teleportation")
public class UnsafeTeleportationConfig implements IConfigModule {
    @ConfigInfo(name = "enabled", comments = """
            启用后，允许非玩家实体进入末地传送门。
            如果你想使用沙子复制，请开启此项。
            警告：这会引发一些不安全的问题，你可以在此了解更多：https://github.com/PaperMC/Folia/issues/297""")
    public static boolean enabled = false;
}