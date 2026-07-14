package zone.little.arbor.config.modules.optimizations;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.config.flags.HotReloadUnsupported;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.OPTIMIZATIONS, name = "lithium_sleeping_block_entity")
public class LeavesSleepingBlockEntityConfig implements IConfigModule {
    @ConfigInfo(name = "enabled", comments = """
            使用来自 lithium 的休眠方块实体优化，\s
             在 arbor 上，paper 的漏斗优化已被完全移除并替换为 lithium 的优化，\s
            并且默认开启。""")
    @HotReloadUnsupported
    public static boolean enabled = true;
}
