package com.pickaid.passivestjs.kubejs.id;

import com.pickaid.passivestjs.kubejs.builder.Ids;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PSTEnchantmentConditionId(ResourceLocation location) implements PSTRegistryEntryId {
    public PSTEnchantmentConditionId {
        Objects.requireNonNull(location, "location");
    }

    public static PSTEnchantmentConditionId of(ResourceLocation location) {
        return new PSTEnchantmentConditionId(location);
    }

    public static PSTEnchantmentConditionId parse(Object value) {
        if (value instanceof PSTEnchantmentConditionId id) {
            return id;
        }
        return new PSTEnchantmentConditionId(Ids.parse(value, "enchantmentConditionId"));
    }

    @Override
    public PSTSerializerFamily family() {
        return PSTSerializerFamily.ENCHANTMENT_CONDITIONS;
    }

    @Override
    public String toString() {
        return id();
    }
}
