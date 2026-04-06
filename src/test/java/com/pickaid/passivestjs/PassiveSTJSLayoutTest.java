package com.pickaid.passivestjs;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PassiveSTJSLayoutTest {
    @Test
    void projectOwnsItsEntrypointPluginResourceAndWrapper() throws IOException {
        Path projectRoot = Path.of("").toAbsolutePath();
        Path modEntrypoint = projectRoot.resolve("src/main/java/com/pickaid/passivestjs/PassiveSTJS.java");
        Path pluginResource = projectRoot.resolve("src/main/resources/kubejs.plugins.txt");
        Path modsToml = projectRoot.resolve("src/main/resources/META-INF/mods.toml");
        Path buildTxt = projectRoot.resolve("build.txt");
        Path gradleWrapperScript = projectRoot.resolve("gradlew");
        Path gradleWrapperProperties = projectRoot.resolve("gradle/wrapper/gradle-wrapper.properties");
        Properties buildProperties = readProperties(buildTxt);
        String modsTomlContent = Files.readString(modsToml);

        assertTrue(Files.exists(modEntrypoint));
        assertTrue(Files.exists(pluginResource));
        assertTrue(Files.exists(modsToml));
        assertTrue(Files.exists(gradleWrapperScript));
        assertTrue(Files.exists(gradleWrapperProperties));
        assertEquals(
                "com.pickaid.passivestjs.kubejs.PassiveSTJSKubePlugin",
                Files.readString(pluginResource).trim()
        );
        assertTrue(modsTomlContent.contains("modId=\"" + buildProperties.getProperty("mod_id") + "\""));
        assertTrue(modsTomlContent.contains("displayName=\"" + buildProperties.getProperty("mod_name") + "\""));
        assertTrue(modsTomlContent.contains("authors=\"" + buildProperties.getProperty("mod_authors") + "\""));
        assertTrue(modsTomlContent.contains("license=\"" + buildProperties.getProperty("mod_license") + "\""));
        assertTrue(modsTomlContent.contains(buildProperties.getProperty("mod_description")));
        assertTrue(modsTomlContent.contains("loaderVersion=\"" + buildProperties.getProperty("forge_range") + "\""));
        assertTrue(modsTomlContent.contains("versionRange=\"" + buildProperties.getProperty("mc_range") + "\""));
        assertTrue(modsTomlContent.contains("versionRange=\"[" + buildProperties.getProperty("kubejs_version") + ",)\""));
    }

    private static Properties readProperties(Path path) throws IOException {
        Properties properties = new Properties();
        try (var inputStream = Files.newInputStream(path)) {
            properties.load(inputStream);
        }
        return properties;
    }
}
