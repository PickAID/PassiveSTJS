package com.pickaid.passivestjs.kubejs.texture;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PSTTextureWrapperTest {
    @Test
    void textureWrapperAcceptsResourceLocationsAndStrings() {
        PSTTexture fromString = PSTTexture.parse("minecraft:textures/item/amethyst_shard.png");
        PSTTexture fromLocation = PSTTexture.parse(ResourceLocation.fromNamespaceAndPath("skilltree", "textures/tooltip/lesser.png"));

        assertEquals("minecraft:textures/item/amethyst_shard.png", fromString.id());
        assertEquals("skilltree:textures/tooltip/lesser.png", fromLocation.id());
    }

    @Test
    void skillFrameTypeMapsToBuiltinBackgroundTextures() {
        assertEquals(
                "skilltree:textures/icons/background/notable.png",
                PSTSkillFrameType.parse("notable").texture().id()
        );
        assertEquals(
                "skilltree:textures/icons/background/class.png",
                PSTSkillFrameType.parse("class").texture().id()
        );
    }

    @Test
    void tooltipFrameTypeMapsToBuiltinTooltipTextures() {
        assertEquals(
                "skilltree:textures/tooltip/gateway.png",
                PSTTooltipFrameType.parse("gateway").texture().id()
        );
        assertEquals(
                "skilltree:textures/tooltip/lesser.png",
                PSTTooltipFrameType.parse("lesser").texture().id()
        );
    }

    @Test
    void invalidPresetValuesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> PSTSkillFrameType.parse("invalid"));
        assertThrows(IllegalArgumentException.class, () -> PSTTooltipFrameType.parse("class"));
    }
}
