package com.pickaid.passivestjs.kubejs.id;

import com.pickaid.passivestjs.kubejs.builder.Ids;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PSTItemConditionId(ResourceLocation location) implements PSTRegistryEntryId {
    public PSTItemConditionId {
        Objects.requireNonNull(location, "location");
    }

    public static PSTItemConditionId of(ResourceLocation location) {
        return new PSTItemConditionId(location);
    }

    public static PSTItemConditionId parse(Object value) {
        if (value instanceof PSTItemConditionId id) {
            return id;
        }
        return new PSTItemConditionId(Ids.parse(value, "itemConditionId"));
    }

    @Override
    public PSTSerializerFamily family() {
        return PSTSerializerFamily.ITEM_CONDITIONS;
    }

    @Override
    public String toString() {
        return id();
    }
}
