package com.pickaid.passivestjs.kubejs.registry;

import com.pickaid.passivestjs.schema.PSTNodeFamily;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class PSTSerializerMetadataIndex {
    private static final EnumMap<PSTSerializerFamily, Map<ResourceLocation, PSTSerializerMetadata>> BY_FAMILY =
            new EnumMap<>(PSTSerializerFamily.class);

    static {
        for (PSTSerializerFamily family : PSTSerializerFamily.values()) {
            BY_FAMILY.put(family, new LinkedHashMap<>());
        }
    }

    private PSTSerializerMetadataIndex() {
    }

    public static synchronized PSTSerializerMetadata remember(PSTSerializerMetadata metadata) {
        BY_FAMILY.get(metadata.family()).put(metadata.id(), metadata);
        return metadata;
    }

    public static synchronized Optional<PSTSerializerMetadata> find(PSTSerializerFamily family, ResourceLocation id) {
        return Optional.ofNullable(BY_FAMILY.get(family).get(id));
    }

    public static synchronized Optional<PSTSerializerMetadata> find(PSTNodeFamily family, ResourceLocation id) {
        return BY_FAMILY.values().stream()
                .map(byId -> byId.get(id))
                .filter(metadata -> metadata != null && metadata.nodeFamily() == family)
                .findFirst();
    }

    public static synchronized Map<PSTSerializerFamily, Map<ResourceLocation, PSTSerializerMetadata>> snapshot() {
        EnumMap<PSTSerializerFamily, Map<ResourceLocation, PSTSerializerMetadata>> snapshot =
                new EnumMap<>(PSTSerializerFamily.class);
        for (Map.Entry<PSTSerializerFamily, Map<ResourceLocation, PSTSerializerMetadata>> entry : BY_FAMILY.entrySet()) {
            snapshot.put(entry.getKey(), Map.copyOf(entry.getValue()));
        }
        return Map.copyOf(snapshot);
    }

    public static synchronized void clear() {
        BY_FAMILY.values().forEach(Map::clear);
    }
}
