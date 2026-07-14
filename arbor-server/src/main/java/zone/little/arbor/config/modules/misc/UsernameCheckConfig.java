package zone.little.arbor.config.modules.misc;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.logging.LogUtils;
import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.config.flags.DoNotLoad;
import zone.little.arbor.enums.EnumConfigCategory;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Set;
import java.util.regex.Pattern;

@ConfigClassInfo(category = EnumConfigCategory.MISC, name = "username_checks")
public class UsernameCheckConfig implements IConfigModule {
    @DoNotLoad
    private static final Logger LOGGER = LogUtils.getLogger();

    @ConfigInfo(name = "enabled", comments = "决定是否启用用户名检测，\n" +
            " 如果你的玩家使用中文用户名，你可以将其禁用，但也请注意禁用它可能带来的任何安全影响")
    public static boolean enabled = true;
    @ConfigInfo(name = "enforce_skull_validation", comments = """
            强制进行头颅校验，防止带有非法名称的头颅导致客户端断开连接。
            """)
    public static boolean enforceSkullValidation = true;
    @ConfigInfo(name = "allow_old_player_join", comments = """
            在用户名正则表达式更改后，允许老玩家加入服务器，
            即使他们的名称不符合新的要求。
            """)
    public static boolean allowOldPlayersJoin = false;

    @DoNotLoad
    private static final String defaultUsernameCheckRegex = "^[a-zA-Z0-9_.]*$";
    @ConfigInfo(name = "username_check_regex", comments = """
             使用用户名正则表达式来校验用户名，
             仅允许正则表达式中指定的字符。
            """)
    public static final String usernameCheckRegex = defaultUsernameCheckRegex;

    @DoNotLoad
    public static Pattern usernameRegex;

    public static boolean useCustomUsernameRegex() {
        return !usernameCheckRegex.equals(defaultUsernameCheckRegex);
    }

    public static boolean shouldSkipNonPlayerNameCheck() { // helper
        return !enabled || !usernameCheckRegex.equals(defaultUsernameCheckRegex);
    }

    @Override
    public void onLoaded(CommentedFileConfig configInstance, @Nullable Set<Exception> e) {
        try {
            usernameRegex = Pattern.compile(usernameCheckRegex);
        } catch (Exception ex) {
            LOGGER.error("Failed to parse regex! Falling back to default", ex);

            usernameRegex = Pattern.compile(defaultUsernameCheckRegex);
        }
    }
}