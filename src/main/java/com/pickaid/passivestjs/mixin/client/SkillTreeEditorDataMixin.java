package com.pickaid.passivestjs.mixin.client;

import com.pickaid.passivestjs.skilltree.PSTEditorCompat;
import daripher.skilltree.client.data.SkillTreeEditorData;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(value = SkillTreeEditorData.class, remap = false)
abstract class SkillTreeEditorDataMixin {
    @Inject(method = "getSkillSaveFile", at = @At("RETURN"), cancellable = true, remap = false)
    private static void passivestjs$ensureNestedSkillFolders(ResourceLocation id, CallbackInfoReturnable<File> callbackInfo) {
        File file = callbackInfo.getReturnValue();
        PSTEditorCompat.ensureParentDirectories(file);
        callbackInfo.setReturnValue(file);
    }
}
