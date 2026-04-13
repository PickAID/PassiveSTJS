package com.pickaid.passivestjs.compat.skilltree;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class PSTContentTitles {
    public static final String TITLE_COMPONENT_FIELD = "passivestjs$title_component";

    private static final Map<ResourceLocation, Component> SKILL_TITLES = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Component> TREE_TITLES = new LinkedHashMap<>();

    private PSTContentTitles() {
    }

    public static synchronized void writeJson(JsonObject json, Component title) {
        Component normalized = normalize(title);
        if (json == null || normalized == null) {
            return;
        }
        json.add(TITLE_COMPONENT_FIELD, Component.Serializer.toJsonTree(normalized));
    }

    public static synchronized Component readExplicit(JsonObject json) {
        if (json == null || !json.has(TITLE_COMPONENT_FIELD)) {
            return null;
        }
        JsonElement element = json.get(TITLE_COMPONENT_FIELD);
        MutableComponent component = Component.Serializer.fromJson(element);
        return normalize(component);
    }

    public static synchronized void captureSkillTitle(ResourceLocation id, JsonObject json) {
        putSkillTitle(id, readExplicit(json));
    }

    public static synchronized void captureTreeTitle(ResourceLocation id, JsonObject json) {
        putTreeTitle(id, readExplicit(json));
    }

    public static synchronized void putSkillTitle(ResourceLocation id, Component title) {
        put(SKILL_TITLES, id, title);
    }

    public static synchronized void putTreeTitle(ResourceLocation id, Component title) {
        put(TREE_TITLES, id, title);
    }

    public static synchronized Component getSkillTitle(ResourceLocation id) {
        return copy(SKILL_TITLES.get(id));
    }

    public static synchronized Component getTreeTitle(ResourceLocation id) {
        return copy(TREE_TITLES.get(id));
    }

    public static synchronized void retainSkillTitles(Set<ResourceLocation> ids) {
        SKILL_TITLES.keySet().retainAll(ids);
    }

    public static synchronized void retainTreeTitles(Set<ResourceLocation> ids) {
        TREE_TITLES.keySet().retainAll(ids);
    }

    public static synchronized void clearSkillTitles() {
        SKILL_TITLES.clear();
    }

    public static synchronized void clearTreeTitles() {
        TREE_TITLES.clear();
    }

    public static void write(FriendlyByteBuf buffer, Component title) {
        buffer.writeNullable(normalize(title), FriendlyByteBuf::writeComponent);
    }

    public static Component read(FriendlyByteBuf buffer) {
        return normalize(buffer.readNullable(FriendlyByteBuf::readComponent));
    }

    public static String literalText(Component title) {
        return literalTextInternal(normalize(title));
    }

    public static Component copy(Component title) {
        return normalize(title);
    }

    private static void put(Map<ResourceLocation, Component> target, ResourceLocation id, Component title) {
        if (id == null) {
            return;
        }
        Component normalized = normalize(title);
        if (normalized == null) {
            target.remove(id);
            return;
        }
        target.put(id, normalized);
    }

    private static Component normalize(Component title) {
        if (title == null) {
            return null;
        }
        String json = Component.Serializer.toJson(title);
        if (json == null || json.isBlank()) {
            return null;
        }
        MutableComponent parsed = Component.Serializer.fromJson(json);
        if (parsed == null) {
            return null;
        }
        return parsed;
    }

    private static String literalTextInternal(Component title) {
        if (title == null) {
            return null;
        }
        JsonElement element = Component.Serializer.toJsonTree(title);
        if (!element.isJsonObject()) {
            return null;
        }
        JsonObject object = element.getAsJsonObject();
        if (object.size() != 1 || !object.has("text") || !object.get("text").isJsonPrimitive()) {
            return null;
        }
        return normalizeText(object.get("text").getAsString());
    }

    private static String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
