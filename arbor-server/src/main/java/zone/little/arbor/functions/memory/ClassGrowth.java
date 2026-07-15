package zone.little.arbor.functions.memory;

public record ClassGrowth(String className, long instanceDelta, long byteDelta, long currentInstances, long currentBytes) {
}
