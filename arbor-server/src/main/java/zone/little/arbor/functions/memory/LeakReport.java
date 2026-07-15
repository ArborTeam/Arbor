package zone.little.arbor.functions.memory;

import java.util.List;

public record LeakReport(boolean suspected, double slopeBytesPerSecond, long usedBytes, long maxBytes, double usedRatio,
                         int fullGcCount, long sampleCount, List<ClassGrowth> topGrowth) {
}
