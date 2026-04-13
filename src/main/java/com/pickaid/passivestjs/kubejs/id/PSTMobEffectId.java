package com.pickaid.passivestjs.kubejs.id;

import com.pickaid.passivestjs.kubejs.builder.Ids;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PSTMobEffectId(ResourceLocation location) {
    public PSTMobEffectId {
        Objects.requireNonNull(location, "location");
    }

    public static PSTMobEffectId of(ResourceLocation location) {
        return new PSTMobEffectId(location);
    }

    public static PSTMobEffectId parse(Object value) {
        if (value instanceof PSTMobEffectId id) {
            return id;
        }
        return new PSTMobEffectId(Ids.parse(value, "mobEffectId"));
    }

    public String id() {
        return location.toString();
    }

    @Override
    public String toString() {
        return id();
    }
}
