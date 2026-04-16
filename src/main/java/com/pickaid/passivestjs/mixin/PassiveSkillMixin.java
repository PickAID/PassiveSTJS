package com.pickaid.passivestjs.mixin;

import com.pickaid.passivestjs.runtime.PSTEventListenerRuntimeBridge;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PassiveSkill.class, remap = false)
abstract class PassiveSkillMixin {
    @Inject(method = "learn", at = @At("TAIL"), remap = false)
    private void passivestjs$dispatchRuntimeLearn(ServerPlayer player, boolean firstTime, CallbackInfo callbackInfo) {
        PSTEventListenerRuntimeBridge.dispatchSkillLearned((PassiveSkill) (Object) this, player, firstTime);
    }

    @Inject(method = "remove", at = @At("TAIL"), remap = false)
    private void passivestjs$dispatchRuntimeRemove(ServerPlayer player, CallbackInfo callbackInfo) {
        PSTEventListenerRuntimeBridge.dispatchSkillRemoved((PassiveSkill) (Object) this, player);
    }
}
