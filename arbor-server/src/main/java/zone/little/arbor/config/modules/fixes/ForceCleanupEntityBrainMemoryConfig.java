package zone.little.arbor.config.modules.fixes;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.FIXES, name = "force_cleanup_drop_non_owned_entity_memory_module", comments = "此配置是针对每个生物记忆中那些不正确的归属数据的临时修复，更多信息可参见 https://github.com/PaperMC/Folia/issues/203")
public class ForceCleanupEntityBrainMemoryConfig implements IConfigModule {
    @ConfigInfo(name = "enabled_for_entity", comments = "启用后，实体的 brain 将清理类型为 entity 且不属于当前 tick 区域的记忆")
    public static boolean enabledForEntity = false;

    @ConfigInfo(name = "enabled_for_block_pos", comments = "启用后，实体的 brain 将清理类型为 block_pos 且不属于当前 tick 区域的记忆")
    public static boolean enabledForBlockPos = false;

    @ConfigInfo(name = "enabled_for_position_tracker", comments = "启用后，实体的 brain 将清理类型为 position_tracker 且不属于当前 tick 区域的记忆")
    public static boolean enabledForPositionTracker = false;
}