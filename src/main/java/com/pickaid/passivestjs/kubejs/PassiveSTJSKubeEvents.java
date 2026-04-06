package com.pickaid.passivestjs.kubejs;

import com.pickaid.passivestjs.compat.passiveintegration.PassiveIntegrationCompat;
import com.pickaid.passivestjs.kubejs.event.AmmoBurstAboutToEndEventJS;
import com.pickaid.passivestjs.kubejs.event.AmmoBurstEndEventJS;
import com.pickaid.passivestjs.kubejs.event.AmmoBurstFailEventJS;
import com.pickaid.passivestjs.kubejs.event.AmmoBurstStartEventJS;
import com.pickaid.passivestjs.kubejs.event.AmmoBurstSustainEventJS;
import com.pickaid.passivestjs.kubejs.event.AmmoBurstTryStartEventJS;
import com.pickaid.passivestjs.kubejs.event.SkillTreeContentEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.script.ScriptType;

public final class PassiveSTJSKubeEvents {
    public static final EventGroup GROUP = EventGroup.of("PassiveSTJSEvents");
    private static EventHandler skillTreeContent;
    private static EventHandler ammoBurstTryStart;
    private static EventHandler ammoBurstStart;
    private static EventHandler ammoBurstFail;
    private static EventHandler ammoBurstAboutToEnd;
    private static EventHandler ammoBurstSustain;
    private static EventHandler ammoBurstEnd;

    private PassiveSTJSKubeEvents() {
    }

    public static void registerCore() {
        if (skillTreeContent == null) {
            skillTreeContent = GROUP.server("skillTreeContent", () -> SkillTreeContentEventJS.class);
        }
        if (PassiveIntegrationCompat.isLoaded() && ammoBurstTryStart == null) {
            ammoBurstTryStart = GROUP.server("ammoBurstTryStart", () -> AmmoBurstTryStartEventJS.class).hasResult();
            ammoBurstStart = GROUP.server("ammoBurstStart", () -> AmmoBurstStartEventJS.class);
            ammoBurstFail = GROUP.server("ammoBurstFail", () -> AmmoBurstFailEventJS.class);
            ammoBurstAboutToEnd = GROUP.server("ammoBurstAboutToEnd", () -> AmmoBurstAboutToEndEventJS.class);
            ammoBurstSustain = GROUP.server("ammoBurstSustain", () -> AmmoBurstSustainEventJS.class);
            ammoBurstEnd = GROUP.server("ammoBurstEnd", () -> AmmoBurstEndEventJS.class);
        }
    }

    public static void postTryStart(AmmoBurstTryStartEventJS event) {
        if (ammoBurstTryStart != null) {
            ammoBurstTryStart.post(ScriptType.SERVER, event);
        }
    }

    public static void postStart(AmmoBurstStartEventJS event) {
        if (ammoBurstStart != null) {
            ammoBurstStart.post(ScriptType.SERVER, event);
        }
    }

    public static void postFail(AmmoBurstFailEventJS event) {
        if (ammoBurstFail != null) {
            ammoBurstFail.post(ScriptType.SERVER, event);
        }
    }

    public static void postAboutToEnd(AmmoBurstAboutToEndEventJS event) {
        if (ammoBurstAboutToEnd != null) {
            ammoBurstAboutToEnd.post(ScriptType.SERVER, event);
        }
    }

    public static void postSustain(AmmoBurstSustainEventJS event) {
        if (ammoBurstSustain != null) {
            ammoBurstSustain.post(ScriptType.SERVER, event);
        }
    }

    public static void postEnd(AmmoBurstEndEventJS event) {
        if (ammoBurstEnd != null) {
            ammoBurstEnd.post(ScriptType.SERVER, event);
        }
    }
}
