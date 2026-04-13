package com.pickaid.passivestjs.runtime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadata;
import com.pickaid.passivestjs.kubejs.content.JsonHelper;
import com.pickaid.passivestjs.schema.PSTNodeFamily;
import com.pickaid.passivestjs.schema.PSTSchemaField;
import com.pickaid.passivestjs.schema.PSTSchemaFieldKind;
import daripher.skilltree.data.serializers.Serializer;
import daripher.skilltree.init.PSTRegistries;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

import java.util.List;
import java.util.Optional;

public final class PSTRuntimeNode {
    private final PSTSerializerMetadata metadata;
    private final JsonObject payload;

    public PSTRuntimeNode(PSTSerializerMetadata metadata, JsonObject payload) {
        this.metadata = metadata;
        this.payload = JsonHelper.copy(payload == null ? new JsonObject() : payload).getAsJsonObject();
    }

    public PSTSerializerMetadata metadata() {
        return metadata;
    }

    public ResourceLocation id() {
        return metadata.id();
    }

    public PSTSerializerFamily family() {
        return metadata.family();
    }

    public JsonObject payload() {
        return JsonHelper.copy(payload).getAsJsonObject();
    }

    public JsonObject json() {
        JsonObject json = payload();
        json.addProperty("type", id().toString());
        return json;
    }

    public Optional<String> string(String key) {
        JsonElement element = payload.get(key);
        if (element == null || element.isJsonNull()) {
            return Optional.empty();
        }
        return Optional.of(element.getAsString());
    }

    public Optional<Integer> integer(String key) {
        JsonElement element = payload.get(key);
        if (element == null || element.isJsonNull()) {
            return Optional.empty();
        }
        return Optional.of(element.getAsInt());
    }

    public Optional<Double> number(String key) {
        JsonElement element = payload.get(key);
        if (element == null || element.isJsonNull()) {
            return Optional.empty();
        }
        return Optional.of(element.getAsDouble());
    }

    public Optional<Boolean> bool(String key) {
        JsonElement element = payload.get(key);
        if (element == null || element.isJsonNull()) {
            return Optional.empty();
        }
        return Optional.of(element.getAsBoolean());
    }

    public Optional<JsonObject> object(String key) {
        JsonElement element = payload.get(key);
        if (element == null || element.isJsonNull() || !element.isJsonObject()) {
            return Optional.empty();
        }
        return Optional.of(JsonHelper.copy(element).getAsJsonObject());
    }

    public SkillBonus<?> skillBonus(String key) {
        return deserializeNode(key, PSTSerializerFamily.SKILL_BONUSES);
    }

    public List<SkillBonus<?>> skillBonuses() {
        return deserializeSkillBonusChildren();
    }

    public LivingMultiplier livingMultiplier(String key) {
        return deserializeNode(key, PSTSerializerFamily.LIVING_MULTIPLIERS);
    }

    public LivingEntityPredicate livingCondition(String key) {
        return deserializeNode(key, PSTSerializerFamily.LIVING_CONDITIONS);
    }

    public DamageCondition damageCondition(String key) {
        return deserializeNode(key, PSTSerializerFamily.DAMAGE_CONDITIONS);
    }

    public ItemStackPredicate itemCondition(String key) {
        return deserializeNode(key, PSTSerializerFamily.ITEM_CONDITIONS);
    }

    public EnchantmentCondition enchantmentCondition(String key) {
        return deserializeNode(key, PSTSerializerFamily.ENCHANTMENT_CONDITIONS);
    }

    public SkillEventListener eventListener(String key) {
        return deserializeNode(key, PSTSerializerFamily.EVENT_LISTENERS);
    }

    public FloatFunction<?> valueProvider(String key) {
        return deserializeNode(key, PSTSerializerFamily.FLOAT_FUNCTIONS);
    }

    public SkillRequirement<?> skillRequirement(String key) {
        return deserializeNode(key, PSTSerializerFamily.SKILL_REQUIREMENTS);
    }

    public ItemBonus<?> itemBonus(String key) {
        return deserializeNode(key, PSTSerializerFamily.ITEM_BONUSES);
    }

    public List<ItemBonus<?>> itemBonuses() {
        return deserializeItemBonusChildren();
    }

    public boolean testLivingCondition(String key, LivingEntity entity, boolean defaultValue) {
        LivingEntityPredicate condition = livingCondition(key);
        return condition == null ? defaultValue : condition.test(entity);
    }

    public boolean testDamageCondition(String key, DamageSource damageSource, boolean defaultValue) {
        DamageCondition condition = damageCondition(key);
        return condition == null ? defaultValue : condition.met(damageSource);
    }

    public boolean testItemCondition(String key, ItemStack stack, boolean defaultValue) {
        ItemStackPredicate condition = itemCondition(key);
        return condition == null ? defaultValue : condition.test(stack);
    }

    public boolean testEnchantmentCondition(String key, EnchantmentCategory category, boolean defaultValue) {
        EnchantmentCondition condition = enchantmentCondition(key);
        return condition == null ? defaultValue : condition.met(category);
    }

    public boolean testSkillRequirement(String key, Player player, boolean defaultValue) {
        SkillRequirement<?> requirement = skillRequirement(key);
        return requirement == null ? defaultValue : requirement.test(player);
    }

    public double getLivingMultiplier(String key, LivingEntity entity, double defaultValue) {
        LivingMultiplier multiplier = livingMultiplier(key);
        return multiplier == null ? defaultValue : multiplier.getValue(entity);
    }

