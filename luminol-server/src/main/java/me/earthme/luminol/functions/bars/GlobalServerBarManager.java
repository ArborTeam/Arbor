package me.earthme.luminol.functions.bars;

import me.earthme.luminol.enums.EnumBarType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalServerBarManager {
    private final static Map<EnumBarType, AbstractGlobalServerBar> bars = new ConcurrentHashMap<>();

    public static AbstractGlobalServerBar get(EnumBarType type) {
        return bars.computeIfAbsent(type, (_) -> type.newInstance());
    }

    public static AbstractGlobalServerBar getOrNull(EnumBarType type) {
        return bars.get(type);
    }

    public static void cancelAll() {
        bars.values().forEach(AbstractGlobalServerBar::cancelBarUpdateTask);
    }
}
