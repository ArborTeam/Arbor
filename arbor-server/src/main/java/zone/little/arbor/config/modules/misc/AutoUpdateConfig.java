package zone.little.arbor.config.modules.misc;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.config.flags.DoNotLoad;
import zone.little.arbor.enums.EnumConfigCategory;
import zone.little.arbor.utils.AutoUpdateHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

@ConfigClassInfo(
        category = EnumConfigCategory.MISC,
        name = "auto_update",
        comments = """
                按计划检查 GitHub Releases 上是否有更新的 Arbor jar。
                下载的文件会暂存在 auto_update/arbor 下，并写入 auto_update/core.path，
                Hyacinthusclip 可在下次重启时使用它。
                如果设置了 target_jar_path，Arbor 还会尝试直接替换该启动器 jar。"""
)
public class AutoUpdateConfig implements IConfigModule {
    @ConfigInfo(name = "enabled", comments = "Arbor 是否应自动检查更新。")
    public static boolean enabled = false;

    @ConfigInfo(name = "check_times", comments = "每日检查时间列表，格式为 HH:mm，基于服务器的本地时区。")
    public static List<String> checkTimes = List.of("06:00");

    @ConfigInfo(name = "allow_prerelease", comments = "在选择更新时是否允许使用 GitHub 的预发布版本。")
    public static boolean allowPrerelease = false;

    @ConfigInfo(name = "target_jar_path", comments = """
            可选的启动器 jar 路径，用于在下载成功后进行替换。
            留空则将下载的 jar 暂存在 auto_update/arbor 中，
            并让 Hyacinthusclip 在重启时通过 auto_update/core.path 切换到它。""")
    public static String targetJarPath = "";

    @DoNotLoad
    public AutoUpdateHelper instance = null;

    @Override
    public void onLoaded(CommentedFileConfig configInstance, @Nullable Set<Exception> exs) {
        if (enabled) {
            if (instance == null) {
                instance = new AutoUpdateHelper();
            }
            instance.load(false);
        }
    }

    @Override
    public void onUnloaded(CommentedFileConfig configInstance) {
        if (instance != null) instance.shutdown();
    }
}