    public double getNumericValue(String key, LivingEntity entity, double defaultValue) {
        FloatFunction<?> provider = valueProvider(key);
        return provider == null ? defaultValue : provider.apply(entity);
    }

    @SuppressWarnings("unchecked")
    private <T> T deserializeNode(String key, PSTSerializerFamily family) {
        JsonObject json = object(key).orElse(null);
        if (json == null || !json.has("type") || !json.get("type").isJsonPrimitive()) {
            return null;
        }
        ResourceLocation typeId = ResourceLocation.tryParse(json.get("type").getAsString());
        if (typeId == null) {
            return null;
        }

        Object serializer = PSTSerializerObjectIndex.find(family, typeId).orElseGet(() -> registrySerializer(family, typeId));
        if (!(serializer instanceof Serializer<?> typedSerializer)) {
            return null;
        }
        return ((Serializer<T>) typedSerializer).deserialize(JsonHelper.copy(json).getAsJsonObject());
    }

    private List<SkillBonus<?>> deserializeSkillBonusChildren() {
        java.util.ArrayList<SkillBonus<?>> values = new java.util.ArrayList<>();
        for (Object value : deserializeChildNodes(
                PSTNodeFamily.SKILL_BONUS,
                PSTSerializerFamily.SKILL_BONUSES,
                SkillBonus.class
        )) {
            values.add((SkillBonus<?>) value);
        }
        return List.copyOf(values);
    }

    private List<ItemBonus<?>> deserializeItemBonusChildren() {
        java.util.ArrayList<ItemBonus<?>> values = new java.util.ArrayList<>();
        for (Object value : deserializeChildNodes(
                PSTNodeFamily.ITEM_BONUS,
                PSTSerializerFamily.ITEM_BONUSES,
                ItemBonus.class
        )) {
            values.add((ItemBonus<?>) value);
        }
        return List.copyOf(values);
    }

    private List<Object> deserializeChildNodes(PSTNodeFamily nodeFamily, PSTSerializerFamily family, Class<?> type) {
        if (metadata.schema().isEmpty()) {
            return List.of();
        }

        java.util.ArrayList<Object> values = new java.util.ArrayList<>();
        for (PSTSchemaField field : metadata.requireSchema().fields().values()) {
            if (field.nodeTarget() != nodeFamily) {
                continue;
            }
            if (field.kind() == PSTSchemaFieldKind.NODE) {
                Object value = deserializeNode(field.name(), family);
                if (type.isInstance(value)) {
                    values.add(value);
                }
                continue;
            }
            if (field.kind() != PSTSchemaFieldKind.NODE_LIST) {
                continue;
            }
            JsonElement element = payload.get(field.name());
            if (element == null || !element.isJsonArray()) {
                continue;
            }
            for (JsonElement child : element.getAsJsonArray()) {
                if (!child.isJsonObject()) {
                    continue;
                }
                Object value = deserializeTypedNode(child.getAsJsonObject(), family);
                if (type.isInstance(value)) {
                    values.add(value);
                }
            }
        }
        return List.copyOf(values);
    }

    @SuppressWarnings("unchecked")
    static <T> T deserializeTypedNode(JsonObject json, PSTSerializerFamily family) {
        if (!json.has("type") || !json.get("type").isJsonPrimitive()) {
            return null;
        }
        ResourceLocation typeId = ResourceLocation.tryParse(json.get("type").getAsString());
        if (typeId == null) {
            return null;
        }

        Object serializer = PSTSerializerObjectIndex.find(family, typeId).orElseGet(() -> registrySerializer(family, typeId));
        if (!(serializer instanceof Serializer<?> typedSerializer)) {
            return null;
        }
        return ((Serializer<T>) typedSerializer).deserialize(JsonHelper.copy(json).getAsJsonObject());
    }

    private static Object registrySerializer(PSTSerializerFamily family, ResourceLocation id) {
        try {
            return switch (family) {
                case SKILL_BONUSES -> registryValue(PSTRegistries.SKILL_BONUSES, id);
                case LIVING_MULTIPLIERS -> registryValue(PSTRegistries.LIVING_MULTIPLIERS, id);
                case LIVING_CONDITIONS -> registryValue(PSTRegistries.LIVING_CONDITIONS, id);
                case DAMAGE_CONDITIONS -> registryValue(PSTRegistries.DAMAGE_CONDITIONS, id);
                case ITEM_CONDITIONS -> registryValue(PSTRegistries.ITEM_CONDITIONS, id);
                case ENCHANTMENT_CONDITIONS -> registryValue(PSTRegistries.ENCHANTMENT_CONDITIONS, id);
                case EVENT_LISTENERS -> registryValue(PSTRegistries.EVENT_LISTENERS, id);
                case FLOAT_FUNCTIONS -> registryValue(PSTRegistries.FLOAT_FUNCTIONS, id);
                case SKILL_REQUIREMENTS -> registryValue(PSTRegistries.SKILL_REQUIREMENTS, id);
                case ITEM_BONUSES -> registryValue(PSTRegistries.ITEM_BONUSES, id);
            };
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Object registryValue(Supplier<? extends IForgeRegistry<?>> supplier, ResourceLocation id) {
        try {
            IForgeRegistry<?> registry = supplier == null ? null : supplier.get();
            return registry == null ? null : registry.getValue(id);
        } catch (Throwable ignored) {
            return null;
        }
    }
}
