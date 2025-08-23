package me.earthme.luminol.config.modules.misc;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClass;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClass
public class SecureSeedConfig implements IConfigModule {
    @ConfigInfo(baseName = "enabled", comments = """
            Once you enable secure seed, all ores and structures are generated with 1024-bit seed
            instead of using 64-bit seed in vanilla, made seed cracker become impossible.""")
    public static boolean enabled = false;

    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.MISC;
    }

    @Override
    public String getBaseName() {
        return "secure_seed";
    }
}