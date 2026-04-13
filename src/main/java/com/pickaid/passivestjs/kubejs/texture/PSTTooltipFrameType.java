package com.pickaid.passivestjs.kubejs.texture;

import dev.latvian.mods.rhino.Wrapper;

import java.util.Arrays;
import java.util.Locale;

public enum PSTTooltipFrameType {
    LESSER("lesser", "skilltree:textures/tooltip/lesser.png"),
    NOTABLE("notable", "skilltree:textures/tooltip/notable.png"),
    KEYSTONE("keystone", "skilltree:textures/tooltip/keystone.png"),
    GATEWAY("gateway", "skilltree:textures/tooltip/gateway.png");

    private final String id;
    private final PSTTexture texture;

    PSTTooltipFrameType(String id, String texture) {
        this.id = id;
        this.texture = PSTTexture.parse(texture);
    }

    public static PSTTooltipFrameType parse(Object value) {
        if (value instanceof PSTTooltipFrameType frameType) {
            return frameType;
        }

        Object unwrapped = Wrapper.unwrapped(value);
        if (unwrapped instanceof CharSequence charSequence) {
            String normalized = charSequence.toString().trim().toLowerCase(Locale.ROOT);
            return Arrays.stream(values())
                    .filter(frameType -> frameType.id.equals(normalized))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown tooltip frame type: " + charSequence));
        }

        throw new IllegalArgumentException("Unsupported tooltip frame type: " + unwrapped);
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
