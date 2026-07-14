package zone.little.arbor.config.modules.optimizations;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.OPTIMIZATIONS, name = "variable_entity_waking_up")
public class GaleVariableEntityWakeupConfig implements IConfigModule {
    @ConfigInfo(name = "entity_wakeup_duration_ratio_standard_deviation", comments = """
            如果该值设置为任意大于 0 的值，唤醒非活跃实体的过程将分散到一段时间内进行，而不是许多实体同时唤醒。这会让实体的感觉和行为更加自然。
            该设置为非活跃持续时间的变异系数，即 σ / μ（标准差与均值的比值）。

            换句话说，该设置即为 σ 值，使得常规的非活跃持续时间将乘以一个 normal_distribution(μ = 1, σ) 的系数。
            如果给定的值 ≤ 0，则禁用可变实体唤醒。""")
    public static double entityWakeUpDurationRatioStandardDeviation = 0.2;
}