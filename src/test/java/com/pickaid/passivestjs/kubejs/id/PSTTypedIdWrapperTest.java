package com.pickaid.passivestjs.kubejs.id;

import com.pickaid.passivestjs.kubejs.builder.SkillBuilder;
import com.pickaid.passivestjs.kubejs.builder.SkillTreeBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PSTTypedIdWrapperTest {
    @Test
    void skillIdWrapperAcceptsBuiltSkillInstances() {
        SkillBuilder skill = new SkillBuilder("kubejs:builder_skill", false);

        PSTSkillId wrapped = PSTSkillId.parse(skill);

        assertEquals("kubejs:builder_skill", wrapped.id());
    }

    @Test
    void treeIdWrapperAcceptsBuiltTreeInstances() {
        SkillTreeBuilder tree = new SkillTreeBuilder("kubejs:builder_tree");

        PSTTreeId wrapped = PSTTreeId.parse(tree);

        assertEquals("kubejs:builder_tree", wrapped.id());
    }
}
