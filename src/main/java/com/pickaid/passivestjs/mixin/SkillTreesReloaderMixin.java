package com.pickaid.passivestjs.mixin;

import com.pickaid.passivestjs.skilltree.PSTContentTitles;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SkillTreesReloader.class, remap = false)
abstract class SkillTreesReloaderMixin {
    @Inject(method = "loadFromByteBuf", at = @At("HEAD"), remap = false)
    private static void passivestjs$clearTreeTitles(FriendlyByteBuf buffer, CallbackInfo callbackInfo) {
        PSTContentTitles.clearTreeTitles();
    }
}
