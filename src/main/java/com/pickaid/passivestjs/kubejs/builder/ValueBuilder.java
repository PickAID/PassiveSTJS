package com.pickaid.passivestjs.kubejs.builder;

public class ValueBuilder
        extends TypedJsonBuilder<ValueBuilder> {
    public ValueBuilder(String type) {
        super(type);
    }

    public ValueBuilder effectType(String value) {
        return string("effect_type", value);
    }

    public ValueBuilder attribute(String value) {
        return string("attribute", value);
    }

    public ValueBuilder percentage(boolean value) {
        return bool("percentage", value);
    }

    public ValueBuilder missing(boolean value) {
        return bool("missing", value);
    }
}
