package com.pickaid.passivestjs.kubejs.id;

import com.pickaid.passivestjs.kubejs.builder.Ids;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PSTItemId(ResourceLocation location) {
    public PSTItemId {
        Objects.requireNonNull(location, "location");
    }

    public static PSTItemId of(ResourceLocation location) {
        return new PSTItemId(location);
    }

    public static PSTItemId parse(Object value) {
        if (value instanceof PSTItemId id) {
            return id;
        }
        return new PSTItemId(Ids.parse(value, "itemId"));
    }

    public String id() {
        return location.toString();
    }

    @Override
    public String toString() {
        return id();
    }
}
