package com.pickaid.passivestjs.kubejs.id;

import com.pickaid.passivestjs.kubejs.builder.Ids;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PSTEventListenerId(ResourceLocation location) implements PSTRegistryEntryId {
    public PSTEventListenerId {
        Objects.requireNonNull(location, "location");
    }

    public static PSTEventListenerId of(ResourceLocation location) {
        return new PSTEventListenerId(location);
    }

    public static PSTEventListenerId parse(Object value) {
        if (value instanceof PSTEventListenerId id) {
            return id;
        }
        return new PSTEventListenerId(Ids.parse(value, "eventListenerId"));
    }

    @Override
    public PSTSerializerFamily family() {
        return PSTSerializerFamily.EVENT_LISTENERS;
    }

    @Override
    public String toString() {
        return id();
    }
}
