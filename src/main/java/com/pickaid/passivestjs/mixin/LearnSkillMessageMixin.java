package com.pickaid.passivestjs.mixin;

import com.pickaid.passivestjs.skilltree.PSTSkillLearningRules;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.capability.skill.PlayerSkillsProvider;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.network.NetworkDispatcher;
import daripher.skilltree.network.message.LearnSkillMessage;
import daripher.skilltree.network.message.SyncPlayerSkillsMessage;
import daripher.skilltree.skill.PassiveSkill;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.function.Supplier;

@Mixin(value = LearnSkillMessage.class, remap = false)
abstract class LearnSkillMessageMixin {
    @Inject(method = "receive", at = @At("HEAD"), cancellable = true, remap = false)
    private static void passivestjs$validateServerLearning(LearnSkillMessage message, Supplier<NetworkEvent.Context> ctxSupplier, CallbackInfo callbackInfo) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.setPacketHandled(true);

        ServerPlayer player = ctx.getSender();
        Objects.requireNonNull(player);

        IPlayerSkills capability = PlayerSkillsProvider.get((Player) player);
        PassiveSkill skill = SkillsReloader.getSkillById(((LearnSkillMessageAccessor) message).passivestjs$getSkillId());
        Objects.requireNonNull(skill);

        if (PSTSkillLearningRules.canLearn(player, capability, skill) && capability.learnSkill(skill)) {
            skill.learn(player, true);
        }

        NetworkDispatcher.network_channel.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncPlayerSkillsMessage(player)
        );
        callbackInfo.cancel();
    }
}
