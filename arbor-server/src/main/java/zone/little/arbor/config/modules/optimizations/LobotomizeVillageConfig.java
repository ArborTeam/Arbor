package zone.little.arbor.config.modules.optimizations;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.OPTIMIZATIONS, name = "lobotomize_villager", comments = "当村民无法移动时对其进行呆滞化处理（不会禁用交易）")
public class LobotomizeVillageConfig implements IConfigModule {
    @ConfigInfo(name = "enabled")
    public static boolean villagerLobotomizeEnabled = false;
    @ConfigInfo(name = "check_interval", comments = "检查村民是否处于呆滞化状态的间隔（单位：刻）")
    public static int villagerLobotomizeCheckInterval = 100;
    @ConfigInfo(name = "wait_until_trade_locked", comments = "在对村民进行呆滞化之前，先等待其被交易过")
    public static boolean villagerLobotomizeWaitUntilTradeLocked = false;
}