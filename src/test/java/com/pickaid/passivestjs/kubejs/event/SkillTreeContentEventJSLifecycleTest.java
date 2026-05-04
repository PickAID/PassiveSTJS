package com.pickaid.passivestjs.kubejs.event;

import com.google.gson.JsonElement;
import com.pickaid.passivestjs.kubejs.builder.SkillBuilder;
import com.pickaid.passivestjs.kubejs.builder.SkillTreeBuilder;
import com.pickaid.passivestjs.kubejs.content.ManagedContent;
import com.pickaid.passivestjs.kubejs.content.SkillTreeContentState;
import com.pickaid.passivestjs.kubejs.content.Skills;
import com.pickaid.passivestjs.kubejs.id.PSTSkillId;
import com.pickaid.passivestjs.kubejs.id.PSTTreeId;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.script.data.VirtualKubeJSDataPack;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkillTreeContentEventJSLifecycleTest {
    @AfterEach
    void clearRuntimeState() {
        SkillsReloader.getSkills().clear();
        SkillTreesReloader.getSkillTrees().clear();
        ManagedContent.replace(Set.of(), Set.of());
    }

    @Test
    void removedImmediateTreeDoesNotWriteTreeOrBuiltSkillsDuringFlush() {
        RecordingSkillTreeContentEvent event = new RecordingSkillTreeContentEvent();
        ResourceLocation treeId = new ResourceLocation("kubejs", "immediate_tree");
        ResourceLocation rootId = new ResourceLocation("kubejs", "immediate_root");
        ResourceLocation childId = new ResourceLocation("kubejs", "immediate_child");

        event.addSkillTree(PSTTreeId.of(treeId), tree -> {
            var root = tree.startingSkill(PSTSkillId.of(rootId)).titleLiteral("Root");
            tree.skill(PSTSkillId.of(childId)).titleLiteral("Child").connect(PSTSkillId.of(rootId));
        });
        event.removeTree(PSTTreeId.of(treeId));
        event.flushGeneratedContent();

        assertNull(event.getJson(Skills.skillTreeResource(treeId)));
        assertNull(event.getJson(Skills.skillResource(rootId)));
        assertNull(event.getJson(Skills.skillResource(childId)));
    }

    @Test
    void removedTreeOwnedQueuedSkillDoesNotWriteSkillAndIsFilteredFromTreeJson() {
        RecordingSkillTreeContentEvent event = new RecordingSkillTreeContentEvent();
        ResourceLocation treeId = new ResourceLocation("kubejs", "queued_tree");
        ResourceLocation rootId = new ResourceLocation("kubejs", "queued_root");
        ResourceLocation childId = new ResourceLocation("kubejs", "queued_child");

        var tree = event.editTree(PSTTreeId.of(treeId));
        var root = tree.startingSkill(PSTSkillId.of(rootId)).titleLiteral("Root");
        tree.skill(PSTSkillId.of(childId)).titleLiteral("Child").connect(PSTSkillId.of(rootId));

        event.removeSkill(PSTSkillId.of(childId));
        event.flushGeneratedContent();

        String treeJson = event.getJson(Skills.skillTreeResource(treeId));
        assertNotNull(treeJson);
        assertTrue(treeJson.contains(rootId.toString()));
        assertFalse(treeJson.contains(childId.toString()));
        assertNotNull(event.getJson(Skills.skillResource(rootId)));
        assertNull(event.getJson(Skills.skillResource(childId)));
    }

    @Test
    void removedExistingTreeMemberIsFilteredInEventOrderFlushAndApply() {
        RecordingSkillTreeContentEvent event = new RecordingSkillTreeContentEvent();
        ResourceLocation treeId = new ResourceLocation("kubejs", "existing_tree");
        ResourceLocation skillId = new ResourceLocation("kubejs", "existing_skill");

        SkillBuilder existingSkill = new SkillBuilder(skillId, true).titleLiteral("Existing Skill");
        SkillTreeBuilder existingTree = new SkillTreeBuilder(treeId).addSkill(skillId);
        Skills.INSTANCE.applyRuntimeSkill(existingSkill.toJson());
        Skills.INSTANCE.applyRuntimeTree(existingTree.toJson());

        event.editTree(PSTTreeId.of(treeId));
        event.removeSkill(PSTSkillId.of(skillId));
        event.flushAndApplyForTest();

        String treeJson = event.getJson(Skills.skillTreeResource(treeId));
        assertNotNull(treeJson);
        assertFalse(treeJson.contains(skillId.toString()));
        assertNull(event.getJson(Skills.skillResource(skillId)));
        assertTrue(SkillTreesReloader.getSkillTrees().containsKey(treeId));
        assertFalse(SkillTreesReloader.getSkillTrees().get(treeId).getSkillIds().contains(skillId));
        assertFalse(SkillsReloader.getSkills().containsKey(skillId));
    }

    @Test
    void afterPostedWithoutWrappedResourceManagerStillAppliesRuntimeContent() {
        RecordingSkillTreeContentEvent event = new RecordingSkillTreeContentEvent();
        ResourceLocation treeId = new ResourceLocation("kubejs", "after_post_tree");
        ResourceLocation skillId = new ResourceLocation("kubejs", "after_post_root");

        event.editTree(PSTTreeId.of(treeId))
                .startingSkill(PSTSkillId.of(skillId))
                .titleLiteral("After Posted Root")
                .position(0, 0);

        event.afterPosted(EventResult.PASS);

        assertTrue(SkillTreesReloader.getSkillTrees().containsKey(treeId));
        assertTrue(SkillsReloader.getSkills().containsKey(skillId));
        assertTrue(ManagedContent.managesTree(treeId));
        assertTrue(ManagedContent.managesSkill(skillId));
        assertTrue(event.getJson(Skills.skillResource(skillId)).contains("\"title\":\"After Posted Root\""));
    }

    @Test
    void translationDraftExportRunsOnlyWhenEnabledAndConfigDirExists() {
        assertTrue(SkillTreeContentEventJS.shouldExportTranslationDraft(Path.of("config"), true));
        assertFalse(SkillTreeContentEventJS.shouldExportTranslationDraft(Path.of("config"), false));
        assertFalse(SkillTreeContentEventJS.shouldExportTranslationDraft(null, true));
    }

    private static final class RecordingSkillTreeContentEvent extends SkillTreeContentEventJS {
        private final Map<ResourceLocation, String> jsonById = new LinkedHashMap<>();

        private RecordingSkillTreeContentEvent() {
            super(new VirtualKubeJSDataPack(false), null);
        }

        @Override
        public void addJson(ResourceLocation id, JsonElement json) {
            if (json == null) {
                return;
            }
            ResourceLocation jsonId = id.getPath().endsWith(".json")
                    ? id
                    : new ResourceLocation(id.getNamespace(), id.getPath() + ".json");
            jsonById.put(jsonId, json.toString());
        }

        private String getJson(ResourceLocation id) {
            ResourceLocation jsonId = new ResourceLocation(id.getNamespace(), id.getPath() + ".json");
            return jsonById.get(jsonId);
        }

        private void flushAndApplyForTest() {
            flushGeneratedContent();
            try {
                Field field = SkillTreeContentEventJS.class.getDeclaredField("contentState");
                field.setAccessible(true);
                SkillTreeContentState state = (SkillTreeContentState) field.get(this);
                state.apply();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
