package com.pickaid.passivestjs.skilltree;

import com.pickaid.passivestjs.kubejs.id.PSTSkillId;
import com.pickaid.passivestjs.kubejs.runtime.PSTPlayerView;
import daripher.skilltree.capability.skill.PlayerSkills;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.skill.requirement.LearnedSkillRequirement;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PSTSkillLearningRulesTest {
    @AfterEach
    void clearReloaders() {
        SkillsReloader.getSkills().clear();
        SkillTreesReloader.getSkillTrees().clear();
    }

    @Test
    void playerViewLearningRespectsLearnedSkillRequirement() {
        PassiveSkill root = skill("kubejs:root");
        root.setStartingPoint(true);
        PassiveSkill utility = skill("kubejs:utility");
        utility.getDirectConnections().add(root.getId());
        PassiveSkill defense = skill("kubejs:defense");
        defense.getDirectConnections().add(root.getId());
        defense.addSkillRequirement(new LearnedSkillRequirement(utility.getId()));

        SkillsReloader.getSkills().put(root.getId(), root);
        SkillsReloader.getSkills().put(utility.getId(), utility);
        SkillsReloader.getSkills().put(defense.getId(), defense);

        PassiveSkillTree tree = new PassiveSkillTree(ResourceLocation.fromNamespaceAndPath("kubejs", "test_tree"));
        tree.getSkillIds().add(root.getId());
        tree.getSkillIds().add(utility.getId());
        tree.getSkillIds().add(defense.getId());
        SkillTreesReloader.getSkillTrees().put(tree.getId(), tree);

        PlayerSkills playerSkills = new PlayerSkills();
        playerSkills.setSkillPoints(3);
        playerSkills.getPlayerSkills().add(root);
        PSTPlayerView view = new PSTPlayerView(playerSkills);

        assertFalse(view.learn(PSTSkillId.of(defense.getId())));
        assertTrue(view.learn(PSTSkillId.of(utility.getId())));
        assertTrue(view.learn(PSTSkillId.of(defense.getId())));
    }

    private static PassiveSkill skill(String id) {
        ResourceLocation resourceId = ResourceLocation.tryParse(id);
        return new PassiveSkill(
                resourceId,
                16,
                ResourceLocation.tryParse("skilltree:textures/icons/background/lesser.png"),
                ResourceLocation.tryParse("minecraft:textures/item/barrier.png"),
                ResourceLocation.tryParse("skilltree:textures/tooltip/lesser.png"),
                false
        );
    }
}
