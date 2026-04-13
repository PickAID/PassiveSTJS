package com.pickaid.passivestjs.kubejs.type;

import dev.latvian.mods.rhino.Wrapper;

import java.util.Arrays;
import java.util.Locale;

public enum PSTSkillTarget {
    PLAYER("player"),
    ENEMY("enemy");

    private final String id;

    PSTSkillTarget(String id) {
        this.id = id;
    }

    public static PSTSkillTarget parse(Object value) {
        if (value instanceof PSTSkillTarget target) {
            return target;
        }

        Object unwrapped = Wrapper.unwrapped(value);
        if (unwrapped instanceof CharSequence charSequence) {
            String normalized = charSequence.toString().trim().toLowerCase(Locale.ROOT);
            return Arrays.stream(values())
                    .filter(target -> target.id.equals(normalized))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown skill target: " + charSequence));
        }

        throw new IllegalArgumentException("Unsupported skill target: " + unwrapped);
    }

    public String id() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }
}
