package com.pickaid.passivestjs.kubejs.content;

import com.google.gson.JsonObject;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.skill.PassiveSkill;
import daripher.skilltree.skill.PassiveSkillTree;
import dev.latvian.mods.kubejs.script.data.DataPackEventJS;
import net.minecraft.resources.ResourceLocation;
import com.pickaid.passivestjs.kubejs.builder.Ids;
import com.pickaid.passivestjs.kubejs.builder.SkillBuilder;
import com.pickaid.passivestjs.kubejs.builder.SkillTreeBuilder;

public final class Skills {
    public static final Skills INSTANCE = new Skills();

    private static final String LESSER_BACKGROUND = "skilltree:textures/icons/background/lesser.png";
    private static final String CLASS_BACKGROUND = "skilltree:textures/icons/background/class.png";
    private static final String DEFAULT_BORDER = "skilltree:textures/tooltip/lesser.png";
    private static final String DEFAULT_ICON = "minecraft:textures/item/barrier.png";

    private Skills() {
    }

    public SkillBuilder createSkill(Object id) {
        ResourceLocation skillId = Ids.parse(id, "skillId");
        JsonObject existing = existingSkillJson(skillId);
        if (existing != null) {
            return SkillBuilder.fromJson(existing);
        }
        return new SkillBuilder(skillId, false);
    }

    public SkillBuilder createStartingSkill(Object id) {
        return createSkill(id).startingPoint(true);
    }

    public SkillTreeBuilder createTree(Object id) {
        ResourceLocation treeId = Ids.parse(id, "treeId");
        JsonObject existing = existingTreeJson(treeId);
        if (existing != null) {
            return SkillTreeBuilder.fromJson(existing);
        }
        return new SkillTreeBuilder(treeId);
    }

    public JsonObject skill(String id) {
        return createSkill(id).toJson();
    }

    public JsonObject startingSkill(String id) {
        return createStartingSkill(id).toJson();
    }

    public JsonObject skillTree(String id) {
        return createTree(id).toJson();
    }

    public void addSkill(DataPackEventJS event, String id, Object overrides) {
        ResourceLocation skillId = parseId(id);
        JsonObject json = buildSkillJson(skillId, overrides);
        event.addJson(toSkillResource(skillId), json);
    }

    public void addStartingSkill(DataPackEventJS event, String id, Object overrides) {
        ResourceLocation skillId = parseId(id);
        JsonObject json = buildStartingSkillJson(skillId, overrides);
        event.addJson(toSkillResource(skillId), json);
    }

    public void addSkillTree(DataPackEventJS event, String id, Object overrides) {
        ResourceLocation treeId = parseId(id);
        JsonObject json = buildSkillTreeJson(treeId, overrides);
        event.addJson(toSkillTreeResource(treeId), json);
    }

    public void writeSkill(DataPackEventJS event, SkillBuilder skill) {
        event.addJson(toSkillResource(skill.idLocation()), skill.toJson());
    }

    public void writeTree(DataPackEventJS event, SkillTreeBuilder tree) {
        for (SkillBuilder skill : tree.builtSkills()) {
            writeSkill(event, skill);
        }
        event.addJson(toSkillTreeResource(tree.idLocation()), tree.toJson());
    }

    public JsonObject buildSkillJson(String id, Object overrides) {
        return buildSkillJson(parseId(id), overrides);
    }

    public JsonObject buildStartingSkillJson(String id, Object overrides) {
        return buildStartingSkillJson(parseId(id), overrides);
    }

    public JsonObject buildSkillTreeJson(String id, Object overrides) {
        return buildSkillTreeJson(parseId(id), overrides);
    }

    public PassiveSkill deserializeSkill(JsonObject json) {
        return SkillsReloader.GSON.fromJson(json, PassiveSkill.class);
    }

    public PassiveSkillTree deserializeTree(JsonObject json) {
        return SkillTreesReloader.GSON.fromJson(json, PassiveSkillTree.class);
    }

    public void applyRuntimeSkill(JsonObject json) {
        PassiveSkill skill = deserializeSkill(json);
        SkillsReloader.getSkills().put(skill.getId(), skill);
    }

