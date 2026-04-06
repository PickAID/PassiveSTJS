package com.pickaid.passivestjs.kubejs.content;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonMergeTest {
    @Test
    void mergesArrayFieldsByAppendingBonuses() {
        JsonObject base = new JsonObject();
        JsonArray bonuses = new JsonArray();
        JsonObject existingBonus = new JsonObject();
        existingBonus.addProperty("type", "skilltree:damage");
        bonuses.add(existingBonus);
        base.add("bonuses", bonuses);

        JsonObject overrides = new JsonObject();
        JsonArray extraBonuses = new JsonArray();
        JsonObject newBonus = new JsonObject();
        newBonus.addProperty("type", "passiveintegration:gun_ammo_free_unlock");
        extraBonuses.add(newBonus);
        overrides.add("bonuses", extraBonuses);

        JsonObject merged = JsonMerge.merge(base, overrides);
        assertEquals(2, merged.getAsJsonArray("bonuses").size());
    }

    @Test
    void mergesSkillIdsWithoutDuplicatingEntries() {
        JsonObject base = new JsonObject();
        JsonArray skillIds = new JsonArray();
        skillIds.add(new JsonPrimitive("minecraft:existing"));
        base.add("skillIds", skillIds);

        JsonObject overrides = new JsonObject();
        JsonArray extraSkillIds = new JsonArray();
        extraSkillIds.add(new JsonPrimitive("minecraft:existing"));
        extraSkillIds.add(new JsonPrimitive("kubejs:new_skill"));
        overrides.add("skillIds", extraSkillIds);

        JsonObject merged = JsonMerge.merge(base, overrides);
        assertEquals(2, merged.getAsJsonArray("skillIds").size());
    }

    @Test
    void mergesSkillLimitationsObjectEntries() {
        JsonObject base = new JsonObject();
        JsonObject limitations = new JsonObject();
        limitations.addProperty("existing", 1);
        base.add("skillLimitations", limitations);

        JsonObject overrides = new JsonObject();
        JsonObject overrideLimitations = new JsonObject();
        overrideLimitations.addProperty("added", 2);
        overrides.add("skillLimitations", overrideLimitations);

        JsonObject merged = JsonMerge.merge(base, overrides);
        JsonObject mergedLimitations = merged.getAsJsonObject("skillLimitations");

        assertEquals(1, mergedLimitations.get("existing").getAsInt());
        assertEquals(2, mergedLimitations.get("added").getAsInt());
    }
}
