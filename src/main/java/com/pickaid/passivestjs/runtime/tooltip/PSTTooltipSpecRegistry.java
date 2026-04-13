package com.pickaid.passivestjs.runtime.tooltip;

import com.pickaid.passivestjs.schema.PSTNodeFamily;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class PSTTooltipSpecRegistry {
    private static final EnumMap<PSTNodeFamily, Map<ResourceLocation, PSTTooltipSpec>> BY_FAMILY =
            new EnumMap<>(PSTNodeFamily.class);

    static {
        for (PSTNodeFamily family : PSTNodeFamily.values()) {
            BY_FAMILY.put(family, new LinkedHashMap<>());
        }
    }

    private PSTTooltipSpecRegistry() {
    }

    public static synchronized PSTTooltipSpec remember(PSTNodeFamily family, ResourceLocation id, PSTTooltipSpec spec) {
        if (spec == null || spec.isEmpty()) {
            BY_FAMILY.get(family).remove(id);
            return spec;
        }
        BY_FAMILY.get(family).put(id, spec);
        return spec;
    }

    public static synchronized Optional<PSTTooltipSpec> find(PSTNodeFamily family, ResourceLocation id) {
        return Optional.ofNullable(BY_FAMILY.get(family).get(id));
    }

    public static synchronized void clear() {
        BY_FAMILY.values().forEach(Map::clear);
    }
}
