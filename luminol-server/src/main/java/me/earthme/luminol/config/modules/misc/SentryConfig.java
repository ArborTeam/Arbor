package me.earthme.luminol.config.modules.misc;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import gg.pufferfish.pufferfish.sentry.SentryManager;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.CommandSuggestions;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.enums.EnumConfigCategory;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@ConfigClassInfo(category = EnumConfigCategory.MISC, name = "sentry")
public class SentryConfig implements IConfigModule {

    @ConfigInfo(name = "dsn", comments =
            " Sentry DSN for improved error logging, leave blank to disable,\n" +
                    " Obtain from https://sentry.io/")
    public static String sentryDsn = "";

    @CommandSuggestions(suggest = {"OFF", "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL"})
    @ConfigInfo(name = "log_level", comments = " Logs with a level higher than or equal to this level will be recorded.")
    public static Level logLevel = Level.WARN;

    @ConfigInfo(name = "only_log_thrown", comments = " Only log with a Throwable will be recorded after enabling this.")
    public static boolean onlyLogThrown = true;

    @Override
    public void onLoaded(CommentedFileConfig configInstance, @Nullable Set<Exception> e) {
        if (sentryDsn != null && !sentryDsn.isBlank()) {
            SentryManager.init(logLevel);
        }
    }
}