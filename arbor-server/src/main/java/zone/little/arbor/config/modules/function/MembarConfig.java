package zone.little.arbor.config.modules.function;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.config.flags.DoNotLoad;
import zone.little.arbor.enums.EnumBarType;
import zone.little.arbor.enums.EnumConfigCategory;
import zone.little.arbor.enums.EnumStatusBarDisplay;
import zone.little.arbor.functions.bars.TickableStatusBarList;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

@ConfigClassInfo(category = EnumConfigCategory.FUNCTION, name = "membar")
public class MembarConfig implements IConfigModule {
    @ConfigInfo(name = "enabled")
    public static boolean memoryBarEnabled = false;
    @ConfigInfo(name = "format")
    public static String memBarFormat = "<gray>Memory usage <yellow>:</yellow> <used>MB<yellow>/</yellow><available>MB";
    @ConfigInfo(name = "bar_color_list")
    public static List<BossBar.Color> barColors = List.of(BossBar.Color.GREEN, BossBar.Color.YELLOW, BossBar.Color.RED, BossBar.Color.PURPLE);
    @ConfigInfo(name = "memory_color_list")
    public static List<String> memColors = List.of("<gradient:#55ff55:#00aa00><text></gradient>", "<gradient:#ffff55:#ffaa00><text></gradient>", "<gradient:#ff5555:#aa0000><text></gradient>", "<gradient:#55ff55:#00aa00><text></gradient>");
    @ConfigInfo(name = "update_interval_ticks")
    public static int updateInterval = 15;
    @ConfigInfo(name = "display", comments = "可用的显示方式：BOSS_BAR, ACTION_BAR, TAB_LIST")
    public static EnumStatusBarDisplay display = EnumStatusBarDisplay.BOSS_BAR;

    @DoNotLoad
    private static boolean inited = false;

    @Override
    public void onLoaded(CommentedFileConfig configInstance, @Nullable Set<Exception> e) {
        TickableStatusBarList.raiseGlobalReload(EnumBarType.MEMORY);

        if (!inited) { // command has moved to CommandRegister
            inited = true;
        }
    }

    @Override
    public void onUnloaded(CommentedFileConfig configInstance) {
        Bukkit.getCommandMap().getKnownCommands().remove("arbor:membar");
    }
}