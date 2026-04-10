package com.pickaid.passivestjs.kubejs.registry;

import com.pickaid.passivestjs.schema.PSTNodeFamily;
import com.pickaid.passivestjs.schema.PSTSchema;
import com.pickaid.passivestjs.schema.PSTSchemaRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.Optional;

public record PSTSerializerMetadata(PSTSerializerFamily family, PSTNodeFamily nodeFamily, ResourceLocation id) {
    public PSTSerializerMetadata(PSTSerializerFamily family, ResourceLocation id) {
        this(family, toNodeFamily(family), id);
    }

    public PSTSerializerMetadata {
        Objects.requireNonNull(family, "family");
        Objects.requireNonNull(nodeFamily, "nodeFamily");
        Objects.requireNonNull(id, "id");
        PSTNodeFamily expectedNodeFamily = toNodeFamily(family);
        if (nodeFamily != expectedNodeFamily) {
            throw new IllegalArgumentException("nodeFamily " + nodeFamily + " does not match family " + family);
        }
    }

    public Optional<PSTSchema> schema() {
        return PSTSchemaRegistry.find(nodeFamily, id);
    }

    private static PSTNodeFamily toNodeFamily(PSTSerializerFamily family) {
        return switch (family) {
            case SKILL_BONUSES -> PSTNodeFamily.SKILL_BONUS;
            case LIVING_MULTIPLIERS -> PSTNodeFamily.MULTIPLIER;
            case LIVING_CONDITIONS -> PSTNodeFamily.LIVING_CONDITION;
            case DAMAGE_CONDITIONS -> PSTNodeFamily.DAMAGE_CONDITION;
            case ITEM_CONDITIONS -> PSTNodeFamily.ITEM_CONDITION;
            case ENCHANTMENT_CONDITIONS -> PSTNodeFamily.ENCHANTMENT_CONDITION;
            case EVENT_LISTENERS -> PSTNodeFamily.EVENT_LISTENER;
            case FLOAT_FUNCTIONS -> PSTNodeFamily.NUMERIC_VALUE;
            case SKILL_REQUIREMENTS -> PSTNodeFamily.SKILL_REQUIREMENT;
            case ITEM_BONUSES -> PSTNodeFamily.ITEM_BONUS;
        };
    }
}
