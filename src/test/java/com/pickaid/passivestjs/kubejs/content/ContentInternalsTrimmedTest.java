package com.pickaid.passivestjs.kubejs.content;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ContentInternalsTrimmedTest {
    @Test
    void unusedHelperMethodsAreGone() {
        assertThrows(NoSuchMethodException.class, () -> JsonHelper.class.getDeclaredMethod("addObject", com.google.gson.JsonObject.class, String.class, Object.class));
        assertThrows(NoSuchMethodException.class, () -> ManagedContent.class.getDeclaredMethod("managedTreeIds"));
        assertThrows(NoSuchMethodException.class, () -> ManagedContent.class.getDeclaredMethod("managedSkillIds"));
    }
}
