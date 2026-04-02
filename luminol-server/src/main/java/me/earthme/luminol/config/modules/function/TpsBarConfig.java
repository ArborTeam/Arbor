package me.earthme.luminol.config.modules.function;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.*;
import me.earthme.luminol.enums.EnumBarType;
import me.earthme.luminol.enums.EnumConfigCategory;
import me.earthme.luminol.enums.EnumStatusBarDisplay;
import me.earthme.luminol.functions.bars.AbstractGlobalServerBar;
import me.earthme.luminol.functions.bars.GlobalServerBarManager;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

@ConfigClassInfo(category = EnumConfigCategory.FUNCTION, name = "tpsbar")
public class TpsBarConfig implements IConfigModule {
    @TransformedConfig(name = "enabled", directory = {"misc", "tpsbar"})
    @ConfigInfo(name = "enabled")
    public static boolean tpsbarEnabled = false;
    @TransformedConfig(name = "format", directory = {"misc", "tpsbar"})
    @ConfigInfo(name = "format")
    public static String tpsBarFormat = "<gray>TPS<yellow>:</yellow> <tps> MSPT<yellow>:</yellow> <mspt> Ping<yellow>:</yellow> <ping>ms ChunkHot<yellow>:</yellow> <chunkhot>";
    @TransformedConfig(name = "tps_color_list", directory = {"misc", "tpsbar"})
    @ConfigInfo(name = "tps_color_list")
    public static List<BossBar.Color> tpsColors = List.of(BossBar.Color.GREEN, BossBar.Color.YELLOW, BossBar.Color.RED, BossBar.Color.PURPLE);
    @TransformedConfig(name = "ping_color_list", directory = {"misc", "tpsbar"})
    @ConfigInfo(name = "ping_color_list")
    public static List<BossBar.Color> pingColors = List.of(BossBar.Color.GREEN, BossBar.Color.YELLOW, BossBar.Color.RED, BossBar.Color.PURPLE);
    @TransformedConfig(name = "chunkhot_color_list", directory = {"misc", "tpsbar"})
    @ConfigInfo(name = "chunkhot_color_list")
    public static List<BossBar.Color> chunkHotColors = List.of(BossBar.Color.GREEN, BossBar.Color.YELLOW, BossBar.Color.RED, BossBar.Color.PURPLE);
    @TransformedConfig(name = "update_interval_ticks", directory = {"misc", "tpsbar"})
    @ConfigInfo(name = "update_interval_ticks")
    public static int updateInterval = 15;
    @TransformedConfig(name = "precision_of_tps_value", directory = {"misc", "tpsbar"})
    @ConfigInfo(name = "precision_of_tps_value", comments = "Example(if tps is 20.00000000)(value -> result): 2 -> 20.00, 1 -> 20.0")
    public static int precisionOfTPS = 2;
    @TransformedConfig(name = "precision_of_mspt_value", directory = {"misc", "tpsbar"})
    @ConfigInfo(name = "precision_of_mspt_value", comments = "Example(if mspt is 20.00000000)(value -> result): 2 -> 20.00, 1 -> 20.0")
    public static int precisionOfMSPT = 2;
    @TransformedConfig(name = "display", directory = {"misc", "tpsbar"})
    @CommandSuggestions(suggest = {"BOSS_BAR", "ACTION_BAR", "TAB_LIST"})
    @ConfigInfo(name = "display", comments = "Available displays: BOSS_BAR, ACTION_BAR, TAB_LIST")
    public static EnumStatusBarDisplay display = EnumStatusBarDisplay.BOSS_BAR;

    @DoNotLoad
    private static boolean inited = false;

    @Override
    public void onLoaded(CommentedFileConfig configInstance, @Nullable Set<Exception> e) {
        AbstractGlobalServerBar tpsbar = GlobalServerBarManager.get(EnumBarType.TPS);

        if (tpsbarEnabled) {
            tpsbar.init();
        } else {
            tpsbar.cancelBarUpdateTask();
        }

        if (!inited) { // command has moved to CommandRegister
            inited = true;
        }
    }

    @Override
    public void onUnloaded(CommentedFileConfig configInstance) {
        AbstractGlobalServerBar tpsbar = GlobalServerBarManager.get(EnumBarType.TPS);
        tpsbar.cancelBarUpdateTask();
        tpsbar.runUnloadTask();
        Bukkit.getCommandMap().getKnownCommands().remove("luminol:tpsbar");
    }
}