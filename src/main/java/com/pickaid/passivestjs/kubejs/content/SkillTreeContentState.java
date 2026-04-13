package com.pickaid.passivestjs.kubejs.content;

import com.google.gson.JsonObject;
import com.pickaid.passivestjs.compat.skilltree.PSTContentTitles;
import com.pickaid.passivestjs.kubejs.builder.Ids;
import com.pickaid.passivestjs.kubejs.builder.SkillBuilder;
import com.pickaid.passivestjs.kubejs.builder.SkillTreeBuilder;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import dev.latvian.mods.kubejs.script.data.DataPackEventJS;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class SkillTreeContentState {
    private final Map<ResourceLocation, JsonObject> skills = new LinkedHashMap<>();
    private final Map<ResourceLocation, JsonObject> trees = new LinkedHashMap<>();
    private final Map<ResourceLocation, Set<ResourceLocation>> treeSkillIds = new LinkedHashMap<>();
    private final Set<ResourceLocation> removedSkills = new LinkedHashSet<>();
    private final Set<ResourceLocation> removedTrees = new LinkedHashSet<>();
    private final Set<ResourceLocation> blockedSkills = new LinkedHashSet<>();
    private final Set<ResourceLocation> blockedTrees = new LinkedHashSet<>();
    private boolean clearDefaultTrees;

    public void putTree(SkillTreeBuilder tree) {
        if (blockedTrees.contains(tree.idLocation())) {
            return;
        }
        Set<ResourceLocation> builtSkillIds = new LinkedHashSet<>();
        putTree(tree.idLocation(), tree.toJson());
        for (SkillBuilder skill : tree.builtSkills()) {
            builtSkillIds.add(skill.idLocation());
            putSkill(skill.idLocation(), skill.toJson());
        }
        treeSkillIds.put(tree.idLocation(), builtSkillIds);
    }

    public void putLooseSkill(SkillBuilder skill) {
        putSkill(skill.idLocation(), skill.toJson());
    }

    public void putSkill(ResourceLocation id, JsonObject json) {
        if (blockedSkills.contains(id)) {
            return;
        }
        removedSkills.remove(id);
        skills.put(id, JsonHelper.copy(json).getAsJsonObject());
        attachSkillToReferencingTrees(id);
    }

    public void putTree(ResourceLocation id, JsonObject json) {
        if (blockedTrees.contains(id)) {
            return;
        }
        removedTrees.remove(id);
        JsonObject copy = JsonHelper.copy(json).getAsJsonObject();
        trees.put(id, copy);
        treeSkillIds.put(id, extractOwnedSkillIds(copy));
    }

    public void removeSkill(ResourceLocation id) {
        blockedSkills.add(id);
        skills.remove(id);
        removedSkills.add(id);
        for (Map.Entry<ResourceLocation, JsonObject> entry : trees.entrySet()) {
            JsonObject treeJson = entry.getValue();
            if (!treeJson.has("skillIds")) {
                continue;
            }

            var filteredSkillIds = JsonHelper.array();
            for (var element : treeJson.getAsJsonArray("skillIds")) {
                if (element.isJsonPrimitive() && id.equals(Ids.parse(element.getAsString(), "skillId"))) {
                    continue;
                }
                filteredSkillIds.add(element);
            }
            treeJson.add("skillIds", filteredSkillIds);
            Set<ResourceLocation> ownedSkillIds = treeSkillIds.get(entry.getKey());
            if (ownedSkillIds != null) {
                ownedSkillIds.remove(id);
            }
        }
    }

    public void removeTree(ResourceLocation id) {
        blockedTrees.add(id);
        trees.remove(id);
        removedTrees.add(id);
        Set<ResourceLocation> ownedSkillIds = new LinkedHashSet<>(treeSkillIds.getOrDefault(id, Set.of()));
        treeSkillIds.remove(id);
        for (ResourceLocation skillId : ownedSkillIds) {
            if (isOwnedByAnotherTree(skillId)) {
                continue;
            }
            blockedSkills.add(skillId);
            skills.remove(skillId);
            removedSkills.add(skillId);
        }
    }

    public void clearDefaultTrees() {
        clearDefaultTrees = true;
    }

    public void writeTo(DataPackEventJS event) {
        for (Map.Entry<ResourceLocation, JsonObject> entry : skills.entrySet()) {
            event.addJson(Skills.skillResource(entry.getKey()), entry.getValue());
        }
        for (ResourceLocation treeId : trees.keySet()) {
            event.addJson(Skills.skillTreeResource(treeId), effectiveTreeJson(treeId));
        }
    }

    public void apply() {
        Set<ResourceLocation> appliedTreeIds = new LinkedHashSet<>(trees.keySet());
        Set<ResourceLocation> appliedSkillIds = new LinkedHashSet<>(skills.keySet());
        ManagedContent.Snapshot previousManaged = ManagedContent.snapshot();
        Set<ResourceLocation> clearedDefaultSkillIds = new LinkedHashSet<>();

        if (clearDefaultTrees) {
            Set<ResourceLocation> retainedTreeSkillIds = retainedTreeSkillIds();
            for (var entry : SkillTreesReloader.getSkillTrees().entrySet()) {
                if (appliedTreeIds.contains(entry.getKey())) {
                    continue;
                }
                for (ResourceLocation skillId : entry.getValue().getSkillIds()) {
                    if (!appliedSkillIds.contains(skillId) && !retainedTreeSkillIds.contains(skillId)) {
                        clearedDefaultSkillIds.add(skillId);
                    }
                }
            }
            SkillTreesReloader.getSkillTrees().keySet().removeIf(id -> !appliedTreeIds.contains(id));
        }

        for (ResourceLocation treeId : previousManaged.treeIds()) {
            if (!appliedTreeIds.contains(treeId)) {
                Skills.INSTANCE.removeRuntimeTree(treeId);
            }
        }
        for (ResourceLocation skillId : previousManaged.skillIds()) {
            if (!appliedSkillIds.contains(skillId)) {
                Skills.INSTANCE.removeRuntimeSkill(skillId);
            }
        }
        for (ResourceLocation treeId : removedTrees) {
            Skills.INSTANCE.removeRuntimeTree(treeId);
        }
        for (ResourceLocation skillId : removedSkills) {
            Skills.INSTANCE.removeRuntimeSkill(skillId);
        }
        for (ResourceLocation skillId : clearedDefaultSkillIds) {
            Skills.INSTANCE.removeRuntimeSkill(skillId);
        }
        for (JsonObject skillJson : skills.values()) {
            Skills.INSTANCE.applyRuntimeSkill(skillJson);
        }
        for (ResourceLocation treeId : trees.keySet()) {
            Skills.INSTANCE.applyRuntimeTree(effectiveTreeJson(treeId));
        }

        PSTContentTitles.retainSkillTitles(appliedSkillIds);
        PSTContentTitles.retainTreeTitles(appliedTreeIds);

        ManagedContent.replace(appliedTreeIds, appliedSkillIds);
    }

    private Set<ResourceLocation> retainedTreeSkillIds() {
        Set<ResourceLocation> retained = new LinkedHashSet<>();
        for (ResourceLocation treeId : trees.keySet()) {
            JsonObject json = effectiveTreeJson(treeId);
            if (!json.has("skillIds")) {
                continue;
            }
            for (var element : json.getAsJsonArray("skillIds")) {
                if (element.isJsonPrimitive()) {
                    retained.add(Ids.parse(element.getAsString(), "skillId"));
                }
            }
        }
        return retained;
    }

    private Set<ResourceLocation> extractOwnedSkillIds(JsonObject treeJson) {
        Set<ResourceLocation> ids = extractSkillIds(treeJson);
        ids.retainAll(skills.keySet());
        return ids;
    }

    private Set<ResourceLocation> extractSkillIds(JsonObject treeJson) {
        Set<ResourceLocation> ids = new LinkedHashSet<>();
        if (!treeJson.has("skillIds")) {
            return ids;
        }
        for (var element : treeJson.getAsJsonArray("skillIds")) {
            if (element.isJsonPrimitive()) {
                ids.add(Ids.parse(element.getAsString(), "skillId"));
            }
        }
        return ids;
    }

    private void attachSkillToReferencingTrees(ResourceLocation skillId) {
        for (Map.Entry<ResourceLocation, JsonObject> entry : trees.entrySet()) {
            if (treeReferencesSkill(entry.getValue(), skillId)) {
                treeSkillIds.computeIfAbsent(entry.getKey(), ignored -> new LinkedHashSet<>()).add(skillId);
            }
        }
    }

    private boolean treeReferencesSkill(JsonObject treeJson, ResourceLocation skillId) {
        return extractSkillIds(treeJson).contains(skillId);
    }

    private boolean isOwnedByAnotherTree(ResourceLocation skillId) {
        for (Set<ResourceLocation> ownedSkillIds : treeSkillIds.values()) {
            if (ownedSkillIds.contains(skillId)) {
                return true;
            }
        }
        return false;
    }

    private JsonObject effectiveTreeJson(ResourceLocation treeId) {
        JsonObject json = JsonHelper.copy(trees.get(treeId)).getAsJsonObject();
        Set<ResourceLocation> ownedSkillIds = treeSkillIds.getOrDefault(treeId, Set.of());
        if (!json.has("skillIds")) {
            return json;
        }

        var filteredSkillIds = JsonHelper.array();
        for (var element : json.getAsJsonArray("skillIds")) {
            if (!element.isJsonPrimitive()) {
                filteredSkillIds.add(element);
                continue;
            }

            ResourceLocation skillId = Ids.parse(element.getAsString(), "skillId");
            if (blockedSkills.contains(skillId) && !skills.containsKey(skillId)) {
                continue;
            }
            if (ownedSkillIds.contains(skillId) && !skills.containsKey(skillId)) {
                continue;
            }
            if (!isKnownSkillId(skillId)) {
                continue;
            }
            filteredSkillIds.add(element);
        }
        json.add("skillIds", filteredSkillIds);
        return json;
    }

    private boolean isKnownSkillId(ResourceLocation skillId) {
        return skills.containsKey(skillId) || SkillsReloader.getSkills().containsKey(skillId);
    }
}
