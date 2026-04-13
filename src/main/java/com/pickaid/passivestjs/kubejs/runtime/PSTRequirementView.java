package com.pickaid.passivestjs.kubejs.runtime;

import com.pickaid.passivestjs.runtime.PSTCustomRuntimeSkillRequirement;
import com.pickaid.passivestjs.runtime.PSTRuntimeNode;
import com.pickaid.passivestjs.runtime.PSTRuntimeTypeIds;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.requirement.LearnedSkillRequirement;
import daripher.skilltree.skill.requirement.SkillRequirement;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public final class PSTRequirementView {
    private final Player player;
    private final IPlayerSkills playerSkills;
    private final SkillRequirement<?> requirement;

    public PSTRequirementView(Player player, IPlayerSkills playerSkills, SkillRequirement<?> requirement) {
        this.player = player;
        this.playerSkills = Objects.requireNonNull(playerSkills, "playerSkills");
        this.requirement = Objects.requireNonNull(requirement, "requirement");
    }

    @Info("Returns whether this requirement is currently met.")
    public boolean passed() {
        if (requirement instanceof LearnedSkillRequirement learned) {
            ResourceLocation requiredSkillId = learned.getSkillId();
            if (requiredSkillId == null) {
                return false;
            }
            for (PassiveSkill skill : playerSkills.getPlayerSkills()) {
                if (skill != null && requiredSkillId.equals(skill.getId())) {
                    return true;
                }
            }
            return false;
        }

        try {
            return requirement.test(player);
        } catch (Throwable ignored) {
            return false;
        }
    }

    @Info("Returns the registry/type id for this requirement.")
    public String typeId() {
        return PSTRuntimeTypeIds.skillRequirementId(requirement);
    }

    @Info("Returns the requirement tooltip text.")
    public Component text() {
        return requirement.getTooltip();
    }

    @Info("Returns the runtime node for this requirement when it is serializer-backed, otherwise null.")
    public PSTRuntimeNode node() {
        return requirement instanceof PSTCustomRuntimeSkillRequirement runtime ? runtime.node() : null;
    }
}

