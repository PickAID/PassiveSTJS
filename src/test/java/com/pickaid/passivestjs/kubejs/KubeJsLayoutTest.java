package com.pickaid.passivestjs.kubejs;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class KubeJsLayoutTest {
    @Test
    void runtimeScriptsUsePassivestjsFolders() {
        Path kubeJsRoot = Path.of("").toAbsolutePath().resolve("run/client/kubejs");

        assertTrue(Files.isDirectory(kubeJsRoot.resolve("server_scripts/passivestjs/content")));
        assertTrue(Files.exists(kubeJsRoot.resolve("server_scripts/passivestjs/content/10_test_tree.js")));
        assertTrue(Files.exists(kubeJsRoot.resolve("server_scripts/passivestjs/docs/90_probe_docs.js")));
    }
}
