package com.pickaid.passivestjs.mixin;

import com.pickaid.passivestjs.skilltree.PSTNetworkComponents;
import com.pickaid.passivestjs.skilltree.PSTContentTitles;
import daripher.skilltree.network.NetworkHelper;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NetworkHelper.class, remap = false)
abstract class NetworkHelperMixin {
    @Inject(method = "writePassiveSkill", at = @At("TAIL"), remap = false)
    private static void passivestjs$writeSkillTitle(FriendlyByteBuf buffer, PassiveSkill skill, CallbackInfo callbackInfo) {
        PSTContentTitles.write(buffer, PSTContentTitles.getSkillTitle(skill.getId()));
    }

    @Inject(method = "readPassiveSkill", at = @At("RETURN"), remap = false)
    private static void passivestjs$readSkillTitle(FriendlyByteBuf buffer, CallbackInfoReturnable<PassiveSkill> callbackInfo) {
        PassiveSkill skill = callbackInfo.getReturnValue();
        PSTContentTitles.putSkillTitle(skill.getId(), PSTContentTitles.read(buffer));
    }

    @Inject(method = "writePassiveSkillTree", at = @At("TAIL"), remap = false)
    private static void passivestjs$writeTreeTitle(FriendlyByteBuf buffer, PassiveSkillTree tree, CallbackInfo callbackInfo) {
        PSTContentTitles.write(buffer, PSTContentTitles.getTreeTitle(tree.getId()));
    }

    @Inject(method = "readPassiveSkillTree", at = @At("RETURN"), remap = false)
    private static void passivestjs$readTreeTitle(FriendlyByteBuf buffer, CallbackInfoReturnable<PassiveSkillTree> callbackInfo) {
        PassiveSkillTree tree = callbackInfo.getReturnValue();
        PSTContentTitles.putTreeTitle(tree.getId(), PSTContentTitles.read(buffer));
    }

    @Inject(method = "writeChatComponent", at = @At("HEAD"), cancellable = true, remap = false)
    private static void passivestjs$writeChatComponent(FriendlyByteBuf buffer, MutableComponent component, CallbackInfo callbackInfo) {
        PSTNetworkComponents.write(buffer, component);
        callbackInfo.cancel();
    }

    @Inject(method = "readChatComponent", at = @At("HEAD"), cancellable = true, remap = false)
    private static void passivestjs$readChatComponent(FriendlyByteBuf buffer, CallbackInfoReturnable<MutableComponent> callbackInfo) {
        callbackInfo.setReturnValue(PSTNetworkComponents.read(buffer));
    }
}
