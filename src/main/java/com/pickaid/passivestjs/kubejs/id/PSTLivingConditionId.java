package com.pickaid.passivestjs.kubejs.id;

import com.pickaid.passivestjs.kubejs.builder.Ids;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PSTLivingConditionId(ResourceLocation location) implements PSTRegistryEntryId {
    public PSTLivingConditionId {
        Objects.requireNonNull(location, "location");
    }

    public static PSTLivingConditionId of(ResourceLocation location) {
        return new PSTLivingConditionId(location);
    }

    public static PSTLivingConditionId parse(Object value) {
        if (value instanceof PSTLivingConditionId id) {
            return id;
        }
        return new PSTLivingConditionId(Ids.parse(value, "livingConditionId"));
    }

    @Override
    public PSTSerializerFamily family() {
        return PSTSerializerFamily.LIVING_CONDITIONS;
    }

    @Override
    public String toString() {
        return id();
    }
}
