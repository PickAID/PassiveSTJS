package com.pickaid.passivestjs.mixin;

import com.pickaid.passivestjs.runtime.PSTEventListenerRuntimeBridge;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SkillBonusHandler.class, remap = false)
abstract class SkillBonusHandlerMixin {
    @Inject(method = "applyEventListenerEffect(Lnet/minecraftforge/event/entity/living/LivingHurtEvent;)V", at = @At("TAIL"), remap = false)
    private static void passivestjs$dispatchRuntimeLivingHurt(LivingHurtEvent event, CallbackInfo callbackInfo) {
        PSTEventListenerRuntimeBridge.dispatchLivingHurt(event);
    }

    @Inject(method = "applyEventListenerEffect(Lnet/minecraftforge/event/entity/player/CriticalHitEvent;)V", at = @At("TAIL"), remap = false)
    private static void passivestjs$dispatchRuntimeCriticalHit(CriticalHitEvent event, CallbackInfo callbackInfo) {
        PSTEventListenerRuntimeBridge.dispatchCriticalHit(event);
    }

    @Inject(method = "applyEventListenerEffect(Lnet/minecraftforge/event/entity/living/ShieldBlockEvent;)V", at = @At("TAIL"), remap = false)
    private static void passivestjs$dispatchRuntimeShieldBlock(ShieldBlockEvent event, CallbackInfo callbackInfo) {
        PSTEventListenerRuntimeBridge.dispatchShieldBlock(event);
    }

    @Inject(method = "applyEventListenerEffect(Lnet/minecraftforge/event/entity/living/LivingEntityUseItemEvent$Finish;)V", at = @At("TAIL"), remap = false)
    private static void passivestjs$dispatchRuntimeItemUse(LivingEntityUseItemEvent.Finish event, CallbackInfo callbackInfo) {
        PSTEventListenerRuntimeBridge.dispatchItemUsed(event);
    }

    @Inject(method = "applyEventListenerEffect(Lnet/minecraftforge/event/entity/living/LivingDeathEvent;)V", at = @At("TAIL"), remap = false)
    private static void passivestjs$dispatchRuntimeKill(LivingDeathEvent event, CallbackInfo callbackInfo) {
        PSTEventListenerRuntimeBridge.dispatchLivingDeath(event);
    }

    @Inject(method = "tickSkillBonuses", at = @At("TAIL"), remap = false)
    private static void passivestjs$dispatchRuntimeTick(TickEvent.PlayerTickEvent event, CallbackInfo callbackInfo) {
        PSTEventListenerRuntimeBridge.dispatchPlayerTick(event);
    }
}
