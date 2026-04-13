package com.pickaid.passivestjs.kubejs.id;

import com.pickaid.passivestjs.kubejs.builder.Ids;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PSTDamageConditionId(ResourceLocation location) implements PSTRegistryEntryId {
    public PSTDamageConditionId {
        Objects.requireNonNull(location, "location");
    }

    public static PSTDamageConditionId of(ResourceLocation location) {
        return new PSTDamageConditionId(location);
    }

    public static PSTDamageConditionId parse(Object value) {
        if (value instanceof PSTDamageConditionId id) {
            return id;
        }
        return new PSTDamageConditionId(Ids.parse(value, "damageConditionId"));
    }

    @Override
    public PSTSerializerFamily family() {
        return PSTSerializerFamily.DAMAGE_CONDITIONS;
    }

    @Override
    public String toString() {
        return id();
    }
}
