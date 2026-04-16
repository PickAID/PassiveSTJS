package com.pickaid.passivestjs.skilltree;

import com.google.gson.JsonElement;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PSTNetworkComponentsTest {
    @Test
    void roundTripPreservesTranslatableDescriptionComponent() {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        MutableComponent original = Component.translatable(
                "kubejs.passivestjs.skill.root.desc.0",
                Component.literal("ARG")
        ).withStyle(style -> style.withItalic(true));

        PSTNetworkComponents.write(buffer, original);

        MutableComponent decoded = PSTNetworkComponents.read(buffer);
        JsonElement originalJson = Component.Serializer.toJsonTree(original);
        JsonElement decodedJson = Component.Serializer.toJsonTree(decoded);

        assertNotNull(decoded);
        assertEquals(originalJson, decodedJson);
    }
}
