package com.pickaid.passivestjs.mixin.client;

import com.pickaid.passivestjs.skilltree.PSTContentTitles;
import daripher.skilltree.client.widget.SkillTreeSelectionButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SkillTreeSelectionButton.class, remap = false)
abstract class SkillTreeSelectionButtonMixin {
    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void passivestjs$applyTreeTitle(int x, int y, int width, int height, ResourceLocation skillTreeId, CallbackInfo callbackInfo) {
        Component title = PSTContentTitles.getTreeTitle(skillTreeId);
        if (title != null) {
            ((net.minecraft.client.gui.components.Button) (Object) this).setMessage(title.copy());
        }
    }
}
