package com.pickaid.passivestjs.kubejs.content;

import com.pickaid.passivestjs.kubejs.builder.SkillBuilder;
import com.pickaid.passivestjs.kubejs.builder.SkillTreeBuilder;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkillTreeContentStateTest {
    @AfterEach
    void clearRuntimeState() {
        SkillsReloader.getSkills().clear();
        SkillTreesReloader.getSkillTrees().clear();
        ManagedContent.replace(Set.of(), Set.of());
    }

    @Test
    void applyPopulatesRuntimeMapsAndManagedIds() {
        SkillTreeBuilder tree = new SkillTreeBuilder("kubejs:state_tree");
        SkillBuilder root = tree.startingSkill("kubejs:state_root")
                .titleLiteral("State Root");
        tree.skill("kubejs:state_child")
                .titleLiteral("State Child")
                .connect(root);

        SkillTreeContentState state = new SkillTreeContentState();
        state.putTree(tree);

        state.apply();

        assertNotNull(SkillsReloader.getSkillById(new ResourceLocation("kubejs", "state_root")));
        assertNotNull(SkillsReloader.getSkillById(new ResourceLocation("kubejs", "state_child")));
        assertNotNull(SkillTreesReloader.getSkillTreeById(new ResourceLocation("kubejs", "state_tree")));
        assertTrue(ManagedContent.managesSkill(new ResourceLocation("kubejs", "state_root")));
        assertTrue(ManagedContent.managesSkill(new ResourceLocation("kubejs", "state_child")));
        assertTrue(ManagedContent.managesTree(new ResourceLocation("kubejs", "state_tree")));
    }

    @Test
    void applyClearsDefaultTreesAndRemovesPreviouslyManagedContent() {
        ResourceLocation previousTreeId = new ResourceLocation("kubejs", "old_managed_tree");
        ResourceLocation previousSkillId = new ResourceLocation("kubejs", "old_managed_skill");
        ResourceLocation defaultTreeId = new ResourceLocation("skilltree", "default_tree");
        ResourceLocation defaultSkillId = new ResourceLocation("skilltree", "default_skill");

        SkillTreeBuilder previousTree = new SkillTreeBuilder(previousTreeId)
                .addSkill(new SkillBuilder(previousSkillId, true).titleLiteral("Old Managed"));
        for (SkillBuilder skill : previousTree.builtSkills()) {
            Skills.INSTANCE.applyRuntimeSkill(skill.toJson());
        }
        Skills.INSTANCE.applyRuntimeTree(previousTree.toJson());

        SkillTreeBuilder defaultTree = new SkillTreeBuilder(defaultTreeId)
                .addSkill(new SkillBuilder(defaultSkillId, true).titleLiteral("Default"));
        for (SkillBuilder skill : defaultTree.builtSkills()) {
            Skills.INSTANCE.applyRuntimeSkill(skill.toJson());
        }
        Skills.INSTANCE.applyRuntimeTree(defaultTree.toJson());
        ManagedContent.replace(Set.of(previousTreeId), Set.of(previousSkillId));

        SkillTreeBuilder replacementTree = new SkillTreeBuilder("kubejs:new_tree");
        replacementTree.startingSkill("kubejs:new_root").titleLiteral("Replacement");

        SkillTreeContentState state = new SkillTreeContentState();
        state.clearDefaultTrees();
        state.putTree(replacementTree);

        state.apply();

        assertFalse(SkillTreesReloader.getSkillTrees().containsKey(defaultTreeId));
        assertFalse(SkillsReloader.getSkills().containsKey(defaultSkillId));
        assertFalse(SkillTreesReloader.getSkillTrees().containsKey(previousTreeId));
        assertFalse(SkillsReloader.getSkills().containsKey(previousSkillId));
        assertTrue(SkillTreesReloader.getSkillTrees().containsKey(new ResourceLocation("kubejs", "new_tree")));
        assertTrue(SkillsReloader.getSkills().containsKey(new ResourceLocation("kubejs", "new_root")));
        assertFalse(ManagedContent.managesTree(previousTreeId));
        assertFalse(ManagedContent.managesSkill(previousSkillId));
    }

    @Test
    void removedTreeWinsOverLaterRequeueFromQueuedBuilderSnapshot() {
        ResourceLocation treeId = new ResourceLocation("kubejs", "queued_tree");
        ResourceLocation rootId = new ResourceLocation("kubejs", "queued_root");
        ResourceLocation childId = new ResourceLocation("kubejs", "queued_child");

        SkillTreeBuilder tree = new SkillTreeBuilder(treeId);
        SkillBuilder root = tree.startingSkill(rootId).titleLiteral("Queued Root");
        tree.skill(childId).titleLiteral("Queued Child").connect(root);

        SkillTreeContentState state = new SkillTreeContentState();
        state.putTree(tree);
        state.removeTree(treeId);
        state.putTree(tree);

        state.apply();

        assertFalse(SkillTreesReloader.getSkillTrees().containsKey(treeId));
        assertFalse(SkillsReloader.getSkills().containsKey(rootId));
        assertFalse(SkillsReloader.getSkills().containsKey(childId));
        assertFalse(ManagedContent.managesTree(treeId));
        assertFalse(ManagedContent.managesSkill(rootId));
        assertFalse(ManagedContent.managesSkill(childId));
    }

    @Test
    void removedTreeOwnedSkillWinsOverLaterRequeueFromQueuedBuilderSnapshot() {
        ResourceLocation treeId = new ResourceLocation("kubejs", "queued_tree");
        ResourceLocation rootId = new ResourceLocation("kubejs", "queued_root");
        ResourceLocation childId = new ResourceLocation("kubejs", "queued_child");

        SkillTreeBuilder tree = new SkillTreeBuilder(treeId);
        SkillBuilder root = tree.startingSkill(rootId).titleLiteral("Queued Root");
        tree.skill(childId).titleLiteral("Queued Child").connect(root);

        SkillTreeContentState state = new SkillTreeContentState();
        state.putTree(tree);
        state.removeSkill(childId);
        state.putTree(tree);

        state.apply();

        assertTrue(SkillTreesReloader.getSkillTrees().containsKey(treeId));
        assertTrue(SkillsReloader.getSkills().containsKey(rootId));
        assertFalse(SkillsReloader.getSkills().containsKey(childId));
        assertTrue(ManagedContent.managesTree(treeId));
        assertTrue(ManagedContent.managesSkill(rootId));
        assertFalse(ManagedContent.managesSkill(childId));
    }

    @Test
    void removedExistingTreeMemberIsFilteredFromManagedEditedTreeJson() {
        ResourceLocation treeId = new ResourceLocation("kubejs", "existing_tree");
        ResourceLocation skillId = new ResourceLocation("kubejs", "existing_skill");

        SkillBuilder existingSkill = new SkillBuilder(skillId, true).titleLiteral("Existing Skill");
        SkillTreeBuilder existingTree = new SkillTreeBuilder(treeId).addSkill(skillId);
        Skills.INSTANCE.applyRuntimeSkill(existingSkill.toJson());
        Skills.INSTANCE.applyRuntimeTree(existingTree.toJson());

        SkillTreeBuilder editedTree = Skills.INSTANCE.createTree(treeId);
        SkillTreeContentState state = new SkillTreeContentState();
        state.putTree(editedTree);
        state.removeSkill(skillId);

        state.apply();

        assertFalse(SkillsReloader.getSkills().containsKey(skillId));
        assertTrue(SkillTreesReloader.getSkillTrees().containsKey(treeId));
        assertFalse(SkillTreesReloader.getSkillTrees().get(treeId).getSkillIds().contains(skillId));
        assertFalse(ManagedContent.managesSkill(skillId));
    }

    @Test
    void removingRawInsertedTreeDoesNotRemoveReferencedExternalSkills() {
        ResourceLocation treeId = new ResourceLocation("kubejs", "raw_external_tree");
        ResourceLocation externalSkillId = new ResourceLocation("kubejs", "shared_external_skill");

        SkillBuilder externalSkill = new SkillBuilder(externalSkillId, true).titleLiteral("Shared External Skill");
        Skills.INSTANCE.applyRuntimeSkill(externalSkill.toJson());

        SkillTreeContentState state = new SkillTreeContentState();
        state.putTree(treeId, new SkillTreeBuilder(treeId).addSkill(externalSkillId).toJson());
        state.removeTree(treeId);

        state.apply();

        assertTrue(SkillsReloader.getSkills().containsKey(externalSkillId));
        assertFalse(ManagedContent.managesSkill(externalSkillId));
    }

    @Test
    void removingRawInsertedTreeStillRemovesReferencedManagedSkills() {
        ResourceLocation treeId = new ResourceLocation("kubejs", "raw_owned_tree");
        ResourceLocation rootId = new ResourceLocation("kubejs", "raw_owned_root");
        ResourceLocation childId = new ResourceLocation("kubejs", "raw_owned_child");

        SkillTreeBuilder tree = new SkillTreeBuilder(treeId);
        SkillBuilder root = tree.startingSkill(rootId).titleLiteral("Root");
        SkillBuilder child = tree.skill(childId).titleLiteral("Child").connect(root);

        SkillTreeContentState state = new SkillTreeContentState();
        state.putTree(treeId, tree.toJson());
        state.putSkill(rootId, root.toJson());
        state.putSkill(childId, child.toJson());
        state.removeTree(treeId);

        state.apply();

        assertFalse(SkillTreesReloader.getSkillTrees().containsKey(treeId));
        assertFalse(SkillsReloader.getSkills().containsKey(rootId));
        assertFalse(SkillsReloader.getSkills().containsKey(childId));
    }

    @Test
    void removingOneTreeDoesNotRemoveSkillStillOwnedByAnotherTree() {
        ResourceLocation firstTreeId = new ResourceLocation("kubejs", "first_tree");
        ResourceLocation secondTreeId = new ResourceLocation("kubejs", "second_tree");
        ResourceLocation sharedSkillId = new ResourceLocation("kubejs", "shared_skill");

        SkillBuilder sharedSkill = new SkillBuilder(sharedSkillId, true).titleLiteral("Shared Skill");

        SkillTreeContentState state = new SkillTreeContentState();
        state.putTree(firstTreeId, new SkillTreeBuilder(firstTreeId).addSkill(sharedSkillId).toJson());
        state.putTree(secondTreeId, new SkillTreeBuilder(secondTreeId).addSkill(sharedSkillId).toJson());
        state.putSkill(sharedSkillId, sharedSkill.toJson());
        state.removeTree(firstTreeId);

        state.apply();

        assertFalse(SkillTreesReloader.getSkillTrees().containsKey(firstTreeId));
        assertTrue(SkillTreesReloader.getSkillTrees().containsKey(secondTreeId));
        assertTrue(SkillsReloader.getSkills().containsKey(sharedSkillId));
        assertTrue(ManagedContent.managesSkill(sharedSkillId));
    }

    @Test
    void applyFiltersMissingSkillIdsFromTreesBeforeRuntimeRegistration() {
        ResourceLocation treeId = new ResourceLocation("kubejs", "filtered_tree");
        ResourceLocation presentSkillId = new ResourceLocation("kubejs", "present_skill");
        ResourceLocation missingSkillId = new ResourceLocation("kubejs", "missing_skill");

        SkillTreeContentState state = new SkillTreeContentState();
        state.putTree(treeId, new SkillTreeBuilder(treeId)
                .addSkill(presentSkillId)
                .addSkill(missingSkillId)
                .toJson());
        state.putSkill(presentSkillId, new SkillBuilder(presentSkillId, true).titleLiteral("Present Skill").toJson());

        state.apply();

        assertTrue(SkillTreesReloader.getSkillTrees().containsKey(treeId));
        assertEquals(Set.of(presentSkillId), Set.copyOf(SkillTreesReloader.getSkillTrees().get(treeId).getSkillIds()));
        assertTrue(SkillsReloader.getSkills().containsKey(presentSkillId));
        assertFalse(SkillsReloader.getSkills().containsKey(missingSkillId));
    }

    @Test
    @SuppressWarnings("unchecked")
    void rawTreeOverwriteClearsPreviousOwnedSkillTracking() throws Exception {
        ResourceLocation treeId = new ResourceLocation("kubejs", "raw_tree");
        ResourceLocation rootId = new ResourceLocation("kubejs", "raw_root");

        SkillTreeBuilder managedTree = new SkillTreeBuilder(treeId);
        managedTree.startingSkill(rootId).titleLiteral("Root");

        SkillTreeContentState state = new SkillTreeContentState();
        state.putTree(managedTree);
        state.putTree(treeId, new SkillTreeBuilder(treeId).toJson());

        Field field = SkillTreeContentState.class.getDeclaredField("treeSkillIds");
        field.setAccessible(true);
        Map<ResourceLocation, Set<ResourceLocation>> treeSkillIds = (Map<ResourceLocation, Set<ResourceLocation>>) field.get(state);

        assertTrue(treeSkillIds.containsKey(treeId));
        assertEquals(Set.of(), treeSkillIds.get(treeId));
    }

    @Test
    @SuppressWarnings("unchecked")
    void removingTreeClearsOwnedSkillTrackingEntry() throws Exception {
        ResourceLocation treeId = new ResourceLocation("kubejs", "cleanup_tree");
        ResourceLocation rootId = new ResourceLocation("kubejs", "cleanup_root");

        SkillTreeBuilder tree = new SkillTreeBuilder(treeId);
        tree.startingSkill(rootId).titleLiteral("Root");

        SkillTreeContentState state = new SkillTreeContentState();
        state.putTree(tree);
        state.removeTree(treeId);

        Field field = SkillTreeContentState.class.getDeclaredField("treeSkillIds");
        field.setAccessible(true);
        Map<ResourceLocation, Set<ResourceLocation>> treeSkillIds = (Map<ResourceLocation, Set<ResourceLocation>>) field.get(state);

        assertFalse(treeSkillIds.containsKey(treeId));
    }
}
