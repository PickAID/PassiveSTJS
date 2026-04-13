package com.pickaid.passivestjs.runtime.tooltip;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public final class PSTTooltipComposer {
    public static final String JOIN_PREFIX_KEY = "passivestjs.tooltip.join_prefix";
    public static final String RENDER_EFFECT_KEY = "passivestjs.tooltip.render_effect";
    public static final String JOIN_REQUIREMENT_KEY = "passivestjs.tooltip.join_requirement";

    private PSTTooltipComposer() {
    }

    public static MutableComponent joinPrefixes(List<Component> prefixes) {
        if (prefixes.isEmpty()) {
            return Component.empty();
        }
        MutableComponent joined = prefixes.get(0).copy();
        for (int i = 1; i < prefixes.size(); i++) {
            joined = Component.translatable(JOIN_PREFIX_KEY, joined, prefixes.get(i));
        }
        return joined;
    }

    public static MutableComponent renderEffect(List<Component> prefixes, Component effect) {
        if (prefixes.isEmpty()) {
            return effect.copy();
        }
        return Component.translatable(RENDER_EFFECT_KEY, joinPrefixes(prefixes), effect);
    }

    public static MutableComponent renderRequirement(List<Component> requirements) {
        if (requirements.isEmpty()) {
            return Component.empty();
        }
        MutableComponent joined = requirements.get(0).copy();
        for (int i = 1; i < requirements.size(); i++) {
            joined = Component.translatable(JOIN_REQUIREMENT_KEY, joined, requirements.get(i));
        }
        return joined;
    }
}
