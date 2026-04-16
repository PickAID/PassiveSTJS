package com.pickaid.passivestjs.skilltree;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PSTEditorCompatTest {
    @Test
    void ensureParentDirectoriesCreatesNestedSkillFolders() throws Exception {
        Path tempDir = Files.createTempDirectory("passivestjs-editor");
        File nestedSkillFile = tempDir.resolve("skills/kubejs/passivestjs_layout/root.json").toFile();

        PSTEditorCompat.ensureParentDirectories(nestedSkillFile);

        assertTrue(nestedSkillFile.getParentFile().isDirectory());
    }

    @Test
    void disabledEditorMessagePointsToCommonConfig() {
        assertEquals(
                "PassiveSTJS disabled the Passive Skill Tree editor. Set editor.disabled=false in passivestjs-common.toml to re-enable it.",
                PSTEditorCompat.editorDisabledMessage().getString()
        );
    }
}
