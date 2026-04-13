package com.pickaid.passivestjs.runtime;

import com.mojang.logging.LogUtils;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.SkillBonusHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import org.slf4j.Logger;

import java.util.List;

public final class PSTEventListenerRuntimeBridge {
    private static final Logger LOGGER = LogUtils.getLogger();

    private PSTEventListenerRuntimeBridge() {
    }

    public static void dispatchSkillLearned(PassiveSkill skill, ServerPlayer player, boolean firstTime) {
        if (skill == null) {
            return;
        }
        for (var bonus : skill.getBonuses()) {
            if (bonus instanceof EventListenerBonus<?> eventBonus) {
                dispatchSkillLearned(eventBonus, player, firstTime);
            }
        }
    }

    public static void dispatchSkillRemoved(PassiveSkill skill, ServerPlayer player) {
        if (skill == null) {
            return;
        }
        for (var bonus : skill.getBonuses()) {
            if (bonus instanceof EventListenerBonus<?> eventBonus) {
                dispatchSkillRemoved(eventBonus, player);
            }
        }
    }

    public static void dispatchSkillLearned(EventListenerBonus<?> bonus, ServerPlayer player, boolean firstTime) {
        if (!firstTime) {
            return;
        }
        PSTCustomRuntimeEventListener listener = runtimeListener(bonus);
        if (listener == null) {
            return;
        }
        listener.onSkillLearned(new PSTCustomRuntimeContexts.SkillLearnedListenerContext(node(listener), bonus, player, true));
    }

    public static void dispatchSkillRemoved(EventListenerBonus<?> bonus, ServerPlayer player) {
        PSTCustomRuntimeEventListener listener = runtimeListener(bonus);
        if (listener == null) {
            return;
        }
        listener.onSkillRemoved(new PSTCustomRuntimeContexts.SkillRemovedListenerContext(node(listener), bonus, player));
    }

    public static void dispatchTick(EventListenerBonus<?> bonus, ServerPlayer player) {
        PSTCustomRuntimeEventListener listener = runtimeListener(bonus);
        if (listener == null) {
            return;
        }
        if (player.tickCount % 20 == 0) {
            LOGGER.info(
                    "[PassiveSTJS Runtime Debug] listenerPayload player={} bonusType={} payload={}",
                    player.getScoreboardName(),
                    bonus.getClass().getName(),
                    listener.node().payload()
            );
        }
        listener.onTick(new PSTCustomRuntimeContexts.TickListenerContext(node(listener), bonus, player));
    }

    public static void dispatchAttack(Player player, LivingEntity enemy, DamageSource damageSource, EventListenerBonus<?> bonus) {
        PSTCustomRuntimeEventListener listener = runtimeListener(bonus);
        if (listener == null) {
            return;
        }
        listener.onAttack(new PSTCustomRuntimeContexts.AttackListenerContext(node(listener), bonus, player, enemy, damageSource));
    }

    public static void dispatchDamageTaken(Player player, LivingEntity attacker, DamageSource damageSource, EventListenerBonus<?> bonus) {
        PSTCustomRuntimeEventListener listener = runtimeListener(bonus);
        if (listener == null) {
            return;
        }
        listener.onDamageTaken(new PSTCustomRuntimeContexts.DamageTakenListenerContext(node(listener), bonus, player, attacker, damageSource));
    }

    public static void dispatchCriticalHit(Player player, LivingEntity enemy, EventListenerBonus<?> bonus) {
        PSTCustomRuntimeEventListener listener = runtimeListener(bonus);
        if (listener == null) {
            return;
        }
        listener.onCriticalHit(new PSTCustomRuntimeContexts.CriticalHitListenerContext(node(listener), bonus, player, enemy));
    }

    public static void dispatchBlock(Player player, LivingEntity attacker, DamageSource damageSource, EventListenerBonus<?> bonus) {
        PSTCustomRuntimeEventListener listener = runtimeListener(bonus);
        if (listener == null) {
            return;
        }
        listener.onBlock(new PSTCustomRuntimeContexts.BlockListenerContext(node(listener), bonus, player, attacker, damageSource));
    }

    public static void dispatchItemUsed(Player player, ItemStack itemStack, EventListenerBonus<?> bonus) {
        PSTCustomRuntimeEventListener listener = runtimeListener(bonus);
        if (listener == null) {
            return;
        }
        listener.onItemUsed(new PSTCustomRuntimeContexts.ItemUsedListenerContext(node(listener), bonus, player, itemStack));
    }

