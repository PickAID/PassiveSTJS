package com.pickaid.passivestjs.kubejs.registry.builder;

import com.pickaid.passivestjs.kubejs.registry.AbstractPSTSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadata;
import com.pickaid.passivestjs.runtime.PSTCustomRuntimeContexts;
import com.pickaid.passivestjs.runtime.serializer.PSTCustomRuntimeSerializers;
import com.pickaid.passivestjs.runtime.tooltip.PSTTooltipFactory;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Predicate;

public class PSTItemConditionSerializerBuilder extends AbstractPSTSerializerBuilder<ItemStackPredicate.Serializer> {
    private Predicate<PSTCustomRuntimeContexts.ItemConditionContext> tester = context -> false;

    public PSTItemConditionSerializerBuilder(ResourceLocation id) {
        super(id, PSTSerializerFamily.ITEM_CONDITIONS);
    }

    @Info(value = "Sets the executable runtime predicate for this custom item condition type.", params = {
            @Param(name = "tester", value = "Receives the runtime node and item stack, and must return whether the condition passes.")
    })
    public PSTItemConditionSerializerBuilder test(Predicate<PSTCustomRuntimeContexts.ItemConditionContext> tester) {
        this.tester = tester == null ? context -> false : tester;
        return this;
    }

    @Info(value = "Sets the translated prefix fragment shown for this custom item condition type.", params = {
            @Param(name = "component", value = "The component used as the condition prefix text.")
    })
    public PSTItemConditionSerializerBuilder prefixText(Component component) {
        tooltipBuilder().prefixText(component);
        return this;
    }

    @Info(value = "Sets the dynamic prefix fragment factory shown for this custom item condition type.", params = {
            @Param(name = "factory", value = "Receives the runtime tooltip context and returns the condition prefix component.")
    })
    public PSTItemConditionSerializerBuilder prefix(PSTTooltipFactory factory) {
        tooltipBuilder().prefix(factory);
        return this;
    }

    @Override
    protected ItemStackPredicate.Serializer createSerializer(PSTSerializerMetadata metadata) {
        metadata.schema();
        return PSTCustomRuntimeSerializers.itemCondition(metadata, tester);
    }
}
