package com.pickaid.passivestjs.kubejs;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class KubeJsLayoutTest {
    @Test
    void runtimeScriptsUsePassivestjsFolders() {
        Path repoRoot = Path.of("").toAbsolutePath();
        Path examplesRoot = repoRoot.resolve("examples/kubejs/passivestjs");
        Path docsRoot = repoRoot.resolve("docs");

        assertTrue(Files.isDirectory(examplesRoot));
        assertTrue(Files.exists(examplesRoot.resolve("01_startup_registry_runtime.js")));
        assertTrue(Files.exists(examplesRoot.resolve("02_server_skill_tree_content.js")));
        assertTrue(Files.exists(examplesRoot.resolve("03_server_runtime_api.js")));
        assertTrue(Files.exists(examplesRoot.resolve("05_server_requirement_listener_chain.js")));
        assertTrue(Files.exists(examplesRoot.resolve("06_client_lang_entries.js")));
        assertTrue(Files.exists(examplesRoot.resolve("07_server_runtime_helper_samples.js")));
        assertTrue(Files.exists(examplesRoot.resolve("08_server_workbench_item_bonus_recipe.js")));
        assertTrue(Files.exists(examplesRoot.resolve("09_server_item_bonus_runtime_api.js")));
        assertTrue(Files.exists(examplesRoot.resolve("README.md")));

        assertTrue(Files.isDirectory(docsRoot.resolve("serverevents")));
        assertTrue(Files.isDirectory(docsRoot.resolve("startupevents")));
        assertTrue(Files.exists(docsRoot.resolve("overview.mdx")));
        assertTrue(Files.exists(docsRoot.resolve("config.mdx")));
        assertTrue(Files.exists(docsRoot.resolve("recipes.mdx")));
        assertTrue(Files.exists(docsRoot.resolve("runtime-helpers.mdx")));
        assertTrue(Files.exists(docsRoot.resolve("serverevents/content.mdx")));
        assertTrue(Files.exists(docsRoot.resolve("startupevents/registry.mdx")));
    }
}
