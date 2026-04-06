package com.pickaid.passivestjs.kubejs.content;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SkillsPathTest {
    @Test
    void skillContentUsesPstSkillsFolder() {
        ResourceLocation path = Skills.toSkillResource(new ResourceLocation("kubejs", "pi_test_root"));
        assertEquals("kubejs:skills/pi_test_root", path.toString());
    }

    @Test
    void treeContentUsesPstSkillTreesFolder() {
        ResourceLocation path = Skills.toSkillTreeResource(new ResourceLocation("kubejs", "pi_test_tree"));
        assertEquals("kubejs:skill_trees/pi_test_tree", path.toString());
    }
}
