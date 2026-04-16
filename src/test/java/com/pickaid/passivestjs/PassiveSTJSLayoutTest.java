package com.pickaid.passivestjs;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.jar.JarFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PassiveSTJSLayoutTest {
    @Test
    void projectOwnsItsEntrypointPluginResourceAndWrapper() throws IOException {
        Path projectRoot = projectRoot();
        Path modEntrypoint = projectRoot.resolve("src/main/java/com/pickaid/passivestjs/PassiveSTJS.java");
        Path pluginResource = projectRoot.resolve("src/main/resources/kubejs.plugins.txt");
        Path modsToml = projectRoot.resolve("src/main/resources/META-INF/mods.toml");
        Path buildTxt = projectRoot.resolve("build.txt");
        Path gradleWrapperScript = projectRoot.resolve("gradlew");
        Path gradleWrapperProperties = projectRoot.resolve("gradle/wrapper/gradle-wrapper.properties");
        Path pstSkilltreePackage = projectRoot.resolve("src/main/java/com/pickaid/passivestjs/skilltree");
        Path legacyCompatSkilltreePackage = projectRoot.resolve("src/main/java/com/pickaid/passivestjs/compat/skilltree");
        Properties buildProperties = readProperties(buildTxt);
        String modsTomlContent = Files.readString(modsToml);

        assertTrue(Files.exists(modEntrypoint));
        assertTrue(Files.exists(pluginResource));
        assertTrue(Files.exists(modsToml));
        assertTrue(Files.exists(gradleWrapperScript));
        assertTrue(Files.exists(gradleWrapperProperties));
        assertTrue(Files.exists(pstSkilltreePackage));
        assertTrue(Files.exists(pstSkilltreePackage.resolve("PSTContentTitles.java")));
        assertTrue(Files.exists(pstSkilltreePackage.resolve("PSTEditorCompat.java")));
        assertTrue(Files.exists(pstSkilltreePackage.resolve("PSTNetworkComponents.java")));
        assertTrue(Files.exists(pstSkilltreePackage.resolve("PSTSkillLearningRules.java")));
        assertTrue(Files.notExists(legacyCompatSkilltreePackage));
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

    @Test
    void packagedJarCarriesBuildVersion() throws IOException {
        Path projectRoot = projectRoot();
        Properties buildProperties = readProperties(projectRoot.resolve("build.txt"));
        Path builtJar = projectRoot.resolve("build/libs/" +
                buildProperties.getProperty("archive_name") + "-" +
                buildProperties.getProperty("mc_version") + "-" +
                buildProperties.getProperty("mod_version") + ".jar");

        assertTrue(Files.exists(builtJar));
        try (JarFile jarFile = new JarFile(builtJar.toFile())) {
            assertEquals(
                    buildProperties.getProperty("mod_version"),
                    jarFile.getManifest().getMainAttributes().getValue("Implementation-Version")
            );
        }
    }

    private static Properties readProperties(Path path) throws IOException {
        Properties properties = new Properties();
        try (var inputStream = Files.newInputStream(path)) {
            properties.load(inputStream);
        }
        return properties;
    }

    private static Path projectRoot() {
        try {
            Path testClassesDirectory = Path.of(
                    PassiveSTJSLayoutTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()
            );
            return testClassesDirectory.getParent().getParent().getParent().getParent();
        } catch (URISyntaxException exception) {
            throw new IllegalStateException("Failed to locate PassiveSTJS project root", exception);
        }
    }
}
