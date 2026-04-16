package com.pickaid.passivestjs.runtime;

import com.google.gson.JsonObject;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadata;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadataIndex;
import com.pickaid.passivestjs.schema.PSTNodeFamily;
import com.pickaid.passivestjs.schema.PSTSchemaFieldKind;
import com.pickaid.passivestjs.schema.PSTSchemaRegistry;
import com.pickaid.passivestjs.runtime.tooltip.PSTTooltipSpecRegistry;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PSTRuntimeNodeTest {
    @AfterEach
    void clearMetadata() {
        PSTSchemaRegistry.clear();
        PSTSerializerMetadataIndex.clear();
        PSTSerializerObjectIndex.clear();
        PSTTooltipSpecRegistry.clear();
    }

    @Test
    void runtimeNodeResolvesCustomNestedSerializersFromSerializerIndex() {
        new com.pickaid.passivestjs.kubejs.registry.builder.PSTLivingConditionSerializerBuilder(
                id("kubejs", "runtime_condition")
        )
                .test(context -> context.node().integer("threshold").orElse(0) == 4)
                .createObject();
        new com.pickaid.passivestjs.kubejs.registry.builder.PSTLivingMultiplierSerializerBuilder(
                id("kubejs", "runtime_multiplier")
        )
                .value(context -> context.node().number("amount").orElse(0.0D))
                .createObject();
        new com.pickaid.passivestjs.kubejs.registry.builder.PSTEventListenerSerializerBuilder(
                id("kubejs", "runtime_listener")
        )
                .createObject();
        new com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillRequirementSerializerBuilder(
                id("kubejs", "runtime_requirement")
        )
                .test(context -> "ready".equals(context.node().string("flag").orElse("")))
                .createObject();

        JsonObject payload = new JsonObject();
        payload.add("player_condition", typed("kubejs:runtime_condition", object -> object.addProperty("threshold", 4)));
        payload.add("player_multiplier", typed("kubejs:runtime_multiplier", object -> object.addProperty("amount", 2.5D)));
        payload.add("event_listener", typed("kubejs:runtime_listener", object -> object.addProperty("target", "enemy")));
        payload.add("requirement", typed("kubejs:runtime_requirement", object -> object.addProperty("flag", "ready")));

        PSTRuntimeNode node = new PSTRuntimeNode(
                new PSTSerializerMetadata(PSTSerializerFamily.SKILL_BONUSES, id("kubejs", "runtime_parent")),
                payload
        );

        assertTrue(node.testLivingCondition("player_condition", null, false));
        assertEquals(2.5D, node.getLivingMultiplier("player_multiplier", null, 1.0D));
        assertEquals(SkillBonus.Target.ENEMY, node.eventListener("event_listener").getTarget());
        assertTrue(node.testSkillRequirement("requirement", null, false));
    }

    @Test
    void runtimeNodeReturnsFallbacksForMissingNestedSerializers() {
        PSTRuntimeNode node = new PSTRuntimeNode(
                new PSTSerializerMetadata(PSTSerializerFamily.SKILL_BONUSES, id("kubejs", "runtime_parent")),
                new JsonObject()
        );

        assertTrue(node.testLivingCondition("player_condition", null, true));
        assertEquals(3.0D, node.getLivingMultiplier("player_multiplier", null, 3.0D));
        assertTrue(node.testSkillRequirement("requirement", null, true));
        assertNull(node.eventListener("event_listener"));
    }

    @Test
    void runtimeNodeReturnsNullForUnknownNestedSerializerId() {
        JsonObject payload = new JsonObject();
        payload.add("event_listener", typed("kubejs:missing_listener", object -> object.addProperty("target", "enemy")));

        PSTRuntimeNode node = new PSTRuntimeNode(
                new PSTSerializerMetadata(PSTSerializerFamily.SKILL_BONUSES, id("kubejs", "runtime_parent")),
                payload
        );

        assertNull(node.eventListener("event_listener"));
        assertNotNull(node.payload());
    }

    @Test
    void customItemBonusSerializerRoundTripsPayloadAcrossJsonNbtAndNetwork() {
        ItemBonus.Serializer serializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTItemBonusSerializerBuilder(
                id("kubejs", "runtime_item_bonus")
        )
                .schema(schema -> schema.field("child_bonus", field -> field
                        .kind(PSTSchemaFieldKind.NODE)
                        .nodeTarget(PSTNodeFamily.ITEM_BONUS)))
                .createObject();

        new com.pickaid.passivestjs.kubejs.registry.builder.PSTItemBonusSerializerBuilder(
                id("kubejs", "runtime_item_bonus_child")
        ).createObject();

        JsonObject json = typed("kubejs:runtime_item_bonus", object -> {
            object.addProperty("mode", "smoke");
            object.add("child_bonus", typed("kubejs:runtime_item_bonus_child", child -> child.addProperty("flag", "ready")));
        });

        ItemBonus<?> value = serializer.deserialize(json);

        JsonObject serializedJson = new JsonObject();
        serializer.serialize(serializedJson, value);
        assertEquals("smoke", serializedJson.get("mode").getAsString());
        assertEquals("kubejs:runtime_item_bonus_child", serializedJson.getAsJsonObject("child_bonus").get("type").getAsString());

        CompoundTag tag = serializer.serialize(value);
        ItemBonus<?> fromTag = serializer.deserialize(tag);
        JsonObject fromTagJson = new JsonObject();
        serializer.serialize(fromTagJson, fromTag);
        assertEquals(serializedJson, fromTagJson);

        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        serializer.serialize(buffer, value);
        ItemBonus<?> fromBuffer = serializer.deserialize(buffer);
        JsonObject fromBufferJson = new JsonObject();
        serializer.serialize(fromBufferJson, fromBuffer);
        assertEquals(serializedJson, fromBufferJson);

        AtomicReference<Component> tooltip = new AtomicReference<>();
        fromBuffer.addTooltip(component -> tooltip.set(component.copy()));
        assertNotNull(tooltip.get());
    }

    @Test
    void runtimeNodeResolvesCustomNestedItemBonusesFromSerializerIndex() {
        new com.pickaid.passivestjs.kubejs.registry.builder.PSTItemBonusSerializerBuilder(
                id("kubejs", "runtime_item_bonus_parent")
        )
                .schema(schema -> schema.field("child_bonus", field -> field
                        .kind(PSTSchemaFieldKind.NODE)
                        .nodeTarget(PSTNodeFamily.ITEM_BONUS)))
                .createObject();
        new com.pickaid.passivestjs.kubejs.registry.builder.PSTItemBonusSerializerBuilder(
                id("kubejs", "runtime_item_bonus_child")
        ).createObject();

        JsonObject payload = new JsonObject();
        payload.add("child_bonus", typed("kubejs:runtime_item_bonus_child", object -> object.addProperty("flag", "ready")));

        PSTRuntimeNode node = new PSTRuntimeNode(
                new PSTSerializerMetadata(PSTSerializerFamily.ITEM_BONUSES, id("kubejs", "runtime_item_bonus_parent")),
                payload
        );

        assertNotNull(node.itemBonus("child_bonus"));
    }

    private static ResourceLocation id(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    private static JsonObject typed(String type, java.util.function.Consumer<JsonObject> consumer) {
        JsonObject json = new JsonObject();
        json.addProperty("type", type);
        consumer.accept(json);
        return json;
    }
}
