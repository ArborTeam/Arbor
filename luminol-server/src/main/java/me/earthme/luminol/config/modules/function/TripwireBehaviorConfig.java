package me.earthme.luminol.config.modules.function;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.CommandSuggestions;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.TransformedConfig;
import me.earthme.luminol.enums.EnumConfigCategory;
import me.earthme.luminol.enums.EnumTripwireBehavior;

@ConfigClassInfo(category = EnumConfigCategory.FUNCTION, name = "tripwire_dupe")
public class TripwireBehaviorConfig implements IConfigModule {
    @TransformedConfig(name = "enabled", directory = {"misc", "tripwire_dupe"})
    @ConfigInfo(name = "enabled")
    public static boolean enabled = false;
    @TransformedConfig(name = "behavior_mode", directory = {"misc", "tripwire_dupe"})
    @TransformedConfig(name = "behavior-mode", directory = {"misc", "tripwire_dupe"})
    @CommandSuggestions(suggest = {"VANILLA20", "VANILLA21", "MIXED"})
    @ConfigInfo(name = "behavior_mode", comments =
            """
                    Available Value:
                    VANILLA20
                    VANILLA21
                    MIXED""")
    public static EnumTripwireBehavior behaviorMode = EnumTripwireBehavior.VANILLA21;
}