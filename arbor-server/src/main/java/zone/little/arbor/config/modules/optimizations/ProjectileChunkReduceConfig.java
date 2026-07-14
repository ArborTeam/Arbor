package zone.little.arbor.config.modules.optimizations;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.OPTIMIZATIONS, name = "projectile")
public class ProjectileChunkReduceConfig implements IConfigModule {
    @ConfigInfo(name = "max-loads-per-tick", comments = "控制每一刻内弹射物允许同步加载的区块数量。")
    public static int maxProjectileLoadsPerTick;
    @ConfigInfo(name = "max-loads-per-projectile", comments = "控制单个弹射物在被自动移除之前，其生命周期内可加载的区块数量。")
    public static int maxProjectileLoadsPerProjectile;
}