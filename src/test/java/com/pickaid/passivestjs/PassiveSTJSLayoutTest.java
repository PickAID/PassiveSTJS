package com.pickaid.passivestjs;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PassiveSTJSLayoutTest {
    @Test
    void projectOwnsItsEntrypointPluginResourceAndWrapper() throws IOException {
        Path projectRoot = projectRoot();
        Path modEntrypoint = projectRoot.resolve("src/main/java/com/pickaid/passivestjs/PassiveSTJS.java");
        Path pluginResource = projectRoot.resolve("src/main/resources/kubejs.plugins.txt");
        Path modsToml = projectRoot.resolve("src/main/resources/META-INF/mods.toml");
        Path projectToml = projectRoot.resolve("project.toml");
        Path templateDefaultsToml = projectRoot.resolve("gradle/template-defaults.toml");
        Path gradleWrapperScript = projectRoot.resolve("gradlew");
        Path gradleWrapperProperties = projectRoot.resolve("gradle/wrapper/gradle-wrapper.properties");
        Path pstSkilltreePackage = projectRoot.resolve("src/main/java/com/pickaid/passivestjs/skilltree");
        Path legacyCompatSkilltreePackage = projectRoot.resolve("src/main/java/com/pickaid/passivestjs/compat/skilltree");
        String projectTomlContent = Files.readString(projectToml);
        String templateDefaultsContent = Files.readString(templateDefaultsToml);
        String modsTomlContent = Files.readString(modsToml);
        String modAuthors = stringArrayValue(projectTomlContent, "mod", "authors");

        assertTrue(Files.exists(modEntrypoint));
        assertTrue(Files.exists(pluginResource));
        assertTrue(Files.exists(modsToml));
        assertTrue(Files.exists(projectToml));
        assertTrue(Files.exists(templateDefaultsToml));
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
        assertTrue(modsTomlContent.contains("modId=\"" + stringValue(projectTomlContent, "mod", "mod_id") + "\""));
        assertTrue(modsTomlContent.contains("displayName=\"" + stringValue(projectTomlContent, "mod", "mod_name") + "\""));
        assertTrue(modsTomlContent.contains("authors=\"" + modAuthors + "\""));
        assertTrue(modsTomlContent.contains("license=\"" + stringValue(projectTomlContent, "mod", "license") + "\""));
        assertTrue(modsTomlContent.contains(stringValue(projectTomlContent, "mod", "description")));
        assertTrue(modsTomlContent.contains("loaderVersion=\"" + stringValue(templateDefaultsContent, "platform", "forge_range") + "\""));
        assertTrue(modsTomlContent.contains("versionRange=\"" + stringValue(templateDefaultsContent, "platform", "mc_range") + "\""));
        assertTrue(modsTomlContent.contains("versionRange=\"[" + stringValue(projectTomlContent, "compat", "kubejs_version") + ",)\""));
    }

    @Test
    void packagedJarCarriesBuildVersion() throws IOException {
        Path projectRoot = projectRoot();
        String projectTomlContent = Files.readString(projectRoot.resolve("project.toml"));
        String templateDefaultsContent = Files.readString(projectRoot.resolve("gradle/template-defaults.toml"));
        String modVersion = stringValue(projectTomlContent, "mod", "version");
        Path builtJar = projectRoot.resolve("build/libs/" +
                stringValue(projectTomlContent, "naming", "archive_name") + "-" +
                stringValue(templateDefaultsContent, "platform", "mc_version") + "-" +
                modVersion + ".jar");

        assertTrue(Files.exists(builtJar));
        try (JarFile jarFile = new JarFile(builtJar.toFile())) {
            assertEquals(
                    modVersion,
                    jarFile.getManifest().getMainAttributes().getValue("Implementation-Version")
            );
        }
    }

    private static String stringValue(String toml, String table, String key) {
        Matcher matcher = Pattern.compile("(?m)^" + Pattern.quote(key) + "\\s*=\\s*\"([^\"]*)\"\\s*$")
                .matcher(tableSection(toml, table));
        assertTrue(matcher.find(), () -> "Missing TOML value [" + table + "]." + key);
        return matcher.group(1);
    }

    private static String stringArrayValue(String toml, String table, String key) {
        Matcher matcher = Pattern.compile("(?m)^" + Pattern.quote(key) + "\\s*=\\s*\\[(.*)]\\s*$")
                .matcher(tableSection(toml, table));
        assertTrue(matcher.find(), () -> "Missing TOML array [" + table + "]." + key);
        return matcher.group(1).replace("\"", "").replace(",", ",").trim();
    }

    private static String tableSection(String toml, String table) {
        Matcher matcher = Pattern.compile("(?ms)^\\[" + Pattern.quote(table) + "]\\s*(.*?)(?=^\\[|\\z)")
                .matcher(toml);
        assertTrue(matcher.find(), () -> "Missing TOML table [" + table + "]");
        return matcher.group(1);
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
