package com.pickaid.passivestjs.kubejs.registry.builder;

import com.pickaid.passivestjs.kubejs.registry.AbstractPSTSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadata;
import com.pickaid.passivestjs.runtime.PSTCustomRuntimeContexts;
import com.pickaid.passivestjs.runtime.serializer.PSTCustomRuntimeSerializers;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class PSTLivingMultiplierSerializerBuilder extends AbstractPSTSerializerBuilder<LivingMultiplier.Serializer> {
    private Function<PSTCustomRuntimeContexts.LivingMultiplierContext, Number> valueFactory = context -> 0.0D;

    public PSTLivingMultiplierSerializerBuilder(ResourceLocation id) {
        super(id, PSTSerializerFamily.LIVING_MULTIPLIERS);
    }

    @Info(value = "Sets the executable runtime value factory for this custom living multiplier type.", params = {
            @Param(name = "valueFactory", value = "Receives the runtime node and living entity, and must return the multiplier value.")
    })
    public PSTLivingMultiplierSerializerBuilder value(Function<PSTCustomRuntimeContexts.LivingMultiplierContext, Number> valueFactory) {
        this.valueFactory = valueFactory == null ? context -> 0.0D : valueFactory;
        return this;
    }

    @Override
    protected LivingMultiplier.Serializer createSerializer(PSTSerializerMetadata metadata) {
        metadata.schema();
        return PSTCustomRuntimeSerializers.livingMultiplier(metadata, valueFactory);
    }
}
