package com.pickaid.passivestjs.kubejs.builder;

import com.pickaid.passivestjs.kubejs.id.PSTAttributeId;
import com.pickaid.passivestjs.kubejs.id.PSTMobEffectId;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.HideFromJS;

public class ValueBuilder
        extends TypedJsonBuilder<ValueBuilder> {
    public ValueBuilder(String type) {
        super(type);
    }

    @HideFromJS
    public ValueBuilder effectType(String value) {
        return effectType(PSTMobEffectId.parse(value));
    }

    @Info(value = "Sets the mob effect id for this numeric value provider.", params = {
            @Param(name = "effectId", value = "The mob effect id.")
    })
    public ValueBuilder effectType(PSTMobEffectId effectId) {
        return string("effect_type", effectId.id());
    }

    @HideFromJS
    public ValueBuilder attribute(String value) {
        return attribute(PSTAttributeId.parse(value));
    }

    @Info(value = "Sets the attribute id for this numeric value provider.", params = {
            @Param(name = "attributeId", value = "The attribute id.")
    })
    public ValueBuilder attribute(PSTAttributeId attributeId) {
        return string("attribute", attributeId.id());
    }

    public ValueBuilder percentage(boolean value) {
        return bool("percentage", value);
    }

    public ValueBuilder missing(boolean value) {
        return bool("missing", value);
    }
}
