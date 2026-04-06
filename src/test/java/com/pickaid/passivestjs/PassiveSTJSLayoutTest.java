package com.pickaid.passivestjs;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PassiveSTJSLayoutTest {
    @Test
    void projectOwnsItsEntrypointAndPluginResource() throws IOException {
        Path projectRoot = Path.of("").toAbsolutePath();
        Path modEntrypoint = projectRoot.resolve("src/main/java/com/pickaid/passivestjs/PassiveSTJS.java");
        Path pluginResource = projectRoot.resolve("src/main/resources/kubejs.plugins.txt");
        Path modsToml = projectRoot.resolve("src/main/resources/META-INF/mods.toml");

        assertTrue(Files.exists(modEntrypoint));
        assertTrue(Files.exists(pluginResource));
        assertTrue(Files.exists(modsToml));
        assertEquals(
                "com.pickaid.passivestjs.kubejs.PassiveSTJSKubePlugin",
                Files.readString(pluginResource).trim()
        );
    }
}
