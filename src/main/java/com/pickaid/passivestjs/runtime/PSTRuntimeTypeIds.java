package com.pickaid.passivestjs.runtime;

import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.requirement.SkillRequirement;
import net.minecraft.resources.ResourceLocation;

/**
 * Shared runtime type id resolution for KubeJS runtime view helpers.
 * <p>
 * Runtime (serializer-backed) objects report their ids from {@link PSTRuntimeNode#id()}.
 * Vanilla/PST-provided objects fall back to Forge registry keys when available.
 */
public final class PSTRuntimeTypeIds {
    private static final String UNKNOWN = "<unknown>";

    private PSTRuntimeTypeIds() {
    }

    public static String skillBonusId(SkillBonus<?> bonus) {
        if (bonus == null) {
            return UNKNOWN;
        }
        if (bonus instanceof PSTCustomRuntimeSkillBonus runtime) {
            return runtime.node().id().toString();
        }

        try {
            ResourceLocation key = PSTRegistries.SKILL_BONUSES.get().getKey(bonus.getSerializer());
            return key == null ? UNKNOWN : key.toString();
        } catch (Throwable ignored) {
            return UNKNOWN;
        }
    }

    public static String eventListenerId(EventListenerBonus<?> bonus) {
        if (bonus == null) {
            return UNKNOWN;
        }

        SkillEventListener listener = bonus.getEventListener();
        if (listener instanceof PSTCustomRuntimeEventListener runtime) {
            return runtime.node().id().toString();
        }

        try {
            ResourceLocation key = PSTRegistries.EVENT_LISTENERS.get().getKey(listener.getSerializer());
            return key == null ? UNKNOWN : key.toString();
        } catch (Throwable ignored) {
            return UNKNOWN;
        }
    }

    public static String skillRequirementId(SkillRequirement<?> requirement) {
        if (requirement == null) {
            return UNKNOWN;
        }
        if (requirement instanceof PSTCustomRuntimeSkillRequirement runtime) {
            return runtime.node().id().toString();
        }

        try {
            ResourceLocation key = PSTRegistries.SKILL_REQUIREMENTS.get().getKey(requirement.getSerializer());
            return key == null ? UNKNOWN : key.toString();
        } catch (Throwable ignored) {
            return UNKNOWN;
        }
    }
}
