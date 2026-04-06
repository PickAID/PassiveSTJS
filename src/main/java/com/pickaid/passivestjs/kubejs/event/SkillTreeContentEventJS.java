package com.pickaid.passivestjs.kubejs.event;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.script.data.DataPackEventJS;
import dev.latvian.mods.kubejs.script.data.VirtualKubeJSDataPack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import com.pickaid.passivestjs.kubejs.content.Bonuses;
import com.pickaid.passivestjs.kubejs.content.Conditions;
import com.pickaid.passivestjs.kubejs.content.ContentApi;
import com.pickaid.passivestjs.kubejs.content.ManagedContent;
import com.pickaid.passivestjs.kubejs.content.Listeners;
import com.pickaid.passivestjs.kubejs.content.JsonHelper;
import com.pickaid.passivestjs.kubejs.content.Multipliers;
import com.pickaid.passivestjs.kubejs.content.Requirements;
import com.pickaid.passivestjs.kubejs.content.Skills;
import com.pickaid.passivestjs.kubejs.content.Values;
import com.pickaid.passivestjs.kubejs.builder.Ids;
import com.pickaid.passivestjs.kubejs.builder.SkillBuilder;
import com.pickaid.passivestjs.kubejs.builder.SkillTreeBuilder;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SkillTreeContentEventJS extends DataPackEventJS {
    private final Map<ResourceLocation, SkillTreeBuilder> trees = new LinkedHashMap<>();
    private final Map<ResourceLocation, SkillBuilder> looseSkills = new LinkedHashMap<>();
    private final Map<ResourceLocation, JsonObject> pendingSkillJsons = new LinkedHashMap<>();
    private final Map<ResourceLocation, JsonObject> pendingTreeJsons = new LinkedHashMap<>();
    private final Set<ResourceLocation> removedTreeIds = new LinkedHashSet<>();
    private final Set<ResourceLocation> removedSkillIds = new LinkedHashSet<>();
    private final Set<ResourceLocation> managedTreeIds = new LinkedHashSet<>();
    private final Set<ResourceLocation> managedSkillIds = new LinkedHashSet<>();

    public SkillTreeContentEventJS(VirtualKubeJSDataPack dataPack, MultiPackResourceManager resourceManager) {
        super(dataPack, resourceManager);
    }

    public ContentApi pst() {
        return ContentApi.INSTANCE;
    }

    public Bonuses bonuses() {
        return Bonuses.INSTANCE;
    }

    public Conditions conditions() {
        return Conditions.INSTANCE;
    }

    public Requirements requirements() {
        return Requirements.INSTANCE;
    }

    public Listeners listeners() {
        return Listeners.INSTANCE;
    }

    public Values values() {
        return Values.INSTANCE;
    }

    public Multipliers multipliers() {
        return Multipliers.INSTANCE;
    }

    public Skills skills() {
        return Skills.INSTANCE;
    }

    public SkillTreeBuilder createTree(Object id) {
        SkillTreeBuilder template = Skills.INSTANCE.createTree(id);
        managedTreeIds.add(template.idLocation());
        return trees.computeIfAbsent(template.idLocation(), ignored -> template);
    }

    public SkillBuilder createSkill(Object id) {
        SkillBuilder template = Skills.INSTANCE.createSkill(id);
        managedSkillIds.add(template.idLocation());
        return looseSkills.computeIfAbsent(template.idLocation(), ignored -> template);
    }

    public SkillBuilder createStartingSkill(Object id) {
        SkillBuilder skill = createSkill(id);
        skill.startingPoint(true);
        return skill;
    }

    public JsonElement json(Object value) {
        return JsonHelper.requireElement(value, "value");
    }

    public String prettyJson(Object value) {
        return JsonHelper.pretty(value);
    }

    public JsonObject skill(String id) {
        return Skills.INSTANCE.skill(id);
    }

    public JsonObject startingSkill(String id) {
        return Skills.INSTANCE.startingSkill(id);
    }

    public JsonObject skillTree(String id) {
        return Skills.INSTANCE.skillTree(id);
    }

    public void addSkill(String id, Object overrides) {
        ResourceLocation skillId = Ids.parse(id, "skillId");
        JsonObject json = Skills.INSTANCE.buildSkillJson(id, overrides);
        managedSkillIds.add(skillId);
        queueRuntimeSkill(skillId, json);
        addJson(Skills.skillResource(skillId), json);
    }

    public void addStartingSkill(String id, Object overrides) {
        ResourceLocation skillId = Ids.parse(id, "skillId");
        JsonObject json = Skills.INSTANCE.buildStartingSkillJson(id, overrides);
        managedSkillIds.add(skillId);
        queueRuntimeSkill(skillId, json);
        addJson(Skills.skillResource(skillId), json);
    }

    public void addSkillTree(String id, Object overrides) {
        ResourceLocation treeId = Ids.parse(id, "treeId");
        JsonObject json = Skills.INSTANCE.buildSkillTreeJson(id, overrides);
        managedTreeIds.add(treeId);
        queueRuntimeTree(treeId, json);
        addJson(Skills.skillTreeResource(treeId), json);
    }

    public void disableTree(Object id) {
        removeTree(id);
    }

    public void removeTree(Object id) {
        ResourceLocation treeId = Ids.parse(id, "treeId");
        managedTreeIds.add(treeId);
        removedTreeIds.add(treeId);
    }

    public void disableSkill(Object id) {
        removeSkill(id);
    }

    public void removeSkill(Object id) {
        ResourceLocation skillId = Ids.parse(id, "skillId");
        managedSkillIds.add(skillId);
        removedSkillIds.add(skillId);
    }

    public void addDataJson(String id, Object value) {
        ResourceLocation resourceLocation = ResourceLocation.tryParse(id);
        if (resourceLocation == null) {
            throw new IllegalArgumentException("Invalid resource location: " + id);
        }
        addJson(resourceLocation, JsonHelper.requireElement(value, "value"));
    }

    public void flushGeneratedContent() {
        Set<ResourceLocation> writtenSkills = new LinkedHashSet<>();
        for (SkillTreeBuilder tree : trees.values()) {
            managedTreeIds.add(tree.idLocation());
            for (SkillBuilder skill : tree.builtSkills()) {
                writtenSkills.add(skill.idLocation());
                managedSkillIds.add(skill.idLocation());
                queueRuntimeSkill(skill.idLocation(), skill.toJson());
            }
            queueRuntimeTree(tree.idLocation(), tree.toJson());
            Skills.INSTANCE.writeTree(this, tree);
        }
        for (SkillBuilder skill : looseSkills.values()) {
            managedSkillIds.add(skill.idLocation());
            if (writtenSkills.add(skill.idLocation())) {
                queueRuntimeSkill(skill.idLocation(), skill.toJson());
                Skills.INSTANCE.writeSkill(this, skill);
            }
        }
    }

    @Override
    protected void afterPosted(EventResult result) {
        flushGeneratedContent();
        for (JsonObject skillJson : pendingSkillJsons.values()) {
            Skills.INSTANCE.applyRuntimeSkill(skillJson);
        }
        for (JsonObject treeJson : pendingTreeJsons.values()) {
            Skills.INSTANCE.applyRuntimeTree(treeJson);
        }
        for (ResourceLocation treeId : removedTreeIds) {
            Skills.INSTANCE.removeRuntimeTree(treeId);
        }
        for (ResourceLocation skillId : removedSkillIds) {
            Skills.INSTANCE.removeRuntimeSkill(skillId);
        }
        ManagedContent.replace(managedTreeIds, managedSkillIds);
        super.afterPosted(result);
    }

    private void queueRuntimeSkill(ResourceLocation skillId, JsonObject json) {
        pendingSkillJsons.put(skillId, JsonHelper.copy(json).getAsJsonObject());
    }

    private void queueRuntimeTree(ResourceLocation treeId, JsonObject json) {
        pendingTreeJsons.put(treeId, JsonHelper.copy(json).getAsJsonObject());
    }
}
