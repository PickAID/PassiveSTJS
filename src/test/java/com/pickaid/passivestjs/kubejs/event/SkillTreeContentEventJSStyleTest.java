package com.pickaid.passivestjs.kubejs.event;

import com.pickaid.passivestjs.kubejs.id.PSTSkillId;
import com.pickaid.passivestjs.kubejs.id.PSTTreeId;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkillTreeContentEventJSStyleTest {
    @Test
    void publicTreeAndSkillApisUseTypedIdSignatures() throws Exception {
        assertPublicMethod("editTree", PSTTreeId.class);
        assertPublicMethod("editSkill", PSTSkillId.class);
        assertPublicMethod("editStartingSkill", PSTSkillId.class);
        assertPublicMethod("addSkill", PSTSkillId.class, Object.class);
        assertPublicMethod("addStartingSkill", PSTSkillId.class, Object.class);
        assertPublicMethod("addSkillTree", PSTTreeId.class, Object.class);
        assertPublicMethod("disableTree", PSTTreeId.class);
        assertPublicMethod("removeTree", PSTTreeId.class);
        assertPublicMethod("disableSkill", PSTSkillId.class);
        assertPublicMethod("removeSkill", PSTSkillId.class);
    }

    @Test
    void noPublicTreeAndSkillApisExposeLooseObjectOrStringIds() {
        Map<String, List<Method>> methodsByName = Arrays.stream(SkillTreeContentEventJS.class.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .collect(Collectors.groupingBy(Method::getName));

        assertNoPublicIdMethod(methodsByName, "createTree");
        assertNoPublicIdMethod(methodsByName, "editTree");
        assertNoPublicIdMethod(methodsByName, "createSkill");
        assertNoPublicIdMethod(methodsByName, "editSkill");
        assertNoPublicIdMethod(methodsByName, "createStartingSkill");
        assertNoPublicIdMethod(methodsByName, "editStartingSkill");
        assertNoPublicIdMethod(methodsByName, "addSkill");
        assertNoPublicIdMethod(methodsByName, "addStartingSkill");
        assertNoPublicIdMethod(methodsByName, "addSkillTree");
        assertNoPublicIdMethod(methodsByName, "disableTree");
        assertNoPublicIdMethod(methodsByName, "removeTree");
        assertNoPublicIdMethod(methodsByName, "disableSkill");
        assertNoPublicIdMethod(methodsByName, "removeSkill");
    }

    @Test
    void publicApisCarryKubejsDocAnnotations() throws Exception {
        assertHasInfo("registries");
        assertHasInfo("editTree", PSTTreeId.class);
        assertHasInfo("editSkill", PSTSkillId.class);
        assertHasInfo("editStartingSkill", PSTSkillId.class);
        assertHasInfo("addSkill", PSTSkillId.class, Object.class);
        assertHasInfo("addStartingSkill", PSTSkillId.class, Object.class);
        assertHasInfo("addSkillTree", PSTTreeId.class, Object.class);
        assertHasInfo("disableTree", PSTTreeId.class);
        assertHasInfo("removeTree", PSTTreeId.class);
        assertHasInfo("disableSkill", PSTSkillId.class);
        assertHasInfo("removeSkill", PSTSkillId.class);
        assertHasInfo("clearDefaultTree");
        assertHasInfo("clearDefaultTrees");
        assertHasInfo("flushGeneratedContent");
        assertHidden("flushGeneratedContent");
    }

    @Test
    void publicEventApiDoesNotExposeLegacyHelperFactories() {
        Set<String> publicMethodNames = Arrays.stream(SkillTreeContentEventJS.class.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .filter(method -> method.getAnnotation(HideFromJS.class) == null)
                .map(Method::getName)
                .collect(Collectors.toSet());

        assertTrue(publicMethodNames.contains("registries"));
        assertTrue(publicMethodNames.contains("editTree"));
        assertTrue(publicMethodNames.contains("editSkill"));
        assertTrue(publicMethodNames.contains("editStartingSkill"));
        assertFalse(publicMethodNames.contains("createTree"));
        assertFalse(publicMethodNames.contains("createSkill"));
        assertFalse(publicMethodNames.contains("createStartingSkill"));
        assertFalse(publicMethodNames.contains("pst"));
        assertFalse(publicMethodNames.contains("skills"));
        assertFalse(publicMethodNames.contains("json"));
        assertFalse(publicMethodNames.contains("prettyJson"));
        assertFalse(publicMethodNames.contains("skill"));
        assertFalse(publicMethodNames.contains("startingSkill"));
        assertFalse(publicMethodNames.contains("skillTree"));
        assertFalse(publicMethodNames.contains("addDataJson"));
        assertFalse(publicMethodNames.contains("bonuses"));
        assertFalse(publicMethodNames.contains("conditions"));
        assertFalse(publicMethodNames.contains("requirements"));
        assertFalse(publicMethodNames.contains("listeners"));
        assertFalse(publicMethodNames.contains("values"));
        assertFalse(publicMethodNames.contains("multipliers"));
    }

    @Test
    void rawAndLegacyHelperApisAreHiddenFromJs() throws Exception {
        assertHidden("skills");
        assertHidden("json", Object.class);
        assertHidden("prettyJson", Object.class);
        assertHidden("skill", PSTSkillId.class);
        assertHidden("startingSkill", PSTSkillId.class);
        assertHidden("skillTree", PSTTreeId.class);
        assertHidden("disableTree", PSTTreeId.class);
        assertHidden("disableSkill", PSTSkillId.class);
        assertHidden("addDataJson", String.class, Object.class);
    }

    private static void assertPublicMethod(String name, Class<?>... parameterTypes) throws Exception {
        Method method = SkillTreeContentEventJS.class.getDeclaredMethod(name, parameterTypes);
        assertFalse(!Modifier.isPublic(method.getModifiers()), name + " should be public");
    }

    private static void assertHasInfo(String name, Class<?>... parameterTypes) throws Exception {
        Method method = SkillTreeContentEventJS.class.getDeclaredMethod(name, parameterTypes);
        assertNotNull(method.getAnnotation(Info.class), name + " should carry @Info");
    }

    private static void assertHidden(String name, Class<?>... parameterTypes) throws Exception {
        Method method = SkillTreeContentEventJS.class.getDeclaredMethod(name, parameterTypes);
        assertNotNull(method.getAnnotation(HideFromJS.class), name + " should be hidden from JS");
    }

    private static void assertNoPublicIdMethod(Map<String, List<Method>> methodsByName, String name) {
        for (Method method : methodsByName.getOrDefault(name, List.of())) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 0) {
                continue;
            }
            assertFalse(parameterTypes[0] == Object.class, name + " should not expose Object ids publicly");
            assertFalse(parameterTypes[0] == String.class, name + " should not expose String ids publicly");
        }
    }
}
