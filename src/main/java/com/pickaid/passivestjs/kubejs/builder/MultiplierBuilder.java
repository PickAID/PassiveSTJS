package com.pickaid.passivestjs.kubejs.builder;

import com.pickaid.passivestjs.kubejs.id.PSTNumericValueProviderId;
import com.pickaid.passivestjs.schema.PSTNodeFamily;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.function.Consumer;

public class MultiplierBuilder
        extends TypedJsonBuilder<MultiplierBuilder> {
    public MultiplierBuilder(String type) {
        super(type);
    }

    @HideFromJS
    public MultiplierBuilder valueProvider(Object value) {
        return optionalRaw("value_provider", value);
    }

    @Info("Adds a prebuilt numeric value provider builder.")
    public MultiplierBuilder valueProvider(ValueBuilder value) {
        return valueProvider((Object) value);
    }

    @Info(value = "Configures value provider via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The numeric value type id."),
            @Param(name = "consumer", value = "The callback that configures the created numeric value builder.")
    })
    public MultiplierBuilder valueProvider(PSTNumericValueProviderId typeId, Consumer<ValueBuilder> consumer) {
        ValueBuilder builder = new ValueBuilder(PSTNumericValueProviderId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return valueProvider(builder);
    }

    @Info(value = "Configures value provider through the schema writer for the given numeric value id.", params = {
            @Param(name = "typeId", value = "The numeric value type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields.")
    })
    public MultiplierBuilder valueProviderSchema(PSTNumericValueProviderId typeId, Consumer<PSTNodeWriter> consumer) {
        PSTNodeWriter writer = new PSTNodeWriter(PSTNodeFamily.NUMERIC_VALUE, PSTNumericValueProviderId.parse(typeId).location());
        if (consumer != null) {
            consumer.accept(writer);
        }
        return valueProvider(writer);
    }

    public MultiplierBuilder divisor(Number value) {
        return number("divisor", value);
    }
}
