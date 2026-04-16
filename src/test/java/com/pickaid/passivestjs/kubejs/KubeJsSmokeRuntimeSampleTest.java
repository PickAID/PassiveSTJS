package com.pickaid.passivestjs.kubejs;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KubeJsSmokeRuntimeSampleTest {
    @Test
    void clientSmokeRuntimeSampleMatchesPassiveSkillTreeUnderwaterSemantics() throws IOException {
        Path script = Path.of("examples/kubejs/passivestjs/01_startup_registry_runtime.js").toAbsolutePath();
        String text = Files.readString(script);

        assertTrue(text.contains("getEyeInFluidType()"), script + " should use eye-in-fluid detection");
        assertTrue(text.contains("WATER.getFluidType()"), script + " should compare against water fluid type");
        assertTrue(text.contains("context.tickCount()"), script + " should use the runtime tick helper instead of raw field access");
        assertFalse(text.contains("isUnderWater()"), script + " should no longer use the stricter helper");
        assertFalse(text.contains("player.tickCount"), script + " should not rely on Rhino field access for tick cooldown gating");
    }
}
