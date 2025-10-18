package me.earthme.luminol.config.modules.function;

import abomination.LinearRegionFile;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.*;
import me.earthme.luminol.enums.EnumConfigCategory;
import me.earthme.luminol.enums.EnumRegionFormat;
import me.earthme.luminol.utils.BufferedLinearRegionFileFlusher;
import net.minecraft.server.MinecraftServer;

@ConfigClassInfo(category = EnumConfigCategory.FUNCTION, name = "region_format")
public class RegionFormatConfig implements IConfigModule {
    @HotReloadUnsupported
    @TransformedConfig(name = "format", directory = {"misc", "region_format"})
    @ConfigInfo(name = "format")
    public static String format = "MCA";
    @HotReloadUnsupported
    @TransformedConfig(name = "linear_compression_level", directory = {"misc", "region_format"})
    @ConfigInfo(name = "linear_compression_level")
    public static int linearCompressionLevel = 1;
    @HotReloadUnsupported
    @TransformedConfig(name = "linear_io_thread_count", directory = {"misc", "region_format"})
    @ConfigInfo(name = "linear_io_thread_count")
    public static int linearIoThreadCount = 6;
    @HotReloadUnsupported
    @TransformedConfig(name = "linear_io_flush_delay_ms", directory = {"misc", "region_format"})
    @ConfigInfo(name = "linear_io_flush_delay_ms")
    public static int linearIoFlushDelayMs = 100;
    @HotReloadUnsupported
    @ConfigInfo(name = "blinear_io_flush_delay_ms")
    public static int blinearIoFlushDelayMs = 3000;
    @HotReloadUnsupported
    @ConfigInfo(name = "blinear_io_thread_count")
    public static int blinearIoThreadCount = 6;
    @HotReloadUnsupported
    @TransformedConfig(name = "linear_use_virtual_thread", directory = {"misc", "region_format"})
    @ConfigInfo(name = "linear_use_virtual_thread")
    public static boolean linearUseVirtualThread = true;

    @DoNotLoad
    public static BufferedLinearRegionFileFlusher blinearFlusher = null;

    @DoNotLoad
    public static EnumRegionFormat regionFormat;

    @Override
    public void onLoaded(CommentedFileConfig configInstance) {
        regionFormat = EnumRegionFormat.fromString(format.toUpperCase());

        if (regionFormat == null) {
            throw new RuntimeException("Invalid region format: " + format);
        }

        if (regionFormat == EnumRegionFormat.LINEAR_V2) {
            checkCompressionLevel();

            LinearRegionFile.SAVE_DELAY_MS = linearIoFlushDelayMs;
            LinearRegionFile.SAVE_THREAD_MAX_COUNT = linearIoThreadCount;
            LinearRegionFile.USE_VIRTUAL_THREAD = linearUseVirtualThread;
        }

        if (regionFormat == EnumRegionFormat.B_LINEAR) {
            blinearFlusher = new BufferedLinearRegionFileFlusher(blinearIoThreadCount, 20, blinearIoFlushDelayMs);

            checkCompressionLevel();
        }
    }

    private static void checkCompressionLevel() {
        if (RegionFormatConfig.linearCompressionLevel > 23 || RegionFormatConfig.linearCompressionLevel < 1) {
            MinecraftServer.LOGGER.error("Linear or BufferedLinear region compression level should be between 1 and 22 in config: {}", RegionFormatConfig.linearCompressionLevel);
            MinecraftServer.LOGGER.error("Falling back to compression level 1.");
            RegionFormatConfig.linearCompressionLevel = 1;
        }
    }
}