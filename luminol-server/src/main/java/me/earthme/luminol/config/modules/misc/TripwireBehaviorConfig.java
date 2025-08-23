package me.earthme.luminol.config.modules.misc;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClass;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.TransformedConfig;

@ConfigClass
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

    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.MISC;
    }

    @Override
    public String getBaseName() {
        return "tripwire_dupe";
    }
}