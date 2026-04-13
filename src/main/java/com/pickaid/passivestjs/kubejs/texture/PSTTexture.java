package com.pickaid.passivestjs.kubejs.texture;

import com.pickaid.passivestjs.kubejs.builder.Ids;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PSTTexture(ResourceLocation location) {
    public PSTTexture {
        Objects.requireNonNull(location, "location");
    }

    public static PSTTexture of(ResourceLocation location) {
        return new PSTTexture(location);
    }

    public static PSTTexture parse(Object value) {
        if (value instanceof PSTTexture texture) {
            return texture;
        }
        return new PSTTexture(Ids.parse(value, "texture"));
    }

    public String id() {
        return location.toString();
    }

    @Override
    public String toString() {
        return id();
    }
}
