package com.pickaid.passivestjs.kubejs.content;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public final class ManagedContent {
    private static final Set<ResourceLocation> MANAGED_TREE_IDS = new LinkedHashSet<>();
    private static final Set<ResourceLocation> MANAGED_SKILL_IDS = new LinkedHashSet<>();

    private ManagedContent() {
    }

    public static synchronized void replace(Collection<ResourceLocation> treeIds, Collection<ResourceLocation> skillIds) {
        MANAGED_TREE_IDS.clear();
        MANAGED_TREE_IDS.addAll(treeIds);
        MANAGED_SKILL_IDS.clear();
        MANAGED_SKILL_IDS.addAll(skillIds);
    }

    public static synchronized boolean managesTree(ResourceLocation id) {
        return MANAGED_TREE_IDS.contains(id);
    }

    public static synchronized boolean managesSkill(ResourceLocation id) {
        return MANAGED_SKILL_IDS.contains(id);
    }

    public static synchronized Set<ResourceLocation> managedTreeIds() {
        return new LinkedHashSet<>(MANAGED_TREE_IDS);
    }

    public static synchronized Set<ResourceLocation> managedSkillIds() {
        return new LinkedHashSet<>(MANAGED_SKILL_IDS);
    }
}
