package com.pickaid.passivestjs.kubejs.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.rhino.Wrapper;
import net.minecraft.resources.ResourceLocation;
import com.pickaid.passivestjs.kubejs.content.Skills;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SkillTreeBuilder implements JsonFragment {
    private final ResourceLocation id;
    private final Map<ResourceLocation, SkillBuilder> skills = new LinkedHashMap<>();
    private final Set<ResourceLocation> externalSkillIds = new LinkedHashSet<>();
    private final Map<String, Integer> limits = new LinkedHashMap<>();

    public SkillTreeBuilder(Object id) {
        this.id = Ids.parse(id, "treeId");
    }

    private SkillTreeBuilder(JsonObject json) {
        this.id = Ids.parse(json.get("id").getAsString(), "treeId");
        JsonArray skillIds = json.getAsJsonArray("skillIds");
        if (skillIds != null) {
            for (JsonElement element : skillIds) {
                if (element.isJsonPrimitive()) {
                    externalSkillIds.add(Ids.parse(element.getAsString(), "skillId"));
                }
            }
        }
        JsonObject limitationJson = json.getAsJsonObject("skillLimitations");
        if (limitationJson != null) {
            for (Map.Entry<String, JsonElement> entry : limitationJson.entrySet()) {
                limits.put(entry.getKey(), entry.getValue().getAsInt());
            }
        }
    }

    public static SkillTreeBuilder fromJson(JsonObject json) {
        return new SkillTreeBuilder(json);
    }

    public String id() {
        return id.toString();
    }

    public ResourceLocation idLocation() {
        return id;
    }

    public SkillBuilder skill(Object skillId) {
        ResourceLocation key = Ids.parse(skillId, "skillId");
        externalSkillIds.remove(key);
        return skills.computeIfAbsent(key, ignored -> Skills.INSTANCE.createSkill(key));
    }

    public SkillBuilder startingSkill(Object skillId) {
        return skill(skillId).startingPoint(true);
    }

    public SkillTreeBuilder addSkill(Object skill) {
        Object unwrapped = Wrapper.unwrapped(skill);
        if (unwrapped instanceof SkillBuilder builder) {
            externalSkillIds.remove(builder.idLocation());
            skills.put(builder.idLocation(), builder);
        } else {
            externalSkillIds.add(Ids.parse(unwrapped, "skillId"));
        }
        return this;
    }

    public SkillTreeBuilder includeSkill(Object skillId) {
        externalSkillIds.add(Ids.parse(skillId, "skillId"));
        return this;
    }

    public SkillTreeBuilder limit(String tag, Number amount) {
        if (tag != null && !tag.isBlank() && amount != null) {
            limits.put(tag, amount.intValue());
        }
        return this;
    }

    public Collection<SkillBuilder> builtSkills() {
        return skills.values();
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id());
        JsonArray skillIds = new JsonArray();
        for (ResourceLocation skillId : externalSkillIds) {
            skillIds.add(new JsonPrimitive(skillId.toString()));
        }
        for (SkillBuilder skill : skills.values()) {
            skillIds.add(new JsonPrimitive(skill.id()));
        }
        json.add("skillIds", skillIds);
        if (!limits.isEmpty()) {
            JsonObject limitJson = new JsonObject();
            for (Map.Entry<String, Integer> entry : limits.entrySet()) {
                limitJson.addProperty(entry.getKey(), entry.getValue());
            }
            json.add("skillLimitations", limitJson);
        }
        return json;
    }
}
