package zone.little.arbor.config.modules.misc;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import gg.pufferfish.pufferfish.sentry.SentryManager;
import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.CommandSuggestions;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@ConfigClassInfo(category = EnumConfigCategory.MISC, name = "sentry")
public class SentryConfig implements IConfigModule {

    @ConfigInfo(name = "dsn", comments =
            " 用于改进错误日志记录的 Sentry DSN，留空则禁用，\n" +
                    " 请从 https://sentry.io/ 获取")
    public static String sentryDsn = "";

    @CommandSuggestions(suggest = {"OFF", "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL"})
    @ConfigInfo(name = "log_level", comments = " 级别高于或等于此级别的日志将被记录。")
    public static String logLevel = "WARN";

    @ConfigInfo(name = "only_log_thrown", comments = " 启用后，仅记录带有 Throwable 的日志。")
    public static boolean onlyLogThrown = true;

    @Override
    public void onLoaded(CommentedFileConfig configInstance, @Nullable Set<Exception> exs) {
        String sentryEnvironment = System.getenv("SENTRY_DSN");

        sentryDsn = sentryEnvironment != null && !sentryEnvironment.isBlank()
                ? sentryEnvironment
                : configInstance.getOrElse("sentry.dsn", sentryDsn);

        logLevel = configInstance.getOrElse("sentry.log-level", logLevel);
        onlyLogThrown = configInstance.getOrElse("sentry.only-log-thrown", onlyLogThrown);

        if (sentryDsn != null && !sentryDsn.isBlank()) {
            SentryManager.init(Level.getLevel(logLevel));
        }
    }
}