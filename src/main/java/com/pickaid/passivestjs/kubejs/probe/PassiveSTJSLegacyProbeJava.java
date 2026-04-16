package com.pickaid.passivestjs.kubejs.probe;

import com.pickaid.passivestjs.kubejs.Bindings;
import com.pickaid.passivestjs.kubejs.builder.BonusBuilder;
import com.pickaid.passivestjs.kubejs.builder.ConditionBuilder;
import com.pickaid.passivestjs.kubejs.builder.ItemBonusBuilder;
import com.pickaid.passivestjs.kubejs.builder.ListenerBuilder;
import com.pickaid.passivestjs.kubejs.builder.MultiplierBuilder;
import com.pickaid.passivestjs.kubejs.builder.PSTNodeWriter;
import com.pickaid.passivestjs.kubejs.builder.RequirementBuilder;
import com.pickaid.passivestjs.kubejs.builder.SkillBuilder;
import com.pickaid.passivestjs.kubejs.builder.SkillTreeBuilder;
import com.pickaid.passivestjs.kubejs.builder.ValueBuilder;
import com.pickaid.passivestjs.kubejs.event.SkillTreeContentEventJS;
import com.pickaid.passivestjs.kubejs.recipe.PSTWorkbenchItemBonusRecipeJS;
import com.pickaid.passivestjs.kubejs.recipe.builder.PSTAttributeSkillBonusBuilder;
import com.pickaid.passivestjs.kubejs.recipe.builder.PSTItemBonusListBuilder;
import com.pickaid.passivestjs.kubejs.recipe.builder.PSTSkillBonusItemBonusBuilder;
import com.pickaid.passivestjs.kubejs.registry.PSTDamageConditionRegistryView;
import com.pickaid.passivestjs.kubejs.registry.PSTEnchantmentConditionRegistryView;
import com.pickaid.passivestjs.kubejs.registry.PSTEventListenerRegistryView;
import com.pickaid.passivestjs.kubejs.registry.PSTItemBonusRegistryView;
import com.pickaid.passivestjs.kubejs.registry.PSTItemConditionRegistryView;
import com.pickaid.passivestjs.kubejs.registry.PSTLivingConditionRegistryView;
import com.pickaid.passivestjs.kubejs.registry.PSTLivingMultiplierRegistryView;
import com.pickaid.passivestjs.kubejs.registry.PSTNumericValueRegistryView;
import com.pickaid.passivestjs.kubejs.registry.PSTRegistriesApi;
import com.pickaid.passivestjs.kubejs.registry.PSTRegistryTypeHandle;
import com.pickaid.passivestjs.kubejs.registry.PSTRegistryView;
import com.pickaid.passivestjs.kubejs.registry.PSTSkillBonusRegistryView;
import com.pickaid.passivestjs.kubejs.registry.PSTSkillRequirementRegistryView;
import com.pickaid.passivestjs.kubejs.runtime.PSTBonusView;
import com.pickaid.passivestjs.kubejs.runtime.PSTListenerView;
import com.pickaid.passivestjs.kubejs.runtime.PSTPlayerView;
import com.pickaid.passivestjs.kubejs.runtime.PSTPlayerSkillView;
import com.pickaid.passivestjs.kubejs.runtime.PSTRequirementView;
import com.pickaid.passivestjs.kubejs.runtime.PSTSkillView;
import com.pickaid.passivestjs.kubejs.runtime.PSTTreeView;
import com.pickaid.passivestjs.runtime.PSTRuntimeNode;

import java.util.Set;

final class PassiveSTJSLegacyProbeJava {
    private static final Set<Class<?>> PROVIDED_CLASSES = Set.of(
            Bindings.class,
            SkillTreeContentEventJS.class,
            PSTPlayerView.class,
            PSTPlayerSkillView.class,
            PSTSkillView.class,
            PSTTreeView.class,
            PSTBonusView.class,
            PSTListenerView.class,
            PSTRequirementView.class,
            PSTRuntimeNode.class,
            SkillTreeBuilder.class,
            SkillBuilder.class,
            BonusBuilder.class,
            RequirementBuilder.class,
            MultiplierBuilder.class,
            ConditionBuilder.class,
            ListenerBuilder.class,
            ValueBuilder.class,
            ItemBonusBuilder.class,
            PSTNodeWriter.class,
            PSTWorkbenchItemBonusRecipeJS.class,
            PSTAttributeSkillBonusBuilder.class,
            PSTSkillBonusItemBonusBuilder.class,
            PSTItemBonusListBuilder.class,
            PSTRegistriesApi.class,
            PSTRegistryView.class,
            PSTRegistryTypeHandle.class,
            PSTSkillBonusRegistryView.class,
            PSTSkillRequirementRegistryView.class,
            PSTLivingConditionRegistryView.class,
            PSTLivingMultiplierRegistryView.class,
            PSTDamageConditionRegistryView.class,
            PSTItemConditionRegistryView.class,
            PSTEnchantmentConditionRegistryView.class,
            PSTEventListenerRegistryView.class,
            PSTNumericValueRegistryView.class,
            PSTItemBonusRegistryView.class
    );

    private PassiveSTJSLegacyProbeJava() {
    }

    static Set<Class<?>> providedClasses() {
        return PROVIDED_CLASSES;
    }
}
