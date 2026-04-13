package com.pickaid.passivestjs.kubejs.id;

import com.pickaid.passivestjs.kubejs.builder.Ids;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PSTPotionId(ResourceLocation location) {
    public PSTPotionId {
        Objects.requireNonNull(location, "location");
    }

    public static PSTPotionId of(ResourceLocation location) {
        return new PSTPotionId(location);
    }

    public static PSTPotionId parse(Object value) {
        if (value instanceof PSTPotionId id) {
            return id;
        }
        return new PSTPotionId(Ids.parse(value, "potionId"));
    }

    public String id() {
        return location.toString();
    }

    @Override
    public String toString() {
        return id();
    }
}
