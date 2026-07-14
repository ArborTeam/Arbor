package zone.little.arbor.config.modules.optimizations;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.OPTIMIZATIONS, name = "reduce_sensor_work", comments = "启用后，将降低清除视线缓存的频率，并使用更快的邻近比较方式。")
public class PetalReduceSensorWorkConfig implements IConfigModule {
    @ConfigInfo(name = "enabled")
    public static boolean enabled = true;
    @ConfigInfo(name = "delay_ticks", comments = "每个实体清除缓存的间隔（单位：刻）")
    public static int delayTicks = 10;
}