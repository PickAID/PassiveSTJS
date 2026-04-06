package com.pickaid.passivestjs.kubejs.content;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Set;

public final class JsonMerge {
    private static final Set<String> UNIQUE_ARRAY_FIELDS = Set.of(
            "skillIds",
            "directConnections",
            "longConnections",
            "oneWayConnections",
            "tags"
    );

    private JsonMerge() {
    }

    public static JsonObject merge(JsonObject base, Object overrides) {
        JsonObject merged = JsonHelper.copy(base).getAsJsonObject();
        if (overrides == null) {
            return merged;
        }

        JsonObject overrideObject = JsonHelper.requireObject(overrides, "overrides");
        mergeInto(merged, overrideObject);
        return merged;
    }

    private static void mergeInto(JsonObject target, JsonObject overrides) {
        for (var entry : overrides.entrySet()) {
            String key = entry.getKey();
            JsonElement overrideValue = entry.getValue();
            JsonElement existingValue = target.get(key);

            if (existingValue == null || existingValue.isJsonNull()) {
                target.add(key, JsonHelper.copy(overrideValue));
                continue;
            }

            if (existingValue.isJsonObject() && overrideValue.isJsonObject()) {
                JsonObject mergedChild = JsonHelper.copy(existingValue).getAsJsonObject();
                mergeInto(mergedChild, overrideValue.getAsJsonObject());
                target.add(key, mergedChild);
                continue;
            }

            if (existingValue.isJsonArray() && overrideValue.isJsonArray()) {
                target.add(key, mergeArrays(key, existingValue.getAsJsonArray(), overrideValue.getAsJsonArray()));
                continue;
            }

            target.add(key, JsonHelper.copy(overrideValue));
        }
    }

    private static JsonArray mergeArrays(String key, JsonArray base, JsonArray overrides) {
        JsonArray merged = JsonHelper.copy(base).getAsJsonArray();
        boolean unique = UNIQUE_ARRAY_FIELDS.contains(key);

        for (JsonElement element : overrides) {
            JsonElement copy = JsonHelper.copy(element);
            if (!unique || !contains(merged, copy)) {
                merged.add(copy);
            }
        }

        return merged;
    }

    private static boolean contains(JsonArray array, JsonElement candidate) {
        for (JsonElement element : array) {
            if (element.equals(candidate)) {
                return true;
            }
        }
        return false;
    }
}
