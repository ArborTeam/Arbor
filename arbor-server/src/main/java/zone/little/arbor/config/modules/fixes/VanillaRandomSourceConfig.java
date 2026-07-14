package zone.little.arbor.config.modules.fixes;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.FIXES, name = "use_vanilla_random_source")
public class VanillaRandomSourceConfig implements IConfigModule {
    @ConfigInfo(name = "enable_for_player_entity", comments = "与 RNG 破解相关")
    public static boolean useLegacyRandomSourceForPlayers = false;
}