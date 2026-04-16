package com.pickaid.passivestjs.kubejs.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.pickaid.passivestjs.skilltree.PSTContentTitles;
import com.pickaid.passivestjs.kubejs.content.Skills;
import com.pickaid.passivestjs.kubejs.id.PSTSkillId;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

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
    private Component title;

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
        this.title = PSTContentTitles.readExplicit(json);
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

    @HideFromJS
    public SkillTreeBuilder titleLiteral(String value) {
        this.title = literalComponent(value);
        return this;
    }

    @Info("Sets the display title component for this tree selection entry.")
    public SkillTreeBuilder title(Component value) {
        this.title = PSTContentTitles.copy(value);
        return this;
    }

    @HideFromJS
    public String nativeTitleKey() {
        return id.toString();
    }

    @Info(value = "Returns a managed skill builder inside this tree, creating it when absent.", params = {
            @Param(name = "skillId", value = "The skill resource location.")
    })
    public SkillBuilder skill(PSTSkillId skillId) {
        ResourceLocation key = PSTSkillId.parse(skillId).location();
        externalSkillIds.remove(key);
        return skills.computeIfAbsent(key, ignored -> Skills.INSTANCE.createSkill(key));
    }

    @HideFromJS
    public SkillBuilder skill(Object skillId) {
        return skill(PSTSkillId.of(Ids.parse(skillId, "skillId")));
    }

    @Info(value = "Returns a managed starting-skill builder inside this tree, creating it when absent.", params = {
            @Param(name = "skillId", value = "The skill resource location.")
    })
    public SkillBuilder startingSkill(PSTSkillId skillId) {
        return skill(skillId).startingPoint(true);
    }

    @HideFromJS
    public SkillBuilder startingSkill(Object skillId) {
        return startingSkill(PSTSkillId.of(Ids.parse(skillId, "skillId")));
    }

    @Info(value = "Adds an already-built managed skill builder into this tree.", params = {
            @Param(name = "skill", value = "The prebuilt skill builder to include.")
    })
    public SkillTreeBuilder addSkill(SkillBuilder skill) {
        if (skill == null) {
            return this;
        }
        externalSkillIds.remove(skill.idLocation());
        skills.put(skill.idLocation(), skill);
        return this;
    }

    @HideFromJS
    public SkillTreeBuilder addSkill(Object skill) {
        Object unwrapped = Wrapper.unwrapped(skill);
        if (unwrapped instanceof SkillBuilder builder) {
            return addSkill(builder);
        } else {
            externalSkillIds.add(Ids.parse(unwrapped, "skillId"));
        }
        return this;
    }

    @Info(value = "Includes an external skill id in this tree without creating a managed builder.", params = {
            @Param(name = "skillId", value = "The external skill resource location.")
    })
    public SkillTreeBuilder includeSkill(PSTSkillId skillId) {
        externalSkillIds.add(PSTSkillId.parse(skillId).location());
        return this;
    }

    @HideFromJS
    public SkillTreeBuilder includeSkill(Object skillId) {
        return includeSkill(PSTSkillId.of(Ids.parse(skillId, "skillId")));
    }

    @Info(value = "Sets the per-tag skill limit for this tree.", params = {
            @Param(name = "tag", value = "The skill tag being limited."),
            @Param(name = "amount", value = "The maximum number of skills with that tag.")
    })
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
        PSTContentTitles.writeJson(json, title);
        return json;
    }

    private static String normalizeText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    private static Component literalComponent(String value) {
        String normalized = normalizeText(value);
        return normalized == null ? null : Component.literal(normalized);
    }
}
