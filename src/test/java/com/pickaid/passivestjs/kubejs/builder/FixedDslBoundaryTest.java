package com.pickaid.passivestjs.kubejs.builder;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FixedDslBoundaryTest {
    @Test
    void skillTreeBuilderDoesNotExposeSchemaDrivenTopLevelFields() {
        Set<String> methodNames = methodNames(SkillTreeBuilder.class);

        assertTrue(methodNames.contains("skill"));
        assertTrue(methodNames.contains("startingSkill"));
        assertTrue(methodNames.contains("limit"));
        assertFalse(methodNames.contains("time"));
    }

    @Test
    void skillBuilderKeepsKnownDslAndDoesNotExposeArbitraryTopLevelFields() {
        Set<String> methodNames = methodNames(SkillBuilder.class);

        assertTrue(methodNames.contains("position"));
        assertTrue(methodNames.contains("positionPolar"));
        assertTrue(methodNames.contains("bonus"));
        assertTrue(methodNames.contains("requirement"));
        assertFalse(methodNames.contains("time"));
    }

    private static Set<String> methodNames(Class<?> type) {
        return Arrays.stream(type.getDeclaredMethods())
                .map(Method::getName)
                .collect(Collectors.toSet());
    }
}
