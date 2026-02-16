package me.earthme.luminol.config.modules.fixes;

import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(name = "recompute_path_when_path_finding_out_of_tick_region", category = EnumConfigCategory.FIXES)
public class PathfindingFixesConfig {
    @ConfigInfo(name = "break_down_pathfinding_when_out_of_region", comments = "Recompute path or stop pathfinding when it's touching the blocks out of current tick region")
    public static boolean breakDownPathfindingWhenOutOfRegion = false;
    @ConfigInfo(name = "do_not_pathfind_to_not_owned_targets", comments = "Skip pathfinding target when it's out of current tick region")
    public static boolean doNotPathfindToNotOwnedTargets = false;
}
