package com.pickaid.passivestjs.kubejs.content;

import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import net.minecraft.resources.ResourceLocation;
import com.pickaid.passivestjs.kubejs.content.ManagedContent;
import com.pickaid.passivestjs.kubejs.builder.SkillBuilder;
import com.pickaid.passivestjs.kubejs.builder.SkillTreeBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

class SkillsRuntimeRegistrationTest {
    @AfterEach
    void clearReloaders() {
        SkillsReloader.getSkills().clear();
        SkillTreesReloader.getSkillTrees().clear();
    }

    @Test
    void generatedKubejsContentPopulatesPstRuntimeMaps() {
        SkillTreeBuilder tree = new SkillTreeBuilder("kubejs:pi_test_tree");
        SkillBuilder root = tree.startingSkill("kubejs:pi_test_root")
                .title("PI Test Root")
                .position(0, 0);
        tree.skill("kubejs:pi_test_child")
                .title("PI Test Child")
                .position(48, 0)
                .connect(root);
        tree.limit("pi_test", 2);

        for (SkillBuilder skill : tree.builtSkills()) {
            Skills.INSTANCE.applyRuntimeSkill(skill.toJson());
        }
        Skills.INSTANCE.applyRuntimeTree(tree.toJson());

        var runtimeRoot = SkillsReloader.getSkillById(new ResourceLocation("kubejs", "pi_test_root"));
        var runtimeChild = SkillsReloader.getSkillById(new ResourceLocation("kubejs", "pi_test_child"));
        var runtimeTree = SkillTreesReloader.getSkillTreeById(new ResourceLocation("kubejs", "pi_test_tree"));

        assertNotNull(runtimeRoot);
        assertNotNull(runtimeChild);
        assertNotNull(runtimeTree);
        assertEquals("PI Test Root", runtimeRoot.getTitle());
        assertEquals(0, runtimeRoot.getBonuses().size());
        assertEquals(0, runtimeChild.getBonuses().size());
        assertEquals(2, runtimeTree.getSkillIds().size());
        assertEquals(2, runtimeTree.getSkillLimitations().get("pi_test"));
        assertTrue(runtimeTree.getSkillIds().contains(new ResourceLocation("kubejs", "pi_test_root")));
        assertTrue(runtimeTree.getSkillIds().contains(new ResourceLocation("kubejs", "pi_test_child")));
    }

    @Test
    void runtimeSkillsAndTreesCanBeRemovedAgain() {
        ResourceLocation treeId = new ResourceLocation("kubejs", "pi_test_tree");
        ResourceLocation rootId = new ResourceLocation("kubejs", "pi_test_root");

        SkillTreeBuilder tree = new SkillTreeBuilder(treeId);
        tree.startingSkill(rootId).title("PI Test Root");

        for (SkillBuilder skill : tree.builtSkills()) {
            Skills.INSTANCE.applyRuntimeSkill(skill.toJson());
        }
        Skills.INSTANCE.applyRuntimeTree(tree.toJson());

        Skills.INSTANCE.removeRuntimeTree(treeId);
        Skills.INSTANCE.removeRuntimeSkill(rootId);

        assertFalse(SkillTreesReloader.getSkillTrees().containsKey(treeId));
        assertFalse(SkillsReloader.getSkills().containsKey(rootId));
    }

    @Test
    void editingExistingTreeSkillKeepsExistingVisualFields() {
        ResourceLocation treeId = new ResourceLocation("kubejs", "pi_test_tree");
        ResourceLocation skillId = new ResourceLocation("kubejs", "pi_test_root");

        SkillBuilder existingSkill = Skills.INSTANCE.createStartingSkill(skillId)
                .title("Existing Root")
                .icon("minecraft:textures/item/diamond.png")
                .background("skilltree:textures/icons/background/notable.png")
                .border("skilltree:textures/tooltip/notable.png")
                .position(12, 24);
        SkillTreeBuilder existingTree = new SkillTreeBuilder(treeId)
                .addSkill(existingSkill);

        Skills.INSTANCE.applyRuntimeSkill(existingSkill.toJson());
        Skills.INSTANCE.applyRuntimeTree(existingTree.toJson());

        SkillTreeBuilder editedTree = Skills.INSTANCE.createTree(treeId);
        var editedSkill = editedTree.skill(skillId).title("Edited Root");

        assertEquals("minecraft:textures/item/diamond.png", editedSkill.toJson().get("iconTexture").getAsString());
        assertEquals("skilltree:textures/icons/background/notable.png", editedSkill.toJson().get("backgroundTexture").getAsString());
        assertEquals("skilltree:textures/tooltip/notable.png", editedSkill.toJson().get("borderTexture").getAsString());
        assertEquals(12.0D, editedSkill.toJson().get("positionX").getAsDouble());
        assertEquals(24.0D, editedSkill.toJson().get("positionY").getAsDouble());
    }

    @Test
    void managedRuntimeSkillsStartFromFreshBaseOnReload() {
        ResourceLocation treeId = new ResourceLocation("kubejs", "pi_test_tree");
        ResourceLocation skillId = new ResourceLocation("kubejs", "pi_test_root");

        SkillBuilder existingSkill = Skills.INSTANCE.createStartingSkill(skillId)
                .title("Generated Root")
                .icon("minecraft:textures/item/diamond.png");
        SkillTreeBuilder existingTree = new SkillTreeBuilder(treeId)
                .addSkill(existingSkill);

        Skills.INSTANCE.applyRuntimeSkill(existingSkill.toJson());
        Skills.INSTANCE.applyRuntimeTree(existingTree.toJson());
        ManagedContent.replace(Set.of(treeId), Set.of(skillId));

        try {
            SkillTreeBuilder regeneratedTree =
                    Skills.INSTANCE.createTree(treeId);
            SkillBuilder regeneratedSkill = regeneratedTree.startingSkill(skillId);

            assertFalse(regeneratedSkill.toJson().has("title"));
            assertEquals("minecraft:textures/item/barrier.png", regeneratedSkill.toJson().get("iconTexture").getAsString());
        } finally {
            ManagedContent.replace(Set.of(), Set.of());
        }
    }
}
