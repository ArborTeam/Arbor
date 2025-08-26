package me.earthme.luminol.config.modules.optimizations;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.HotReloadUnsupported;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(configAttribution = EnumConfigCategory.OPTIMIZATIONS, mainName = "lithium_sleeping_block_entity")
public class LeavesSleepingBlockEntityConfig implements IConfigModule {
    @ConfigInfo(baseName = "enabled")
    @HotReloadUnsupported
    public static boolean enabled = true;
}
