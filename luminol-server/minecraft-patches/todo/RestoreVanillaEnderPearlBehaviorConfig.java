package me.earthme.luminol.config.modules.fixes;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.FIXES, name = "restore_Vanilla_EnderPearl_Behavior")
public class RestoreVanillaEnderPearlBehaviorConfig implements IConfigModule {
    @ConfigInfo(name = "enabled" , comments = "Restore Vanilla ender pearl behavior")
    public static boolean enabled = true;
}
