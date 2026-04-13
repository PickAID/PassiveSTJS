package com.pickaid.passivestjs.kubejs.registry.builder;

import com.pickaid.passivestjs.kubejs.registry.AbstractPSTSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadata;
import com.pickaid.passivestjs.runtime.PSTCustomRuntimeContexts;
import com.pickaid.passivestjs.runtime.serializer.PSTCustomRuntimeSerializers;
import com.pickaid.passivestjs.runtime.tooltip.PSTTooltipFactory;
import daripher.skilltree.skill.requirement.SkillRequirement;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Predicate;

public class PSTSkillRequirementSerializerBuilder extends AbstractPSTSerializerBuilder<SkillRequirement.Serializer> {
    private Predicate<PSTCustomRuntimeContexts.SkillRequirementContext> tester = context -> false;

    public PSTSkillRequirementSerializerBuilder(ResourceLocation id) {
        super(id, PSTSerializerFamily.SKILL_REQUIREMENTS);
    }

    @Info(value = "Sets the executable runtime predicate for this custom skill requirement type.", params = {
            @Param(name = "tester", value = "Receives the runtime node and player, and must return whether the requirement is met.")
    })
    public PSTSkillRequirementSerializerBuilder test(Predicate<PSTCustomRuntimeContexts.SkillRequirementContext> tester) {
        this.tester = tester == null ? context -> false : tester;
        return this;
    }

    @Info(value = "Sets the translated requirement fragment shown for this custom skill requirement type.", params = {
            @Param(name = "component", value = "The component used as the requirement text.")
    })
    public PSTSkillRequirementSerializerBuilder requirementText(Component component) {
        tooltipBuilder().requirementText(component);
        return this;
    }

    @Info(value = "Sets the dynamic requirement fragment factory shown for this custom skill requirement type.", params = {
            @Param(name = "factory", value = "Receives the runtime tooltip context and returns the requirement component.")
    })
    public PSTSkillRequirementSerializerBuilder requirement(PSTTooltipFactory factory) {
        tooltipBuilder().requirement(factory);
        return this;
    }

    @Override
    protected SkillRequirement.Serializer createSerializer(PSTSerializerMetadata metadata) {
        metadata.schema();
        return PSTCustomRuntimeSerializers.skillRequirement(metadata, tester);
    }
}
