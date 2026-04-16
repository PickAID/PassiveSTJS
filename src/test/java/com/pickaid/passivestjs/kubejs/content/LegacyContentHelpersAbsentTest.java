package com.pickaid.passivestjs.kubejs.content;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class LegacyContentHelpersAbsentTest {
    @Test
    void legacyHelperClassesAreNotLoadable() {
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.pickaid.passivestjs.kubejs.content.ContentApi"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.pickaid.passivestjs.kubejs.content.Bonuses"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.pickaid.passivestjs.kubejs.content.Conditions"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.pickaid.passivestjs.kubejs.content.Requirements"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.pickaid.passivestjs.kubejs.content.Listeners"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.pickaid.passivestjs.kubejs.content.Values"));
        assertThrows(ClassNotFoundException.class, () -> Class.forName("com.pickaid.passivestjs.kubejs.content.Multipliers"));
    }
}
