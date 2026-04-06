package com.pickaid.passivestjs.kubejs.content;

import com.pickaid.passivestjs.kubejs.builder.MultiplierBuilder;

public final class Multipliers {
    public static final Multipliers INSTANCE = new Multipliers();

    private Multipliers() {
    }

    public MultiplierBuilder none() {
        return new MultiplierBuilder("skilltree:none");
    }

    public MultiplierBuilder numericValue() {
        return new MultiplierBuilder("skilltree:numeric_value");
    }

    public MultiplierBuilder numericValue(Object valueProvider, Number divisor) {
        return numericValue().valueProvider(valueProvider).divisor(divisor);
    }
}
