package zone.little.arbor.config.modules.experiment;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.EXPERIMENT, name = "bandwidth_optimization", comments = """
        实验性网络带宽优化，默认关闭。
        除非您知道你在做什么，否请勿随意改动默认配置！！！
        """)
public class BandwidthOptimizationConfig implements IConfigModule {
    @ConfigInfo(name = "enabled", comments = "开启实验性带宽优化，默认关闭。")
    public static boolean enabled = false;

    @ConfigInfo(name = "compression_threshold", comments = "达到这个大小的数据包会尝试压缩。数值越小，则省下更多中小包流量。")
    public static int compressionThreshold = 64;

    @ConfigInfo(name = "minimum_saving", comments = "至少要省下多少字节才会使用压缩结果。")
    public static int minimumSaving = 1;
}
