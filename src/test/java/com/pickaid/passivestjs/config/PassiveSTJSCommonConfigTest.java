package com.pickaid.passivestjs.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PassiveSTJSCommonConfigTest {
    @Test
    void editorIsDisabledByDefault() {
        assertTrue(PassiveSTJSCommonConfig.editorDisabled());
    }

    @Test
    void translationDraftExportIsDisabledByDefault() {
        assertFalse(PassiveSTJSCommonConfig.translationDraftExportEnabled());
    }
}
