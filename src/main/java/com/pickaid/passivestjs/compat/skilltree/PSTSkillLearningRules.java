package com.pickaid.passivestjs.compat.skilltree;

import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.skill.requirement.LearnedSkillRequirement;
import daripher.skilltree.skill.requirement.SkillRequirement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class PSTSkillLearningRules {
    private PSTSkillLearningRules() {
    }

    public static boolean canLearn(Player player, IPlayerSkills playerSkills, PassiveSkill skill) {
        return canLearnInternal(player, playerSkills, skill);
    }

    public static boolean canLearn(IPlayerSkills playerSkills, PassiveSkill skill) {
        return canLearnInternal(null, playerSkills, skill);
    }

    private static boolean canLearnInternal(Player player, IPlayerSkills playerSkills, PassiveSkill skill) {
        if (playerSkills == null || skill == null || skill.isInvalid()) {
            return false;
        }
        if (playerSkills.getSkillPoints() <= 0) {
            return false;
        }

        Set<ResourceLocation> learnedIds = learnedSkillIds(playerSkills);
        if (skill.getId() == null || learnedIds.contains(skill.getId())) {
            return false;
        }
        if (!requirementsMet(player, playerSkills, learnedIds, skill)) {
            return false;
        }

        List<PassiveSkillTree> containingTrees = SkillTreesReloader.getSkillTrees().values().stream()
                .filter(Objects::nonNull)
                .filter(tree -> tree.getSkillIds().contains(skill.getId()))
                .toList();
        if (containingTrees.isEmpty()) {
            return true;
        }

        for (PassiveSkillTree tree : containingTrees) {
            if (respectsTagLimits(tree, playerSkills, skill) && isReachableOnTree(tree, learnedIds, skill)) {
                return true;
            }
        }
        return false;
    }

    private static boolean requirementsMet(Player player, IPlayerSkills playerSkills, Set<ResourceLocation> learnedIds, PassiveSkill skill) {
        if (player != null && player.isCreative()) {
            return true;
        }

        for (SkillRequirement<?> requirement : skill.getRequirements()) {
            if (requirement instanceof LearnedSkillRequirement learnedRequirement) {
                if (!learnedIds.contains(learnedRequirement.getSkillId())) {
                    return false;
                }
                continue;
            }
            try {
                if (!requirement.test(player)) {
                    return false;
                }
            } catch (Throwable ignored) {
                return false;
            }
        }
        return true;
    }

    private static boolean respectsTagLimits(PassiveSkillTree tree, IPlayerSkills playerSkills, PassiveSkill skill) {
        Map<String, Integer> limitations = tree.getSkillLimitations();
        if (limitations.isEmpty()) {
            return true;
        }

        for (String tag : skill.getTags()) {
            int limit = limitations.getOrDefault(tag, 0);
            if (limit > 0 && learnedSkillsWithTag(playerSkills, tag) >= limit) {
                return false;
            }
        }
        return true;
    }

    private static long learnedSkillsWithTag(IPlayerSkills playerSkills, String tag) {
        return playerSkills.getPlayerSkills().stream()
                .filter(Objects::nonNull)
                .filter(skill -> skill.getTags().contains(tag))
                .count();
    }

    private static boolean isReachableOnTree(PassiveSkillTree tree, Set<ResourceLocation> learnedIds, PassiveSkill skill) {
        Set<ResourceLocation> learnedOnTree = learnedIds.stream()
                .filter(tree.getSkillIds()::contains)
                .collect(Collectors.toSet());
        if (learnedOnTree.isEmpty()) {
            return skill.isStartingPoint();
        }

        ResourceLocation skillId = skill.getId();
        if (skillId == null) {
            return false;
        }
        if (containsAny(skill.getDirectConnections(), learnedOnTree) || containsAny(skill.getLongConnections(), learnedOnTree)) {
            return true;
        }

        for (ResourceLocation learnedId : learnedOnTree) {
            PassiveSkill learnedSkill = SkillsReloader.getSkillById(learnedId);
            if (learnedSkill == null) {
                continue;
            }
            if (learnedSkill.getDirectConnections().contains(skillId)
                    || learnedSkill.getLongConnections().contains(skillId)
                    || learnedSkill.getOneWayConnections().contains(skillId)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsAny(List<ResourceLocation> connections, Set<ResourceLocation> learnedIds) {
        for (ResourceLocation connection : connections) {
            if (learnedIds.contains(connection)) {
                return true;
            }
        }
        return false;
    }

    private static Set<ResourceLocation> learnedSkillIds(IPlayerSkills playerSkills) {
        return playerSkills.getPlayerSkills().stream()
                .filter(Objects::nonNull)
                .map(PassiveSkill::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
