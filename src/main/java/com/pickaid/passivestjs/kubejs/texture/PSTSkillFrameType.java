package com.pickaid.passivestjs.kubejs.texture;

import dev.latvian.mods.rhino.Wrapper;

import java.util.Arrays;
import java.util.Locale;

public enum PSTSkillFrameType {
    LESSER("lesser", "skilltree:textures/icons/background/lesser.png"),
    CLASS("class", "skilltree:textures/icons/background/class.png"),
    NOTABLE("notable", "skilltree:textures/icons/background/notable.png"),
    KEYSTONE("keystone", "skilltree:textures/icons/background/keystone.png"),
    GATEWAY("gateway", "skilltree:textures/icons/background/gateway.png"),
    RECIPE("recipe", "skilltree:textures/icons/background/recipe.png");

    private final String id;
    private final PSTTexture texture;

    PSTSkillFrameType(String id, String texture) {
        this.id = id;
        this.texture = PSTTexture.parse(texture);
    }

    public static PSTSkillFrameType parse(Object value) {
        if (value instanceof PSTSkillFrameType frameType) {
            return frameType;
        }

        Object unwrapped = Wrapper.unwrapped(value);
        if (unwrapped instanceof CharSequence charSequence) {
            String normalized = charSequence.toString().trim().toLowerCase(Locale.ROOT);
            return Arrays.stream(values())
                    .filter(frameType -> frameType.id.equals(normalized))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown skill frame type: " + charSequence));
        }

        throw new IllegalArgumentException("Unsupported skill frame type: " + unwrapped);
    }

    public String id() {
        return id;
    }

    public PSTTexture texture() {
        return texture;
    }

    @Override
    public String toString() {
        return id;
    }
}
