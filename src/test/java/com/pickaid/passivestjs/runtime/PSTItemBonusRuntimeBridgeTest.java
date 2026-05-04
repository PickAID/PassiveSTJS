package com.pickaid.passivestjs.runtime;

import com.google.gson.JsonObject;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadata;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadataIndex;
import com.pickaid.passivestjs.runtime.tooltip.PSTTooltipSpecRegistry;
import com.pickaid.passivestjs.schema.PSTNodeFamily;
import com.pickaid.passivestjs.schema.PSTSchemaFieldKind;
import com.pickaid.passivestjs.schema.PSTSchemaRegistry;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.SkillBonusItemBonus;
import daripher.skilltree.skill.bonus.player.AttributeBonus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PSTItemBonusRuntimeBridgeTest {
    private static final RangedAttribute TEST_ATTRIBUTE =
            new RangedAttribute("attribute.name.passivestjs.test", 0.0D, -1024.0D, 1024.0D);

    @AfterEach
    void clearMetadata() {
        PSTSchemaRegistry.clear();
        PSTSerializerMetadataIndex.clear();
        PSTSerializerObjectIndex.clear();
        PSTTooltipSpecRegistry.clear();
    }

    @Test
    void collectCustomAttributeBonusesIgnoresTopLevelBuiltInItemBonuses() {
        AttributeBonus attributeBonus = attributeBonus("top_level_builtin");
        ItemBonus<?> builtIn = new SkillBonusItemBonus(attributeBonus);

        List<AttributeBonus> collected = PSTItemBonusRuntimeBridge.collectCustomAttributeBonuses(List.of(builtIn));

        assertTrue(collected.isEmpty());
    }

    @Test
    void collectCustomAttributeBonusesFindsNestedAttributeBonusesThroughCustomChildren() {
        AttributeBonus attributeBonus = attributeBonus("custom_child");
        rememberSkillBonus(id("kubejs", "test_attribute_bonus"), attributeBonus);

        ItemBonus.Serializer parentSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTItemBonusSerializerBuilder(
                id("kubejs", "runtime_item_bonus_parent")
        )
                .schema(schema -> schema.field("child_bonus", field -> field
                        .kind(PSTSchemaFieldKind.NODE)
                        .nodeTarget(PSTNodeFamily.ITEM_BONUS)))
                .createObject();

        new com.pickaid.passivestjs.kubejs.registry.builder.PSTItemBonusSerializerBuilder(
                id("kubejs", "runtime_item_bonus_child")
        )
                .schema(schema -> schema.field("skill_bonus", field -> field
                        .kind(PSTSchemaFieldKind.NODE)
                        .nodeTarget(PSTNodeFamily.SKILL_BONUS)))
                .createObject();

        JsonObject parentJson = typed("kubejs:runtime_item_bonus_parent", json -> json.add(
                "child_bonus",
                typed("kubejs:runtime_item_bonus_child", child -> child.add("skill_bonus", typed("kubejs:test_attribute_bonus", ignored -> {
                })))
        ));

        ItemBonus<?> value = parentSerializer.deserialize(parentJson);
        List<AttributeBonus> collected = PSTItemBonusRuntimeBridge.collectCustomAttributeBonuses(List.of(value));

        assertEquals(1, collected.size());
        assertEquals(attributeBonus.getAttribute(), collected.get(0).getAttribute());
        assertModifierMatches(attributeBonus.getModifier(), collected.get(0).getModifier());
    }

    @Test
    void collectCustomAttributeBonusesFindsBuiltInSkillBonusItemBonusesNestedInsideCustomRuntimeItemBonuses() {
        AttributeBonus attributeBonus = attributeBonus("built_in_child");
        rememberItemBonus(id("skilltree", "skill_bonus"), new SkillBonusItemBonus(attributeBonus));

        ItemBonus.Serializer parentSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTItemBonusSerializerBuilder(
                id("kubejs", "runtime_item_bonus_parent")
        )
                .schema(schema -> schema.field("child_bonus", field -> field
                        .kind(PSTSchemaFieldKind.NODE)
                        .nodeTarget(PSTNodeFamily.ITEM_BONUS)))
                .createObject();

        JsonObject parentJson = typed("kubejs:runtime_item_bonus_parent", json -> json.add(
                "child_bonus",
                typed("skilltree:skill_bonus", ignored -> {
                })
        ));

        ItemBonus<?> value = parentSerializer.deserialize(parentJson);
        List<AttributeBonus> collected = PSTItemBonusRuntimeBridge.collectCustomAttributeBonuses(List.of(value));

        assertEquals(1, collected.size());
        assertEquals(attributeBonus.getAttribute(), collected.get(0).getAttribute());
        assertModifierMatches(attributeBonus.getModifier(), collected.get(0).getModifier());
    }

    @Test
    void addAndRemoveAttributeBonusesUseStableModifierIdentityAcrossEquivalentCustomPayloadReads() {
        AttributeBonus attributeBonus = attributeBonus("stable_identity");
        rememberSkillBonus(id("kubejs", "test_attribute_bonus"), attributeBonus);

        ItemBonus.Serializer parentSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTItemBonusSerializerBuilder(
                id("kubejs", "runtime_item_bonus_parent")
        )
                .schema(schema -> schema.field("child_bonus", field -> field
                        .kind(PSTSchemaFieldKind.NODE)
                        .nodeTarget(PSTNodeFamily.ITEM_BONUS)))
                .createObject();

        new com.pickaid.passivestjs.kubejs.registry.builder.PSTItemBonusSerializerBuilder(
                id("kubejs", "runtime_item_bonus_child")
        )
                .schema(schema -> schema.field("skill_bonus", field -> field
                        .kind(PSTSchemaFieldKind.NODE)
                        .nodeTarget(PSTNodeFamily.SKILL_BONUS)))
                .createObject();

        JsonObject parentJson = typed("kubejs:runtime_item_bonus_parent", json -> json.add(
                "child_bonus",
                typed("kubejs:runtime_item_bonus_child", child -> child.add("skill_bonus", typed("kubejs:test_attribute_bonus", ignored -> {
                })))
        ));

        ItemBonus<?> equipped = parentSerializer.deserialize(parentJson);
        ItemBonus<?> unequipped = parentSerializer.deserialize(parentJson);
        List<AttributeBonus> added = PSTItemBonusRuntimeBridge.collectCustomAttributeBonuses(List.of(equipped));
        List<AttributeBonus> removed = PSTItemBonusRuntimeBridge.collectCustomAttributeBonuses(List.of(unequipped));
        AttributeInstance instance = new AttributeInstance(TEST_ATTRIBUTE, ignored -> {
        });

        PSTItemBonusRuntimeBridge.addAttributeBonuses(attribute -> attribute == TEST_ATTRIBUTE ? instance : null, added);
        assertEquals(1, added.size());
        assertEquals(1, removed.size());
        assertEquals(added.get(0).getModifier().getId(), removed.get(0).getModifier().getId());
        assertNotNull(instance.getModifier(added.get(0).getModifier().getId()));

        PSTItemBonusRuntimeBridge.removeAttributeBonuses(attribute -> attribute == TEST_ATTRIBUTE ? instance : null, removed);
        assertNull(instance.getModifier(added.get(0).getModifier().getId()));
    }

    @Test
    void addAndRemoveAttributeBonusesMutateResolvedAttributeInstances() {
        AttributeBonus attributeBonus = attributeBonus("apply_remove");
        AttributeInstance instance = new AttributeInstance(TEST_ATTRIBUTE, ignored -> {
        });

        PSTItemBonusRuntimeBridge.addAttributeBonuses(attribute -> attribute == TEST_ATTRIBUTE ? instance : null, List.of(attributeBonus));
        assertTrue(instance.hasModifier(attributeBonus.getModifier()));
        assertNotNull(instance.getModifier(attributeBonus.getModifier().getId()));

        PSTItemBonusRuntimeBridge.removeAttributeBonuses(attribute -> attribute == TEST_ATTRIBUTE ? instance : null, List.of(attributeBonus));
        assertFalse(instance.hasModifier(attributeBonus.getModifier()));
        assertNull(instance.getModifier(attributeBonus.getModifier().getId()));
    }

    private static void rememberSkillBonus(net.minecraft.resources.ResourceLocation id, SkillBonus<?> bonus) {
        PSTSerializerObjectIndex.remember(
                new PSTSerializerMetadata(PSTSerializerFamily.SKILL_BONUSES, id),
                new FixedSkillBonusSerializer(bonus)
        );
    }

    private static void rememberItemBonus(net.minecraft.resources.ResourceLocation id, ItemBonus<?> bonus) {
        PSTSerializerObjectIndex.remember(
                new PSTSerializerMetadata(PSTSerializerFamily.ITEM_BONUSES, id),
                new FixedItemBonusSerializer(bonus)
        );
    }

    private static AttributeBonus attributeBonus(String name) {
        return new AttributeBonus(
                TEST_ATTRIBUTE,
                new AttributeModifier(UUID.nameUUIDFromBytes(name.getBytes()), "PassiveSTJS Test " + name, 2.0D, AttributeModifier.Operation.ADDITION)
        );
    }

    private static net.minecraft.resources.ResourceLocation id(String namespace, String path) {
        return new net.minecraft.resources.ResourceLocation(namespace, path);
    }

    private static JsonObject typed(String type, java.util.function.Consumer<JsonObject> consumer) {
        JsonObject json = new JsonObject();
        json.addProperty("type", type);
        consumer.accept(json);
        return json;
    }

    private static void assertModifierMatches(AttributeModifier expected, AttributeModifier actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getOperation(), actual.getOperation());
    }

    private static final class FixedSkillBonusSerializer implements SkillBonus.Serializer {
        private final SkillBonus<?> bonus;

        private FixedSkillBonusSerializer(SkillBonus<?> bonus) {
            this.bonus = bonus;
        }

        @Override
        public SkillBonus<?> deserialize(JsonObject json) {
            return bonus.copy();
        }

        @Override
        public void serialize(JsonObject json, SkillBonus<?> value) {
        }

        @Override
        public SkillBonus<?> deserialize(CompoundTag tag) {
            return bonus.copy();
        }

        @Override
        public CompoundTag serialize(SkillBonus<?> value) {
            return new CompoundTag();
        }

        @Override
        public SkillBonus<?> deserialize(FriendlyByteBuf buffer) {
            return bonus.copy();
        }

        @Override
        public void serialize(FriendlyByteBuf buffer, SkillBonus<?> value) {
        }

        @Override
        public SkillBonus<?> createDefaultInstance() {
            return bonus.copy();
        }
    }

    private static final class FixedItemBonusSerializer implements ItemBonus.Serializer {
        private final ItemBonus<?> bonus;

        private FixedItemBonusSerializer(ItemBonus<?> bonus) {
            this.bonus = bonus;
        }

        @Override
        public ItemBonus<?> deserialize(JsonObject json) {
            return bonus.copy();
        }

        @Override
        public void serialize(JsonObject json, ItemBonus<?> value) {
        }

        @Override
        public ItemBonus<?> deserialize(CompoundTag tag) {
            return bonus.copy();
        }

        @Override
        public CompoundTag serialize(ItemBonus<?> value) {
            return new CompoundTag();
        }

        @Override
        public ItemBonus<?> deserialize(FriendlyByteBuf buffer) {
            return bonus.copy();
        }

        @Override
        public void serialize(FriendlyByteBuf buffer, ItemBonus<?> value) {
        }

        @Override
        public ItemBonus<?> createDefaultInstance() {
            return bonus.copy();
        }
    }
}
