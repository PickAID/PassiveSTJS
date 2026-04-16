package com.pickaid.passivestjs.mixin;

import com.pickaid.passivestjs.skilltree.PSTContentTitles;
import daripher.skilltree.data.reloader.SkillsReloader;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SkillsReloader.class, remap = false)
abstract class SkillsReloaderMixin {
    @Inject(method = "loadFromByteBuf", at = @At("HEAD"), remap = false)
    private static void passivestjs$clearSkillTitles(FriendlyByteBuf buffer, CallbackInfo callbackInfo) {
        PSTContentTitles.clearSkillTitles();
    }
}
