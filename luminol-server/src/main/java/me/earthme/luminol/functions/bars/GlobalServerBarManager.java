package me.earthme.luminol.functions.bars;

import me.earthme.luminol.enums.EnumBarType;

import java.util.HashMap;
import java.util.Map;

public class GlobalServerBarManager {
    private final static Map<EnumBarType, AbstractGlobalServerBar> bars = new HashMap<>();

    static {
        bars.put(EnumBarType.TPS, new GlobalServerTpsBar());
        bars.put(EnumBarType.MEMORY, new GlobalServerMemoryBar());
        bars.put(EnumBarType.REGION, new GlobalServerRegionBar());
    } // initial data

    @SuppressWarnings("unchecked")
    public static <T extends AbstractGlobalServerBar> T get(EnumBarType type) {
        AbstractGlobalServerBar bar = bars.get(type);
        if (bar == null) throw new IllegalArgumentException("No bar found for " + type.name());
        return (T) bar;
    }

    public static void cancelAll() {
        bars.values().forEach(AbstractGlobalServerBar::cancelBarUpdateTask);
    }
}
