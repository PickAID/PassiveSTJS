package com.pickaid.passivestjs.schema;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PSTSchemaRegistryTest {
    @AfterEach
    void clearRegistry() {
        PSTSchemaRegistry.clear();
    }

    @Test
    void schemaTracksFieldKindsDefaultsDocsAndRegistryTargets() {
        PSTSchema schema = PSTSchema.builder(PSTNodeFamily.SKILL_BONUS)
                .field("amount", field -> field.kind(PSTSchemaFieldKind.DOUBLE).required().doc("Base bonus amount"))
                .field("operation", field -> field.kind(PSTSchemaFieldKind.ENUM)
                        .enumChoice("ADDITION")
                        .enumChoice("MULTIPLY_BASE")
                        .defaultValue("ADDITION"))
                .field("multiplier", field -> field.kind(PSTSchemaFieldKind.REGISTRY_ID)
                        .registryTarget(new ResourceLocation("skilltree", "skill_bonus_multipliers")))
                .build(new ResourceLocation("kubejs", "bleed_bonus"));

        PSTSchemaRegistry.remember(schema);

        PSTSchema remembered = PSTSchemaRegistry.find(PSTNodeFamily.SKILL_BONUS, new ResourceLocation("kubejs", "bleed_bonus"))
                .orElseThrow();

        assertEquals(PSTSchemaFieldKind.DOUBLE, remembered.field("amount").kind());
        assertEquals("Base bonus amount", remembered.field("amount").doc());
        assertEquals("ADDITION", remembered.field("operation").defaultValue());
        assertTrue(remembered.field("operation").enumChoices().contains("MULTIPLY_BASE"));
        assertEquals(new ResourceLocation("skilltree", "skill_bonus_multipliers"), remembered.field("multiplier").registryTarget());
    }
}
