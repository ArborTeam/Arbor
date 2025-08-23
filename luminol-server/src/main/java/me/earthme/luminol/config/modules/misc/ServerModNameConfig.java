package me.earthme.luminol.config.modules.misc;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClass;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClass
public class ServerModNameConfig implements IConfigModule {
    @ConfigInfo(baseName = "name")
    public static String serverModName = "Luminol";

    @ConfigInfo(baseName = "vanilla_spoof")
    public static boolean fakeVanilla = false;

    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.MISC;
    }

    @Override
    public String getBaseName() {
        return "server_mod_name";
    }
}