package com.pickaid.passivestjs.kubejs.builder;

import dev.latvian.mods.rhino.Wrapper;
import net.minecraft.resources.ResourceLocation;

public final class Ids {
    private Ids() {
    }

    public static ResourceLocation parse(Object value, String fieldName) {
        Object unwrapped = Wrapper.unwrapped(value);
        if (unwrapped instanceof ResourceLocation resourceLocation) {
            return resourceLocation;
        }

        if (unwrapped instanceof CharSequence charSequence) {
            String raw = charSequence.toString().trim();
            if (raw.isEmpty()) {
                throw new IllegalArgumentException(fieldName + " can't be empty");
            }

            String normalized = raw.contains(":") ? raw : "kubejs:" + raw;
            ResourceLocation parsed = ResourceLocation.tryParse(normalized);
            if (parsed == null) {
                throw new IllegalArgumentException("Invalid resource location for " + fieldName + ": " + raw);
            }

            return parsed;
        }

        throw new IllegalArgumentException("Unsupported resource location for " + fieldName + ": " + unwrapped);
    }

    public static String stringify(Object value, String fieldName) {
        return parse(value, fieldName).toString();
    }
}
