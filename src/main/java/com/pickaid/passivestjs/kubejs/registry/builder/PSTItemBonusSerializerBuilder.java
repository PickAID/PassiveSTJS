package com.pickaid.passivestjs.kubejs.registry.builder;

import com.pickaid.passivestjs.kubejs.registry.AbstractPSTSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadata;
import com.pickaid.passivestjs.runtime.serializer.PSTCustomRuntimeSerializers;
import com.pickaid.passivestjs.runtime.tooltip.PSTTooltipFactory;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PSTItemBonusSerializerBuilder extends AbstractPSTSerializerBuilder<ItemBonus.Serializer> {
    public PSTItemBonusSerializerBuilder(ResourceLocation id) {
        super(id, PSTSerializerFamily.ITEM_BONUSES);
    }

    @Info(value = "Sets the translated effect fragment shown for this custom item bonus type.", params = {
            @Param(name = "component", value = "The component used as the item bonus text.")
    })
    public PSTItemBonusSerializerBuilder effectText(Component component) {
        tooltipBuilder().effectText(component);
        return this;
    }

    @Info(value = "Sets the dynamic effect fragment factory shown for this custom item bonus type.", params = {
            @Param(name = "factory", value = "Receives the runtime tooltip context and returns the item bonus component.")
    })
    public PSTItemBonusSerializerBuilder effect(PSTTooltipFactory factory) {
        tooltipBuilder().effect(factory);
        return this;
    }

    @Override
    protected ItemBonus.Serializer createSerializer(PSTSerializerMetadata metadata) {
        metadata.schema();
        return PSTCustomRuntimeSerializers.itemBonus(metadata);
    }
}
