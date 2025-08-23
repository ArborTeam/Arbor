package me.earthme.luminol.config.modules.misc;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClassInfo(configAttribution = EnumConfigCategory.MISC, mainName = "heightmap_warn_disable")
public class DisableHeightmapWarnConfig implements IConfigModule {
    @ConfigInfo(baseName = "enabled", comments =
            """
                    Disable heightmap-check's warning""")
    public static boolean enabled = false;
}