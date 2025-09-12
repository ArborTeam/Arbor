package me.earthme.luminol.config.modules.misc;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import gg.pufferfish.pufferfish.sentry.SentryManager;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.CommandSuggestions;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.enums.EnumConfigCategory;
import org.apache.logging.log4j.Level;

@ConfigClassInfo(configAttribution = EnumConfigCategory.MISC, mainName = "sentry")
public class SentryConfig implements IConfigModule {

    @ConfigInfo(baseName = "dsn", comments =
            " Sentry DSN for improved error logging, leave blank to disable,\n" +
                    " Obtain from https://sentry.io/")
    public static String sentryDsn = "";

    @CommandSuggestions(suggest = {"DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF", "TRACE", "ALL"})
    @ConfigInfo(baseName = "log_level", comments = " Logs with a level higher than or equal to this level will be recorded.")
    public static String logLevel = "WARN";

    @ConfigInfo(baseName = "only_log_thrown", comments = " Only log with a Throwable will be recorded after enabling this.")
    public static boolean onlyLogThrown = true;

    @Override
    public void onLoaded(CommentedFileConfig configInstance) {
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