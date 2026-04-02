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

@ConfigClassInfo(category = EnumConfigCategory.FUNCTION, name = "membar")
public class MembarConfig implements IConfigModule {
    @TransformedConfig(name = "enabled", directory = {"misc", "membar"})
    @ConfigInfo(name = "enabled")
    public static boolean memoryBarEnabled = false;
    @TransformedConfig(name = "format", directory = {"misc", "membar"})
    @ConfigInfo(name = "format")
    public static String memBarFormat = "<gray>Memory usage <yellow>:</yellow> <used>MB<yellow>/</yellow><available>MB";
    @TransformedConfig(name = "memory_color_list", directory = {"misc", "membar"})
    @ConfigInfo(name = "memory_color_list")
    public static List<BossBar.Color> memColors = List.of(BossBar.Color.GREEN, BossBar.Color.YELLOW, BossBar.Color.RED, BossBar.Color.PURPLE);
    @TransformedConfig(name = "update_interval_ticks", directory = {"misc", "membar"})
    @ConfigInfo(name = "update_interval_ticks")
    public static int updateInterval = 15;
    @TransformedConfig(name = "display", directory = {"misc", "membar"})
    @CommandSuggestions(suggest = {"BOSS_BAR", "ACTION_BAR", "TAB_LIST"})
    @ConfigInfo(name = "display", comments = "Available displays: BOSS_BAR, ACTION_BAR, TAB_LIST")
    public static EnumStatusBarDisplay display = EnumStatusBarDisplay.BOSS_BAR;

    @DoNotLoad
    private static boolean inited = false;

    @Override
    public void onLoaded(CommentedFileConfig configInstance, @Nullable Set<Exception> e) {
        AbstractGlobalServerBar membar = GlobalServerBarManager.get(EnumBarType.MEMORY);
        if (memoryBarEnabled) {
            membar.init();
        } else {
            membar.cancelBarUpdateTask();
        }

        if (!inited) { // command has moved to CommandRegister
            inited = true;
        }
    }

    @Override
    public void onUnloaded(CommentedFileConfig configInstance) {
        AbstractGlobalServerBar membar = GlobalServerBarManager.get(EnumBarType.MEMORY);
        membar.cancelBarUpdateTask();
        membar.runUnloadTask();
        Bukkit.getCommandMap().getKnownCommands().remove("luminol:membar");
    }
}