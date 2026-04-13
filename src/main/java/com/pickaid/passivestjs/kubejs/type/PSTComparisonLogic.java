package com.pickaid.passivestjs.kubejs.type;

import dev.latvian.mods.rhino.Wrapper;

import java.util.Arrays;
import java.util.Locale;

public enum PSTComparisonLogic {
    MORE("more"),
    LESS("less"),
    EQUAL("equal"),
    AT_LEAST("at_least"),
    AT_MOST("at_most");

    private final String id;

    PSTComparisonLogic(String id) {
        this.id = id;
    }

    public static PSTComparisonLogic parse(Object value) {
        if (value instanceof PSTComparisonLogic logic) {
            return logic;
        }

        Object unwrapped = Wrapper.unwrapped(value);
        if (unwrapped instanceof CharSequence charSequence) {
            String normalized = charSequence.toString().trim().toLowerCase(Locale.ROOT);
            return Arrays.stream(values())
                    .filter(logic -> logic.id.equals(normalized))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown comparison logic: " + charSequence));
        }

        throw new IllegalArgumentException("Unsupported comparison logic: " + unwrapped);
    }

    public String id() {
        return id;
    }

    public String serializedName() {
        return name();
    }

    @Override
    public String toString() {
        return id;
    }
}
