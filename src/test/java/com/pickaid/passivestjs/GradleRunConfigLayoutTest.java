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

        assertTrue(buildGradleText.contains("runs {"));
        assertTrue(buildGradleText.contains("client {"));
        assertTrue(buildGradleText.contains("server {"));
        assertTrue(buildGradleText.contains("run/client"));
        assertTrue(buildGradleText.contains("run/server"));
        assertTrue(buildGradleText.contains("curios-forge"));
        assertTrue(buildGradleText.contains("attributefix-280510"));
        assertTrue(buildGradleText.contains("probejs-legacy-956446"));
    }
}
