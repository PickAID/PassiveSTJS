package com.pickaid.passivestjs;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GradleRunConfigLayoutTest {
    @Test
    void buildGradleDefinesClientAndServerRuns() throws IOException {
        String buildGradleText = Files.readString(Path.of("").toAbsolutePath().resolve("build.gradle"));
        String projectTomlText = Files.readString(Path.of("").toAbsolutePath().resolve("project.toml"));

        assertTrue(buildGradleText.contains("runs {"));
        assertTrue(buildGradleText.contains("client {"));
        assertTrue(buildGradleText.contains("server {"));
        assertTrue(buildGradleText.contains("run/client"));
        assertTrue(buildGradleText.contains("run/server"));
        assertTrue(projectTomlText.contains("curios-forge"));
        assertTrue(projectTomlText.contains("attributefix-280510"));
        assertTrue(projectTomlText.contains("probejs-585406"));
        assertTrue(projectTomlText.contains("probejs-legacy-956446"));
    }
}
