package com.pickaid.passivestjs.kubejs.id;

import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import net.minecraft.resources.ResourceLocation;

public interface PSTRegistryEntryId {
    ResourceLocation location();

    PSTSerializerFamily family();

    default String id() {
        return location().toString();
    }
}
