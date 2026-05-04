package com.pickaid.passivestjs.kubejs.builder;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.pickaid.passivestjs.schema.PSTNodeFamily;
import com.pickaid.passivestjs.schema.PSTSchema;
import com.pickaid.passivestjs.schema.PSTSchemaFieldKind;
import com.pickaid.passivestjs.schema.PSTSchemaRegistry;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PSTNodeWriterTest {
    @AfterEach
    void clearSchemas() {
        PSTSchemaRegistry.clear();
    }

    @Test
    void writesSchemaBackedNodeFields() {
        ResourceLocation typeId = new ResourceLocation("kubejs", "bleed_bonus");
        PSTSchemaRegistry.remember(PSTSchema.builder(PSTNodeFamily.SKILL_BONUS)
                .field("amount", field -> field.kind(PSTSchemaFieldKind.DOUBLE).required())
                .field("target", field -> field.kind(PSTSchemaFieldKind.STRING))
                .build(typeId));

        JsonObject json = new PSTNodeWriter(PSTNodeFamily.SKILL_BONUS, typeId)
                .set("amount", 2.5)
                .set("target", "enemy")
                .toJson();

        assertEquals("kubejs:bleed_bonus", json.get("type").getAsString());
        assertEquals(2.5D, json.get("amount").getAsDouble(), 0.001D);
        assertEquals("enemy", json.get("target").getAsString());
    }

    @Test
    void rejectsUnknownSchemaField() {
        ResourceLocation typeId = new ResourceLocation("kubejs", "bleed_bonus");
        PSTSchemaRegistry.remember(PSTSchema.builder(PSTNodeFamily.SKILL_BONUS)
                .field("amount", field -> field.kind(PSTSchemaFieldKind.DOUBLE).required())
                .build(typeId));

        PSTNodeWriter writer = new PSTNodeWriter(PSTNodeFamily.SKILL_BONUS, typeId);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> writer.set("missingField", 1));
        assertTrue(exception.getMessage().contains("missingField"));
    }

    @Test
    void rejectsValueThatDoesNotMatchSchemaKind() {
        ResourceLocation typeId = new ResourceLocation("kubejs", "int_bonus");
        PSTSchemaRegistry.remember(PSTSchema.builder(PSTNodeFamily.SKILL_BONUS)
                .field("operation", field -> field.kind(PSTSchemaFieldKind.INT).required())
                .build(typeId));

        PSTNodeWriter writer = new PSTNodeWriter(PSTNodeFamily.SKILL_BONUS, typeId);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> writer.set("operation", "oops"));
        assertTrue(exception.getMessage().contains("operation"));
    }

    @Test
    void writesNestedNodeFieldsAndNodeLists() {
        ResourceLocation typeId = new ResourceLocation("kubejs", "nested_bonus");
        PSTSchemaRegistry.remember(PSTSchema.builder(PSTNodeFamily.SKILL_BONUS)
                .field("event_listener", field -> field.kind(PSTSchemaFieldKind.NODE).nodeTarget(PSTNodeFamily.EVENT_LISTENER))
                .field("conditions", field -> field.kind(PSTSchemaFieldKind.NODE_LIST).nodeTarget(PSTNodeFamily.LIVING_CONDITION))
                .build(typeId));

        JsonObject listener = new JsonObject();
        listener.addProperty("type", "kubejs:listener");
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "kubejs:condition");
        JsonArray conditions = new JsonArray();
        conditions.add(condition);

        JsonObject json = new PSTNodeWriter(PSTNodeFamily.SKILL_BONUS, typeId)
                .set("event_listener", listener)
                .set("conditions", conditions)
                .toJson();

        assertEquals("kubejs:listener", json.getAsJsonObject("event_listener").get("type").getAsString());
        assertEquals("kubejs:condition", json.getAsJsonArray("conditions").get(0).getAsJsonObject().get("type").getAsString());
    }
}
