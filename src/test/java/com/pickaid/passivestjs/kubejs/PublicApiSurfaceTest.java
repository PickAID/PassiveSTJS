package com.pickaid.passivestjs.kubejs;

import com.pickaid.passivestjs.kubejs.builder.SkillBuilder;
import com.pickaid.passivestjs.kubejs.builder.SkillTreeBuilder;
import com.pickaid.passivestjs.kubejs.builder.BonusBuilder;
import com.pickaid.passivestjs.kubejs.builder.ConditionBuilder;
import com.pickaid.passivestjs.kubejs.builder.ListenerBuilder;
import com.pickaid.passivestjs.kubejs.builder.RequirementBuilder;
import com.pickaid.passivestjs.kubejs.builder.ValueBuilder;
import com.pickaid.passivestjs.kubejs.event.SkillTreeContentEventJS;
import com.pickaid.passivestjs.kubejs.id.PSTAttributeId;
import com.pickaid.passivestjs.kubejs.id.PSTItemId;
import com.pickaid.passivestjs.kubejs.id.PSTItemTagId;
import com.pickaid.passivestjs.kubejs.id.PSTMobEffectId;
import com.pickaid.passivestjs.kubejs.id.PSTPotionId;
import com.pickaid.passivestjs.kubejs.id.PSTSkillId;
import com.pickaid.passivestjs.kubejs.id.PSTTreeId;
import com.pickaid.passivestjs.kubejs.runtime.PSTPlayerView;
import com.pickaid.passivestjs.kubejs.runtime.PSTSkillView;
import com.pickaid.passivestjs.runtime.PSTCustomRuntimeContexts;
import com.pickaid.passivestjs.kubejs.texture.PSTSkillFrameType;
import com.pickaid.passivestjs.kubejs.texture.PSTTexture;
import com.pickaid.passivestjs.kubejs.texture.PSTTooltipFrameType;
import com.pickaid.passivestjs.kubejs.type.PSTComparisonLogic;
import com.pickaid.passivestjs.kubejs.type.PSTEquipmentType;
import com.pickaid.passivestjs.kubejs.type.PSTSkillTarget;
import com.pickaid.passivestjs.schema.PSTSchemaBuilder;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PublicApiSurfaceTest {
    @Test
    void bindingsExposeOnlyFormalJsSurface() throws Exception {
        Set<String> visibleMethodNames = visibleMethodNames(Bindings.class);
        Set<String> declaredPublicMethodNames = declaredPublicMethodNames(Bindings.class);

        assertEquals(Set.of("player", "skill", "tree"), visibleMethodNames);
        assertEquals(Set.of("player", "skill", "tree"), declaredPublicMethodNames);
        assertInfo(Bindings.class.getDeclaredMethod("player", net.minecraft.world.entity.player.Player.class));
        assertInfo(Bindings.class.getDeclaredMethod("skill", PSTSkillId.class));
        assertInfo(Bindings.class.getDeclaredMethod("tree", PSTTreeId.class));
    }

    @Test
    void publicApisUseTypedSkillAndTreeIdsInsteadOfBareResourceLocations() throws Exception {
        assertNotNull(Bindings.class.getDeclaredMethod("skill", PSTSkillId.class));
        assertNotNull(Bindings.class.getDeclaredMethod("tree", PSTTreeId.class));
        assertNotNull(PSTPlayerView.class.getDeclaredMethod("hasSkill", PSTSkillId.class));
        assertNotNull(PSTPlayerView.class.getDeclaredMethod("learn", PSTSkillId.class));
        assertNotNull(PSTPlayerView.class.getDeclaredMethod("learnWithoutSkillPointCost", PSTSkillId.class));
        assertNotNull(PSTPlayerView.class.getDeclaredMethod("learnWithSkillPointCost", PSTSkillId.class, int.class));
        assertNotNull(PSTPlayerView.class.getDeclaredMethod("remove", PSTSkillId.class));
        assertNotNull(PSTPlayerView.class.getDeclaredMethod("learnedSkill", PSTSkillId.class));
        assertNotNull(SkillTreeContentEventJS.class.getDeclaredMethod("editTree", PSTTreeId.class));
        assertNotNull(SkillTreeContentEventJS.class.getDeclaredMethod("editSkill", PSTSkillId.class));
        assertNotNull(SkillTreeContentEventJS.class.getDeclaredMethod("editStartingSkill", PSTSkillId.class));
        assertNotNull(SkillTreeBuilder.class.getDeclaredMethod("skill", PSTSkillId.class));
        assertNotNull(SkillTreeBuilder.class.getDeclaredMethod("startingSkill", PSTSkillId.class));
        assertNotNull(SkillTreeBuilder.class.getDeclaredMethod("includeSkill", PSTSkillId.class));
        assertNotNull(SkillBuilder.class.getDeclaredMethod("bonus", com.pickaid.passivestjs.kubejs.id.PSTSkillBonusId.class, java.util.function.Consumer.class));
        assertNotNull(SkillBuilder.class.getDeclaredMethod("requirement", com.pickaid.passivestjs.kubejs.id.PSTSkillRequirementId.class, java.util.function.Consumer.class));
        assertNotNull(SkillBuilder.class.getDeclaredMethod("connect", PSTSkillId.class));
        assertNotNull(SkillBuilder.class.getDeclaredMethod("longConnect", PSTSkillId.class));
        assertNotNull(SkillBuilder.class.getDeclaredMethod("oneWayConnect", PSTSkillId.class));
        assertNotNull(SkillBuilder.class.getDeclaredMethod("requiresSkill", PSTSkillId.class));

        assertFalse(hasPublicSignature(Bindings.class, "skill", ResourceLocation.class));
        assertFalse(hasPublicSignature(Bindings.class, "tree", ResourceLocation.class));
        assertFalse(hasPublicSignature(PSTPlayerView.class, "hasSkill", ResourceLocation.class));
        assertFalse(hasPublicSignature(PSTPlayerView.class, "learn", ResourceLocation.class));
        assertFalse(hasPublicSignature(PSTPlayerView.class, "learnWithoutSkillPointCost", ResourceLocation.class));
        assertFalse(hasPublicSignature(PSTPlayerView.class, "learnWithSkillPointCost", ResourceLocation.class, int.class));
        assertFalse(hasPublicSignature(PSTPlayerView.class, "remove", ResourceLocation.class));
        assertFalse(hasPublicSignature(PSTPlayerView.class, "learnedSkill", ResourceLocation.class));
        assertFalse(hasPublicSignature(SkillTreeContentEventJS.class, "editTree", ResourceLocation.class));
        assertFalse(hasPublicSignature(SkillTreeContentEventJS.class, "editSkill", ResourceLocation.class));
        assertFalse(hasPublicSignature(SkillTreeContentEventJS.class, "editStartingSkill", ResourceLocation.class));
        assertFalse(hasPublicSignature(SkillBuilder.class, "connect", SkillBuilder.class));
        assertFalse(hasPublicSignature(SkillBuilder.class, "longConnect", SkillBuilder.class));
        assertFalse(hasPublicSignature(SkillBuilder.class, "oneWayConnect", SkillBuilder.class));
        assertFalse(hasPublicSignature(SkillBuilder.class, "requiresSkill", SkillBuilder.class));
    }

    @Test
    void skillTreeContentEventUsesEditAsVisibleManagedEntryPoint() throws Exception {
        Set<String> visibleMethodNames = visibleMethodNames(SkillTreeContentEventJS.class);

        assertTrue(visibleMethodNames.contains("registries"));
        assertTrue(visibleMethodNames.contains("editTree"));
        assertTrue(visibleMethodNames.contains("editSkill"));
        assertTrue(visibleMethodNames.contains("editStartingSkill"));

        assertFalse(visibleMethodNames.contains("createTree"));
        assertFalse(visibleMethodNames.contains("createSkill"));
        assertFalse(visibleMethodNames.contains("createStartingSkill"));
        assertFalse(visibleMethodNames.contains("disableTree"));
        assertFalse(visibleMethodNames.contains("disableSkill"));

        Method createTree = SkillTreeContentEventJS.class.getDeclaredMethod("createTree", PSTTreeId.class);
        Method createSkill = SkillTreeContentEventJS.class.getDeclaredMethod("createSkill", PSTSkillId.class);
        Method createStartingSkill = SkillTreeContentEventJS.class.getDeclaredMethod("createStartingSkill", PSTSkillId.class);
        assertFalse(Modifier.isPublic(createTree.getModifiers()));
        assertFalse(Modifier.isPublic(createSkill.getModifiers()));
        assertFalse(Modifier.isPublic(createStartingSkill.getModifiers()));

        assertHidden(SkillTreeContentEventJS.class.getDeclaredMethod("disableTree", PSTTreeId.class));
        assertHidden(SkillTreeContentEventJS.class.getDeclaredMethod("disableSkill", PSTSkillId.class));
        assertInfo(SkillTreeContentEventJS.class.getDeclaredMethod("editTree", PSTTreeId.class));
        assertInfo(SkillTreeContentEventJS.class.getDeclaredMethod("editSkill", PSTSkillId.class));
        assertInfo(SkillTreeContentEventJS.class.getDeclaredMethod("editStartingSkill", PSTSkillId.class));
    }

    @Test
    void contentTextApisUseComponentFirstEntryPoints() throws Exception {
        assertNotNull(SkillBuilder.class.getDeclaredMethod("title", Component.class));
        assertNotNull(SkillBuilder.class.getDeclaredMethod("descriptionLine", Component.class));
        assertNotNull(SkillTreeBuilder.class.getDeclaredMethod("title", Component.class));
        assertNotNull(PSTSchemaBuilder.FieldBuilder.class.getDeclaredMethod("doc", Component.class));
        assertNotNull(SkillBuilder.class.getDeclaredMethod("icon", PSTTexture.class));
        assertNotNull(SkillBuilder.class.getDeclaredMethod("background", PSTTexture.class));
        assertNotNull(SkillBuilder.class.getDeclaredMethod("border", PSTTexture.class));
        assertNotNull(SkillBuilder.class.getDeclaredMethod("frame", PSTSkillFrameType.class));
        assertNotNull(SkillBuilder.class.getDeclaredMethod("tooltipFrame", PSTTooltipFrameType.class));

        assertFalse(hasPublicSignature(SkillBuilder.class, "title", String.class));
        assertFalse(hasPublicSignature(SkillBuilder.class, "descriptionLine", String.class));
        assertFalse(hasPublicSignature(SkillBuilder.class, "descriptionLine", Object.class));
        assertFalse(hasPublicSignature(SkillTreeBuilder.class, "title", String.class));
        assertFalse(hasPublicSignature(PSTSchemaBuilder.FieldBuilder.class, "doc", String.class));
    }

    @Test
    void runtimeTitleViewReturnsComponent() throws Exception {
        assertEquals(Component.class, PSTSkillView.class.getDeclaredMethod("title").getReturnType());
        assertNotNull(PSTCustomRuntimeContexts.TickListenerContext.class.getDeclaredMethod("tickCount"));
    }

    @Test
    void playerRuntimeViewsExposeHelperSurface() throws Exception {
        Class<?> playerView = Class.forName("com.pickaid.passivestjs.kubejs.runtime.PSTPlayerView");
        Class<?> playerSkillView = Class.forName("com.pickaid.passivestjs.kubejs.runtime.PSTPlayerSkillView");
        Class<?> bonusView = Class.forName("com.pickaid.passivestjs.kubejs.runtime.PSTBonusView");
        Class<?> listenerView = Class.forName("com.pickaid.passivestjs.kubejs.runtime.PSTListenerView");
        Class<?> requirementView = Class.forName("com.pickaid.passivestjs.kubejs.runtime.PSTRequirementView");
        Class<?> runtimeNode = Class.forName("com.pickaid.passivestjs.runtime.PSTRuntimeNode");

        Method skillMethod = playerView.getDeclaredMethod("skill", PSTSkillId.class);
        assertEquals(playerSkillView, skillMethod.getReturnType());

        assertPublicReturn(playerSkillView.getDeclaredMethod("learned"), boolean.class);
        assertPublicReturn(playerSkillView.getDeclaredMethod("canLearn"), boolean.class);
        assertPublicReturn(playerSkillView.getDeclaredMethod("learn"), boolean.class);
        assertPublicReturn(playerSkillView.getDeclaredMethod("remove"), boolean.class);
        assertPublicReturn(playerSkillView.getDeclaredMethod("requirements"), List.class);
        assertPublicReturn(playerSkillView.getDeclaredMethod("bonuses"), List.class);
        assertPublicReturn(playerSkillView.getDeclaredMethod("listeners"), List.class);

        assertEquals(Component.class, bonusView.getDeclaredMethod("text").getReturnType());
        assertEquals(runtimeNode, bonusView.getDeclaredMethod("node").getReturnType());

        assertEquals(Component.class, listenerView.getDeclaredMethod("text").getReturnType());
        assertEquals(runtimeNode, listenerView.getDeclaredMethod("node").getReturnType());

        assertEquals(Component.class, requirementView.getDeclaredMethod("text").getReturnType());
        assertEquals(runtimeNode, requirementView.getDeclaredMethod("node").getReturnType());
    }

    @Test
    void resourceDslApisUseTypedRegistryWrappersForAutocomplete() throws Exception {
        assertNotNull(BonusBuilder.class.getDeclaredMethod("effect", PSTMobEffectId.class));
        assertNotNull(BonusBuilder.class.getDeclaredMethod("effectType", PSTMobEffectId.class));
        assertNotNull(BonusBuilder.class.getDeclaredMethod("attribute", PSTAttributeId.class));
        assertNotNull(BonusBuilder.class.getDeclaredMethod("target", PSTSkillTarget.class));
        assertNotNull(BonusBuilder.class.getDeclaredMethod("onAttack", PSTSkillTarget.class));
        assertNotNull(ConditionBuilder.class.getDeclaredMethod("effect", PSTMobEffectId.class));
        assertNotNull(ConditionBuilder.class.getDeclaredMethod("logic", PSTComparisonLogic.class));
        assertNotNull(ConditionBuilder.class.getDeclaredMethod("equipmentType", PSTEquipmentType.class));
        assertNotNull(ConditionBuilder.class.getDeclaredMethod("potionType", PSTPotionId.class));
        assertNotNull(ConditionBuilder.class.getDeclaredMethod("itemId", PSTItemId.class));
        assertNotNull(ConditionBuilder.class.getDeclaredMethod("tagId", PSTItemTagId.class));
        assertNotNull(ListenerBuilder.class.getDeclaredMethod("target", PSTSkillTarget.class));
        assertNotNull(RequirementBuilder.class.getDeclaredMethod("logic", PSTComparisonLogic.class));
        assertNotNull(ValueBuilder.class.getDeclaredMethod("effectType", PSTMobEffectId.class));
        assertNotNull(ValueBuilder.class.getDeclaredMethod("attribute", PSTAttributeId.class));
        assertNotNull(RequirementBuilder.class.getDeclaredMethod("skill", PSTSkillId.class));

        assertHidden(BonusBuilder.class.getDeclaredMethod("effect", String.class));
        assertHidden(BonusBuilder.class.getDeclaredMethod("effectType", String.class));
        assertHidden(BonusBuilder.class.getDeclaredMethod("attribute", String.class));
        assertHidden(BonusBuilder.class.getDeclaredMethod("target", String.class));
        assertHidden(ConditionBuilder.class.getDeclaredMethod("effect", String.class));
        assertHidden(ConditionBuilder.class.getDeclaredMethod("logic", String.class));
        assertHidden(ConditionBuilder.class.getDeclaredMethod("equipmentType", String.class));
        assertHidden(ConditionBuilder.class.getDeclaredMethod("potionType", String.class));
        assertHidden(ConditionBuilder.class.getDeclaredMethod("itemId", String.class));
        assertHidden(ConditionBuilder.class.getDeclaredMethod("tagId", String.class));
        assertHidden(ListenerBuilder.class.getDeclaredMethod("target", String.class));
        assertHidden(RequirementBuilder.class.getDeclaredMethod("logic", String.class));
        assertHidden(ValueBuilder.class.getDeclaredMethod("effectType", String.class));
        assertHidden(ValueBuilder.class.getDeclaredMethod("attribute", String.class));
        assertHidden(RequirementBuilder.class.getDeclaredMethod("skill", Object.class));

        assertInfo(BonusBuilder.class.getDeclaredMethod("effect", PSTMobEffectId.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("effectType", PSTMobEffectId.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("attribute", PSTAttributeId.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("target", PSTSkillTarget.class));
        assertInfo(BonusBuilder.class.getDeclaredMethod("onAttack", PSTSkillTarget.class));
        assertInfo(ConditionBuilder.class.getDeclaredMethod("effect", PSTMobEffectId.class));
        assertInfo(ConditionBuilder.class.getDeclaredMethod("logic", PSTComparisonLogic.class));
        assertInfo(ConditionBuilder.class.getDeclaredMethod("equipmentType", PSTEquipmentType.class));
        assertInfo(ConditionBuilder.class.getDeclaredMethod("potionType", PSTPotionId.class));
        assertInfo(ConditionBuilder.class.getDeclaredMethod("itemId", PSTItemId.class));
        assertInfo(ConditionBuilder.class.getDeclaredMethod("tagId", PSTItemTagId.class));
        assertInfo(ListenerBuilder.class.getDeclaredMethod("target", PSTSkillTarget.class));
        assertInfo(RequirementBuilder.class.getDeclaredMethod("logic", PSTComparisonLogic.class));
        assertInfo(ValueBuilder.class.getDeclaredMethod("effectType", PSTMobEffectId.class));
        assertInfo(ValueBuilder.class.getDeclaredMethod("attribute", PSTAttributeId.class));
        assertInfo(RequirementBuilder.class.getDeclaredMethod("skill", PSTSkillId.class));
    }

    private static Set<String> declaredPublicMethodNames(Class<?> type) {
        return Arrays.stream(type.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .map(Method::getName)
                .collect(Collectors.toSet());
    }

    private static Set<String> visibleMethodNames(Class<?> type) {
        return Arrays.stream(type.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .filter(method -> Objects.isNull(method.getAnnotation(HideFromJS.class)))
                .map(Method::getName)
                .collect(Collectors.toSet());
    }

    private static void assertHasMethod(Class<?> type, String name) {
        assertTrue(Arrays.stream(type.getDeclaredMethods()).anyMatch(method -> method.getName().equals(name)),
                type.getName() + " should have " + name);
    }

    private static void assertHidden(Method method) {
        assertNotNull(method.getAnnotation(HideFromJS.class), method.getName() + " should be hidden from JS");
    }

    private static void assertInfo(Method method) {
        assertNotNull(method.getAnnotation(Info.class), method.getName() + " should carry @Info");
    }

    private static void assertPublicReturn(Method method, Class<?> expectedReturn) {
        assertTrue(Modifier.isPublic(method.getModifiers()), method.getName() + " should be public");
        assertEquals(expectedReturn, method.getReturnType(), method.getName() + " should return " + expectedReturn.getName());
    }

    private static boolean hasPublicSignature(Class<?> type, String name, Class<?>... parameterTypes) {
        return Arrays.stream(type.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .anyMatch(method -> method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), parameterTypes));
    }
}
