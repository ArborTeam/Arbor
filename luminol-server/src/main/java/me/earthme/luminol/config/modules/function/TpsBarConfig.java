package me.earthme.luminol.config.modules.function;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.logging.LogUtils;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigClassInfo;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.DoNotLoad;
import me.earthme.luminol.config.flags.TransformedConfig;
import me.earthme.luminol.enums.EnumConfigCategory;
import me.earthme.luminol.enums.EnumStatusBarDisplay;
import me.earthme.luminol.functions.GlobalServerBarManager;
import me.earthme.luminol.functions.GlobalServerTpsBar;
import org.bukkit.Bukkit;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

@ConfigClassInfo(configAttribution = EnumConfigCategory.FUNCTION, mainName = "tpsbar")
public class TpsBarConfig implements IConfigModule {
    @DoNotLoad
    private static final Logger logger = LogUtils.getLogger();
    @TransformedConfig(name = "enabled", category = {"misc", "tpsbar"})
    @ConfigInfo(baseName = "enabled")
    public static boolean tpsbarEnabled = false;
    @TransformedConfig(name = "format", category = {"misc", "tpsbar"})
    @ConfigInfo(baseName = "format")
    public static String tpsBarFormat = "<gray>TPS<yellow>:</yellow> <tps> MSPT<yellow>:</yellow> <mspt> Ping<yellow>:</yellow> <ping>ms ChunkHot<yellow>:</yellow> <chunkhot>";
    @TransformedConfig(name = "tps_color_list", category = {"misc", "tpsbar"})
    @ConfigInfo(baseName = "tps_color_list")
    public static List<String> tpsColors = List.of("GREEN", "YELLOW", "RED", "PURPLE");
    @TransformedConfig(name = "ping_color_list", category = {"misc", "tpsbar"})
    @ConfigInfo(baseName = "ping_color_list")
    public static List<String> pingColors = List.of("GREEN", "YELLOW", "RED", "PURPLE");
    @TransformedConfig(name = "chunkhot_color_list", category = {"misc", "tpsbar"})
    @ConfigInfo(baseName = "chunkhot_color_list")
    public static List<String> chunkHotColors = List.of("GREEN", "YELLOW", "RED", "PURPLE");
    @TransformedConfig(name = "update_interval_ticks", category = {"misc", "tpsbar"})
    @ConfigInfo(baseName = "update_interval_ticks")
    public static int updateInterval = 15;
    @TransformedConfig(name = "display", category = {"misc", "tpsbar"})
    @ConfigInfo(baseName = "display")
    public static String displayString = "BOSS_BAR";
    @TransformedConfig(name = "precision_of_tps_value", category = {"misc", "tpsbar"})
    @ConfigInfo(baseName = "precision_of_tps_value")
    public static int precisionOfTPS = 2;
    @TransformedConfig(name = "precision_of_mspt_value", category = {"misc", "tpsbar"})
    @ConfigInfo(baseName = "precision_of_mspt_value")
    public static int precisionOfMSPT = 2;

    @DoNotLoad
    public static EnumStatusBarDisplay display = EnumStatusBarDisplay.BOSS_BAR;

    @DoNotLoad
    private static boolean inited = false;

    @Override
    public void onLoaded(CommentedFileConfig configInstance) {
        if (Arrays.stream(EnumStatusBarDisplay.values()).map(Enum::name).noneMatch(s -> s.equals(displayString))) {
            logger.warn("Could not found display : {} ! Falling back to default", displayString);
            display = EnumStatusBarDisplay.BOSS_BAR;
        } else {
            display = EnumStatusBarDisplay.valueOf(displayString);
        }

        GlobalServerTpsBar tpsbar = GlobalServerBarManager.get("tps");

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
        GlobalServerTpsBar tpsbar = GlobalServerBarManager.get("tps");
        tpsbar.cancelBarUpdateTask();
        tpsbar.runUnloadTask();
        Bukkit.getCommandMap().getKnownCommands().remove("luminol:tpsbar");
    }
}