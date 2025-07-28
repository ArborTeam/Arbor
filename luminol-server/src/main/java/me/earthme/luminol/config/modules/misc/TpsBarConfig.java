package me.earthme.luminol.config.modules.misc;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.logging.LogUtils;
import me.earthme.luminol.commands.TpsBarCommand;
import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.DoNotLoad;
import me.earthme.luminol.functions.GlobalServerTpsBar;
import me.earthme.luminol.utils.EnumStatusBarDisplay;
import org.bukkit.Bukkit;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

public class TpsBarConfig implements IConfigModule {
    private static final Logger logger = LogUtils.getLogger();

    @ConfigInfo(baseName = "enabled")
    public static boolean tpsbarEnabled = false;
    @ConfigInfo(baseName = "format")
    public static String tpsBarFormat = "<gray>TPS<yellow>:</yellow> <tps> MSPT<yellow>:</yellow> <mspt> Ping<yellow>:</yellow> <ping>ms ChunkHot<yellow>:</yellow> <chunkhot>";
    @ConfigInfo(baseName = "tps_color_list")
    public static List<String> tpsColors = List.of("GREEN", "YELLOW", "RED", "PURPLE");
    @ConfigInfo(baseName = "ping_color_list")
    public static List<String> pingColors = List.of("GREEN", "YELLOW", "RED", "PURPLE");
    @ConfigInfo(baseName = "chunkhot_color_list")
    public static List<String> chunkHotColors = List.of("GREEN", "YELLOW", "RED", "PURPLE");
    @ConfigInfo(baseName = "update_interval_ticks")
    public static int updateInterval = 15;
    @ConfigInfo(baseName = "display")
    public static String displayString = "BOSS_BAR";
    @ConfigInfo(baseName = "precision_of_tps_value")
    public static int precisionOfTPS = 2;
    @ConfigInfo(baseName = "precision_of_mspt_value")
    public static int precisionOfMSPT = 2;


    @DoNotLoad
    public static EnumStatusBarDisplay display = EnumStatusBarDisplay.BOSS_BAR;

    @DoNotLoad
    private static boolean inited = false;

    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.MISC;
    }

    @Override
    public String getBaseName() {
        return "tpsbar";
    }

    @Override
    public void onLoaded(CommentedFileConfig configInstance) {
        if (Arrays.stream(EnumStatusBarDisplay.values()).map(Enum::name).noneMatch(s -> s.equals(displayString))) {
            logger.warn("Could not found display : {} ! Falling back to default", displayString);
            display = EnumStatusBarDisplay.BOSS_BAR;
        } else {
            display = EnumStatusBarDisplay.valueOf(displayString);
        }

        if (tpsbarEnabled) {
            GlobalServerTpsBar.init();
        } else {
            GlobalServerTpsBar.cancelBarUpdateTask();
        }

        if (!inited) {
            Bukkit.getCommandMap().register("tpsbar", "luminol", new TpsBarCommand("tpsbar"));
            inited = true;
        }
    }
}