package com.pickaid.passivestjs.kubejs.registry;

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
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class PSTRegistryTargets {
    public static final ResourceKey<Registry<SkillBonus.Serializer>> SKILL_BONUSES =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("skilltree", "skill_bonuses"));
    public static final ResourceKey<Registry<LivingMultiplier.Serializer>> LIVING_MULTIPLIERS =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("skilltree", "skill_bonus_multipliers"));
    public static final ResourceKey<Registry<LivingEntityPredicate.Serializer>> LIVING_CONDITIONS =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("skilltree", "living_conditions"));
    public static final ResourceKey<Registry<DamageCondition.Serializer>> DAMAGE_CONDITIONS =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("skilltree", "damage_conditions"));
    public static final ResourceKey<Registry<ItemStackPredicate.Serializer>> ITEM_CONDITIONS =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("skilltree", "item_conditions"));
    public static final ResourceKey<Registry<EnchantmentCondition.Serializer>> ENCHANTMENT_CONDITIONS =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("skilltree", "enchantment_conditions"));
    public static final ResourceKey<Registry<SkillEventListener.Serializer>> EVENT_LISTENERS =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("skilltree", "event_listeners"));
    public static final ResourceKey<Registry<FloatFunction.Serializer>> FLOAT_FUNCTIONS =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("skilltree", "numeric_value_providers"));
    public static final ResourceKey<Registry<SkillRequirement.Serializer>> SKILL_REQUIREMENTS =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("skilltree", "skill_requirements"));
    public static final ResourceKey<Registry<ItemBonus.Serializer>> ITEM_BONUSES =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("skilltree", "item_bonuses"));

    private PSTRegistryTargets() {
    }
}
