package com.pickaid.passivestjs.runtime;

/**
 * Marker interface for SkillBonus implementations that were constructed from a {@link PSTRuntimeNode}.
 * This keeps the KubeJS runtime views honest: only serializer-backed runtime objects expose nodes.
 */
public interface PSTCustomRuntimeSkillBonus {
    PSTRuntimeNode node();
}

