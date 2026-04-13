package com.pickaid.passivestjs.kubejs.id;

import com.pickaid.passivestjs.kubejs.builder.Ids;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PSTLivingMultiplierId(ResourceLocation location) implements PSTRegistryEntryId {
    public PSTLivingMultiplierId {
        Objects.requireNonNull(location, "location");
    }

    public static PSTLivingMultiplierId of(ResourceLocation location) {
        return new PSTLivingMultiplierId(location);
    }

    public static PSTLivingMultiplierId parse(Object value) {
        if (value instanceof PSTLivingMultiplierId id) {
            return id;
        }
        return new PSTLivingMultiplierId(Ids.parse(value, "livingMultiplierId"));
    }

    @Override
    public PSTSerializerFamily family() {
        return PSTSerializerFamily.LIVING_MULTIPLIERS;
    }

    @Override
    public String toString() {
        return id();
    }
}
