package me.earthme.luminol.config.modules.function;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.TransformedConfig;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(configAttribution = EnumConfigCategory.FUNCTION, mainName = "tripwire_dupe")
public class TripwireBehaviorConfig implements IConfigModule {
    @TransformedConfig(name = "enabled", category = {"misc", "tripwire_dupe"})
    @ConfigInfo(baseName = "enabled")
    public static boolean enabled = false;
    @TransformedConfig(name = "behavior_mode", category = {"misc", "tripwire_dupe"})
    @TransformedConfig(name = "behavior-mode", category = {"misc", "tripwire_dupe"})
    @ConfigInfo(baseName = "behavior_mode", comments =
            """
                    Available Value:
                    VANILLA20
                    VANILLA21
                    MIXED""")
    public static String behaviorMode = "VANILLA21";
}