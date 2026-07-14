package zone.little.arbor.config.modules.fixes;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.FIXES, name = "item_multitask")
public class ItemMultitaskConfig implements IConfigModule {
    @ConfigInfo(name = "enabled", comments = """
            在方块交互或快捷栏槽位切换期间，
            阻止服务器中断物品的状态。""")
    public static boolean enabled = false;
}
