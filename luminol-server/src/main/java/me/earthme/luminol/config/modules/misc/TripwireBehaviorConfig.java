package me.earthme.luminol.config.modules.misc;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.TransformedConfig;

@ConfigClassInfo(configAttribution = EnumConfigCategory.MISC, mainName = "tripwire_dupe")
public class TripwireBehaviorConfig implements IConfigModule {
    @ConfigInfo(baseName = "enabled")
    public static boolean enabled = false;
    @TransformedConfig(name = "behavior-mode", category = {"misc", "tripwire_dupe"})
    @ConfigInfo(baseName = "behavior_mode", comments =
            """
                    Available Value:
                    VANILLA20
                    VANILLA21
                    MIXED""")
    public static String behaviorMode = "VANILLA21";
}