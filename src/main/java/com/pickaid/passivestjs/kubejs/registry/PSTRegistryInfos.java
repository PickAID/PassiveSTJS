package com.pickaid.passivestjs.kubejs.registry;

import com.pickaid.passivestjs.kubejs.registry.builder.PSTDamageConditionSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTEnchantmentConditionSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTEventListenerSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTFloatFunctionSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTItemBonusSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTItemConditionSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTLivingConditionSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTLivingMultiplierSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillBonusSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillRequirementSerializerBuilder;
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
import dev.latvian.mods.kubejs.registry.RegistryInfo;

public final class PSTRegistryInfos {
    public static final String DEFAULT_TYPE = "default";

    public static final RegistryInfo<SkillBonus.Serializer> SKILL_BONUSES =
            RegistryInfo.of(PSTRegistryTargets.SKILL_BONUSES, SkillBonus.Serializer.class);
    public static final RegistryInfo<LivingMultiplier.Serializer> LIVING_MULTIPLIERS =
            RegistryInfo.of(PSTRegistryTargets.LIVING_MULTIPLIERS, LivingMultiplier.Serializer.class);
    public static final RegistryInfo<LivingEntityPredicate.Serializer> LIVING_CONDITIONS =
            RegistryInfo.of(PSTRegistryTargets.LIVING_CONDITIONS, LivingEntityPredicate.Serializer.class);
    public static final RegistryInfo<DamageCondition.Serializer> DAMAGE_CONDITIONS =
            RegistryInfo.of(PSTRegistryTargets.DAMAGE_CONDITIONS, DamageCondition.Serializer.class);
    public static final RegistryInfo<ItemStackPredicate.Serializer> ITEM_CONDITIONS =
            RegistryInfo.of(PSTRegistryTargets.ITEM_CONDITIONS, ItemStackPredicate.Serializer.class);
    public static final RegistryInfo<EnchantmentCondition.Serializer> ENCHANTMENT_CONDITIONS =
            RegistryInfo.of(PSTRegistryTargets.ENCHANTMENT_CONDITIONS, EnchantmentCondition.Serializer.class);
    public static final RegistryInfo<SkillEventListener.Serializer> EVENT_LISTENERS =
            RegistryInfo.of(PSTRegistryTargets.EVENT_LISTENERS, SkillEventListener.Serializer.class);
    public static final RegistryInfo<FloatFunction.Serializer> FLOAT_FUNCTIONS =
            RegistryInfo.of(PSTRegistryTargets.FLOAT_FUNCTIONS, FloatFunction.Serializer.class);
    public static final RegistryInfo<SkillRequirement.Serializer> SKILL_REQUIREMENTS =
            RegistryInfo.of(PSTRegistryTargets.SKILL_REQUIREMENTS, SkillRequirement.Serializer.class);
    public static final RegistryInfo<ItemBonus.Serializer> ITEM_BONUSES =
            RegistryInfo.of(PSTRegistryTargets.ITEM_BONUSES, ItemBonus.Serializer.class);

    private static boolean registered;

    private PSTRegistryInfos() {
    }

    public static void registerTypes() {
        if (registered) {
            return;
        }
        registered = true;

        SKILL_BONUSES.addType(DEFAULT_TYPE, PSTSkillBonusSerializerBuilder.class, PSTSkillBonusSerializerBuilder::new, true);
        LIVING_MULTIPLIERS.addType(
                DEFAULT_TYPE,
                PSTLivingMultiplierSerializerBuilder.class,
                PSTLivingMultiplierSerializerBuilder::new,
                true
        );
        LIVING_CONDITIONS.addType(
                DEFAULT_TYPE,
                PSTLivingConditionSerializerBuilder.class,
                PSTLivingConditionSerializerBuilder::new,
                true
        );
        DAMAGE_CONDITIONS.addType(
                DEFAULT_TYPE,
                PSTDamageConditionSerializerBuilder.class,
                PSTDamageConditionSerializerBuilder::new,
                true
        );
        ITEM_CONDITIONS.addType(
                DEFAULT_TYPE,
                PSTItemConditionSerializerBuilder.class,
                PSTItemConditionSerializerBuilder::new,
                true
        );
        ENCHANTMENT_CONDITIONS.addType(
                DEFAULT_TYPE,
                PSTEnchantmentConditionSerializerBuilder.class,
                PSTEnchantmentConditionSerializerBuilder::new,
                true
        );
        EVENT_LISTENERS.addType(
                DEFAULT_TYPE,
                PSTEventListenerSerializerBuilder.class,
                PSTEventListenerSerializerBuilder::new,
                true
        );
        FLOAT_FUNCTIONS.addType(
                DEFAULT_TYPE,
                PSTFloatFunctionSerializerBuilder.class,
                PSTFloatFunctionSerializerBuilder::new,
                true
        );
        SKILL_REQUIREMENTS.addType(
                DEFAULT_TYPE,
                PSTSkillRequirementSerializerBuilder.class,
                PSTSkillRequirementSerializerBuilder::new,
                true
        );
        ITEM_BONUSES.addType(DEFAULT_TYPE, PSTItemBonusSerializerBuilder.class, PSTItemBonusSerializerBuilder::new, true);
    }
}
