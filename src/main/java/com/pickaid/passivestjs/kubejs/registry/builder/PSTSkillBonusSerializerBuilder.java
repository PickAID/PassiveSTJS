package com.pickaid.passivestjs.kubejs.registry.builder;

import com.pickaid.passivestjs.kubejs.registry.AbstractPSTSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadata;
import com.pickaid.passivestjs.runtime.PSTCustomRuntimeContexts;
import com.pickaid.passivestjs.runtime.serializer.PSTCustomRuntimeSerializers;
import com.pickaid.passivestjs.runtime.tooltip.PSTTooltipFactory;
import daripher.skilltree.skill.bonus.SkillBonus;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class PSTSkillBonusSerializerBuilder extends AbstractPSTSerializerBuilder<SkillBonus.Serializer> {
    private Consumer<PSTCustomRuntimeContexts.SkillBonusLearnContext> onLearn = context -> {
    };
    private Consumer<PSTCustomRuntimeContexts.SkillBonusRemoveContext> onRemove = context -> {
    };
    private Consumer<PSTCustomRuntimeContexts.SkillBonusApplyContext> onApply = context -> {
    };

    public PSTSkillBonusSerializerBuilder(ResourceLocation id) {
        super(id, PSTSerializerFamily.SKILL_BONUSES);
    }

    @Info(value = "Runs when this custom skill bonus is learned by a player.", params = {
            @Param(name = "consumer", value = "Receives the runtime node, server player, and notify flag.")
    })
    public PSTSkillBonusSerializerBuilder onLearn(Consumer<PSTCustomRuntimeContexts.SkillBonusLearnContext> consumer) {
        this.onLearn = consumer == null ? context -> {
        } : consumer;
        return this;
    }

    @Info(value = "Runs when this custom skill bonus is removed from a player.", params = {
            @Param(name = "consumer", value = "Receives the runtime node and server player.")
    })
    public PSTSkillBonusSerializerBuilder onRemove(Consumer<PSTCustomRuntimeContexts.SkillBonusRemoveContext> consumer) {
        this.onRemove = consumer == null ? context -> {
        } : consumer;
        return this;
    }

    @Info(value = "Runs when this custom skill bonus applies its effect to a target.", params = {
            @Param(name = "consumer", value = "Receives the runtime node, resolved target, and multiplied effect factor.")
    })
    public PSTSkillBonusSerializerBuilder onApply(Consumer<PSTCustomRuntimeContexts.SkillBonusApplyContext> consumer) {
        this.onApply = consumer == null ? context -> {
        } : consumer;
        return this;
    }

    @Info(value = "Sets the translated effect fragment shown for this custom skill bonus type.", params = {
            @Param(name = "component", value = "The component used as the bonus effect text.")
    })
    public PSTSkillBonusSerializerBuilder effectText(Component component) {
        tooltipBuilder().effectText(component);
        return this;
    }

    @Info(value = "Sets the dynamic effect fragment factory shown for this custom skill bonus type.", params = {
            @Param(name = "factory", value = "Receives the runtime tooltip context and returns the effect text component.")
    })
    public PSTSkillBonusSerializerBuilder effect(PSTTooltipFactory factory) {
        tooltipBuilder().effect(factory);
        return this;
    }

    @Override
    protected SkillBonus.Serializer createSerializer(PSTSerializerMetadata metadata) {
        metadata.schema();
        return PSTCustomRuntimeSerializers.skillBonus(metadata, onLearn, onRemove, onApply);
    }
}
