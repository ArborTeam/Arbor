package zone.little.arbor.config.modules.misc;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.MISC, name = "force_disable_packet_limiter_of_paper", comments =
        "强制并完全禁用 Paper 的所有数据包限制器，用于防止在使用某些快速合成 mod 时被踢出，但 \n" +
                "会对安全性产生负面影响"
)
public class PaperPacketLimiterConfig implements IConfigModule {
    @ConfigInfo(name = "force_disable")
    public static boolean forceDisable = false;
}
