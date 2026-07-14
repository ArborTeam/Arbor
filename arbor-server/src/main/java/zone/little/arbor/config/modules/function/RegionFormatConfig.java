package zone.little.arbor.config.modules.function;

import abomination.LinearRegionFile;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.IllegalFormatConversionExceptionWithOrigin;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.config.flags.DoNotLoad;
import zone.little.arbor.config.flags.HotReloadUnsupported;
import zone.little.arbor.enums.EnumConfigCategory;
import zone.little.arbor.enums.EnumRegionFormat;
import zone.little.arbor.utils.BufferedLinearRegionFileFlusher;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@ConfigClassInfo(category = EnumConfigCategory.FUNCTION, name = "region_format")
public class RegionFormatConfig implements IConfigModule {
    @HotReloadUnsupported
    @ConfigInfo(name = "format", allowAutoReset = false, comments = "可选值：MCA, B_LINEAR, LINEAR_V2")
    public static EnumRegionFormat regionFormat = EnumRegionFormat.MCA;
    @HotReloadUnsupported
    @ConfigInfo(name = "linear_compression_level", comments = "决定区域文件的压缩等级（仅对 LINEAR_V2 和 B_LINEAR 生效）")
    public static int linearCompressionLevel = 1;
    @HotReloadUnsupported
    @ConfigInfo(name = "linear_io_thread_count", comments = "决定 linear 的工作线程数量（仅对 LINEAR_V2 生效）")
    public static int linearIoThreadCount = 6;
    @HotReloadUnsupported
    @ConfigInfo(name = "linear_io_flush_delay_ms", comments = "决定当区域文件被标记为需要保存后，经过 n（默认为 100）毫秒时将其刷写到区域文件（仅对 LINEAR_V2 生效）")
    public static int linearIoFlushDelayMs = 100;
    @HotReloadUnsupported
    @ConfigInfo(name = "blinear_io_flush_delay_ms", comments = "决定当没有写操作持续 n（默认为 3000）毫秒后将其刷写到区域文件（仅对 B_LINEAR 生效）")
    public static int blinearIoFlushDelayMs = 3000;
    @HotReloadUnsupported
    @ConfigInfo(name = "blinear_io_thread_count", comments = "决定 buffered linear 的工作线程数量（仅对 B_LINEAR 生效）")
    public static int blinearIoThreadCount = 6;
    @HotReloadUnsupported
    @ConfigInfo(name = "linear_use_virtual_thread", comments = "决定 linear 格式是否可以使用虚拟线程（仅对 LINEAR_V2 生效）")
    public static boolean linearUseVirtualThread = true;

    @DoNotLoad
    public static BufferedLinearRegionFileFlusher blinearFlusher = null;

    @Override
    public void onLoaded(CommentedFileConfig configInstance, @Nullable Set<Exception> exs) {
        if (exs != null) {
            for (Exception e : exs) {
                if (e instanceof IllegalFormatConversionExceptionWithOrigin) {
                    throw new RuntimeException("Invalid region format: " + ((IllegalFormatConversionExceptionWithOrigin) e).getOrigin().toString());
                }
            }
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

            // we don't need to consider that it will be reloaded more than once as this config is unreloadable
            Runtime.getRuntime().addShutdownHook(new Thread(() -> blinearFlusher.shutdown()));
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