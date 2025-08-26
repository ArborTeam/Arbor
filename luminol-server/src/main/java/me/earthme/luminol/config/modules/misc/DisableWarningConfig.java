package me.earthme.luminol.config.modules.misc;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.TransformedConfig;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(configAttribution = EnumConfigCategory.MISC, mainName = "disable_warning")
public class DisableWarningConfig implements IConfigModule {
    @TransformedConfig(name = "enabled", category = {"misc", "heightmap_warn_disable"})
    @ConfigInfo(baseName = "disable_heightmap_warning", comments =
            """
                    Disable heightmap-check's warning""")
    public static boolean disableHeightmapWarning = false;
    @ConfigInfo(baseName = "disable_offline_mode_warning")
    public static boolean disableOfflineModeWarning = false;
}
