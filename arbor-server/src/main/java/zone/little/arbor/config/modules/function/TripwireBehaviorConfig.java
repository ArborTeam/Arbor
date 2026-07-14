package zone.little.arbor.config.modules.function;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;
import zone.little.arbor.enums.EnumTripwireBehavior;

@ConfigClassInfo(category = EnumConfigCategory.FUNCTION, name = "tripwire_dupe")
public class TripwireBehaviorConfig implements IConfigModule {
    @ConfigInfo(name = "enabled")
    public static boolean enabled = false;

    @ConfigInfo(name = "behavior_mode", comments =
            """
                    可选值：
                    VANILLA20
                    VANILLA21
                    MIXED""")
    public static EnumTripwireBehavior behaviorMode = EnumTripwireBehavior.VANILLA21;
}