package me.earthme.luminol.config.modules.function;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.logging.LogUtils;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.*;
import me.earthme.luminol.enums.EnumBarType;
import me.earthme.luminol.enums.EnumConfigCategory;
import me.earthme.luminol.enums.EnumStatusBarDisplay;
import me.earthme.luminol.functions.bars.GlobalServerBarManager;
import me.earthme.luminol.functions.bars.GlobalServerTpsBar;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;

@ConfigClassInfo(category = EnumConfigCategory.FUNCTION, name = "tpsbar")
public class TpsBarConfig implements IConfigModule {
    @DoNotLoad
    private static final Logger logger = LogUtils.getLogger();
    @TransformedConfig(name = "enabled", directory = {"misc", "tpsbar"})
    @ConfigInfo(name = "enabled")
    public static boolean tpsbarEnabled = false;
    @TransformedConfig(name = "format", directory = {"misc", "tpsbar"})
    @ConfigInfo(name = "format")
    public static String tpsBarFormat = "<gray>TPS<yellow>:</yellow> <tps> MSPT<yellow>:</yellow> <mspt> Ping<yellow>:</yellow> <ping>ms ChunkHot<yellow>:</yellow> <chunkhot>";
    @TransformedConfig(name = "tps_color_list", directory = {"misc", "tpsbar"})
    @ConfigInfo(name = "tps_color_list")
    public static List<String> tpsColors = List.of("GREEN", "YELLOW", "RED", "PURPLE");
    @TransformedConfig(name = "ping_color_list", directory = {"misc", "tpsbar"})
    @ConfigInfo(name = "ping_color_list")
    public static List<String> pingColors = List.of("GREEN", "YELLOW", "RED", "PURPLE");
    @TransformedConfig(name = "chunkhot_color_list", directory = {"misc", "tpsbar"})
    @ConfigInfo(name = "chunkhot_color_list")
    public static List<String> chunkHotColors = List.of("GREEN", "YELLOW", "RED", "PURPLE");
    @TransformedConfig(name = "update_interval_ticks", directory = {"misc", "tpsbar"})
    @ConfigInfo(name = "update_interval_ticks")
    public static int updateInterval = 15;
    @TransformedConfig(name = "precision_of_tps_value", directory = {"misc", "tpsbar"})
    @ConfigInfo(name = "precision_of_tps_value")
    public static int precisionOfTPS = 2;
    @TransformedConfig(name = "precision_of_mspt_value", directory = {"misc", "tpsbar"})
    @ConfigInfo(name = "precision_of_mspt_value")
    public static int precisionOfMSPT = 2;
    @TransformedConfig(name = "display", directory = {"misc", "tpsbar"})
    @CommandSuggestions(suggest = {"BOSS_BAR", "ACTION_BAR", "TAB_LIST"})
    @ConfigInfo(name = "display")
    public static EnumStatusBarDisplay display = EnumStatusBarDisplay.BOSS_BAR;

    @DoNotLoad
    private static boolean inited = false;

    @Override
    public void onLoaded(CommentedFileConfig configInstance, @Nullable Set<Exception> e) {
        GlobalServerTpsBar tpsbar = GlobalServerBarManager.get(EnumBarType.TPS);

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
        GlobalServerTpsBar tpsbar = GlobalServerBarManager.get(EnumBarType.TPS);
        tpsbar.cancelBarUpdateTask();
        tpsbar.runUnloadTask();
        Bukkit.getCommandMap().getKnownCommands().remove("luminol:tpsbar");
    }
}