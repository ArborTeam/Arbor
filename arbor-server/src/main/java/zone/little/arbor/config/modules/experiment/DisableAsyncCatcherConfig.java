package zone.little.arbor.config.modules.experiment;

import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;

@ConfigClassInfo(category = EnumConfigCategory.EXPERIMENT, name = "disable_async_catchers")
public class DisableAsyncCatcherConfig implements IConfigModule {
    @ConfigInfo(name = "enabled", comments = """
            禁用异步捕捉器，以防止某些声称支持 folia 但存在问题逻辑的插件引发崩溃。
            注意：当 getChunkAt 被错误调用时会导致区域死锁！
                       参见：https://github.com/PaperMC/Folia/issues/280 该问题已在 folia 中修复(https://github.com/PaperMC/Folia/commit/2e7bc0721af95196c85500c7bb136aeea0bc12ce)
            除非你清楚自己在做什么，否则不要启用！！！
            """)
    public static boolean enabled = false;
}