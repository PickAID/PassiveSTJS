package com.pickaid.passivestjs.runtime;

import daripher.skilltree.skill.bonus.EventListenerBonus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public final class PSTCustomRuntimeContexts {
    private PSTCustomRuntimeContexts() {
    }

    public record SkillRequirementContext(PSTRuntimeNode node, Player player) {
    }

    public record LivingConditionContext(PSTRuntimeNode node, LivingEntity entity) {
    }

    public record DamageConditionContext(PSTRuntimeNode node, DamageSource damageSource) {
    }

    public record ItemConditionContext(PSTRuntimeNode node, ItemStack itemStack) {
    }

    public record EnchantmentConditionContext(PSTRuntimeNode node, EnchantmentCategory category) {
    }

    public record LivingMultiplierContext(PSTRuntimeNode node, LivingEntity entity) {
    }

    public record FloatFunctionContext(PSTRuntimeNode node, LivingEntity entity) {
    }

    public record SkillBonusLearnContext(PSTRuntimeNode node, ServerPlayer player, boolean notifyClient) {
    }

    public record SkillBonusRemoveContext(PSTRuntimeNode node, ServerPlayer player) {
    }

    public record SkillBonusApplyContext(
            PSTRuntimeNode node,
            LivingEntity target,
            double multiplier,
            DamageSource damageSource
    ) {
    }

    public interface EventListenerDispatchContext {
        PSTRuntimeNode node();

        EventListenerBonus<?> bonus();

        default void apply(LivingEntity target) {
            apply(target, 1.0D);
        }

        default void apply(LivingEntity target, Number multiplier) {
            EventListenerBonus<?> runtimeBonus = bonus();
            if (runtimeBonus == null) {
                return;
            }
            double factor = multiplier == null ? 1.0D : multiplier.doubleValue();
            EventListenerBonus<?> scaledBonus = (EventListenerBonus<?>) runtimeBonus.copy().multiply(factor);
            scaledBonus.applyEffect(target);
        }
    }

    public record SkillLearnedListenerContext(
            PSTRuntimeNode node,
            EventListenerBonus<?> bonus,
            ServerPlayer player,
            boolean firstTime
    ) implements EventListenerDispatchContext {
    }

    public record SkillRemovedListenerContext(
            PSTRuntimeNode node,
            EventListenerBonus<?> bonus,
            ServerPlayer player
    ) implements EventListenerDispatchContext {
    }

    public record TickListenerContext(
            PSTRuntimeNode node,
            EventListenerBonus<?> bonus,
            ServerPlayer player
    ) implements EventListenerDispatchContext {
        public int tickCount() {
            return player == null ? 0 : player.tickCount;
        }
    }

    public record AttackListenerContext(
            PSTRuntimeNode node,
            EventListenerBonus<?> bonus,
            Player player,
            LivingEntity enemy,
            DamageSource damageSource
    ) implements EventListenerDispatchContext {
    }

    public record DamageTakenListenerContext(
            PSTRuntimeNode node,
            EventListenerBonus<?> bonus,
            Player player,
            LivingEntity attacker,
            DamageSource damageSource
    ) implements EventListenerDispatchContext {
    }

    public record CriticalHitListenerContext(
            PSTRuntimeNode node,
            EventListenerBonus<?> bonus,
            Player player,
            LivingEntity enemy
    ) implements EventListenerDispatchContext {
    }

    public record BlockListenerContext(
            PSTRuntimeNode node,
            EventListenerBonus<?> bonus,
            Player player,
            LivingEntity attacker,
            DamageSource damageSource
    ) implements EventListenerDispatchContext {
    }

    public record ItemUsedListenerContext(
            PSTRuntimeNode node,
            EventListenerBonus<?> bonus,
            Player player,
            ItemStack itemStack
    ) implements EventListenerDispatchContext {
    }

    public record KillListenerContext(
            PSTRuntimeNode node,
            EventListenerBonus<?> bonus,
            Player player,
            LivingEntity enemy,
            DamageSource damageSource
    ) implements EventListenerDispatchContext {
    }
}
