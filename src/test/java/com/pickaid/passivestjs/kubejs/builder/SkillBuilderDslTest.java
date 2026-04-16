package com.pickaid.passivestjs.kubejs.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pickaid.passivestjs.kubejs.content.Skills;
import com.pickaid.passivestjs.kubejs.event.SkillTreeContentEventJS;
import com.pickaid.passivestjs.kubejs.id.PSTDamageConditionId;
import com.pickaid.passivestjs.kubejs.id.PSTEventListenerId;
import com.pickaid.passivestjs.kubejs.id.PSTAttributeId;
import com.pickaid.passivestjs.kubejs.id.PSTItemId;
import com.pickaid.passivestjs.kubejs.id.PSTItemTagId;
import com.pickaid.passivestjs.kubejs.id.PSTMobEffectId;
import com.pickaid.passivestjs.kubejs.id.PSTPotionId;
import com.pickaid.passivestjs.kubejs.id.PSTItemBonusId;
import com.pickaid.passivestjs.kubejs.id.PSTItemConditionId;
import com.pickaid.passivestjs.kubejs.id.PSTLivingConditionId;
import com.pickaid.passivestjs.kubejs.id.PSTLivingMultiplierId;
import com.pickaid.passivestjs.kubejs.id.PSTNumericValueProviderId;
import com.pickaid.passivestjs.kubejs.id.PSTSkillBonusId;
import com.pickaid.passivestjs.kubejs.id.PSTSkillId;
import com.pickaid.passivestjs.kubejs.id.PSTSkillRequirementId;
import com.pickaid.passivestjs.kubejs.id.PSTTreeId;
import com.pickaid.passivestjs.kubejs.texture.PSTSkillFrameType;
import com.pickaid.passivestjs.kubejs.texture.PSTTexture;
import com.pickaid.passivestjs.kubejs.texture.PSTTooltipFrameType;
import com.pickaid.passivestjs.kubejs.type.PSTComparisonLogic;
import com.pickaid.passivestjs.kubejs.type.PSTEquipmentType;
import com.pickaid.passivestjs.kubejs.type.PSTSkillTarget;
import com.pickaid.passivestjs.kubejs.registry.PSTRegistryTypeHandle;
import com.pickaid.passivestjs.schema.PSTNodeFamily;
import com.pickaid.passivestjs.schema.PSTSchema;
import com.pickaid.passivestjs.schema.PSTSchemaFieldKind;
import com.pickaid.passivestjs.schema.PSTSchemaRegistry;
import dev.latvian.mods.kubejs.script.data.DataPackEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkillBuilderDslTest {
    @AfterEach
    void clearSchemas() {
        PSTSchemaRegistry.clear();
    }

    @BeforeAll
    static void bootstrapMinecraftRegistries() throws ReflectiveOperationException {
        var bootstrapped = net.minecraft.server.Bootstrap.class.getDeclaredField("isBootstrapped");
        bootstrapped.setAccessible(true);
        bootstrapped.setBoolean(null, true);
    }

    @Test
    void skillBonusCanBeAddedFromGenericRegistryHandleWithConsumer() throws Exception {
        PSTRegistryTypeHandle<BonusBuilder> handle = bonusHandle("kubejs:custom_bonus");

        SkillBuilder skill = new SkillBuilder("kubejs:test_skill", false)
                .bonus(handle, bonus -> bonus.amount(2));

        JsonArray bonuses = skill.toJson().getAsJsonArray("bonuses");
        JsonObject bonus = bonuses.get(0).getAsJsonObject();

        assertEquals(1, bonuses.size());
        assertEquals("kubejs:custom_bonus", bonus.get("type").getAsString());
        assertEquals(2, bonus.get("amount").getAsInt());
    }

    @Test
    void skillRequirementCanBeAddedFromGenericRegistryHandleWithConsumer() throws Exception {
        PSTRegistryTypeHandle<RequirementBuilder> handle = requirementHandle("kubejs:custom_requirement");

        SkillBuilder skill = new SkillBuilder("kubejs:test_skill", false)
                .requirement(handle, requirement -> requirement.skill("kubejs:root"));

        JsonArray requirements = skill.toJson().getAsJsonArray("requirements");
        JsonObject requirement = requirements.get(0).getAsJsonObject();

        assertEquals(1, requirements.size());
        assertEquals("kubejs:custom_requirement", requirement.get("type").getAsString());
        assertEquals("kubejs:root", requirement.get("skill_id").getAsString());
    }

    @Test
    void vanillaRequirementFieldNamesMatchPassiveSkillTreeSerializers() {
        JsonObject learned = new RequirementBuilder("skilltree:learned_skill")
                .skill("kubejs:root")
                .toJson();
        JsonObject advancement = new RequirementBuilder("skilltree:advancement")
                .advancement("minecraft:story/mine_diamond")
                .toJson();
        JsonObject stat = new RequirementBuilder("skilltree:stat")
                .statType("minecraft:custom")
                .stat("minecraft:jump")
                .minValue(3)
                .toJson();

        assertEquals("kubejs:root", learned.get("skill_id").getAsString());
        assertEquals("minecraft:story/mine_diamond", advancement.get("advancement").getAsString());
        assertEquals("minecraft:custom", stat.get("statTypeId").getAsString());
        assertEquals("minecraft:jump", stat.get("statId").getAsString());
        assertEquals(3, stat.get("minValue").getAsInt());
    }

    @Test
    void skillBuilderSupportsLiteralTitlesExplicitTranslationDescriptionsAndPolarLayout() {
        SkillBuilder root = new SkillBuilder("kubejs:root", true)
                .titleLiteral("Root")
                .descriptionKey("kubejs.skill.root.desc")
                .position(0, 0);

        SkillBuilder child = new SkillBuilder("kubejs:child", false)
                .titleLiteral("Child")
                .positionPolar(48, 90);

        JsonObject childJson = child.toJson();
        JsonObject rootJson = root.toJson();

        assertEquals(0.0D, childJson.get("positionX").getAsDouble(), 0.001D);
        assertEquals(48.0D, childJson.get("positionY").getAsDouble(), 0.001D);
        assertEquals("Root", rootJson.get("title").getAsString());
        assertEquals(
                "translate",
                rootJson.getAsJsonArray("description").get(0).getAsJsonObject().get("type").getAsString()
        );
    }

    @Test
    void literalDescriptionLinesSerializeToLiteralTextComponents() {
        SkillBuilder root = new SkillBuilder("kubejs:passivestjs/root", true)
                .descriptionLineLiteral("测试");

        JsonObject rootJson = root.toJson();
        JsonObject description = rootJson.getAsJsonArray("description").get(0).getAsJsonObject();

        assertEquals("测试", description.get("text").getAsString());
        assertFalse(description.has("translate"));
    }

    @Test
    void componentDescriptionLinesSerializeUsingChatComponentJson() {
        SkillBuilder root = new SkillBuilder("kubejs:passivestjs/root", true)
                .descriptionLine(Component.translatable("kubejs.passivestjs.skill.root.desc.0"));

        JsonObject rootJson = root.toJson();
        JsonObject description = rootJson.getAsJsonArray("description").get(0).getAsJsonObject();

        assertEquals("kubejs.passivestjs.skill.root.desc.0", description.get("translate").getAsString());
    }

    @Test
    void componentTitlesSerializeSidecarPayloadForRuntimeCompatibility() {
        SkillBuilder root = new SkillBuilder("kubejs:passivestjs/root", true)
                .title(Component.translatable("kubejs.passivestjs.skill.root.title"));

        JsonObject rootJson = root.toJson();
        JsonObject titleComponent = rootJson.getAsJsonObject("passivestjs$title_component");

        assertFalse(rootJson.has("title"));
        assertNotNull(titleComponent);
        assertEquals("kubejs.passivestjs.skill.root.title", titleComponent.get("translate").getAsString());
    }

    @Test
    void typedTextureAndFrameDslSerializeToExpectedRawTextures() {
        SkillBuilder skill = new SkillBuilder("kubejs:passivestjs/root", false)
                .icon(PSTTexture.parse("minecraft:textures/item/amethyst_shard.png"))
                .frame(PSTSkillFrameType.parse("notable"))
                .tooltipFrame(PSTTooltipFrameType.parse("gateway"));

        JsonObject json = skill.toJson();

        assertEquals("minecraft:textures/item/amethyst_shard.png", json.get("iconTexture").getAsString());
        assertEquals("skilltree:textures/icons/background/notable.png", json.get("backgroundTexture").getAsString());
        assertEquals("skilltree:textures/tooltip/gateway.png", json.get("borderTexture").getAsString());
    }

    @Test
    void typedResourceDslSerializesRawIdsForBonusConditionValueAndRequirementBuilders() {
        JsonObject bonusJson = new BonusBuilder("kubejs:typed_bonus")
                .effect(PSTMobEffectId.parse("minecraft:regeneration"))
                .effectType(PSTMobEffectId.parse("minecraft:strength"))
                .attribute(PSTAttributeId.parse("minecraft:generic.attack_damage"))
                .target(PSTSkillTarget.ENEMY)
                .toJson();
        JsonObject conditionJson = new ConditionBuilder("kubejs:typed_condition")
                .logic(PSTComparisonLogic.AT_LEAST)
                .effect(PSTMobEffectId.parse("minecraft:night_vision"))
                .equipmentType(PSTEquipmentType.MELEE_WEAPON)
                .potionType(PSTPotionId.parse("minecraft:regeneration"))
                .itemId(PSTItemId.parse("minecraft:diamond_sword"))
                .tagId(PSTItemTagId.parse("forge:tools"))
                .toJson();
        JsonObject listenerJson = new ListenerBuilder("kubejs:typed_listener")
                .target(PSTSkillTarget.PLAYER)
                .toJson();
        JsonObject valueJson = new ValueBuilder("kubejs:typed_value")
                .effectType(PSTMobEffectId.parse("minecraft:water_breathing"))
                .attribute(PSTAttributeId.parse("minecraft:generic.max_health"))
                .toJson();
        JsonObject requirementJson = new RequirementBuilder("skilltree:learned_skill")
                .skill(PSTSkillId.parse("kubejs:root"))
                .logic(PSTComparisonLogic.LESS)
                .toJson();

        assertEquals("minecraft:regeneration", bonusJson.get("effect").getAsString());
        assertEquals("minecraft:strength", bonusJson.get("effect_type").getAsString());
        assertEquals("minecraft:generic.attack_damage", bonusJson.get("attribute").getAsString());
        assertEquals("enemy", bonusJson.get("target").getAsString());
        assertEquals("AT_LEAST", conditionJson.get("logic").getAsString());
        assertEquals("minecraft:night_vision", conditionJson.get("effect").getAsString());
        assertEquals("melee_weapon", conditionJson.get("equipment_type").getAsString());
        assertEquals("minecraft:regeneration", conditionJson.get("potion_type").getAsString());
        assertEquals("minecraft:diamond_sword", conditionJson.get("id").getAsString());
        assertEquals("forge:tools", conditionJson.get("tag_id").getAsString());
        assertEquals("player", listenerJson.get("target").getAsString());
        assertEquals("minecraft:water_breathing", valueJson.get("effect_type").getAsString());
        assertEquals("minecraft:generic.max_health", valueJson.get("attribute").getAsString());
        assertEquals("kubejs:root", requirementJson.get("skill_id").getAsString());
        assertEquals("LESS", requirementJson.get("logic").getAsString());
    }

    @Test
    void dynamicBonusNodeRejectsUnknownSchemaField() {
        ResourceLocation typeId = ResourceLocation.fromNamespaceAndPath("kubejs", "bleed_bonus");
        PSTSchemaRegistry.remember(PSTSchema.builder(PSTNodeFamily.SKILL_BONUS)
                .field("amount", field -> field.kind(PSTSchemaFieldKind.DOUBLE).required())
                .build(typeId));

        SkillBuilder skill = new SkillBuilder("kubejs:test", false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                skill.bonusSchema(PSTSkillBonusId.of(typeId), bonus -> bonus.set("missingField", 2))
        );

        assertTrue(exception.getMessage().contains("missingField"));
    }

    @Test
    void requirementAndMultiplierAcceptPrebuiltValueBuilders() {
        ValueBuilder value = new ValueBuilder("kubejs:health_level")
                .percentage(true)
                .missing(false);

        JsonObject multiplierJson = new MultiplierBuilder("kubejs:numeric_value")
                .valueProvider(value)
                .divisor(2)
                .toJson();
        JsonObject requirementJson = new RequirementBuilder("kubejs:numeric_requirement")
                .valueProvider(value)
                .requiredValue(0.5)
                .logic(PSTComparisonLogic.LESS)
                .toJson();

        assertEquals("kubejs:health_level", multiplierJson.getAsJsonObject("value_provider").get("type").getAsString());
        assertEquals("kubejs:health_level", requirementJson.getAsJsonObject("value_provider").get("type").getAsString());
        assertTrue(multiplierJson.getAsJsonObject("value_provider").get("percentage").getAsBoolean());
        assertTrue(requirementJson.getAsJsonObject("value_provider").get("percentage").getAsBoolean());
    }

    @Test
    void nestedDslAcceptsPrebuiltTypedBuilders() {
        MultiplierBuilder multiplier = new MultiplierBuilder("kubejs:health_scale")
                .valueProvider(new ValueBuilder("kubejs:health_level").percentage(true))
                .divisor(2);
        ConditionBuilder condition = new ConditionBuilder("kubejs:player_condition")
                .valueProvider(new ValueBuilder("kubejs:stamina").missing(false))
                .requiredValue(0.5)
                .logic(PSTComparisonLogic.MORE);
        ListenerBuilder listener = new ListenerBuilder("kubejs:on_hit")
                .whenPlayer(condition)
                .scalePlayer(multiplier);

        JsonObject bonusJson = new BonusBuilder("kubejs:damage_bonus")
                .whenPlayer(condition)
                .scalePlayer(multiplier)
                .scaleEnemy(multiplier)
                .scaleTarget(multiplier)
                .scaleAttacker(multiplier)
                .eventListener(listener)
                .toJson();
        JsonObject itemBonusJson = new ItemBonusBuilder("kubejs:item_bonus")
                .skillBonus(new BonusBuilder("kubejs:nested_bonus").amount(3))
                .itemBonuses(new ItemBonusBuilder("kubejs:nested_item_bonus"))
                .toJson();

        assertEquals("kubejs:player_condition", bonusJson.getAsJsonObject("player_condition").get("type").getAsString());
        assertEquals("kubejs:health_scale", bonusJson.getAsJsonObject("player_multiplier").get("type").getAsString());
        assertEquals("kubejs:health_scale", bonusJson.getAsJsonObject("enemy_multiplier").get("type").getAsString());
        assertEquals("kubejs:health_scale", bonusJson.getAsJsonObject("target_multiplier").get("type").getAsString());
        assertEquals("kubejs:health_scale", bonusJson.getAsJsonObject("attacker_multiplier").get("type").getAsString());
        assertEquals("kubejs:on_hit", bonusJson.getAsJsonObject("event_listener").get("type").getAsString());
        assertEquals("kubejs:nested_bonus", itemBonusJson.getAsJsonObject("skill_bonus").get("type").getAsString());
        assertEquals("kubejs:nested_item_bonus", itemBonusJson.getAsJsonArray("inner_bonuses").get(0).getAsJsonObject().get("type").getAsString());
    }

    @Test
    void typedIdDslEntryPointsBuildExpectedNestedJson() {
        SkillBuilder skill = new SkillBuilder("kubejs:test_skill", false)
                .bonus(PSTSkillBonusId.parse("skilltree:damage"), bonus -> {
                    bonus.amount(4);
                    bonus.whenPlayer(PSTLivingConditionId.parse("skilltree:crouching"), condition -> {
                    });
                    bonus.whenDamage(PSTDamageConditionId.parse("skilltree:melee"), condition -> {
                    });
                    bonus.scalePlayer(PSTLivingMultiplierId.parse("skilltree:none"), multiplier -> {
                    });
                    bonus.eventListener(PSTEventListenerId.parse("skilltree:attack"), listener -> {
                        listener.whenPlayer(PSTLivingConditionId.parse("skilltree:none"), condition -> {
                        });
                    });
                })
                .requirement(PSTSkillRequirementId.parse("skilltree:numeric_value"), requirement -> {
                    requirement.valueProvider(PSTNumericValueProviderId.parse("skilltree:health_level"), value -> {
                        value.percentage(true);
                    });
                });

        JsonObject skillJson = skill.toJson();
        JsonObject bonusJson = skillJson.getAsJsonArray("bonuses").get(0).getAsJsonObject();
        JsonObject requirementJson = skillJson.getAsJsonArray("requirements").get(0).getAsJsonObject();
        JsonObject listenerJson = bonusJson.getAsJsonObject("event_listener");

        assertEquals("skilltree:damage", bonusJson.get("type").getAsString());
        assertEquals("skilltree:crouching", bonusJson.getAsJsonObject("player_condition").get("type").getAsString());
        assertEquals("skilltree:melee", bonusJson.getAsJsonObject("damage_condition").get("type").getAsString());
        assertEquals("skilltree:none", bonusJson.getAsJsonObject("player_multiplier").get("type").getAsString());
        assertEquals("skilltree:attack", listenerJson.get("type").getAsString());
        assertEquals("skilltree:none", listenerJson.getAsJsonObject("player_condition").get("type").getAsString());
        assertEquals("skilltree:numeric_value", requirementJson.get("type").getAsString());
        assertEquals("skilltree:health_level", requirementJson.getAsJsonObject("value_provider").get("type").getAsString());
    }

    @Test
    void objectCatchAllMethodsAreHiddenFromJsAndTypedDslIsDocumented() throws Exception {
        assertHidden(SkillBuilder.class.getDeclaredMethod("bonus", Object.class));
        assertHidden(SkillBuilder.class.getDeclaredMethod("bonuses", Object.class));
        assertHidden(SkillBuilder.class.getDeclaredMethod("requirement", Object.class));
        assertHidden(SkillBuilder.class.getDeclaredMethod("requirements", Object.class));
        assertHidden(SkillBuilder.class.getDeclaredMethod("tags", Object.class));
        assertHidden(SkillBuilder.class.getDeclaredMethod("descriptionLines", Object.class));
        assertHidden(SkillBuilder.class.getDeclaredMethod("connect", Object.class));
        assertHidden(SkillBuilder.class.getDeclaredMethod("longConnect", Object.class));
        assertHidden(SkillBuilder.class.getDeclaredMethod("oneWayConnect", Object.class));
        assertHidden(SkillBuilder.class.getDeclaredMethod("connectId", ResourceLocation.class));
        assertHidden(SkillBuilder.class.getDeclaredMethod("longConnectId", ResourceLocation.class));
        assertHidden(SkillBuilder.class.getDeclaredMethod("oneWayConnectId", ResourceLocation.class));
        assertFalse(hasPublicSignature(SkillBuilder.class, "requiresSkill", SkillBuilder.class));
        assertFalse(hasPublicSignature(SkillBuilder.class, "connect", SkillBuilder.class));
        assertFalse(hasPublicSignature(SkillBuilder.class, "longConnect", SkillBuilder.class));
        assertFalse(hasPublicSignature(SkillBuilder.class, "oneWayConnect", SkillBuilder.class));
        assertHidden(BonusBuilder.class.getDeclaredMethod("onAttack"));
        assertHidden(BonusBuilder.class.getDeclaredMethod("onAttack", String.class));
        assertHidden(BonusBuilder.class.getDeclaredMethod("onTick", Number.class));
        assertHidden(BonusBuilder.class.getDeclaredMethod("whenPlayer", Object.class));
        assertHidden(BonusBuilder.class.getDeclaredMethod("whenEnemy", Object.class));
        assertHidden(BonusBuilder.class.getDeclaredMethod("whenTarget", Object.class));
        assertHidden(BonusBuilder.class.getDeclaredMethod("whenAttacker", Object.class));
        assertHidden(BonusBuilder.class.getDeclaredMethod("whenDamage", Object.class));
        assertHidden(BonusBuilder.class.getDeclaredMethod("whenItem", Object.class));
        assertHidden(BonusBuilder.class.getDeclaredMethod("scalePlayer", Object.class));
        assertHidden(BonusBuilder.class.getDeclaredMethod("scaleEnemy", Object.class));
        assertHidden(BonusBuilder.class.getDeclaredMethod("scaleTarget", Object.class));
        assertHidden(BonusBuilder.class.getDeclaredMethod("scaleAttacker", Object.class));
        assertHidden(BonusBuilder.class.getDeclaredMethod("eventListener", Object.class));
        assertHidden(ListenerBuilder.class.getDeclaredMethod("whenPlayer", Object.class));
        assertHidden(ListenerBuilder.class.getDeclaredMethod("whenEnemy", Object.class));
        assertHidden(ListenerBuilder.class.getDeclaredMethod("whenDamage", Object.class));
        assertHidden(ListenerBuilder.class.getDeclaredMethod("scalePlayer", Object.class));
        assertHidden(ListenerBuilder.class.getDeclaredMethod("scaleEnemy", Object.class));
        assertHidden(ConditionBuilder.class.getDeclaredMethod("itemCondition", Object.class));
        assertHidden(ConditionBuilder.class.getDeclaredMethod("valueProvider", Object.class));
        assertHidden(MultiplierBuilder.class.getDeclaredMethod("valueProvider", Object.class));
        assertHidden(RequirementBuilder.class.getDeclaredMethod("valueProvider", Object.class));
        assertHidden(ItemBonusBuilder.class.getDeclaredMethod("skillBonus", Object.class));
        assertHidden(ItemBonusBuilder.class.getDeclaredMethod("itemBonuses", Object.class));
        assertHidden(SkillBuilder.class.getDeclaredMethod("titleLiteral", String.class));
        assertHidden(SkillBuilder.class.getDeclaredMethod("descriptionLineLiteral", String.class));
        assertHidden(SkillBuilder.class.getDeclaredMethod("background", String.class));
        assertHidden(SkillBuilder.class.getDeclaredMethod("icon", String.class));
        assertHidden(SkillBuilder.class.getDeclaredMethod("border", String.class));
        assertHidden(SkillBuilder.class.getDeclaredMethod("frame", String.class));
        assertHidden(SkillBuilder.class.getDeclaredMethod("tooltipFrame", String.class));
        assertHidden(SkillTreeBuilder.class.getDeclaredMethod("titleLiteral", String.class));

        assertInfo(SkillBuilder.class.getDeclaredMethod("startingPoint", boolean.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("title", Component.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("titleKey", String.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("titleColor", String.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("background", PSTTexture.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("icon", PSTTexture.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("border", PSTTexture.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("frame", PSTSkillFrameType.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("tooltipFrame", PSTTooltipFrameType.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("position", Number.class, Number.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("positionPolar", Number.class, Number.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("buttonSize", Number.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("tag", String.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("descriptionLine", Component.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("descriptionKey", String.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("bonus", BonusBuilder.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("bonus", PSTSkillBonusId.class, Consumer.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("bonus", com.pickaid.passivestjs.kubejs.registry.PSTRegistryTypeHandle.class, Consumer.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("bonusSchema", PSTSkillBonusId.class, Consumer.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("requirement", RequirementBuilder.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("requirement", PSTSkillRequirementId.class, Consumer.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("requirement", com.pickaid.passivestjs.kubejs.registry.PSTRegistryTypeHandle.class, Consumer.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("requirementSchema", PSTSkillRequirementId.class, Consumer.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("connect", PSTSkillId.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("longConnect", PSTSkillId.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("oneWayConnect", PSTSkillId.class));
        assertInfo(SkillBuilder.class.getDeclaredMethod("requiresSkill", PSTSkillId.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("whenPlayer", PSTLivingConditionId.class, Consumer.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("whenEnemy", PSTLivingConditionId.class, Consumer.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("whenTarget", PSTLivingConditionId.class, Consumer.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("whenAttacker", PSTLivingConditionId.class, Consumer.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("whenDamage", PSTDamageConditionId.class, Consumer.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("whenItem", PSTItemConditionId.class, Consumer.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("scalePlayer", PSTLivingMultiplierId.class, Consumer.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("scaleEnemy", PSTLivingMultiplierId.class, Consumer.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("scaleTarget", PSTLivingMultiplierId.class, Consumer.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("scaleAttacker", PSTLivingMultiplierId.class, Consumer.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("eventListener", PSTEventListenerId.class, Consumer.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("whenPlayer", ConditionBuilder.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("whenEnemy", ConditionBuilder.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("whenTarget", ConditionBuilder.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("whenAttacker", ConditionBuilder.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("whenDamage", ConditionBuilder.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("whenItem", ConditionBuilder.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("scalePlayer", MultiplierBuilder.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("scaleEnemy", MultiplierBuilder.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("scaleTarget", MultiplierBuilder.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("scaleAttacker", MultiplierBuilder.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("eventListener", ListenerBuilder.class));
        assertInfo(ListenerBuilder.class.getDeclaredMethod("whenPlayer", ConditionBuilder.class));
        assertInfo(ListenerBuilder.class.getDeclaredMethod("whenEnemy", ConditionBuilder.class));
        assertInfo(ListenerBuilder.class.getDeclaredMethod("whenDamage", ConditionBuilder.class));
        assertInfo(ListenerBuilder.class.getDeclaredMethod("whenPlayer", PSTLivingConditionId.class, Consumer.class));
        assertInfo(ListenerBuilder.class.getDeclaredMethod("whenEnemy", PSTLivingConditionId.class, Consumer.class));
        assertInfo(ListenerBuilder.class.getDeclaredMethod("whenDamage", PSTDamageConditionId.class, Consumer.class));
        assertInfo(ListenerBuilder.class.getDeclaredMethod("scalePlayer", PSTLivingMultiplierId.class, Consumer.class));
        assertInfo(ListenerBuilder.class.getDeclaredMethod("scaleEnemy", PSTLivingMultiplierId.class, Consumer.class));
        assertInfo(ListenerBuilder.class.getDeclaredMethod("scalePlayer", MultiplierBuilder.class));
        assertInfo(ListenerBuilder.class.getDeclaredMethod("scaleEnemy", MultiplierBuilder.class));
        assertInfo(ConditionBuilder.class.getDeclaredMethod("itemCondition", PSTItemConditionId.class, Consumer.class));
        assertInfo(ConditionBuilder.class.getDeclaredMethod("valueProvider", PSTNumericValueProviderId.class, Consumer.class));
        assertInfo(ConditionBuilder.class.getDeclaredMethod("itemCondition", ConditionBuilder.class));
        assertInfo(ConditionBuilder.class.getDeclaredMethod("valueProvider", ValueBuilder.class));
        assertInfo(MultiplierBuilder.class.getDeclaredMethod("valueProvider", PSTNumericValueProviderId.class, Consumer.class));
        assertInfo(MultiplierBuilder.class.getDeclaredMethod("valueProvider", ValueBuilder.class));
        assertInfo(RequirementBuilder.class.getDeclaredMethod("valueProvider", PSTNumericValueProviderId.class, Consumer.class));
        assertInfo(RequirementBuilder.class.getDeclaredMethod("valueProvider", ValueBuilder.class));
        assertInfo(ItemBonusBuilder.class.getDeclaredMethod("skillBonus", PSTSkillBonusId.class, Consumer.class));
        assertInfo(ItemBonusBuilder.class.getDeclaredMethod("itemBonuses", PSTItemBonusId.class, Consumer.class));
        assertInfo(ItemBonusBuilder.class.getDeclaredMethod("skillBonus", BonusBuilder.class));
        assertInfo(ItemBonusBuilder.class.getDeclaredMethod("itemBonuses", ItemBonusBuilder.class));

        assertFalse(hasPublicSignature(SkillBuilder.class, "bonusSchema", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(SkillBuilder.class, "requirementSchema", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(BonusBuilder.class, "whenPlayer", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(BonusBuilder.class, "whenEnemy", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(BonusBuilder.class, "whenTarget", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(BonusBuilder.class, "whenAttacker", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(BonusBuilder.class, "whenDamage", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(BonusBuilder.class, "whenItem", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(BonusBuilder.class, "scalePlayer", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(BonusBuilder.class, "scaleEnemy", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(BonusBuilder.class, "scaleTarget", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(BonusBuilder.class, "scaleAttacker", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(BonusBuilder.class, "eventListener", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(ListenerBuilder.class, "whenPlayer", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(ListenerBuilder.class, "whenEnemy", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(ListenerBuilder.class, "whenDamage", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(ListenerBuilder.class, "scalePlayer", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(ListenerBuilder.class, "scaleEnemy", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(ConditionBuilder.class, "itemCondition", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(ConditionBuilder.class, "valueProvider", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(MultiplierBuilder.class, "valueProvider", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(RequirementBuilder.class, "valueProvider", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(ItemBonusBuilder.class, "skillBonus", ResourceLocation.class, Consumer.class));
        assertFalse(hasPublicSignature(ItemBonusBuilder.class, "itemBonuses", ResourceLocation.class, Consumer.class));
    }

    @Test
    void skillTreeBuilderHidesObjectCatchAllsAndDocumentsTypedDsl() throws Exception {
        assertHidden(SkillTreeBuilder.class.getDeclaredMethod("skill", Object.class));
        assertHidden(SkillTreeBuilder.class.getDeclaredMethod("startingSkill", Object.class));
        assertHidden(SkillTreeBuilder.class.getDeclaredMethod("addSkill", Object.class));
        assertHidden(SkillTreeBuilder.class.getDeclaredMethod("includeSkill", Object.class));

        assertInfo(SkillTreeBuilder.class.getDeclaredMethod("skill", PSTSkillId.class));
        assertInfo(SkillTreeBuilder.class.getDeclaredMethod("startingSkill", PSTSkillId.class));
        assertInfo(SkillTreeBuilder.class.getDeclaredMethod("addSkill", SkillBuilder.class));
        assertInfo(SkillTreeBuilder.class.getDeclaredMethod("includeSkill", PSTSkillId.class));
        assertInfo(SkillTreeBuilder.class.getDeclaredMethod("title", Component.class));
        assertInfo(SkillTreeBuilder.class.getDeclaredMethod("limit", String.class, Number.class));
    }

    @Test
    void addSkillApisExposeConsumerOverloadsAndHideRawObjectOverloads() throws Exception {
        assertInfo(Skills.class.getDeclaredMethod("addSkill", DataPackEventJS.class, String.class, Consumer.class));
        assertInfo(Skills.class.getDeclaredMethod("addStartingSkill", DataPackEventJS.class, String.class, Consumer.class));
        assertInfo(Skills.class.getDeclaredMethod("addSkillTree", DataPackEventJS.class, String.class, Consumer.class));
        assertHidden(Skills.class.getDeclaredMethod("addSkill", DataPackEventJS.class, String.class, Object.class));
        assertHidden(Skills.class.getDeclaredMethod("addStartingSkill", DataPackEventJS.class, String.class, Object.class));
        assertHidden(Skills.class.getDeclaredMethod("addSkillTree", DataPackEventJS.class, String.class, Object.class));

        assertInfo(SkillTreeContentEventJS.class.getDeclaredMethod("addSkill", PSTSkillId.class, Consumer.class));
        assertInfo(SkillTreeContentEventJS.class.getDeclaredMethod("addStartingSkill", PSTSkillId.class, Consumer.class));
        assertInfo(SkillTreeContentEventJS.class.getDeclaredMethod("addSkillTree", PSTTreeId.class, Consumer.class));
        assertHidden(SkillTreeContentEventJS.class.getDeclaredMethod("addSkill", PSTSkillId.class, Object.class));
        assertHidden(SkillTreeContentEventJS.class.getDeclaredMethod("addStartingSkill", PSTSkillId.class, Object.class));
        assertHidden(SkillTreeContentEventJS.class.getDeclaredMethod("addSkillTree", PSTTreeId.class, Object.class));
    }

    private static void assertHidden(Method method) {
        assertNotNull(method.getAnnotation(HideFromJS.class), method.getName() + " should be hidden from JS");
    }

    private static void assertInfo(Method method) {
        assertNotNull(method.getAnnotation(Info.class), method.getName() + " should carry @Info");
    }

    private static boolean hasPublicSignature(Class<?> type, String name, Class<?>... parameterTypes) {
        return Arrays.stream(type.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .anyMatch(method -> method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), parameterTypes));
    }

    @SuppressWarnings("unchecked")
    private static PSTRegistryTypeHandle<BonusBuilder> bonusHandle(String id) throws Exception {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath("kubejs", id.substring(id.indexOf(':') + 1));
        Class<?> factoryType = Class.forName("com.pickaid.passivestjs.kubejs.registry.PSTRegistryBuilderFactory");
        Object factory = Proxy.newProxyInstance(
                factoryType.getClassLoader(),
                new Class[]{factoryType},
                (proxy, method, args) -> {
                    if ("create".equals(method.getName())) {
                        return new BonusBuilder(((ResourceLocation) args[0]).toString());
                    }
                    throw new UnsupportedOperationException(method.getName());
                }
        );

        var constructor = PSTRegistryTypeHandle.class.getDeclaredConstructor(ResourceLocation.class, String.class, factoryType);
        constructor.setAccessible(true);
        return (PSTRegistryTypeHandle<BonusBuilder>) constructor.newInstance(resourceLocation, "kubejs:test_registry", factory);
    }

    @SuppressWarnings("unchecked")
    private static PSTRegistryTypeHandle<RequirementBuilder> requirementHandle(String id) throws Exception {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath("kubejs", id.substring(id.indexOf(':') + 1));
        Class<?> factoryType = Class.forName("com.pickaid.passivestjs.kubejs.registry.PSTRegistryBuilderFactory");
        Object factory = Proxy.newProxyInstance(
                factoryType.getClassLoader(),
                new Class[]{factoryType},
                (proxy, method, args) -> {
                    if ("create".equals(method.getName())) {
                        return new RequirementBuilder(((ResourceLocation) args[0]).toString());
                    }
                    throw new UnsupportedOperationException(method.getName());
                }
        );

        var constructor = PSTRegistryTypeHandle.class.getDeclaredConstructor(ResourceLocation.class, String.class, factoryType);
        constructor.setAccessible(true);
        return (PSTRegistryTypeHandle<RequirementBuilder>) constructor.newInstance(resourceLocation, "kubejs:test_registry", factory);
    }
}
