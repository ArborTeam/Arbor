package zone.little.arbor.config.modules.optimizations;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.OPTIMIZATIONS, name = "end_dragon")
public class OptimizedDragonRespawnConfig implements IConfigModule {
    @ConfigInfo(name = "optimized_dragon_respawn")
    public static boolean optimizedRespawn = false;
}
