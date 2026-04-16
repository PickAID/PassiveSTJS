package com.pickaid.passivestjs.kubejs.probe;

import com.pickaid.passivestjs.kubejs.Bindings;
import com.pickaid.passivestjs.kubejs.PassiveSTJSKubePlugin;
import com.pickaid.passivestjs.kubejs.builder.PSTNodeWriter;
import com.pickaid.passivestjs.kubejs.content.ManagedContent;
import com.pickaid.passivestjs.kubejs.event.SkillTreeContentEventJS;
import com.pickaid.passivestjs.kubejs.id.PSTAttributeId;
import com.pickaid.passivestjs.kubejs.id.PSTItemId;
import com.pickaid.passivestjs.kubejs.id.PSTItemTagId;
import com.pickaid.passivestjs.kubejs.id.PSTMobEffectId;
import com.pickaid.passivestjs.kubejs.id.PSTPotionId;
import com.pickaid.passivestjs.kubejs.id.PSTSkillBonusId;
import com.pickaid.passivestjs.kubejs.id.PSTSkillId;
import com.pickaid.passivestjs.kubejs.id.PSTTreeId;
import com.pickaid.passivestjs.kubejs.recipe.PSTWorkbenchItemBonusRecipeJS;
import com.pickaid.passivestjs.kubejs.recipe.builder.PSTAttributeSkillBonusBuilder;
import com.pickaid.passivestjs.kubejs.recipe.builder.PSTItemBonusListBuilder;
import com.pickaid.passivestjs.kubejs.recipe.builder.PSTSkillBonusItemBonusBuilder;
import com.pickaid.passivestjs.kubejs.runtime.PSTBonusView;
import com.pickaid.passivestjs.kubejs.runtime.PSTListenerView;
import com.pickaid.passivestjs.kubejs.runtime.PSTPlayerSkillView;
import com.pickaid.passivestjs.kubejs.runtime.PSTRequirementView;
import com.pickaid.passivestjs.kubejs.texture.PSTSkillFrameType;
import com.pickaid.passivestjs.kubejs.texture.PSTTexture;
import com.pickaid.passivestjs.kubejs.texture.PSTTooltipFrameType;
import com.pickaid.passivestjs.kubejs.type.PSTComparisonLogic;
import com.pickaid.passivestjs.kubejs.type.PSTEquipmentType;
import com.pickaid.passivestjs.kubejs.type.PSTSkillTarget;
import com.pickaid.passivestjs.runtime.PSTRuntimeNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import net.minecraft.resources.ResourceLocation;
import zzzank.probejs.lang.snippet.Snippet;
import zzzank.probejs.lang.snippet.SnippetDump;
import zzzank.probejs.lang.typescript.code.Code;
import zzzank.probejs.lang.typescript.code.member.TypeDecl;
import zzzank.probejs.lang.typescript.code.ts.Wrapped;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.plugin.ProbeJSPlugins;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PassiveSTJSLegacyProbePluginTest {
    @BeforeAll
    static void bootstrap() throws ReflectiveOperationException {
        Field bootstrapped = net.minecraft.server.Bootstrap.class.getDeclaredField("isBootstrapped");
        bootstrapped.setAccessible(true);
        bootstrapped.setBoolean(null, true);

        Field gamePath = net.minecraftforge.fml.loading.FMLLoader.class.getDeclaredField("gamePath");
        gamePath.setAccessible(true);
        gamePath.set(null, Path.of(".").toAbsolutePath().normalize());

        new PassiveSTJSKubePlugin().init();
    }

    @AfterEach
    void cleanup() throws ReflectiveOperationException {
        ProbeJSPlugins.remove(PassiveSTJSLegacyProbePlugin.class);
        Field installed = PassiveSTJSLegacyProbeCompat.class.getDeclaredField("installed");
        installed.setAccessible(true);
        installed.setBoolean(null, false);
        ManagedContent.replace(List.of(), List.of());
    }

    @Test
    void compatRegistersLegacyPluginOnlyOnce() {
        PassiveSTJSLegacyProbeCompat.install();
        PassiveSTJSLegacyProbeCompat.install();

        long registrations = ProbeJSPlugins.getAll().stream()
                .filter(PassiveSTJSLegacyProbePlugin.class::isInstance)
                .count();
        assertEquals(1L, registrations);
    }

    @Test
    void probeTypesBuildSpecialTypeDeclarations() {
        List<String> names = PassiveSTJSLegacyProbeTypes.specialTypeDeclarations().stream()
                .map(type -> type.name)
                .collect(Collectors.toList());

        assertTrue(names.contains("PSTSkillId"));
        assertTrue(names.contains("PSTTreeId"));
        assertTrue(names.contains("PSTSkillBonusId"));
        assertTrue(names.contains("PSTTexture"));
        assertTrue(names.contains("PSTSkillFrameType"));
        assertTrue(names.contains("PSTTooltipFrameType"));
        assertTrue(names.contains("PSTSkillTarget"));
        assertTrue(names.contains("PSTComparisonLogic"));
        assertTrue(names.contains("PSTEquipmentType"));
        assertTrue(names.contains("PSTMobEffectId"));
        assertTrue(names.contains("PSTAttributeId"));
        assertTrue(names.contains("PSTItemId"));
        assertTrue(names.contains("PSTItemTagId"));
        assertTrue(names.contains("PSTPotionId"));

        TypeDecl skillId = PassiveSTJSLegacyProbeTypes.specialTypeDeclarations().stream()
                .filter(type -> "PSTSkillId".equals(type.name))
                .findFirst()
                .orElseThrow();
        assertTrue(skillId.type.line(null).contains("string"));

        TypeDecl texture = PassiveSTJSLegacyProbeTypes.specialTypeDeclarations().stream()
                .filter(type -> "PSTTexture".equals(type.name))
                .findFirst()
                .orElseThrow();
        assertEquals("RawTexture", texture.type.line(null));

        TypeDecl mobEffect = PassiveSTJSLegacyProbeTypes.specialTypeDeclarations().stream()
                .filter(type -> "PSTMobEffectId".equals(type.name))
                .findFirst()
                .orElseThrow();
        TypeDecl attribute = PassiveSTJSLegacyProbeTypes.specialTypeDeclarations().stream()
                .filter(type -> "PSTAttributeId".equals(type.name))
                .findFirst()
                .orElseThrow();
        TypeDecl item = PassiveSTJSLegacyProbeTypes.specialTypeDeclarations().stream()
                .filter(type -> "PSTItemId".equals(type.name))
                .findFirst()
                .orElseThrow();
        TypeDecl itemTag = PassiveSTJSLegacyProbeTypes.specialTypeDeclarations().stream()
                .filter(type -> "PSTItemTagId".equals(type.name))
                .findFirst()
                .orElseThrow();
        TypeDecl potion = PassiveSTJSLegacyProbeTypes.specialTypeDeclarations().stream()
                .filter(type -> "PSTPotionId".equals(type.name))
                .findFirst()
                .orElseThrow();
        assertEquals("MobEffect", mobEffect.type.line(null));
        assertEquals("Attribute", attribute.type.line(null));
        assertEquals("Item", item.type.line(null));
        assertEquals("ItemTag", itemTag.type.line(null));
        assertEquals("Potion", potion.type.line(null));

        TypeDecl target = PassiveSTJSLegacyProbeTypes.specialTypeDeclarations().stream()
                .filter(type -> "PSTSkillTarget".equals(type.name))
                .findFirst()
                .orElseThrow();
        TypeDecl logic = PassiveSTJSLegacyProbeTypes.specialTypeDeclarations().stream()
                .filter(type -> "PSTComparisonLogic".equals(type.name))
                .findFirst()
                .orElseThrow();
        TypeDecl equipmentType = PassiveSTJSLegacyProbeTypes.specialTypeDeclarations().stream()
                .filter(type -> "PSTEquipmentType".equals(type.name))
                .findFirst()
                .orElseThrow();
        assertTrue(target.type.line(null).contains("player"));
        assertTrue(logic.type.line(null).contains("at_least"));
        assertTrue(equipmentType.type.line(null).contains("melee_weapon"));
    }

    @Test
    void pluginProvidesPublicClassesAndSnippets() {
        PassiveSTJSLegacyProbePlugin plugin = new PassiveSTJSLegacyProbePlugin();
        SnippetDump dump = new SnippetDump();

        plugin.addVSCodeSnippets(dump);

        List<String> prefixes = dump.snippets.stream()
                .map(Snippet::getPrefixes)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        assertTrue(prefixes.contains("@pst_skill_id"));
        assertTrue(prefixes.contains("@pst_tree_id"));
        assertTrue(prefixes.contains("@pst_skill_bonus_id"));
        assertTrue(prefixes.contains("@pst_texture"));
        assertTrue(prefixes.contains("@pst_skill_frame_type"));
        assertTrue(prefixes.contains("@pst_tooltip_frame_type"));
        assertTrue(prefixes.contains("@pst_skill_target"));
        assertTrue(prefixes.contains("@pst_comparison_logic"));
        assertTrue(prefixes.contains("@pst_equipment_type"));
        assertTrue(prefixes.contains("@pst_mob_effect_id"));
        assertTrue(prefixes.contains("@pst_attribute_id"));
        assertTrue(prefixes.contains("@pst_item_id"));
        assertTrue(prefixes.contains("@pst_item_tag_id"));
        assertTrue(prefixes.contains("@pst_potion_id"));

        var provided = plugin.provideJavaClass(null);
        assertTrue(provided.contains(Bindings.class));
        assertTrue(provided.contains(SkillTreeContentEventJS.class));
        assertTrue(provided.contains(PSTPlayerSkillView.class));
        assertTrue(provided.contains(PSTBonusView.class));
        assertTrue(provided.contains(PSTListenerView.class));
        assertTrue(provided.contains(PSTRequirementView.class));
        assertTrue(provided.contains(PSTRuntimeNode.class));
        assertTrue(provided.contains(PSTNodeWriter.class));
        assertTrue(provided.contains(PSTWorkbenchItemBonusRecipeJS.class));
        assertTrue(provided.contains(PSTAttributeSkillBonusBuilder.class));
        assertTrue(provided.contains(PSTSkillBonusItemBonusBuilder.class));
        assertTrue(provided.contains(PSTItemBonusListBuilder.class));
    }

    @Test
    void probeTypesIncludeManagedTreeAndSkillIdsInSpecialTypes() {
        ResourceLocation treeId = new ResourceLocation("kubejs", "probe_tree");
        ResourceLocation skillId = new ResourceLocation("kubejs", "probe_skill");
        ManagedContent.replace(List.of(treeId), List.of(skillId));

        TypeDecl treeDecl = PassiveSTJSLegacyProbeTypes.specialTypeDeclarations().stream()
                .filter(type -> "PSTTreeId".equals(type.name))
                .findFirst()
                .orElseThrow();
        TypeDecl skillDecl = PassiveSTJSLegacyProbeTypes.specialTypeDeclarations().stream()
                .filter(type -> "PSTSkillId".equals(type.name))
                .findFirst()
                .orElseThrow();

        assertTrue(treeDecl.type.line(null).contains("kubejs:probe_tree"));
        assertTrue(skillDecl.type.line(null).contains("kubejs:probe_skill"));
    }

    @Test
    void probeJavaDoesNotExposeIdWrappersAsStandaloneProbeClasses() {
        var provided = PassiveSTJSLegacyProbeJava.providedClasses();

        assertFalse(provided.contains(PSTSkillId.class));
        assertFalse(provided.contains(PSTTreeId.class));
        assertFalse(provided.contains(PSTSkillBonusId.class));
        assertFalse(provided.contains(PSTTexture.class));
        assertFalse(provided.contains(PSTSkillFrameType.class));
        assertFalse(provided.contains(PSTTooltipFrameType.class));
        assertFalse(provided.contains(PSTSkillTarget.class));
        assertFalse(provided.contains(PSTComparisonLogic.class));
        assertFalse(provided.contains(PSTEquipmentType.class));
        assertFalse(provided.contains(PSTMobEffectId.class));
        assertFalse(provided.contains(PSTAttributeId.class));
        assertFalse(provided.contains(PSTItemId.class));
        assertFalse(provided.contains(PSTItemTagId.class));
        assertFalse(provided.contains(PSTPotionId.class));
    }

    @Test
    void probeTypesBuildModuleAliasesForIdWrappers() {
        List<TypeDecl> declarations = PassiveSTJSLegacyProbeTypes.wrapperTypeDeclarations().stream().toList();

        TypeDecl skillAlias = declarations.stream()
                .filter(type -> "$PSTSkillId".equals(type.name))
                .findFirst()
                .orElseThrow();
        TypeDecl skillTypeAlias = declarations.stream()
                .filter(type -> "$PSTSkillId$$Type".equals(type.name))
                .findFirst()
                .orElseThrow();
        TypeDecl bonusTypeAlias = declarations.stream()
                .filter(type -> "$PSTSkillBonusId$$Type".equals(type.name))
                .findFirst()
                .orElseThrow();
        TypeDecl textureAlias = declarations.stream()
                .filter(type -> "$PSTTexture".equals(type.name))
                .findFirst()
                .orElseThrow();
        TypeDecl targetAlias = declarations.stream()
                .filter(type -> "$PSTSkillTarget".equals(type.name))
                .findFirst()
                .orElseThrow();
        TypeDecl logicAlias = declarations.stream()
                .filter(type -> "$PSTComparisonLogic".equals(type.name))
                .findFirst()
                .orElseThrow();
        TypeDecl equipmentAlias = declarations.stream()
                .filter(type -> "$PSTEquipmentType".equals(type.name))
                .findFirst()
                .orElseThrow();
        TypeDecl mobEffectAlias = declarations.stream()
                .filter(type -> "$PSTMobEffectId".equals(type.name))
                .findFirst()
                .orElseThrow();
        TypeDecl itemAlias = declarations.stream()
                .filter(type -> "$PSTItemId".equals(type.name))
                .findFirst()
                .orElseThrow();

        assertEquals("Special.PSTSkillId", skillAlias.type.line(null));
        assertEquals("Special.PSTSkillId", skillTypeAlias.type.line(null));
        assertEquals("Special.PSTSkillBonusId", bonusTypeAlias.type.line(null));
        assertEquals("Special.PSTTexture", textureAlias.type.line(null));
        assertEquals("Special.PSTSkillTarget", targetAlias.type.line(null));
        assertEquals("Special.PSTComparisonLogic", logicAlias.type.line(null));
        assertEquals("Special.PSTEquipmentType", equipmentAlias.type.line(null));
        assertEquals("Special.PSTMobEffectId", mobEffectAlias.type.line(null));
        assertEquals("Special.PSTItemId", itemAlias.type.line(null));
    }

    @Test
    void probeTypesRewriteWrapperFilesToSingleSpecialTypeAlias() {
        List<Code> codes = new ArrayList<>();
        codes.add(new TypeDecl("$PSTSkillId", Types.primitive("Special.PSTSkillId")));
        codes.add(new TypeDecl("$PSTSkillId$$Type", Types.primitive("Special.PSTSkillId")));
        codes.add(new TypeDecl("$PSTSkillId$$Type", Types.primitive("($PSTSkillId | $PSTSkillId | $PSTSkillId$$Type)")));
        Wrapped.Global global = new Wrapped.Global();
        global.addCode(new TypeDecl("$PSTSkillId_", Types.primitive("$PSTSkillId$$Type")));
        codes.add(global);

        PassiveSTJSLegacyProbeTypes.rewriteWrapperCodes(codes, PSTSkillId.class.getSimpleName(), "PSTSkillId");

        List<TypeDecl> declarations = codes.stream()
                .filter(TypeDecl.class::isInstance)
                .map(TypeDecl.class::cast)
                .toList();
        long typeAliasCount = declarations.stream()
                .filter(type -> "$PSTSkillId$$Type".equals(type.name))
                .count();
        TypeDecl typeAlias = declarations.stream()
                .filter(type -> "$PSTSkillId$$Type".equals(type.name))
                .findFirst()
                .orElseThrow();

        assertEquals(1L, typeAliasCount);
        assertEquals("Special.PSTSkillId", typeAlias.type.line(null));
        assertEquals(1L, codes.stream().filter(Wrapped.Global.class::isInstance).count());
    }

    @Test
    void pluginUsesCanonicalSpecialTypesGlobalFile() {
        assertEquals("special_types", PassiveSTJSLegacyProbePlugin.specialTypesGlobalName());
    }

    @Test
    void probeTypesMarkIdWrappersForTranspilerRejection() {
        var denied = PassiveSTJSLegacyProbeTypes.deniedWrapperClasses();

        assertTrue(denied.contains(PSTSkillId.class));
        assertTrue(denied.contains(PSTTreeId.class));
        assertTrue(denied.contains(PSTSkillBonusId.class));
        assertTrue(denied.contains(PSTTexture.class));
        assertTrue(denied.contains(PSTSkillFrameType.class));
        assertTrue(denied.contains(PSTTooltipFrameType.class));
        assertTrue(denied.contains(PSTMobEffectId.class));
        assertTrue(denied.contains(PSTAttributeId.class));
        assertTrue(denied.contains(PSTItemId.class));
        assertTrue(denied.contains(PSTItemTagId.class));
        assertTrue(denied.contains(PSTPotionId.class));
    }

    @Test
    void cleanupDeletesLegacyGlobalFiles(@TempDir Path tempDir) throws Exception {
        Path globalDir = tempDir.resolve("global");
        Files.createDirectories(globalDir);
        Path legacySpecialTypes = Files.createFile(globalDir.resolve("passivestjs_special_types.d.ts"));
        Path legacyAliases = Files.createFile(globalDir.resolve("passivestjs_id_aliases.d.ts"));

        PassiveSTJSLegacyProbeTypes.cleanupLegacyGlobalFiles(tempDir);

        assertFalse(Files.exists(legacySpecialTypes));
        assertFalse(Files.exists(legacyAliases));
    }
}
