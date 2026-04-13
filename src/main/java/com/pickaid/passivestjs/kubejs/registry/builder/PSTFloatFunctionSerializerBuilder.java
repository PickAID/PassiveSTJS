package com.pickaid.passivestjs.kubejs.registry.builder;

import com.pickaid.passivestjs.kubejs.registry.AbstractPSTSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadata;
import com.pickaid.passivestjs.runtime.PSTCustomRuntimeContexts;
import com.pickaid.passivestjs.runtime.serializer.PSTCustomRuntimeSerializers;
import daripher.skilltree.skill.bonus.function.FloatFunction;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class PSTFloatFunctionSerializerBuilder extends AbstractPSTSerializerBuilder<FloatFunction.Serializer> {
    private Function<PSTCustomRuntimeContexts.FloatFunctionContext, Number> valueFactory = context -> 0.0D;

    public PSTFloatFunctionSerializerBuilder(ResourceLocation id) {
        super(id, PSTSerializerFamily.FLOAT_FUNCTIONS);
    }

    @Info(value = "Sets the executable runtime value factory for this custom numeric value provider type.", params = {
            @Param(name = "valueFactory", value = "Receives the runtime node and living entity, and must return the numeric value.")
    })
    public PSTFloatFunctionSerializerBuilder value(Function<PSTCustomRuntimeContexts.FloatFunctionContext, Number> valueFactory) {
        this.valueFactory = valueFactory == null ? context -> 0.0D : valueFactory;
        return this;
    }

    @Override
    protected FloatFunction.Serializer createSerializer(PSTSerializerMetadata metadata) {
        metadata.schema();
        return PSTCustomRuntimeSerializers.floatFunction(metadata, valueFactory);
    }
}
