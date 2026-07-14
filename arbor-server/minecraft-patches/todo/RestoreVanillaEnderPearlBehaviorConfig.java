package zone.little.arbor.config.modules.fixes;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.FIXES, name = "restore_Vanilla_EnderPearl_Behavior")
public class RestoreVanillaEnderPearlBehaviorConfig implements IConfigModule {
    @ConfigInfo(name = "enabled" , comments = "Restore Vanilla ender pearl behavior")
    public static boolean enabled = true;
}
