package com.pickaid.passivestjs.runtime;

import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadata;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class PSTSerializerObjectIndex {
    private static final EnumMap<PSTSerializerFamily, Map<ResourceLocation, Object>> BY_FAMILY =
            new EnumMap<>(PSTSerializerFamily.class);

    static {
        for (PSTSerializerFamily family : PSTSerializerFamily.values()) {
            BY_FAMILY.put(family, new LinkedHashMap<>());
        }
    }

    private PSTSerializerObjectIndex() {
    }

    public static synchronized <T> T remember(PSTSerializerMetadata metadata, T serializer) {
        BY_FAMILY.get(metadata.family()).put(metadata.id(), serializer);
        return serializer;
    }

    public static synchronized Optional<Object> find(PSTSerializerFamily family, ResourceLocation id) {
        return Optional.ofNullable(BY_FAMILY.get(family).get(id));
    }

    public static synchronized void clear() {
        BY_FAMILY.values().forEach(Map::clear);
    }
}
