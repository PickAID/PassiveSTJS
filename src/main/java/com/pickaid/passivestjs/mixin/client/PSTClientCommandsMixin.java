package com.pickaid.passivestjs.mixin.client;

import com.mojang.brigadier.context.CommandContext;
import com.pickaid.passivestjs.skilltree.PSTEditorCompat;
import com.pickaid.passivestjs.config.PassiveSTJSCommonConfig;
import daripher.skilltree.client.command.PSTClientCommands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PSTClientCommands.class, remap = false)
abstract class PSTClientCommandsMixin {
    @Shadow
    private static ResourceLocation tree_to_display;

    @Shadow
    private static int timer;

    @Inject(method = "registerCommands", at = @At("HEAD"), cancellable = true, remap = false)
    private static void passivestjs$disableEditorCommandRegistration(RegisterClientCommandsEvent event, CallbackInfo callbackInfo) {
        if (PassiveSTJSCommonConfig.editorDisabled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "displaySkillTreeEditor", at = @At("HEAD"), cancellable = true, remap = false)
    private static void passivestjs$blockEditorCommand(CommandContext<CommandSourceStack> context, CallbackInfoReturnable<Integer> callbackInfo) {
        if (!PassiveSTJSCommonConfig.editorDisabled()) {
            return;
        }
        timer = 0;
        tree_to_display = null;
        PSTEditorCompat.showEditorDisabledMessage();
        callbackInfo.setReturnValue(0);
    }

    @Inject(method = "delayedCommandExecution", at = @At("HEAD"), cancellable = true, remap = false)
    private static void passivestjs$blockDelayedEditorScreen(TickEvent.ClientTickEvent event, CallbackInfo callbackInfo) {
        if (!PassiveSTJSCommonConfig.editorDisabled()) {
            return;
        }
        if (timer <= 0 && tree_to_display == null) {
            return;
        }
        timer = 0;
        tree_to_display = null;
        PSTEditorCompat.showEditorDisabledMessage();
        callbackInfo.cancel();
    }
}
