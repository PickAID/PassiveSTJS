package com.pickaid.passivestjs.kubejs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pickaid.passivestjs.kubejs.content.Bonuses;
import com.pickaid.passivestjs.kubejs.content.Conditions;
import com.pickaid.passivestjs.kubejs.content.ContentApi;
import com.pickaid.passivestjs.kubejs.content.JsonHelper;
import com.pickaid.passivestjs.kubejs.content.Listeners;
import com.pickaid.passivestjs.kubejs.content.Multipliers;
import com.pickaid.passivestjs.kubejs.content.Requirements;
import com.pickaid.passivestjs.kubejs.content.Skills;
import com.pickaid.passivestjs.kubejs.content.Values;
import dev.latvian.mods.kubejs.script.data.DataPackEventJS;
import net.minecraft.resources.ResourceLocation;

public final class Bindings {
    public static final Bindings INSTANCE = new Bindings();

    private final ContentApi content = ContentApi.INSTANCE;

    private Bindings() {
    }

    public ContentApi pst() {
        return content;
    }

    public ContentApi content() {
        return content;
    }

    public Bonuses bonuses() {
        return content.bonuses();
    }

    public Conditions conditions() {
        return content.conditions();
    }

    public Requirements requirements() {
        return content.requirements();
    }

    public Listeners listeners() {
        return content.listeners();
    }

    public Values values() {
        return content.values();
    }

    public Multipliers multipliers() {
        return content.multipliers();
    }

    public Skills skills() {
        return content.skills();
    }

    public JsonElement json(Object value) {
        return content.json(value);
    }

    public String prettyJson(Object value) {
        return content.pretty(value);
    }

    public void addDataJson(DataPackEventJS event, String id, Object value) {
        ResourceLocation resourceLocation = ResourceLocation.tryParse(id);
        if (resourceLocation == null) {
            throw new IllegalArgumentException("Invalid resource location: " + id);
        }
        event.addJson(resourceLocation, JsonHelper.requireElement(value, "value"));
    }

    public JsonObject skill(String id) {
        return content.skills().skill(id);
    }

    public JsonObject startingSkill(String id) {
        return content.skills().startingSkill(id);
    }

    public JsonObject skillTree(String id) {
        return content.skills().skillTree(id);
    }

    public void addSkill(DataPackEventJS event, String id, Object overrides) {
        content.skills().addSkill(event, id, overrides);
    }

    public void addStartingSkill(DataPackEventJS event, String id, Object overrides) {
        content.skills().addStartingSkill(event, id, overrides);
    }

    public void addSkillTree(DataPackEventJS event, String id, Object overrides) {
        content.skills().addSkillTree(event, id, overrides);
    }
}
