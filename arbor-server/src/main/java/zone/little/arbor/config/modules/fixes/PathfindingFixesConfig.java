package zone.little.arbor.config.modules.fixes;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.FIXES, name = "pathfinding_fixes")
public class PathfindingFixesConfig implements IConfigModule {
    @ConfigInfo(name = "break_down_pathfinding_when_out_of_region", comments = "当寻路接触到当前 tick 区域之外的方块时，重新计算路径或停止寻路")
    public static boolean breakDownPathfindingWhenOutOfRegion = false;
    @ConfigInfo(name = "do_not_pathfind_to_not_owned_targets", comments = "当寻路目标位于当前 tick 区域之外时，跳过该目标")
    public static boolean doNotPathfindToNotOwnedTargets = false;
}
