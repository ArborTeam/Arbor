package me.earthme.luminol.config.modules.fixes;

import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(name = "recompute_path_when_path_finding_out_of_tick_region", category = EnumConfigCategory.FIXES, comments = "Recompute path when it's touching the blocks out of current tick region")
public class RecomputePathWhenOutOfRegionConfig {
    @ConfigInfo(name = "enabled")
    public static boolean enabled = false;
}
