package com.pickaid.passivestjs.kubejs.id;

import com.pickaid.passivestjs.kubejs.builder.Ids;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PSTItemBonusId(ResourceLocation location) implements PSTRegistryEntryId {
    public PSTItemBonusId {
        Objects.requireNonNull(location, "location");
    }

    public static PSTItemBonusId of(ResourceLocation location) {
        return new PSTItemBonusId(location);
    }

    public static PSTItemBonusId parse(Object value) {
        if (value instanceof PSTItemBonusId id) {
            return id;
        }
        return new PSTItemBonusId(Ids.parse(value, "itemBonusId"));
    }

    @Override
    public PSTSerializerFamily family() {
        return PSTSerializerFamily.ITEM_BONUSES;
    }

    @Override
    public String toString() {
        return id();
    }
}
