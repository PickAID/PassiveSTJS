package com.pickaid.passivestjs.kubejs.id;

import com.pickaid.passivestjs.kubejs.builder.Ids;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PSTStatTypeId(ResourceLocation location) {
    public PSTStatTypeId {
        Objects.requireNonNull(location, "location");
    }

    public static PSTStatTypeId of(ResourceLocation location) {
        return new PSTStatTypeId(location);
    }

    public static PSTStatTypeId parse(Object value) {
        if (value instanceof PSTStatTypeId id) {
            return id;
        }
        return new PSTStatTypeId(Ids.parse(value, "statTypeId"));
    }

    public String id() {
        return location.toString();
    }

    @Override
    public String toString() {
        return id();
    }
}
