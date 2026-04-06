package com.pickaid.passivestjs.kubejs.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkillTreeBuilderTest {
    @Test
    void treeJsonIncludesBuiltSkillsAndLimits() {
        SkillTreeBuilder tree = new SkillTreeBuilder("kubejs:pi_test_tree");
        var root = tree.startingSkill("kubejs:pi_test_root").title("PI Test Root");
        tree.skill("kubejs:pi_test_child").title("PI Test Child").connect(root);
        tree.limit("pi_test", 2);

        JsonObject json = tree.toJson();
        JsonArray skillIds = json.getAsJsonArray("skillIds");

        assertEquals("kubejs:pi_test_tree", json.get("id").getAsString());
        assertEquals(2, skillIds.size());
        assertTrue(skillIds.toString().contains("kubejs:pi_test_root"));
        assertTrue(skillIds.toString().contains("kubejs:pi_test_child"));
        assertEquals(2, json.getAsJsonObject("skillLimitations").get("pi_test").getAsInt());
    }

    @Test
    void existingTreeJsonCanBeExtendedWithoutDroppingOriginalSkillIds() {
        JsonObject existing = new JsonObject();
        existing.addProperty("id", "kubejs:pi_test_tree");
        JsonArray skillIds = new JsonArray();
        skillIds.add(new JsonPrimitive("minecraft:existing_root"));
        existing.add("skillIds", skillIds);

        SkillTreeBuilder tree = SkillTreeBuilder.fromJson(existing);
        tree.skill("kubejs:pi_test_child").title("PI Test Child");

        JsonArray mergedSkillIds = tree.toJson().getAsJsonArray("skillIds");
        assertEquals(2, mergedSkillIds.size());
        assertTrue(mergedSkillIds.toString().contains("minecraft:existing_root"));
        assertTrue(mergedSkillIds.toString().contains("kubejs:pi_test_child"));
    }

    @Test
    void addingBuiltSkillReplacesMatchingExternalId() {
        JsonObject existing = new JsonObject();
        existing.addProperty("id", "kubejs:pi_test_tree");
        JsonArray skillIds = new JsonArray();
        skillIds.add(new JsonPrimitive("kubejs:pi_test_root"));
        existing.add("skillIds", skillIds);

        SkillTreeBuilder tree = SkillTreeBuilder.fromJson(existing);
        tree.addSkill(new SkillBuilder("kubejs:pi_test_root", false).title("PI Test Root"));

        JsonArray mergedSkillIds = tree.toJson().getAsJsonArray("skillIds");
        assertEquals(1, mergedSkillIds.size());
        assertEquals("kubejs:pi_test_root", mergedSkillIds.get(0).getAsString());
    }
}
