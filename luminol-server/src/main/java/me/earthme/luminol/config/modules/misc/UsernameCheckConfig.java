package me.earthme.luminol.config.modules.misc;

import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.MISC, name = "username_checks")
public class UsernameCheckConfig implements IConfigModule {
    @ConfigInfo(name = "enabled", comments = "Decide whether the username checks are enabled, \n" +
            " you could disable it if your players are using Chinese username but also notification any security impacts caused by disabling it")
    public static boolean enabled = true;
}