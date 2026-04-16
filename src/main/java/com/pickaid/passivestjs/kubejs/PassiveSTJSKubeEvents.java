package com.pickaid.passivestjs.kubejs;

import com.pickaid.passivestjs.kubejs.event.SkillTreeContentEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.DataPackEventJS;

public final class PassiveSTJSKubeEvents {
    public static final EventGroup GROUP = EventGroup.of("PassiveSTJSEvents");
    public static final EventHandler SKILL_TREE_CONTENT = GROUP.server("skillTreeContent", () -> SkillTreeContentEventJS.class);

    private PassiveSTJSKubeEvents() {
    }

    public static void postSkillTreeContent() {
        postSkillTreeContent(SkillTreeContentEventJS.synthetic());
    }

    public static void postSkillTreeContent(DataPackEventJS event) {
        SKILL_TREE_CONTENT.post(ScriptType.SERVER, SkillTreeContentEventJS.wrap(event));
    }
}
