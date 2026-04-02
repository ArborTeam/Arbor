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

@ConfigClassInfo(category = EnumConfigCategory.FUNCTION, name = "regionbar")
public class RegionBarConfig implements IConfigModule {
    @TransformedConfig(name = "enabled", directory = {"misc", "regionbar"})
    @ConfigInfo(name = "enabled")
    public static boolean regionbarEnabled = false;
    @TransformedConfig(name = "format", directory = {"misc", "regionbar"})
    @ConfigInfo(name = "format")
    public static String regionBarFormat = "<gray>Util<yellow>:</yellow> <util> Chunks<yellow>:</yellow> <green><chunks></green> Players<yellow>:</yellow> <green><players></green> Entities<yellow>:</yellow> <green><entities></green>";
    @TransformedConfig(name = "util_color_list", directory = {"misc", "regionbar"})
    @ConfigInfo(name = "util_color_list")
    public static List<BossBar.Color> utilColors = List.of(BossBar.Color.GREEN, BossBar.Color.YELLOW, BossBar.Color.RED, BossBar.Color.PURPLE);
    @TransformedConfig(name = "update_interval_ticks", directory = {"misc", "regionbar"})
    @ConfigInfo(name = "update_interval_ticks")
    public static int updateInterval = 15;
    @CommandSuggestions(suggest = {"BOSS_BAR", "ACTION_BAR", "TAB_LIST"})
    @TransformedConfig(name = "display", directory = {"misc", "regionbar"})
    @ConfigInfo(name = "display", comments = "Available displays: BOSS_BAR, ACTION_BAR, TAB_LIST")
    public static EnumStatusBarDisplay display = EnumStatusBarDisplay.BOSS_BAR;

    @DoNotLoad
    private static boolean inited = false;

    @Override
    public void onLoaded(CommentedFileConfig configInstance, @Nullable Set<Exception> e) {
        AbstractGlobalServerBar regionbar = GlobalServerBarManager.get(EnumBarType.REGION);
        if (regionbarEnabled) {
            regionbar.init();
        } else {
            regionbar.cancelBarUpdateTask();
        }

        if (!inited) { // command has moved to CommandRegister
            inited = true;
        }
    }

    @Override
    public void onUnloaded(CommentedFileConfig configInstance) {
        AbstractGlobalServerBar regionbar = GlobalServerBarManager.get(EnumBarType.REGION);
        regionbar.cancelBarUpdateTask();
        regionbar.runUnloadTask();
        Bukkit.getCommandMap().getKnownCommands().remove("luminol:regionbar");
    }
}