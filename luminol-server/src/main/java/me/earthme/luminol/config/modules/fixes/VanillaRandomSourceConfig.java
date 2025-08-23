package me.earthme.luminol.config.modules.fixes;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClass;
import me.earthme.luminol.config.flags.ConfigInfo;

@ConfigClass
public class VanillaRandomSourceConfig implements IConfigModule {
    @ConfigInfo(baseName = "enable_for_player_entity", comments = "Related with RNG cracks")
    public static boolean useLegacyRandomSourceForPlayers = false;

    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.FIXES;
    }

    @Override
    public String getBaseName() {
        return "use_vanilla_random_source";
    }
}