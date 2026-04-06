package com.pickaid.passivestjs.kubejs;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PluginPathTest {
    @Test
    void pluginResourcePointsAtPassiveSTJSPlugin() throws IOException {
        Path projectRoot = Path.of("").toAbsolutePath();
        Path pluginResource = projectRoot.resolve("src/main/resources/kubejs.plugins.txt");
        Path pluginClass = projectRoot.resolve("src/main/java/com/pickaid/passivestjs/kubejs/PassiveSTJSKubePlugin.java");
        Path eventsClass = projectRoot.resolve("src/main/java/com/pickaid/passivestjs/kubejs/PassiveSTJSKubeEvents.java");
        Path bindingsClass = projectRoot.resolve("src/main/java/com/pickaid/passivestjs/kubejs/Bindings.java");

        assertEquals(
                "com.pickaid.passivestjs.kubejs.PassiveSTJSKubePlugin",
                Files.readString(pluginResource).trim()
        );
        assertTrue(Files.exists(pluginClass));
        assertTrue(Files.exists(eventsClass));
        assertTrue(Files.exists(bindingsClass));
    }
}
