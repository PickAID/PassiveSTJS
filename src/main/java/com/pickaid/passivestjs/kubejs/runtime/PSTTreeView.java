package com.pickaid.passivestjs.kubejs.runtime;

import com.pickaid.passivestjs.kubejs.id.PSTSkillId;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import dev.latvian.mods.kubejs.typings.Info;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class PSTTreeView {
    private static final String PST_DEFAULT_NAMESPACE = "skilltree";
    private final PassiveSkillTree tree;

    public PSTTreeView(PassiveSkillTree tree) {
        this.tree = Objects.requireNonNull(tree, "tree");
    }

    @Info("Returns this PST skill tree id.")
    public String id() {
        return tree.getId().toString();
    }

    @Info("Returns the skill ids included in this PST skill tree.")
    public List<String> skillIds() {
        return tree.getSkillIds().stream()
                .map(id -> id == null ? null : id.toString())
                .filter(Objects::nonNull)
                .toList();
    }

    @Info("Returns the tag limits declared by this PST skill tree.")
    public Map<String, Integer> limits() {
        return Map.copyOf(new LinkedHashMap<>(tree.getSkillLimitations()));
    }

    @Info("Returns whether this PST skill tree contains the provided skill id.")
    public boolean hasSkill(PSTSkillId id) {
        if (id == null) {
            return false;
        }
        return tree.getSkillIds().contains(id.location());
    }

    @Info("Returns the read-only PST skill definition view for the provided skill id when it belongs to this tree.")
    public PSTSkillView skill(PSTSkillId id) {
        if (!hasSkill(id)) {
            return null;
        }

        PassiveSkill skill = SkillsReloader.getSkillById(id.location());
        if (skill == null) {
            return null;
        }
        return new PSTSkillView(skill);
    }

    @Info("Returns read-only PST skill definition views for every resolved skill in this tree.")
    public List<PSTSkillView> skills() {
        List<PSTSkillView> views = new ArrayList<>();
        for (ResourceLocation skillId : tree.getSkillIds()) {
            PassiveSkill skill = SkillsReloader.getSkillById(skillId);
            if (skill != null) {
                views.add(new PSTSkillView(skill));
            }
        }
        return List.copyOf(views);
    }

    @Info("Returns whether this PST skill tree belongs to PST's built-in default tree set.")
    public boolean isDefaultTree() {
        return SkillTreesReloader.getSkillTrees().containsKey(tree.getId())
                && PST_DEFAULT_NAMESPACE.equals(tree.getId().getNamespace());
    }
}
