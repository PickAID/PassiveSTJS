package com.pickaid.passivestjs.kubejs;

import com.google.gson.JsonObject;
import com.pickaid.passivestjs.kubejs.id.PSTSkillId;
import com.pickaid.passivestjs.kubejs.id.PSTTreeId;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTEventListenerSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillBonusSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillRequirementSerializerBuilder;
import com.pickaid.passivestjs.kubejs.runtime.PSTBonusView;
import com.pickaid.passivestjs.kubejs.runtime.PSTListenerView;
import com.pickaid.passivestjs.kubejs.runtime.PSTPlayerSkillView;
import com.pickaid.passivestjs.kubejs.runtime.PSTPlayerView;
import com.pickaid.passivestjs.kubejs.runtime.PSTRequirementView;
import com.pickaid.passivestjs.kubejs.runtime.PSTSkillView;
import com.pickaid.passivestjs.kubejs.runtime.PSTTreeView;
import daripher.skilltree.capability.skill.PlayerSkills;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.requirement.SkillRequirement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadataIndex;
import com.pickaid.passivestjs.runtime.PSTSerializerObjectIndex;
import com.pickaid.passivestjs.runtime.tooltip.PSTTooltipSpecRegistry;
import com.pickaid.passivestjs.schema.PSTSchemaRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BindingsRuntimeViewTest {
    @BeforeAll
    static void bootstrapMinecraftRegistries() throws ReflectiveOperationException {
        var bootstrapped = net.minecraft.server.Bootstrap.class.getDeclaredField("isBootstrapped");
        bootstrapped.setAccessible(true);
        bootstrapped.setBoolean(null, true);
    }

    @AfterEach
    void clearReloaders() {
        SkillsReloader.getSkills().clear();
        SkillTreesReloader.getSkillTrees().clear();
        PSTSchemaRegistry.clear();
        PSTSerializerMetadataIndex.clear();
        PSTSerializerObjectIndex.clear();
        PSTTooltipSpecRegistry.clear();
    }

    @Test
    void skillLookupReturnsReadOnlyRuntimeView() {
        PassiveSkill root = skill("kubejs:test_root");
        root.setTitle("Root");
        root.setTitleColor("#ffaa00");
        root.setPosition(12.5F, -4.0F);
        root.getTags().add("core");
        root.getDirectConnections().add(ResourceLocation.fromNamespaceAndPath("kubejs", "other"));
        SkillsReloader.getSkills().put(root.getId(), root);

        PSTSkillView view = Bindings.INSTANCE.skill(PSTSkillId.of(root.getId()));

        assertNotNull(view);
        assertEquals("kubejs:test_root", view.id());
        assertEquals(Component.literal("Root"), view.title());
        assertEquals("#ffaa00", view.titleColor());
        assertEquals(12.5F, view.positionX());
        assertEquals(-4.0F, view.positionY());
        assertEquals(List.of("core"), view.tags());
        assertEquals(List.of("kubejs:other"), view.directConnections());
        assertEquals(0, view.bonusCount());
        assertEquals(0, view.requirementCount());
    }

    @Test
    void treeLookupReturnsReadOnlyRuntimeView() {
        ResourceLocation treeId = ResourceLocation.fromNamespaceAndPath("kubejs", "test_tree");
        PassiveSkillTree tree = new PassiveSkillTree(treeId);
        ResourceLocation rootId = ResourceLocation.fromNamespaceAndPath("kubejs", "root");
        tree.getSkillIds().add(rootId);
        tree.getSkillLimitations().put("core", 1);
        SkillTreesReloader.getSkillTrees().put(treeId, tree);
        SkillsReloader.getSkills().put(rootId, skill("kubejs:root"));

        PSTTreeView view = Bindings.INSTANCE.tree(PSTTreeId.of(treeId));

        assertNotNull(view);
        assertEquals("kubejs:test_tree", view.id());
        assertEquals(List.of("kubejs:root"), view.skillIds());
        assertEquals(1, view.limits().get("core"));
        assertTrue(view.hasSkill(PSTSkillId.of(rootId)));
        assertEquals("kubejs:root", view.skill(PSTSkillId.of(rootId)).id());
        assertEquals(List.of("kubejs:root"), view.skills().stream().map(PSTSkillView::id).toList());
        assertFalse(view.isDefaultTree());
    }

    @Test
    void runtimeViewsReturnNullForMissingDefinitions() {
        assertNull(Bindings.INSTANCE.skill(PSTSkillId.of(ResourceLocation.fromNamespaceAndPath("kubejs", "missing"))));
        assertNull(Bindings.INSTANCE.tree(PSTTreeId.of(ResourceLocation.fromNamespaceAndPath("kubejs", "missing_tree"))));
    }

    @Test
    void playerViewExposesPointsLearnedIdsAndResetState() {
        PlayerSkills playerSkills = new PlayerSkills();
        playerSkills.setSkillPoints(7);
        playerSkills.setTreeReset(true);
        playerSkills.getPlayerSkills().add(skill("kubejs:first"));
        playerSkills.getPlayerSkills().add(skill("kubejs:second"));

        PSTPlayerView view = new PSTPlayerView(playerSkills);

        assertEquals(7, view.skillPoints());
        assertTrue(view.treeReset());
        assertTrue(view.hasSkill(PSTSkillId.of(ResourceLocation.fromNamespaceAndPath("kubejs", "first"))));
        assertFalse(view.hasSkill(PSTSkillId.of(ResourceLocation.fromNamespaceAndPath("kubejs", "missing"))));
        assertEquals(List.of("kubejs:first", "kubejs:second"), view.learnedSkillIds());
        assertEquals(List.of("kubejs:first", "kubejs:second"), view.learnedSkills().stream().map(PSTSkillView::id).toList());
    }

    @Test
    void playerViewSupportsGrantConsumeLearnRemoveAndReset() {
        PassiveSkill root = skill("kubejs:runtime_root");
        PassiveSkill branch = skill("kubejs:runtime_branch");
        PassiveSkill costed = skill("kubejs:runtime_costed");
        SkillsReloader.getSkills().put(root.getId(), root);
        SkillsReloader.getSkills().put(branch.getId(), branch);
        SkillsReloader.getSkills().put(costed.getId(), costed);

        PlayerSkills playerSkills = new PlayerSkills();
        playerSkills.setSkillPoints(1);
        PSTPlayerView view = new PSTPlayerView(playerSkills);

        assertEquals(3, view.grantSkillPoints(2));
        assertTrue(view.consumeSkillPoints(2));
        assertEquals(1, view.skillPoints());
        assertFalse(view.consumeSkillPoints(2));
        assertEquals(1, view.skillPoints());

        assertTrue(view.learn(PSTSkillId.of(root.getId())));
        assertTrue(view.hasSkill(PSTSkillId.of(root.getId())));
        assertTrue(view.learnWithoutSkillPointCost(PSTSkillId.of(branch.getId())));
        assertEquals(List.of("kubejs:runtime_root", "kubejs:runtime_branch"), view.learnedSkillIds());
        assertEquals("kubejs:runtime_branch", view.learnedSkill(PSTSkillId.of(branch.getId())).id());
        assertEquals(1, view.skillPoints());
        assertFalse(view.learn(PSTSkillId.of(root.getId())));
        assertFalse(view.learn(PSTSkillId.of(ResourceLocation.fromNamespaceAndPath("kubejs", "missing_skill"))));

        assertTrue(view.learnWithSkillPointCost(PSTSkillId.of(costed.getId()), 1));
        assertEquals(0, view.skillPoints());
        assertTrue(view.hasSkill(PSTSkillId.of(costed.getId())));
        assertFalse(view.learnWithSkillPointCost(PSTSkillId.of(ResourceLocation.fromNamespaceAndPath("kubejs", "missing_costed")), 1));
        assertFalse(view.learnWithSkillPointCost(PSTSkillId.of(branch.getId()), 1));

        assertTrue(view.remove(PSTSkillId.of(root.getId())));
        assertEquals(0, view.skillPoints());
        assertFalse(view.hasSkill(PSTSkillId.of(root.getId())));
        assertFalse(view.remove(PSTSkillId.of(root.getId())));

        assertEquals(0, view.reset());
        assertEquals(0, view.skillPoints());
        assertTrue(view.learnedSkillIds().isEmpty());
    }

    @Test
    void playerViewCanReportLearnedSkillsInsideTree() {
        PassiveSkill root = skill("kubejs:tree_root");
        PassiveSkill branch = skill("kubejs:tree_branch");
        PassiveSkill other = skill("kubejs:outside");
        SkillsReloader.getSkills().put(root.getId(), root);
        SkillsReloader.getSkills().put(branch.getId(), branch);
        SkillsReloader.getSkills().put(other.getId(), other);

        ResourceLocation treeId = ResourceLocation.fromNamespaceAndPath("kubejs", "runtime_tree");
        PassiveSkillTree tree = new PassiveSkillTree(treeId);
        tree.getSkillIds().add(root.getId());
        tree.getSkillIds().add(branch.getId());
        SkillTreesReloader.getSkillTrees().put(treeId, tree);

        PlayerSkills playerSkills = new PlayerSkills();
        playerSkills.getPlayerSkills().add(root);
        playerSkills.getPlayerSkills().add(other);
        PSTPlayerView view = new PSTPlayerView(playerSkills);

        assertTrue(view.hasLearnedInTree(PSTTreeId.of(treeId)));
        assertEquals(List.of("kubejs:tree_root"), view.learnedSkillsInTree(PSTTreeId.of(treeId)).stream().map(PSTSkillView::id).toList());
        assertFalse(view.hasLearnedInTree(PSTTreeId.of(ResourceLocation.fromNamespaceAndPath("kubejs", "missing_tree"))));
        assertTrue(view.learnedSkillsInTree(PSTTreeId.of(ResourceLocation.fromNamespaceAndPath("kubejs", "missing_tree"))).isEmpty());
    }

    @Test
    void playerViewLearningUsesCanonicalSkillTreeLearnPath() {
        PassiveSkill learnedWithoutCost = skill("kubejs:canonical_free");
        PassiveSkill learnedWithCost = skill("kubejs:canonical_costed");
        SkillsReloader.getSkills().put(learnedWithoutCost.getId(), learnedWithoutCost);
        SkillsReloader.getSkills().put(learnedWithCost.getId(), learnedWithCost);

        TrackingPlayerSkills playerSkills = new TrackingPlayerSkills();
        playerSkills.setSkillPoints(5);
        PSTPlayerView view = new PSTPlayerView(playerSkills);

        assertTrue(view.learn(PSTSkillId.of(learnedWithoutCost.getId())));
        assertEquals(1, playerSkills.learnSkillCalls);
        assertEquals(5, view.skillPoints());
        assertEquals(List.of("kubejs:canonical_free"), view.learnedSkillIds());

        assertTrue(view.learnWithSkillPointCost(PSTSkillId.of(learnedWithCost.getId()), 3));
        assertEquals(2, playerSkills.learnSkillCalls);
        assertEquals(2, view.skillPoints());
        assertEquals(List.of("kubejs:canonical_free", "kubejs:canonical_costed"), view.learnedSkillIds());
    }

    @Test
    void playerSkillViewExposesLearnedCanLearnBonusListenerAndRequirementState() {
        var rootId = ResourceLocation.fromNamespaceAndPath("kubejs", "runtime_root");
        var branchId = ResourceLocation.fromNamespaceAndPath("kubejs", "runtime_branch");

        new PSTEventListenerSerializerBuilder(
                ResourceLocation.fromNamespaceAndPath("kubejs", "smoke_listener")
        ).createObject();
        SkillBonus.Serializer smokeBonusSerializer = new PSTSkillBonusSerializerBuilder(
                ResourceLocation.fromNamespaceAndPath("kubejs", "smoke_bonus")
        ).createObject();
        SkillBonus.Serializer triggerBonusSerializer = new PSTSkillBonusSerializerBuilder(
                ResourceLocation.fromNamespaceAndPath("kubejs", "trigger_bonus")
        ).createObject();
        SkillRequirement.Serializer smokeRequirementSerializer = new PSTSkillRequirementSerializerBuilder(
                ResourceLocation.fromNamespaceAndPath("kubejs", "smoke_requirement")
        )
                .test(context -> context.node().bool("allow").orElse(false))
                .createObject();

        PassiveSkill root = skill("kubejs:runtime_root");
        PassiveSkill branch = skill("kubejs:runtime_branch");
        root.getDirectConnections().add(branchId);

        JsonObject effectBonusJson = new JsonObject();
        effectBonusJson.addProperty("type", "kubejs:smoke_bonus");
        effectBonusJson.addProperty("amount", 2.5D);
        root.getBonuses().add(smokeBonusSerializer.deserialize(effectBonusJson));

        JsonObject triggerBonusJson = new JsonObject();
        triggerBonusJson.addProperty("type", "kubejs:trigger_bonus");
        JsonObject listenerJson = new JsonObject();
        listenerJson.addProperty("type", "kubejs:smoke_listener");
        listenerJson.addProperty("mode", "attack");
        triggerBonusJson.add("event_listener", listenerJson);
        root.getBonuses().add(triggerBonusSerializer.deserialize(triggerBonusJson));

        JsonObject requirementJson = new JsonObject();
        requirementJson.addProperty("type", "kubejs:smoke_requirement");
        requirementJson.addProperty("allow", true);
        branch.getRequirements().add(smokeRequirementSerializer.deserialize(requirementJson));

        SkillsReloader.getSkills().put(rootId, root);
        SkillsReloader.getSkills().put(branchId, branch);

        PassiveSkillTree tree = new PassiveSkillTree(ResourceLocation.fromNamespaceAndPath("kubejs", "runtime_tree"));
        tree.getSkillIds().add(rootId);
        tree.getSkillIds().add(branchId);
        SkillTreesReloader.getSkillTrees().put(tree.getId(), tree);

        PlayerSkills playerSkills = new PlayerSkills();
        playerSkills.setSkillPoints(1);
        playerSkills.getPlayerSkills().add(root);
        PSTPlayerView playerView = new PSTPlayerView(playerSkills);

        PSTPlayerSkillView rootView = playerView.skill(PSTSkillId.of(rootId));
        PSTPlayerSkillView branchView = playerView.skill(PSTSkillId.of(branchId));

        assertTrue(rootView.learned());
        assertFalse(branchView.learned());
        assertTrue(branchView.canLearn());

        assertEquals(1, rootView.bonuses().size());
        PSTBonusView smokeBonus = rootView.bonuses().get(0);
        assertEquals("kubejs:smoke_bonus", smokeBonus.typeId());
        assertEquals(2.5D, smokeBonus.node().number("amount").orElseThrow(), 0.0001D);

        assertEquals(1, rootView.listeners().size());
        PSTListenerView listenerView = rootView.listeners().get(0);
        assertEquals("kubejs:smoke_listener", listenerView.typeId());
        assertEquals("attack", listenerView.node().string("mode").orElseThrow());

        assertEquals(1, branchView.requirements().size());
        PSTRequirementView requirementView = branchView.requirements().get(0);
        assertEquals("kubejs:smoke_requirement", requirementView.typeId());
        assertFalse(requirementView.passed());
        assertTrue(requirementView.node().bool("allow").orElseThrow());
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

    private static final class TrackingPlayerSkills extends PlayerSkills {
        private int learnSkillCalls;

        @Override
        public boolean learnSkill(PassiveSkill skill) {
            learnSkillCalls++;
            return super.learnSkill(skill);
        }
    }
}
