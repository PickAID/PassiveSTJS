package com.pickaid.passivestjs.kubejs;

import com.pickaid.passivestjs.kubejs.recipe.PSTWorkbenchItemBonusRecipeJS;
import com.pickaid.passivestjs.kubejs.id.PSTSkillId;
import com.pickaid.passivestjs.kubejs.id.PSTAttributeId;
import com.pickaid.passivestjs.kubejs.id.PSTItemId;
import com.pickaid.passivestjs.kubejs.id.PSTItemTagId;
import com.pickaid.passivestjs.kubejs.id.PSTMobEffectId;
import com.pickaid.passivestjs.kubejs.id.PSTPotionId;
import com.pickaid.passivestjs.kubejs.id.PSTSkillBonusId;
import com.pickaid.passivestjs.kubejs.id.PSTSkillRequirementId;
import com.pickaid.passivestjs.kubejs.id.PSTStatTypeId;
import com.pickaid.passivestjs.kubejs.id.PSTTreeId;
import com.pickaid.passivestjs.kubejs.id.PSTLivingConditionId;
import com.pickaid.passivestjs.kubejs.id.PSTLivingMultiplierId;
import com.pickaid.passivestjs.kubejs.id.PSTDamageConditionId;
import com.pickaid.passivestjs.kubejs.id.PSTItemConditionId;
import com.pickaid.passivestjs.kubejs.id.PSTEnchantmentConditionId;
import com.pickaid.passivestjs.kubejs.id.PSTEventListenerId;
import com.pickaid.passivestjs.kubejs.id.PSTNumericValueProviderId;
import com.pickaid.passivestjs.kubejs.id.PSTItemBonusId;
import com.pickaid.passivestjs.kubejs.texture.PSTSkillFrameType;
import com.pickaid.passivestjs.kubejs.texture.PSTTexture;
import com.pickaid.passivestjs.kubejs.texture.PSTTooltipFrameType;
import com.pickaid.passivestjs.kubejs.type.PSTComparisonLogic;
import com.pickaid.passivestjs.kubejs.type.PSTEquipmentType;
import com.pickaid.passivestjs.kubejs.type.PSTSkillTarget;
import com.pickaid.passivestjs.kubejs.registry.PSTRegistryInfos;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;

public class PassiveSTJSKubePlugin extends KubeJSPlugin {
    @Override
    public void init() {
        PSTRegistryInfos.registerTypes();
    }

    @Override
    public void registerEvents() {
        PassiveSTJSKubeEvents.GROUP.register();
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("PassiveSkillTreeJS", Bindings.INSTANCE);
    }

    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
        event.register(PSTWorkbenchItemBonusRecipeJS.TYPE, PSTWorkbenchItemBonusRecipeJS.SCHEMA);
    }

    @Override
    public void registerTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {
        typeWrappers.registerSimple(PSTSkillId.class, PSTSkillId::parse);
        typeWrappers.registerSimple(PSTTreeId.class, PSTTreeId::parse);
        typeWrappers.registerSimple(PSTMobEffectId.class, PSTMobEffectId::parse);
        typeWrappers.registerSimple(PSTAttributeId.class, PSTAttributeId::parse);
        typeWrappers.registerSimple(PSTItemId.class, PSTItemId::parse);
        typeWrappers.registerSimple(PSTItemTagId.class, PSTItemTagId::parse);
        typeWrappers.registerSimple(PSTPotionId.class, PSTPotionId::parse);
        typeWrappers.registerSimple(PSTStatTypeId.class, PSTStatTypeId::parse);
        typeWrappers.registerSimple(PSTSkillBonusId.class, PSTSkillBonusId::parse);
        typeWrappers.registerSimple(PSTSkillRequirementId.class, PSTSkillRequirementId::parse);
        typeWrappers.registerSimple(PSTLivingConditionId.class, PSTLivingConditionId::parse);
        typeWrappers.registerSimple(PSTLivingMultiplierId.class, PSTLivingMultiplierId::parse);
        typeWrappers.registerSimple(PSTDamageConditionId.class, PSTDamageConditionId::parse);
        typeWrappers.registerSimple(PSTItemConditionId.class, PSTItemConditionId::parse);
        typeWrappers.registerSimple(PSTEnchantmentConditionId.class, PSTEnchantmentConditionId::parse);
        typeWrappers.registerSimple(PSTEventListenerId.class, PSTEventListenerId::parse);
        typeWrappers.registerSimple(PSTNumericValueProviderId.class, PSTNumericValueProviderId::parse);
        typeWrappers.registerSimple(PSTItemBonusId.class, PSTItemBonusId::parse);
        typeWrappers.registerSimple(PSTTexture.class, PSTTexture::parse);
        typeWrappers.registerSimple(PSTSkillFrameType.class, PSTSkillFrameType::parse);
        typeWrappers.registerSimple(PSTTooltipFrameType.class, PSTTooltipFrameType::parse);
        typeWrappers.registerSimple(PSTSkillTarget.class, PSTSkillTarget::parse);
        typeWrappers.registerSimple(PSTComparisonLogic.class, PSTComparisonLogic::parse);
        typeWrappers.registerSimple(PSTEquipmentType.class, PSTEquipmentType::parse);
    }

}