    public static void dispatchKill(Player player, LivingEntity enemy, DamageSource damageSource, EventListenerBonus<?> bonus) {
        PSTCustomRuntimeEventListener listener = runtimeListener(bonus);
        if (listener == null) {
            return;
        }
        listener.onKill(new PSTCustomRuntimeContexts.KillListenerContext(node(listener), bonus, player, enemy, damageSource));
    }

    public static void dispatchLivingHurt(LivingHurtEvent event) {
        Entity sourceEntity = event.getSource().getEntity();
        if (sourceEntity instanceof Player player) {
            for (EventListenerBonus<?> bonus : SkillBonusHandler.getMergedSkillBonuses(player, EventListenerBonus.class)) {
                dispatchAttack(player, event.getEntity(), event.getSource(), bonus);
            }
        }

        if (event.getEntity() instanceof Player player) {
            LivingEntity attacker = sourceEntity instanceof LivingEntity livingEntity ? livingEntity : null;
            for (EventListenerBonus<?> bonus : SkillBonusHandler.getMergedSkillBonuses(player, EventListenerBonus.class)) {
                dispatchDamageTaken(player, attacker, event.getSource(), bonus);
            }
        }
    }

    public static void dispatchCriticalHit(CriticalHitEvent event) {
        if (!(event.getTarget() instanceof LivingEntity enemy)) {
            return;
        }
        Player player = event.getEntity();
        for (EventListenerBonus<?> bonus : SkillBonusHandler.getMergedSkillBonuses(player, EventListenerBonus.class)) {
            dispatchCriticalHit(player, enemy, bonus);
        }
    }

    public static void dispatchShieldBlock(ShieldBlockEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        Entity sourceEntity = event.getDamageSource().getEntity();
        LivingEntity attacker = sourceEntity instanceof LivingEntity livingEntity ? livingEntity : null;
        for (EventListenerBonus<?> bonus : SkillBonusHandler.getMergedSkillBonuses(player, EventListenerBonus.class)) {
            dispatchBlock(player, attacker, event.getDamageSource(), bonus);
        }
    }

    public static void dispatchItemUsed(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        for (EventListenerBonus<?> bonus : SkillBonusHandler.getMergedSkillBonuses(player, EventListenerBonus.class)) {
            dispatchItemUsed(player, event.getItem(), bonus);
        }
    }

    public static void dispatchLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }
        LivingEntity enemy = event.getEntity();
        for (EventListenerBonus<?> bonus : SkillBonusHandler.getMergedSkillBonuses(player, EventListenerBonus.class)) {
            dispatchKill(player, enemy, event.getSource(), bonus);
        }
    }

    public static void dispatchPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END || event.player.isDeadOrDying() || !(event.player instanceof ServerPlayer player)) {
            return;
        }

        List<EventListenerBonus> bonuses = SkillBonusHandler.getMergedSkillBonuses(player, EventListenerBonus.class);
        if (player.tickCount % 20 == 0) {
            LOGGER.info(
                    "[PassiveSTJS Runtime Debug] tick player={} tickCount={} bonusCount={} bonusTypes={}",
                    player.getScoreboardName(),
                    player.tickCount,
                    bonuses.size(),
                    bonuses.stream()
                            .map(bonus -> {
                                String listenerType = bonus.getEventListener() == null
                                        ? "<null>"
                                        : bonus.getEventListener().getClass().getName();
                                return bonus.getClass().getName() + " listener=" + listenerType;
                            })
                            .toList()
            );
        }

        for (EventListenerBonus bonus : bonuses) {
            if (player.tickCount % 20 == 0) {
                LOGGER.info(
                        "[PassiveSTJS Runtime Debug] dispatchTick player={} bonusType={} listenerType={}",
                        player.getScoreboardName(),
                        bonus.getClass().getName(),
                        bonus.getEventListener() == null ? "<null>" : bonus.getEventListener().getClass().getName()
                );
            }
            dispatchTick(bonus, player);
        }
    }

    private static PSTCustomRuntimeEventListener runtimeListener(EventListenerBonus<?> bonus) {
        if (bonus == null || !(bonus.getEventListener() instanceof PSTCustomRuntimeEventListener listener)) {
            return null;
        }
        return listener;
    }

    private static PSTRuntimeNode node(PSTCustomRuntimeEventListener listener) {
        return listener.node();
    }
}
