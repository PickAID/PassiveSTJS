package com.pickaid.passivestjs.kubejs.runtime;

import com.pickaid.passivestjs.skilltree.PSTContentTitles;
import daripher.skilltree.skill.PassiveSkill;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Objects;

public final class PSTSkillView {
    private final PassiveSkill skill;

    public PSTSkillView(PassiveSkill skill) {
        this.skill = Objects.requireNonNull(skill, "skill");
    }

    @Info("Returns this PST skill id.")
    public String id() {
        return skill.getId().toString();
    }

    @Info("Returns this PST skill title.")
    public Component title() {
        Component component = PSTContentTitles.getSkillTitle(skill.getId());
        if (component != null) {
            return component;
        }
        String fallback = skill.getTitle();
        return fallback == null ? Component.empty() : Component.literal(fallback);
    }

    @Info("Returns this PST skill title color.")
    public String titleColor() {
        return skill.getTitleColor();
    }

    @Info("Returns this PST skill X position.")
    public float positionX() {
        return skill.getPositionX();
    }

    @Info("Returns this PST skill Y position.")
    public float positionY() {
        return skill.getPositionY();
    }

    @Info("Returns the tags assigned to this PST skill.")
    public List<String> tags() {
        return List.copyOf(skill.getTags());
    }

    @Info("Returns direct connection skill ids for this PST skill.")
    public List<String> directConnections() {
        return skill.getDirectConnections().stream()
                .map(id -> id == null ? null : id.toString())
                .filter(Objects::nonNull)
                .toList();
    }

    @Info("Returns the number of bonuses on this PST skill.")
    public int bonusCount() {
        return skill.getBonuses().size();
    }

    @Info("Returns the number of requirements on this PST skill.")
    public int requirementCount() {
        return skill.getRequirements().size();
    }
}
