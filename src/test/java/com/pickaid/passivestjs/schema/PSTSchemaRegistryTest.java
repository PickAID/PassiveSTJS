package com.pickaid.passivestjs.schema;

import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadata;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        assertTrue(remembered.field("amount").required());
        assertEquals("Base bonus amount", remembered.field("amount").doc());
        assertEquals("ADDITION", remembered.field("operation").defaultValue());
        assertTrue(remembered.field("operation").enumChoices().contains("MULTIPLY_BASE"));
        assertEquals(new ResourceLocation("skilltree", "skill_bonus_multipliers"), remembered.field("multiplier").registryTarget());

        PSTSerializerMetadata metadata = new PSTSerializerMetadata(PSTSerializerFamily.SKILL_BONUSES, new ResourceLocation("kubejs", "bleed_bonus"));
        assertEquals(PSTNodeFamily.SKILL_BONUS, metadata.nodeFamily());
        assertEquals(remembered, metadata.schema().orElseThrow());
    }

    @Test
    void schemaBuilderRejectsDuplicateFieldNames() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> PSTSchema.builder(PSTNodeFamily.SKILL_BONUS)
                .field("amount", field -> field.kind(PSTSchemaFieldKind.DOUBLE))
                .field("amount", field -> field.kind(PSTSchemaFieldKind.INT))
                .build(new ResourceLocation("kubejs", "duplicate")));

        assertTrue(exception.getMessage().contains("Duplicate schema field"));
    }

    @Test
    void schemaBuilderPreservesFieldDeclarationOrder() {
        PSTSchema schema = PSTSchema.builder(PSTNodeFamily.SKILL_BONUS)
                .field("first", field -> field.kind(PSTSchemaFieldKind.STRING))
                .field("second", field -> field.kind(PSTSchemaFieldKind.STRING))
                .field("third", field -> field.kind(PSTSchemaFieldKind.STRING))
                .build(new ResourceLocation("kubejs", "ordered"));

        assertEquals(List.of("first", "second", "third"), List.copyOf(schema.fields().keySet()));
    }

    @Test
    void serializerMetadataRejectsMismatchedFamilyAndNodeFamily() {
        assertThrows(IllegalArgumentException.class, () ->
                new PSTSerializerMetadata(
                        PSTSerializerFamily.SKILL_BONUSES,
                        PSTNodeFamily.NUMERIC_VALUE,
                        new ResourceLocation("kubejs", "bad_mapping")
                )
        );
    }
}
