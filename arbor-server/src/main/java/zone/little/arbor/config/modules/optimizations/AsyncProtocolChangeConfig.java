package zone.little.arbor.config.modules.optimizations;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.OPTIMIZATIONS, name = "use_async_protocol_switching")
public class AsyncProtocolChangeConfig implements IConfigModule {
    @ConfigInfo(name = "enabled", comments = """
            为 mc 使用异步协议准备。
            警告：由于该优化改变了数据包的顺序，它可能\s
             与某些插件（ViaVersion 等）不兼容。""")
    public static boolean enabled = false;
}
