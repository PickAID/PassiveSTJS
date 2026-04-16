package com.pickaid.passivestjs.mixin.client;

import com.pickaid.passivestjs.skilltree.PSTContentTitles;
import daripher.skilltree.client.tooltip.TooltipHelper;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TooltipHelper.class, remap = false)
abstract class TooltipHelperMixin {
    @Inject(method = "getSkillTitle(Ldaripher/skilltree/skill/PassiveSkill;)Lnet/minecraft/network/chat/MutableComponent;", at = @At("HEAD"), cancellable = true, remap = false)
    private static void passivestjs$useManagedSkillTitle(PassiveSkill skill, CallbackInfoReturnable<MutableComponent> callbackInfo) {
        Component title = PSTContentTitles.getSkillTitle(skill.getId());
        if (title == null) {
            return;
        }
        callbackInfo.setReturnValue(title.copy().withStyle(TooltipHelper.getSkillTitleStyle(skill)));
    }
}
