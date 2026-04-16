package com.pickaid.passivestjs.kubejs.runtime;

import com.pickaid.passivestjs.skilltree.PSTSkillLearningRules;
import com.pickaid.passivestjs.kubejs.id.PSTSkillId;
import com.pickaid.passivestjs.runtime.PSTCustomRuntimeSkillBonus;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.requirement.SkillRequirement;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PSTPlayerSkillView {
    private final Player player;
    private final IPlayerSkills playerSkills;
    private final PSTPlayerView owner;
    private final PassiveSkill skill;

    PSTPlayerSkillView(Player player, IPlayerSkills playerSkills, PSTPlayerView owner, PassiveSkill skill) {
        this.player = player;
        this.playerSkills = Objects.requireNonNull(playerSkills, "playerSkills");
        this.owner = Objects.requireNonNull(owner, "owner");
        this.skill = Objects.requireNonNull(skill, "skill");
    }

    @Info("Returns whether this skill is currently learned on the owning player.")
    public boolean learned() {
        return owner.hasSkill(PSTSkillId.of(skill.getId()));
    }

    @Info("Returns whether this skill can be learned right now under PST's learning rules.")
    public boolean canLearn() {
        return player != null
                ? PSTSkillLearningRules.canLearn(player, playerSkills, skill)
                : PSTSkillLearningRules.canLearn(playerSkills, skill);
    }

    @Info("Learns this skill using PSTPlayerView's canonical learn path.")
    public boolean learn() {
        return owner.learn(PSTSkillId.of(skill.getId()));
    }

    @Info("Removes this skill if learned, using PSTPlayerView's canonical remove path.")
    public boolean remove() {
        return owner.remove(PSTSkillId.of(skill.getId()));
    }

    @Info("Returns runtime requirement helper views for this skill.")
    public List<PSTRequirementView> requirements() {
        List<PSTRequirementView> views = new ArrayList<>();
        for (SkillRequirement<?> requirement : skill.getRequirements()) {
            if (requirement == null) {
                continue;
            }
            views.add(new PSTRequirementView(player, playerSkills, requirement));
        }
        return List.copyOf(views);
    }

    @Info("Returns learned-only runtime bonus helper views for this skill, excluding event listener bonuses.")
    public List<PSTBonusView> bonuses() {
        if (!learned()) {
            return List.of();
        }

        List<PSTBonusView> views = new ArrayList<>();
        for (SkillBonus<?> bonus : skill.getBonuses()) {
            if (bonus == null || hasEventListener(bonus)) {
                continue;
            }
            views.add(new PSTBonusView(bonus));
        }
        return List.copyOf(views);
    }

    @Info("Returns learned-only runtime listener helper views for this skill.")
    public List<PSTListenerView> listeners() {
        if (!learned()) {
            return List.of();
        }

        List<PSTListenerView> views = new ArrayList<>();
        for (SkillBonus<?> bonus : skill.getBonuses()) {
            if (bonus instanceof EventListenerBonus<?> listenerBonus && hasEventListener(bonus)) {
                views.add(new PSTListenerView(listenerBonus));
            }
        }
        return List.copyOf(views);
    }

    private static boolean hasEventListener(SkillBonus<?> bonus) {
        if (!(bonus instanceof EventListenerBonus<?> listenerBonus)) {
            return false;
        }

        if (bonus instanceof PSTCustomRuntimeSkillBonus runtime) {
            return runtime.node().object("event_listener").isPresent();
        }

        try {
            // PassiveSTJS uses a no-op listener that is not serializable and throws on getSerializer().
            return listenerBonus.getEventListener() != null && listenerBonus.getEventListener().getSerializer() != null;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
