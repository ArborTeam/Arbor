package me.earthme.luminol.config.modules.misc;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClassInfo(configAttribution = EnumConfigCategory.MISC, mainName = "disable_moved_wrongly_threshold")
public class DisableMovedWronglyThreshold implements IConfigModule {
    @ConfigInfo(baseName = "enabled")
    public static boolean enabled = false;
}