    public void applyRuntimeTree(JsonObject json) {
        PassiveSkillTree tree = deserializeTree(json);
        SkillTreesReloader.getSkillTrees().put(tree.getId(), tree);
    }

    public void removeRuntimeSkill(ResourceLocation id) {
        SkillsReloader.getSkills().remove(id);
    }

    public void removeRuntimeTree(ResourceLocation id) {
        SkillTreesReloader.getSkillTrees().remove(id);
    }

    public static ResourceLocation skillResource(ResourceLocation id) {
        return toSkillResource(id);
    }

    public static ResourceLocation skillTreeResource(ResourceLocation id) {
        return toSkillTreeResource(id);
    }

    private static JsonObject createSkillTemplate(ResourceLocation id, boolean startingPoint) {
        JsonObject json = new JsonObject();
        json.addProperty("id", id.toString());
        json.add("bonuses", JsonHelper.array());
        json.add("requirements", JsonHelper.array());
        json.add("directConnections", JsonHelper.array());
        json.add("longConnections", JsonHelper.array());
        json.add("oneWayConnections", JsonHelper.array());
        json.add("tags", JsonHelper.array());
        json.addProperty("backgroundTexture", startingPoint ? CLASS_BACKGROUND : LESSER_BACKGROUND);
        json.addProperty("iconTexture", DEFAULT_ICON);
        json.addProperty("borderTexture", DEFAULT_BORDER);
        json.addProperty("positionX", 0.0D);
        json.addProperty("positionY", 0.0D);
        json.addProperty("buttonSize", startingPoint ? 24 : 16);
        json.addProperty("isStartingPoint", startingPoint);
        return json;
    }

    private static JsonObject createSkillBase(ResourceLocation skillId, boolean startingPoint) {
        JsonObject existing = existingSkillJson(skillId);
        return existing != null ? existing : createSkillTemplate(skillId, startingPoint);
    }

    private static JsonObject createTreeBase(ResourceLocation treeId) {
        JsonObject existing = existingTreeJson(treeId);
        return existing != null ? existing : new SkillTreeBuilder(treeId).toJson();
    }

    private static ResourceLocation parseId(String id) {
        return Ids.parse(id, "id");
    }

    private static JsonObject buildSkillJson(ResourceLocation skillId, Object overrides) {
        JsonObject json = JsonMerge.merge(createSkillBase(skillId, false), overrides);
        json.addProperty("id", skillId.toString());
        return json;
    }

    private static JsonObject buildStartingSkillJson(ResourceLocation skillId, Object overrides) {
        JsonObject json = JsonMerge.merge(createSkillBase(skillId, true), overrides);
        json.addProperty("id", skillId.toString());
        json.addProperty("isStartingPoint", true);
        return json;
    }

    private static JsonObject buildSkillTreeJson(ResourceLocation treeId, Object overrides) {
        JsonObject json = JsonMerge.merge(createTreeBase(treeId), overrides);
        json.addProperty("id", treeId.toString());
        return json;
    }

    private static JsonObject existingSkillJson(ResourceLocation id) {
        if (ManagedContent.managesSkill(id)) {
            return null;
        }
        var existing = SkillsReloader.getSkillById(id);
        if (existing == null) {
            return null;
        }
        return SkillsReloader.GSON.toJsonTree(existing).getAsJsonObject();
    }

    private static JsonObject existingTreeJson(ResourceLocation id) {
        if (ManagedContent.managesTree(id)) {
            return null;
        }
        var existing = SkillTreesReloader.getSkillTrees().get(id);
        if (existing == null) {
            return null;
        }
        return SkillTreesReloader.GSON.toJsonTree(existing).getAsJsonObject();
    }

    static ResourceLocation toSkillResource(ResourceLocation id) {
        return new ResourceLocation(id.getNamespace(), "skills/" + id.getPath());
    }

    static ResourceLocation toSkillTreeResource(ResourceLocation id) {
        return new ResourceLocation(id.getNamespace(), "skill_trees/" + id.getPath());
    }
}
