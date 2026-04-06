package com.pickaid.passivestjs.kubejs.builder;

public class MultiplierBuilder
        extends TypedJsonBuilder<MultiplierBuilder> {
    public MultiplierBuilder(String type) {
        super(type);
    }

    public MultiplierBuilder valueProvider(Object value) {
        return optionalRaw("value_provider", value);
    }

    public MultiplierBuilder divisor(Number value) {
        return number("divisor", value);
    }
}
