package com.pickaid.passivestjs.kubejs.registry;

import com.google.gson.JsonObject;
import com.pickaid.passivestjs.kubejs.builder.BonusBuilder;
import com.pickaid.passivestjs.kubejs.builder.ItemBonusBuilder;
import com.pickaid.passivestjs.kubejs.id.PSTItemBonusId;
import com.pickaid.passivestjs.kubejs.id.PSTSkillBonusId;
import com.pickaid.passivestjs.runtime.PSTCustomRuntimeContexts;
import com.pickaid.passivestjs.runtime.PSTCustomRuntimeEventListener;
import com.pickaid.passivestjs.runtime.PSTEventListenerRuntimeBridge;
import com.pickaid.passivestjs.runtime.PSTRuntimeNode;
import com.pickaid.passivestjs.runtime.PSTSerializerObjectIndex;
import com.pickaid.passivestjs.runtime.tooltip.PSTTooltipComposer;
import com.pickaid.passivestjs.runtime.tooltip.PSTRuntimeTooltipSupport;
import com.pickaid.passivestjs.runtime.tooltip.PSTTooltipSpecRegistry;
import com.pickaid.passivestjs.schema.PSTNodeFamily;
import com.pickaid.passivestjs.schema.PSTSchemaFieldKind;
import com.pickaid.passivestjs.schema.PSTSchemaRegistry;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.function.FloatFunction;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.predicate.damage.DamageCondition;
import daripher.skilltree.skill.bonus.predicate.enchantment.EnchantmentCondition;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.requirement.SkillRequirement;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PSTRegistriesApiTest {
    @AfterEach
    void clearMetadata() {
        PSTSchemaRegistry.clear();
        PSTSerializerMetadataIndex.clear();
        PSTSerializerObjectIndex.clear();
        PSTTooltipSpecRegistry.clear();
    }

    @BeforeAll
    static void bootstrapMinecraftRegistries() throws ReflectiveOperationException {
        var bootstrapped = net.minecraft.server.Bootstrap.class.getDeclaredField("isBootstrapped");
        bootstrapped.setAccessible(true);
        bootstrapped.setBoolean(null, true);
    }

    @Test
    void skillBonusRegistryHandleCreatesTypedBonusBuilder() {
        BonusBuilder builder = PSTRegistriesApi.INSTANCE
                .skillBonuses()
                .get(PSTSkillBonusId.of(new ResourceLocation("skilltree", "damage")))
                .create();

        assertEquals("skilltree:damage", builder.type());
    }

    @Test
    void itemBonusRegistryHandleCreatesTypedItemBonusBuilder() {
        ItemBonusBuilder builder = PSTRegistriesApi.INSTANCE
                .itemBonuses()
                .get(PSTItemBonusId.of(new ResourceLocation("skilltree", "skill_bonus")))
                .create();

        assertEquals("skilltree:skill_bonus", builder.type());
    }

    @Test
    void unknownRegistryEntryThrowsHelpfulErrorMessage() {
        @SuppressWarnings("unchecked")
        var registry = (net.minecraftforge.registries.IForgeRegistry<Object>) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{net.minecraftforge.registries.IForgeRegistry.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getValue" -> null;
                    case "getKeys" -> java.util.Set.of();
                    case "getRegistryName" -> new ResourceLocation("skilltree", "skill_bonuses");
                    default -> null;
                }
        );
        PSTSkillBonusRegistryView view = new PSTSkillBonusRegistryView(
                "skilltree:skill_bonuses",
                () -> registry,
                id -> new BonusBuilder(id.toString())
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> view
                        .get(PSTSkillBonusId.of(new ResourceLocation("kubejs", "missing_bonus")))
        );

        assertTrue(exception.getMessage().contains("kubejs:missing_bonus"));
        assertTrue(exception.getMessage().contains("skilltree:skill_bonuses"));
        assertTrue(exception.getMessage().contains(".has("));
    }

    @Test
    void registryEntryPointsExposeConcreteRegistryIdWrappers() throws Exception {
        assertEquals(PSTSkillBonusRegistryView.class, PSTRegistriesApi.class.getDeclaredMethod("skillBonuses").getReturnType());
        assertEquals(PSTItemBonusRegistryView.class, PSTRegistriesApi.class.getDeclaredMethod("itemBonuses").getReturnType());
        assertNotNull(PSTSkillBonusRegistryView.class.getDeclaredMethod("has", PSTSkillBonusId.class));
        assertNotNull(PSTSkillBonusRegistryView.class.getDeclaredMethod("get", PSTSkillBonusId.class));
        assertNotNull(PSTItemBonusRegistryView.class.getDeclaredMethod("has", PSTItemBonusId.class));
        assertNotNull(PSTItemBonusRegistryView.class.getDeclaredMethod("get", PSTItemBonusId.class));
        assertFalse(hasPublicSignature(PSTSkillBonusRegistryView.class, "get", ResourceLocation.class));
        assertFalse(hasPublicSignature(PSTItemBonusRegistryView.class, "get", ResourceLocation.class));
    }

    @Test
    void customSkillRequirementSerializerProducesExecutableRuntimeRequirement() {
        SkillRequirement.Serializer serializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillRequirementSerializerBuilder(
                new ResourceLocation("kubejs", "runtime_requirement")
        )
                .test(context -> "ready".equals(context.node().string("flag").orElse("")))
                .createObject();

        JsonObject json = new JsonObject();
        json.addProperty("type", "kubejs:runtime_requirement");
        json.addProperty("flag", "ready");

        SkillRequirement<?> requirement = serializer.deserialize(json);

        assertTrue(requirement.test(null));
        assertNotNull(requirement.copy());
    }

    @Test
    void customLivingConditionAndMultiplierRuntimeExecuteAgainstPayload() {
        LivingEntityPredicate.Serializer conditionSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTLivingConditionSerializerBuilder(
                new ResourceLocation("kubejs", "runtime_condition")
        )
                .test(context -> context.node().integer("threshold").orElse(0) == 4)
                .createObject();
        LivingMultiplier.Serializer multiplierSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTLivingMultiplierSerializerBuilder(
                new ResourceLocation("kubejs", "runtime_multiplier")
        )
                .value(context -> context.node().number("amount").orElse(0.0D).floatValue())
                .createObject();

        JsonObject conditionJson = new JsonObject();
        conditionJson.addProperty("type", "kubejs:runtime_condition");
        conditionJson.addProperty("threshold", 4);

        JsonObject multiplierJson = new JsonObject();
        multiplierJson.addProperty("type", "kubejs:runtime_multiplier");
        multiplierJson.addProperty("amount", 2.5D);

        LivingEntityPredicate condition = conditionSerializer.deserialize(conditionJson);
        LivingMultiplier multiplier = multiplierSerializer.deserialize(multiplierJson);

        assertTrue(condition.test(null));
        assertEquals(2.5F, multiplier.getValue(null));
    }

    @Test
    void customNumericValueProviderReturnsConfiguredRuntimeValue() {
        FloatFunction.Serializer serializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTFloatFunctionSerializerBuilder(
                new ResourceLocation("kubejs", "runtime_value")
        )
                .value(context -> context.node().number("value").orElse(0.0D).floatValue())
                .createObject();

        JsonObject json = new JsonObject();
        json.addProperty("type", "kubejs:runtime_value");
        json.addProperty("value", 7.25D);

        FloatFunction<?> function = serializer.deserialize(json);

        assertEquals(7.25F, function.apply(null));
    }

    @Test
    void customDamageItemAndEnchantmentConditionsProduceExecutableRuntimeObjects() {
        DamageCondition.Serializer damageSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTDamageConditionSerializerBuilder(
                new ResourceLocation("kubejs", "runtime_damage_condition")
        )
                .test(context -> context.damageSource() == null)
                .createObject();
        ItemStackPredicate.Serializer itemSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTItemConditionSerializerBuilder(
                new ResourceLocation("kubejs", "runtime_item_condition")
        )
                .test(context -> context.itemStack() == null)
                .createObject();
        EnchantmentCondition.Serializer enchantmentSerializer =
                new com.pickaid.passivestjs.kubejs.registry.builder.PSTEnchantmentConditionSerializerBuilder(
                        new ResourceLocation("kubejs", "runtime_enchantment_condition")
                )
                        .test(context -> context.category() == null)
                        .createObject();

        JsonObject damageJson = new JsonObject();
        damageJson.addProperty("type", "kubejs:runtime_damage_condition");
        JsonObject itemJson = new JsonObject();
        itemJson.addProperty("type", "kubejs:runtime_item_condition");
        JsonObject enchantmentJson = new JsonObject();
        enchantmentJson.addProperty("type", "kubejs:runtime_enchantment_condition");

        assertTrue(damageSerializer.deserialize(damageJson).met(null));
        assertTrue(itemSerializer.deserialize(itemJson).test(null));
        assertTrue(enchantmentSerializer.deserialize(enchantmentJson).met(null));
    }

    @Test
    void customSkillBonusSerializerProducesExecutableRuntimeBonus() {
        AtomicInteger learnCount = new AtomicInteger();
        AtomicInteger removeCount = new AtomicInteger();
        AtomicBoolean applied = new AtomicBoolean(false);

        SkillBonus.Serializer serializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillBonusSerializerBuilder(
                new ResourceLocation("kubejs", "runtime_bonus")
        )
                .onLearn(context -> learnCount.incrementAndGet())
                .onRemove(context -> removeCount.incrementAndGet())
                .onApply(context -> applied.set(context.node().bool("apply").orElse(false)))
                .createObject();

        JsonObject json = new JsonObject();
        json.addProperty("type", "kubejs:runtime_bonus");
        json.addProperty("apply", true);

        SkillBonus<?> bonus = serializer.deserialize(json);

        bonus.onSkillLearned(null, true);
        bonus.onSkillRemoved(null);
        ((EventListenerBonus<?>) bonus).applyEffect(null);

        assertEquals(1, learnCount.get());
        assertEquals(1, removeCount.get());
        assertTrue(applied.get());
    }

    @Test
    void customSkillBonusSerializerUsesIndexedNestedEventListenerCallbacks() {
        AtomicReference<String> modeSeen = new AtomicReference<>("<unset>");

        new com.pickaid.passivestjs.kubejs.registry.builder.PSTEventListenerSerializerBuilder(
                new ResourceLocation("kubejs", "runtime_listener")
        )
                .onTick(context -> modeSeen.set(context.node().string("mode").orElse("<missing>")))
                .createObject();

        SkillBonus.Serializer serializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillBonusSerializerBuilder(
                new ResourceLocation("kubejs", "runtime_bonus")
        ).createObject();

        JsonObject json = new JsonObject();
        json.addProperty("type", "kubejs:runtime_bonus");
        JsonObject listenerJson = new JsonObject();
        listenerJson.addProperty("type", "kubejs:runtime_listener");
        listenerJson.addProperty("mode", "tick");
        json.add("event_listener", listenerJson);

        EventListenerBonus<?> bonus = (EventListenerBonus<?>) serializer.deserialize(json);
        PSTCustomRuntimeEventListener listener = (PSTCustomRuntimeEventListener) bonus.getEventListener();

        listener.onTick(new PSTCustomRuntimeContexts.TickListenerContext(listener.node(), bonus, null));

        assertEquals("tick", modeSeen.get());
    }

    @Test
    void serializerBuildersPublishSchemaMetadataForTheirRegistryEntry() {
        var id = new ResourceLocation("kubejs", "bleed_bonus");
        var builder = new com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillBonusSerializerBuilder(id);

        builder.schema(schema -> schema
                .field("amount", field -> field.kind(PSTSchemaFieldKind.DOUBLE).required())
                .field("operation", field -> field.kind(PSTSchemaFieldKind.ENUM)
                        .enumChoice("ADDITION")
                        .defaultValue("ADDITION")));

        builder.createObject();

        var schema = PSTSchemaRegistry.find(PSTNodeFamily.SKILL_BONUS, id).orElseThrow();
        var bySerializerFamily = PSTSerializerMetadataIndex.find(PSTSerializerFamily.SKILL_BONUSES, id).orElseThrow();
        var byNodeFamily = PSTSerializerMetadataIndex.find(PSTNodeFamily.SKILL_BONUS, id).orElseThrow();

        assertEquals(PSTSchemaFieldKind.DOUBLE, schema.field("amount").kind());
        assertEquals("ADDITION", schema.field("operation").defaultValue());
        assertEquals(id, bySerializerFamily.id());
        assertEquals(PSTNodeFamily.SKILL_BONUS, bySerializerFamily.nodeFamily());
        assertEquals(bySerializerFamily, byNodeFamily);
    }

    @Test
    void runtimeSerializerTooltipsUsePstStyleTranslationKeys() {
        SkillBonus.Serializer bonusSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillBonusSerializerBuilder(
                new ResourceLocation("kubejs", "runtime_bonus")
        ).createObject();
        SkillEventListener.Serializer listenerSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTEventListenerSerializerBuilder(
                new ResourceLocation("kubejs", "runtime_listener")
        ).createObject();
        LivingEntityPredicate.Serializer conditionSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTLivingConditionSerializerBuilder(
                new ResourceLocation("kubejs", "runtime_condition")
        ).createObject();
        LivingMultiplier.Serializer multiplierSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTLivingMultiplierSerializerBuilder(
                new ResourceLocation("kubejs", "runtime_multiplier")
        ).createObject();
        FloatFunction.Serializer valueSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTFloatFunctionSerializerBuilder(
                new ResourceLocation("kubejs", "runtime_value")
        ).createObject();
        SkillRequirement.Serializer requirementSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillRequirementSerializerBuilder(
                new ResourceLocation("kubejs", "runtime_requirement")
        ).createObject();

        JsonObject bonusJson = new JsonObject();
        bonusJson.addProperty("type", "kubejs:runtime_bonus");
        JsonObject listenerJson = new JsonObject();
        listenerJson.addProperty("type", "kubejs:runtime_listener");
        JsonObject conditionJson = new JsonObject();
        conditionJson.addProperty("type", "kubejs:runtime_condition");
        JsonObject multiplierJson = new JsonObject();
        multiplierJson.addProperty("type", "kubejs:runtime_multiplier");
        JsonObject valueJson = new JsonObject();
        valueJson.addProperty("type", "kubejs:runtime_value");
        JsonObject requirementJson = new JsonObject();
        requirementJson.addProperty("type", "kubejs:runtime_requirement");

        assertEquals("skill_bonus.kubejs.runtime_bonus", translationKey(bonusSerializer.deserialize(bonusJson).getTooltip()));
        assertEquals(
                "event_listener.kubejs.runtime_listener",
                translationKey(listenerSerializer.deserialize(listenerJson).getTooltip(Component.literal("bonus")))
        );
        assertEquals(
                "living_condition.kubejs.runtime_condition",
                translationKey(conditionSerializer.deserialize(conditionJson).getTooltip(Component.literal("bonus"), SkillBonus.Target.PLAYER))
        );
        assertEquals(
                "skill_bonus_multiplier.kubejs.runtime_multiplier",
                translationKey(multiplierSerializer.deserialize(multiplierJson).getTooltip(Component.literal("bonus"), SkillBonus.Target.PLAYER))
        );
        assertEquals(
                "value_provider.kubejs.runtime_value.multiplier",
                translationKey(valueSerializer.deserialize(valueJson).getMultiplierTooltip(SkillBonus.Target.PLAYER, 1.0F, Component.literal("bonus")))
        );
        assertEquals("skill_requirements.kubejs.runtime_requirement", translationKey(requirementSerializer.deserialize(requirementJson).getTooltip()));
    }

    @Test
    void customEventListenerSerializerProducesExecutableRuntimeListener() {
        AtomicBoolean applied = new AtomicBoolean(false);
        AtomicReference<Double> appliedMultiplier = new AtomicReference<>(0.0D);

        SkillEventListener.Serializer listenerSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTEventListenerSerializerBuilder(
                new ResourceLocation("kubejs", "runtime_listener")
        )
                .onSkillLearned(context -> context.apply(context.player(), 2.0D))
                .onAttack(context -> context.apply(context.enemy(), context.node().number("scale").orElse(0.0D)))
                .createObject();

        JsonObject listenerJson = new JsonObject();
        listenerJson.addProperty("type", "kubejs:runtime_listener");
        listenerJson.addProperty("scale", 3.5D);

        SkillEventListener listener = listenerSerializer.deserialize(listenerJson);
        assertEquals("event_listener.kubejs.runtime_listener", translationKey(listener.getTooltip(Component.literal("bonus"))));

        EventListenerBonus<?> bonus = new TrackingEventListenerBonus(listener, applied, appliedMultiplier, 1.0D);

        PSTEventListenerRuntimeBridge.dispatchSkillLearned(bonus, null, true);
        assertTrue(applied.get());
        assertEquals(2.0D, appliedMultiplier.get());

        applied.set(false);
        appliedMultiplier.set(0.0D);

        PSTEventListenerRuntimeBridge.dispatchAttack(null, null, null, bonus);
        assertTrue(applied.get());
        assertEquals(3.5D, appliedMultiplier.get());
    }

    @Test
    void runtimeSkillBonusTooltipComposesCustomPrefixFragmentsFromNestedNodes() {
        new com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillBonusSerializerBuilder(
                new ResourceLocation("kubejs", "runtime_bonus")
        )
                .effectText(Component.translatable("skill_bonus.kubejs.runtime_bonus"))
                .schema(schema -> schema.field("event_listener", field -> field.kind(PSTSchemaFieldKind.NODE).nodeTarget(PSTNodeFamily.EVENT_LISTENER)))
                .createObject();
        new com.pickaid.passivestjs.kubejs.registry.builder.PSTEventListenerSerializerBuilder(
                new ResourceLocation("kubejs", "runtime_listener")
        )
                .prefixText(Component.translatable("event_listener.kubejs.runtime_listener"))
                .schema(schema -> schema.field("player_condition", field -> field.kind(PSTSchemaFieldKind.NODE).nodeTarget(PSTNodeFamily.LIVING_CONDITION)))
                .createObject();
        new com.pickaid.passivestjs.kubejs.registry.builder.PSTLivingConditionSerializerBuilder(
                new ResourceLocation("kubejs", "runtime_condition")
        )
                .prefixText(Component.translatable("living_condition.kubejs.runtime_condition"))
                .createObject();

        JsonObject bonusJson = new JsonObject();
        bonusJson.addProperty("type", "kubejs:runtime_bonus");
        JsonObject listenerJson = new JsonObject();
        listenerJson.addProperty("type", "kubejs:runtime_listener");
        JsonObject conditionJson = new JsonObject();
        conditionJson.addProperty("type", "kubejs:runtime_condition");
        listenerJson.add("player_condition", conditionJson);
        bonusJson.add("event_listener", listenerJson);

        PSTRuntimeNode node = new PSTRuntimeNode(
                PSTSerializerMetadataIndex.find(PSTNodeFamily.SKILL_BONUS, new ResourceLocation("kubejs", "runtime_bonus"))
                        .orElseThrow(),
                bonusJson
        );
        JsonObject tooltipJson = Component.Serializer.toJsonTree(PSTRuntimeTooltipSupport.skillBonusTooltip(node)).getAsJsonObject();
        assertEquals(PSTTooltipComposer.RENDER_EFFECT_KEY, tooltipJson.get("translate").getAsString());

        JsonObject joinedPrefixes = tooltipJson.getAsJsonArray("with").get(0).getAsJsonObject();
        assertEquals(PSTTooltipComposer.JOIN_PREFIX_KEY, joinedPrefixes.get("translate").getAsString());
        assertEquals(
                "event_listener.kubejs.runtime_listener",
                joinedPrefixes.getAsJsonArray("with").get(0).getAsJsonObject().get("translate").getAsString()
        );
        assertEquals(
                "living_condition.kubejs.runtime_condition",
                joinedPrefixes.getAsJsonArray("with").get(1).getAsJsonObject().get("translate").getAsString()
        );
        assertEquals(
                "skill_bonus.kubejs.runtime_bonus",
                tooltipJson.getAsJsonArray("with").get(1).getAsJsonObject().get("translate").getAsString()
        );
    }

    @Test
    void placeholderItemBonusSerializerTooltipsUsePstStyleTranslationKeys() {
        ItemBonus.Serializer itemBonusSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTItemBonusSerializerBuilder(
                new ResourceLocation("kubejs", "runtime_item_bonus")
        ).createObject();

        ItemBonus<?> itemBonus = itemBonusSerializer.createDefaultInstance();
        java.util.concurrent.atomic.AtomicReference<Component> tooltip = new java.util.concurrent.atomic.AtomicReference<>();
        itemBonus.addTooltip(tooltip::set);

        assertEquals("item_bonus.kubejs.runtime_item_bonus", translationKey(tooltip.get()));
    }

    private static boolean hasPublicSignature(Class<?> type, String name, Class<?>... parameterTypes) {
        for (var method : type.getDeclaredMethods()) {
            if (java.lang.reflect.Modifier.isPublic(method.getModifiers())
                    && method.getName().equals(name)
                    && java.util.Arrays.equals(method.getParameterTypes(), parameterTypes)) {
                return true;
            }
        }
        return false;
    }

    private static String translationKey(Component component) {
        JsonObject json = Component.Serializer.toJsonTree(component).getAsJsonObject();
        return json.get("translate").getAsString();
    }

    private static final class TrackingEventListenerBonus implements EventListenerBonus<TrackingEventListenerBonus> {
        private final SkillEventListener eventListener;
        private final AtomicBoolean applied;
        private final AtomicReference<Double> appliedMultiplier;
        private final double multiplier;

        private TrackingEventListenerBonus(
                SkillEventListener eventListener,
                AtomicBoolean applied,
                AtomicReference<Double> appliedMultiplier,
                double multiplier
        ) {
            this.eventListener = eventListener;
            this.applied = applied;
            this.appliedMultiplier = appliedMultiplier;
            this.multiplier = multiplier;
        }

        @Override
        public boolean canMerge(SkillBonus<?> other) {
            return false;
        }

        @Override
        public SkillBonus<EventListenerBonus<TrackingEventListenerBonus>> merge(SkillBonus<?> other) {
            return copy();
        }

        @Override
        public TrackingEventListenerBonus copy() {
            return new TrackingEventListenerBonus(eventListener, applied, appliedMultiplier, multiplier);
        }

        @Override
        public TrackingEventListenerBonus multiply(double value) {
            return new TrackingEventListenerBonus(eventListener, applied, appliedMultiplier, multiplier * value);
        }

        @Override
        public SkillBonus.Serializer getSerializer() {
            return null;
        }

        @Override
        public MutableComponent getTooltip() {
            return Component.literal("tracking");
        }

        @Override
        public boolean isPositive() {
            return true;
        }

        @Override
        public void addEditorWidgets(
                daripher.skilltree.client.widget.editor.SkillTreeEditor editor,
                int row,
                java.util.function.Consumer<EventListenerBonus<TrackingEventListenerBonus>> consumer
        ) {
        }

        @Override
        public SkillEventListener getEventListener() {
            return eventListener;
        }

        @Override
        public void applyEffect(LivingEntity target) {
            applied.set(true);
            appliedMultiplier.set(multiplier);
        }
    }
}
