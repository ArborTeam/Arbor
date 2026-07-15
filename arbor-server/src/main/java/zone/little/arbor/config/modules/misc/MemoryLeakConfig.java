package zone.little.arbor.config.modules.misc;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import zone.little.arbor.config.IConfigModule;
import zone.little.arbor.config.flags.ConfigClassInfo;
import zone.little.arbor.config.flags.ConfigInfo;
import zone.little.arbor.enums.EnumConfigCategory;
import zone.little.arbor.functions.memory.MemoryLeakMonitor;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@ConfigClassInfo(category = EnumConfigCategory.MISC, name = "memory_leak_detector")
public class MemoryLeakConfig implements IConfigModule {

    @ConfigInfo(name = "enabled", comments = "启用后台内存泄漏监测，通过采样老年代堆用量的增长趋势进行判断\n" + "非专业人士请勿改动默认配置！！！")
    public static boolean enabled = false;

    @ConfigInfo(name = "sample_interval_seconds", comments = "两次堆采样之间的间隔（秒）")
    public static int sampleIntervalSeconds = 30;

    @ConfigInfo(name = "trend_window_seconds", comments = "用于计算增长趋势的滑动窗口长度（秒），越长越不容易被短时波动误判")
    public static int trendWindowSeconds = 1800;

    @ConfigInfo(name = "min_samples_for_trend", comments = "计算趋势所需的最少样本数")
    public static int minSamplesForTrend = 10;

    @ConfigInfo(name = "growth_threshold_bytes_per_second", comments = "老年代增长速率超过该值（字节/秒）视为可疑")
    public static long growthThresholdBytesPerSecond = 262144L;

    @ConfigInfo(name = "min_heap_usage_ratio_percent", comments = "当老年代占用比例达到该百分比时才判定内存泄漏，避免堆很空时误报")
    public static int minHeapUsageRatioPercent = 70;

    @ConfigInfo(name = "trends_before_warn", comments = "连续多少次采样判定为可疑才输出警告与诊断")
    public static int trendsBeforeWarn = 3;

    @ConfigInfo(name = "top_class_count", comments = "诊断报告中列出的增长最多的类的数量")
    public static int topClassCount = 15;

    @ConfigInfo(name = "auto_heap_dump", comments = "确认可疑泄漏时是否自动写出堆转储（.hprof）文件")
    public static boolean autoHeapDump = false;

    @Override
    public void onLoaded(CommentedFileConfig configInstance, @Nullable Set<Exception> e) {
        MemoryLeakMonitor.get().reload();
    }

    @Override
    public void onUnloaded(CommentedFileConfig configInstance) {
        MemoryLeakMonitor.get().stop();
    }
}
