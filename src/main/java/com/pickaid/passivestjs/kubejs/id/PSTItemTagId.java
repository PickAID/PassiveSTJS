package com.pickaid.passivestjs.kubejs.id;

import com.pickaid.passivestjs.kubejs.builder.Ids;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PSTItemTagId(ResourceLocation location) {
    public PSTItemTagId {
        Objects.requireNonNull(location, "location");
    }

    public static PSTItemTagId of(ResourceLocation location) {
        return new PSTItemTagId(location);
    }

    public static PSTItemTagId parse(Object value) {
        if (value instanceof PSTItemTagId id) {
            return id;
        }
        return new PSTItemTagId(Ids.parse(value, "itemTagId"));
    }

    public String id() {
        return location.toString();
    }

    @Override
    public String toString() {
        return id();
    }
}
