package me.earthme.luminol.config.modules.misc;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.logging.LogUtils;
import me.earthme.luminol.commands.MembarCommand;
import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigInfo;
import me.earthme.luminol.config.flags.DoNotLoad;
import me.earthme.luminol.functions.GlobalServerMemoryBar;
import me.earthme.luminol.utils.EnumStatusBarDisplay;
import org.bukkit.Bukkit;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

public class MembarConfig implements IConfigModule {
    private static final Logger logger = LogUtils.getLogger();

    @ConfigInfo(baseName = "enabled")
    public static boolean memoryBarEnabled = false;
    @ConfigInfo(baseName = "format")
    public static String memBarFormat = "<gray>Memory usage <yellow>:</yellow> <used>MB<yellow>/</yellow><available>MB";
    @ConfigInfo(baseName = "memory_color_list")
    public static List<String> memColors = List.of("GREEN", "YELLOW", "RED", "PURPLE");
    @ConfigInfo(baseName = "update_interval_ticks")
    public static int updateInterval = 15;
    @ConfigInfo(baseName = "display")
    public static String displayString = "BOSS_BAR";

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
        return "membar";
    }

    @Override
    public void onLoaded(CommentedFileConfig configInstance) {
        if (Arrays.stream(EnumStatusBarDisplay.values()).map(Enum::name).noneMatch(s -> s.equals(displayString))) {
            logger.warn("Could not found display : {} ! Falling back to default", displayString);
            display = EnumStatusBarDisplay.BOSS_BAR;
        } else {
            display =  EnumStatusBarDisplay.valueOf(displayString);
        }

        if (memoryBarEnabled) {
            GlobalServerMemoryBar.init();
        } else {
            GlobalServerMemoryBar.cancelBarUpdateTask();
        }

        if (!inited) {
            Bukkit.getCommandMap().register("membar", "luminol", new MembarCommand("membar"));
            inited = true;
        }
    }
}