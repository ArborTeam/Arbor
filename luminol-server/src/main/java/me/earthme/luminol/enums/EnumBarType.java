package me.earthme.luminol.enums;

import me.earthme.luminol.functions.bars.AbstractGlobalServerBar;
import me.earthme.luminol.functions.bars.GlobalServerMemoryBar;
import me.earthme.luminol.functions.bars.GlobalServerRegionBar;
import me.earthme.luminol.functions.bars.GlobalServerTpsBar;
import org.jetbrains.annotations.NotNull;

public enum EnumBarType {
    TPS(GlobalServerTpsBar.class),
    MEMORY(GlobalServerMemoryBar.class),
    REGION(GlobalServerRegionBar.class);

    final Class<? extends AbstractGlobalServerBar> clazz;

    EnumBarType(Class<? extends AbstractGlobalServerBar> clazz) {
        this.clazz = clazz;
    }

    @NotNull
    public AbstractGlobalServerBar newInstance() {
        try {
            return this.clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
