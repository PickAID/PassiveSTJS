package com.pickaid.passivestjs.kubejs;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KubeJsRuntimeHelperExamplesTest {
    @Test
    void runtimeHelperExampleUsesPlayerBoundSkillViews() throws IOException {
        String text = Files.readString(Path.of("examples/kubejs/passivestjs/07_server_runtime_helper_samples.js").toAbsolutePath());

        assertTrue(text.contains("PassiveSkillTreeJS.player("));
        assertTrue(text.contains(".skill(pulseSkillId)"));
        assertTrue(text.contains("kubejs:passivestjs/runtime_threshold"));
        assertTrue(text.contains("kubejs:smoke_bonus"));
        assertTrue(text.contains(".bonuses()"));
        assertTrue(text.contains(".listeners()"));
        assertTrue(text.contains(".requirements()"));
        assertTrue(text.contains(".canLearn()"));
        assertFalse(text.contains("console."));
    }

    @Test
    void itemBonusRuntimeHelperExampleUsesItemBindingAndMutationHelpers() throws IOException {
        String text = Files.readString(Path.of("examples/kubejs/passivestjs/09_server_item_bonus_runtime_api.js").toAbsolutePath());

        assertTrue(text.contains("PassiveSkillTreeJS.item("));
        assertTrue(text.contains(".addItemBonus('kubejs:sample_item_bonus'"));
        assertTrue(text.contains(".addAttributeItemBonus("));
        assertTrue(text.contains(".bonusCount()"));
        assertTrue(text.contains(".bonuses()"));
        assertTrue(text.contains(".clearBonuses()"));
        assertFalse(text.contains("console."));
    }

    @Test
    void runtimeApiExampleUsesCurrentBindingWithoutConsoleLogging() throws IOException {
        String text = Files.readString(Path.of("examples/kubejs/passivestjs/03_server_runtime_api.js").toAbsolutePath());

        assertTrue(text.contains("PassiveSkillTreeJS.player("));
        assertTrue(text.contains("PassiveSkillTreeJS.tree("));
        assertTrue(text.contains("PassiveSkillTreeJS.skill("));
        assertFalse(text.contains("PST."));
        assertFalse(text.contains("console."));
    }

    @Test
    void readmeReferencesCurrentBinding() throws IOException {
        String text = Files.readString(Path.of("examples/kubejs/passivestjs/README.md").toAbsolutePath());

        assertTrue(text.contains("PassiveSkillTreeJS.player(...)"));
        assertTrue(text.contains("PassiveSkillTreeJS.item(...)"));
        assertFalse(text.contains("PST.player(...)"));
    }

    @Test
    void workbenchRecipeExampleUsesCanonicalSkilltreePathAndSchemaKeys() throws IOException {
        String text = Files.readString(Path.of("examples/kubejs/passivestjs/08_server_workbench_item_bonus_recipe.js").toAbsolutePath());

        assertTrue(text.contains("event.recipes.skilltree.workbench_item_bonus("));
        assertTrue(text.contains(".baseEquipmentTypeCondition('shield')"));
        assertTrue(text.contains(".ingredientTag('forge:ingots/copper', 2)"));
        assertTrue(text.contains(".attributeItemBonus(bonus => {"));
        assertTrue(text.contains("bonus.attribute('minecraft:generic.armor')"));
        assertTrue(text.contains("bonus.modifierId('8516d3f4-373e-42c3-9138-3215993b34c4')"));
        assertTrue(text.contains(".requiresPassiveSkill()"));
        assertTrue(text.contains(".id('kubejs:passivestjs_showcase_shield_socket')"));
        assertFalse(text.contains("console."));
    }

    @Test
    void workbenchRecipeExampleDoesNotReferenceDeletedRunCopies() throws IOException {
        String text = Files.readString(Path.of("examples/kubejs/passivestjs/08_server_workbench_item_bonus_recipe.js").toAbsolutePath());

        assertFalse(text.contains("run/client"));
        assertFalse(text.contains("run/server"));
    }

    @Test
    void sampleSkillTreeContentIncludesSocketGuidanceNode() throws IOException {
        String text = Files.readString(Path.of("examples/kubejs/passivestjs/02_server_skill_tree_content.js").toAbsolutePath());

        assertTrue(text.contains("PST_SAMPLE_SOCKET_ID"));
        assertTrue(text.contains("kubejs:sample_runtime_tree/item_bonus_socket"));
        assertTrue(text.contains("kubejs.passivestjs.sample.socket.title"));
        assertTrue(text.contains("kubejs.passivestjs.sample.socket.desc.0"));
        assertTrue(text.contains("kubejs.passivestjs.sample.socket.desc.1"));
        assertTrue(text.contains("minecraft:textures/item/copper_ingot.png"));
    }

    @Test
    void sampleSkillTreeContentIncludesItemBonusRecipeFlowNode() throws IOException {
        String text = Files.readString(Path.of("examples/kubejs/passivestjs/02_server_skill_tree_content.js").toAbsolutePath());

        assertTrue(text.contains("PST_SAMPLE_SOCKET_ID"));
        assertTrue(text.contains("kubejs:sample_runtime_tree/item_bonus_socket"));
        assertTrue(text.contains("kubejs.passivestjs.sample.socket.title"));
        assertTrue(text.contains("kubejs.passivestjs.sample.socket.desc.0"));
        assertTrue(text.contains("kubejs.passivestjs.sample.socket.desc.1"));
    }

    @Test
    void sampleScriptsAvoidConsoleLoggingLegacyRuntimeBindingAndRunDirectoryReferences() throws IOException {
        List<Path> roots = List.of(Path.of("examples/kubejs/passivestjs").toAbsolutePath());

        for (Path root : roots) {
            try (Stream<Path> paths = Files.walk(root)) {
                paths.filter(Files::isRegularFile)
                    .filter(path -> {
                        String name = path.getFileName().toString();
                        return name.endsWith(".js") || name.endsWith(".md");
                    })
                    .forEach(path -> {
                        try {
                            String text = Files.readString(path);
                            assertFalse(text.contains("console."), () -> "Unexpected console logging in " + path);
                            assertFalse(text.contains("PST.player("), () -> "Legacy PST.player binding in " + path);
                            assertFalse(text.contains("PST.tree("), () -> "Legacy PST.tree binding in " + path);
                            assertFalse(text.contains("PST.skill("), () -> "Legacy PST.skill binding in " + path);
                            assertFalse(text.contains("run/client"), () -> "Stale run/client reference in " + path);
                            assertFalse(text.contains("run/server"), () -> "Stale run/server reference in " + path);
                        } catch (IOException exception) {
                            throw new RuntimeException(exception);
                        }
                    });
            }
        }
    }

    @Test
    void mainRegistryAndReadmeOmitLegacyCustomItemBonusExample() throws IOException {
        String registryRuntimeSample = Files.readString(Path.of("examples/kubejs/passivestjs/01_startup_registry_runtime.js").toAbsolutePath());
        String readme = Files.readString(Path.of("examples/kubejs/passivestjs/README.md").toAbsolutePath());

        assertFalse(registryRuntimeSample.contains("skilltree:item_bonuses"));
        assertFalse(registryRuntimeSample.contains("sample_item_bonus_shell"));
        assertFalse(readme.contains("04_startup_item_bonus_metadata.js"));
    }
}
