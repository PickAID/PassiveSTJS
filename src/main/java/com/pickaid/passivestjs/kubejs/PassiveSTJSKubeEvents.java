package com.pickaid.passivestjs.kubejs;

import com.pickaid.passivestjs.kubejs.event.SkillTreeContentEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public final class PassiveSTJSKubeEvents {
    public static final EventGroup GROUP = EventGroup.of("PassiveSTJSEvents");
    private static EventHandler skillTreeContent;

    private PassiveSTJSKubeEvents() {
    }

    public static void registerCore() {
        if (skillTreeContent == null) {
            skillTreeContent = GROUP.server("skillTreeContent", () -> SkillTreeContentEventJS.class);
        }
    }
}
