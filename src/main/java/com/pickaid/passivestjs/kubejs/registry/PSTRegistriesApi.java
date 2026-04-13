package com.pickaid.passivestjs.kubejs.registry;

import com.pickaid.passivestjs.kubejs.builder.BonusBuilder;
import com.pickaid.passivestjs.kubejs.builder.ConditionBuilder;
import com.pickaid.passivestjs.kubejs.builder.ItemBonusBuilder;
import com.pickaid.passivestjs.kubejs.builder.ListenerBuilder;
import com.pickaid.passivestjs.kubejs.builder.MultiplierBuilder;
import com.pickaid.passivestjs.kubejs.builder.RequirementBuilder;
import com.pickaid.passivestjs.kubejs.builder.ValueBuilder;
import daripher.skilltree.init.PSTRegistries;
import dev.latvian.mods.kubejs.typings.Info;

public final class PSTRegistriesApi {
    public static final PSTRegistriesApi INSTANCE = new PSTRegistriesApi();
    private static final String SKILL_BONUSES_ID = "skilltree:skill_bonuses";
    private static final String LIVING_MULTIPLIERS_ID = "skilltree:skill_bonus_multipliers";
    private static final String LIVING_CONDITIONS_ID = "skilltree:living_conditions";
    private static final String DAMAGE_CONDITIONS_ID = "skilltree:damage_conditions";
    private static final String ITEM_CONDITIONS_ID = "skilltree:item_conditions";
    private static final String ENCHANTMENT_CONDITIONS_ID = "skilltree:enchantment_conditions";
    private static final String EVENT_LISTENERS_ID = "skilltree:event_listeners";
    private static final String FLOAT_FUNCTIONS_ID = "skilltree:numeric_value_providers";
    private static final String SKILL_REQUIREMENTS_ID = "skilltree:skill_requirements";
    private static final String ITEM_BONUSES_ID = "skilltree:item_bonuses";

    private final PSTSkillBonusRegistryView skillBonuses = new PSTSkillBonusRegistryView(
            SKILL_BONUSES_ID,
            PSTRegistries.SKILL_BONUSES,
            id -> new BonusBuilder(id.toString())
    );
    private final PSTLivingMultiplierRegistryView livingMultipliers = new PSTLivingMultiplierRegistryView(
            LIVING_MULTIPLIERS_ID,
            PSTRegistries.LIVING_MULTIPLIERS,
            id -> new MultiplierBuilder(id.toString())
    );
    private final PSTLivingConditionRegistryView livingConditions = new PSTLivingConditionRegistryView(
            LIVING_CONDITIONS_ID,
            PSTRegistries.LIVING_CONDITIONS,
            id -> new ConditionBuilder(id.toString())
    );
    private final PSTDamageConditionRegistryView damageConditions = new PSTDamageConditionRegistryView(
            DAMAGE_CONDITIONS_ID,
            PSTRegistries.DAMAGE_CONDITIONS,
            id -> new ConditionBuilder(id.toString())
    );
    private final PSTItemConditionRegistryView itemConditions = new PSTItemConditionRegistryView(
            ITEM_CONDITIONS_ID,
            PSTRegistries.ITEM_CONDITIONS,
            id -> new ConditionBuilder(id.toString())
    );
    private final PSTEnchantmentConditionRegistryView enchantmentConditions = new PSTEnchantmentConditionRegistryView(
            ENCHANTMENT_CONDITIONS_ID,
            PSTRegistries.ENCHANTMENT_CONDITIONS,
            id -> new ConditionBuilder(id.toString())
    );
    private final PSTEventListenerRegistryView eventListeners = new PSTEventListenerRegistryView(
            EVENT_LISTENERS_ID,
            PSTRegistries.EVENT_LISTENERS,
            id -> new ListenerBuilder(id.toString())
    );
    private final PSTNumericValueRegistryView numericValueProviders = new PSTNumericValueRegistryView(
            FLOAT_FUNCTIONS_ID,
            PSTRegistries.FLOAT_FUNCTIONS,
            id -> new ValueBuilder(id.toString())
    );
    private final PSTSkillRequirementRegistryView skillRequirements = new PSTSkillRequirementRegistryView(
            SKILL_REQUIREMENTS_ID,
            PSTRegistries.SKILL_REQUIREMENTS,
            id -> new RequirementBuilder(id.toString())
    );
    private final PSTItemBonusRegistryView itemBonuses = new PSTItemBonusRegistryView(
            ITEM_BONUSES_ID,
            PSTRegistries.ITEM_BONUSES,
            id -> new ItemBonusBuilder(id.toString())
    );

    private PSTRegistriesApi() {
    }

    @Info("Returns the real PST skill bonus registry view.")
    public PSTSkillBonusRegistryView skillBonuses() {
        return skillBonuses;
    }

    @Info("Returns the real PST living multiplier registry view.")
    public PSTLivingMultiplierRegistryView livingMultipliers() {
        return livingMultipliers;
    }

    @Info("Returns the real PST living condition registry view.")
    public PSTLivingConditionRegistryView livingConditions() {
        return livingConditions;
    }

    @Info("Returns the real PST damage condition registry view.")
    public PSTDamageConditionRegistryView damageConditions() {
        return damageConditions;
    }

    @Info("Returns the real PST item condition registry view.")
    public PSTItemConditionRegistryView itemConditions() {
        return itemConditions;
    }

    @Info("Returns the real PST enchantment condition registry view.")
    public PSTEnchantmentConditionRegistryView enchantmentConditions() {
        return enchantmentConditions;
    }

    @Info("Returns the real PST event listener registry view.")
    public PSTEventListenerRegistryView eventListeners() {
        return eventListeners;
    }

    @Info("Returns the real PST numeric value provider registry view.")
    public PSTNumericValueRegistryView numericValueProviders() {
        return numericValueProviders;
    }

    @Info("Returns the real PST skill requirement registry view.")
    public PSTSkillRequirementRegistryView skillRequirements() {
        return skillRequirements;
    }

    @Info("Returns the real PST item bonus registry view.")
    public PSTItemBonusRegistryView itemBonuses() {
        return itemBonuses;
    }
}
