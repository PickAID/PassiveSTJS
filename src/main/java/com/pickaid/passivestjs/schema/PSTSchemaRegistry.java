package com.pickaid.passivestjs.schema;

import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class PSTSchemaRegistry {
    private static final EnumMap<PSTNodeFamily, Map<ResourceLocation, PSTSchema>> BY_FAMILY =
            new EnumMap<>(PSTNodeFamily.class);

    static {
        for (PSTNodeFamily family : PSTNodeFamily.values()) {
            BY_FAMILY.put(family, new LinkedHashMap<>());
        }
    }

    private PSTSchemaRegistry() {
    }

    public static synchronized PSTSchema remember(PSTSchema schema) {
        BY_FAMILY.get(schema.family()).put(schema.id(), schema);
        return schema;
    }

    public static synchronized Optional<PSTSchema> find(PSTNodeFamily family, ResourceLocation id) {
        return Optional.ofNullable(BY_FAMILY.get(family).get(id));
    }

    public static synchronized Map<ResourceLocation, PSTSchema> all(PSTNodeFamily family) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(BY_FAMILY.get(family)));
    }

    public static synchronized void clear() {
        BY_FAMILY.values().forEach(Map::clear);
    }
}
