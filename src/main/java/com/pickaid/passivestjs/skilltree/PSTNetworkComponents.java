package com.pickaid.passivestjs.skilltree;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class PSTNetworkComponents {
    private PSTNetworkComponents() {
    }

    public static void write(FriendlyByteBuf buffer, Component component) {
        MutableComponent normalized = normalize(component);
        String json = Component.Serializer.toJson(normalized);
        buffer.writeUtf(json == null || json.isBlank() ? "{\"text\":\"\"}" : json);
    }

    public static MutableComponent read(FriendlyByteBuf buffer) {
        return normalize(Component.Serializer.fromJson(buffer.readUtf()));
    }

    private static MutableComponent normalize(Component component) {
        if (component == null) {
            return Component.empty();
        }
        String json = Component.Serializer.toJson(component);
        if (json == null || json.isBlank()) {
            return Component.empty();
        }
        MutableComponent parsed = Component.Serializer.fromJson(json);
        return parsed == null ? Component.empty() : parsed;
    }
}
