package me.earthme.luminol.config;

import org.jspecify.annotations.Nullable;

public record ConfigDataPair(
        String key,
        Object value,
        @Nullable String comment,
        @Nullable String[] suggestions
) {
}
