package com.pickaid.passivestjs.runtime.serializer;

import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadata;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class PSTRuntimeTranslationKeys {
    private PSTRuntimeTranslationKeys() {
    }

    public static String descriptionId(PSTSerializerMetadata metadata) {
        String prefix = switch (metadata.family()) {
            case SKILL_BONUSES -> "skill_bonus";
            case LIVING_MULTIPLIERS -> "skill_bonus_multiplier";
            case LIVING_CONDITIONS -> "living_condition";
            case DAMAGE_CONDITIONS -> "damage_condition";
            case ITEM_CONDITIONS -> "item_condition";
            case ENCHANTMENT_CONDITIONS -> "enchantment_condition";
            case EVENT_LISTENERS -> "event_listener";
            case FLOAT_FUNCTIONS -> "value_provider";
            case SKILL_REQUIREMENTS -> "skill_requirements";
            case ITEM_BONUSES -> "item_bonus";
        };
        return prefix + "." + metadata.id().getNamespace() + "." + metadata.id().getPath();
    }

    public static MutableComponent describe(PSTSerializerMetadata metadata, Object... args) {
        return Component.translatable(descriptionId(metadata), args);
    }

    public static MutableComponent describe(String key, Object... args) {
        return Component.translatable(key, args);
    }
}
