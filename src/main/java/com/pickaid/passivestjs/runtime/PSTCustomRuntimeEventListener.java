package com.pickaid.passivestjs.runtime;

import daripher.skilltree.skill.bonus.event.SkillEventListener;

public interface PSTCustomRuntimeEventListener extends SkillEventListener {
    PSTRuntimeNode node();

    default void onSkillLearned(PSTCustomRuntimeContexts.SkillLearnedListenerContext context) {
    }

    default void onSkillRemoved(PSTCustomRuntimeContexts.SkillRemovedListenerContext context) {
    }

    default void onTick(PSTCustomRuntimeContexts.TickListenerContext context) {
    }

    default void onAttack(PSTCustomRuntimeContexts.AttackListenerContext context) {
    }

    default void onDamageTaken(PSTCustomRuntimeContexts.DamageTakenListenerContext context) {
    }

    default void onCriticalHit(PSTCustomRuntimeContexts.CriticalHitListenerContext context) {
    }

    default void onBlock(PSTCustomRuntimeContexts.BlockListenerContext context) {
    }

    default void onItemUsed(PSTCustomRuntimeContexts.ItemUsedListenerContext context) {
    }

    default void onKill(PSTCustomRuntimeContexts.KillListenerContext context) {
    }
}
