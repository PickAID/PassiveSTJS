package com.pickaid.passivestjs.kubejs.id;

import com.pickaid.passivestjs.kubejs.builder.Ids;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PSTAttributeId(ResourceLocation location) {
    public PSTAttributeId {
        Objects.requireNonNull(location, "location");
    }

    public static PSTAttributeId of(ResourceLocation location) {
        return new PSTAttributeId(location);
    }

    public static PSTAttributeId parse(Object value) {
        if (value instanceof PSTAttributeId id) {
            return id;
        }
        return new PSTAttributeId(Ids.parse(value, "attribute"));
    }

    public String id() {
        return location.toString();
    }

    @Override
    public String toString() {
        return id();
    }
}
