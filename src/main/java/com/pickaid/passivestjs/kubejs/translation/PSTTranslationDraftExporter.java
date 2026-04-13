package com.pickaid.passivestjs.kubejs.translation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pickaid.passivestjs.kubejs.builder.SkillBuilder;
import com.pickaid.passivestjs.kubejs.builder.SkillTreeBuilder;
import com.pickaid.passivestjs.kubejs.content.JsonHelper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;

public final class PSTTranslationDraftExporter {
    private final Path configDir;

    public PSTTranslationDraftExporter(Path configDir) {
        this.configDir = Objects.requireNonNull(configDir, "configDir");
    }

    @Deprecated(forRemoval = false)
    public PSTTranslationDraftExporter(Path configDir, String locale) {
        this(configDir);
    }

    public boolean exportSkills(Collection<JsonObject> skills) throws IOException {
        JsonObject draft = new JsonObject();
        for (JsonObject skillJson : skills) {
            exportSkillJson(draft, skillJson);
        }
        return writeDraft(draft);
    }

    public boolean exportBuilders(Collection<SkillBuilder> skills, Collection<SkillTreeBuilder> trees) throws IOException {
        JsonObject draft = new JsonObject();
        for (SkillBuilder skill : skills) {
            exportSkillJson(draft, skill.toJson());
        }
        for (SkillTreeBuilder tree : trees) {
            exportTreeJson(draft, tree.toJson());
        }
        return writeDraft(draft);
    }

    private void exportSkillJson(JsonObject draft, JsonObject skillJson) {
        collectTranslations(draft, skillJson);
    }

    private void exportTreeJson(JsonObject draft, JsonObject treeJson) {
        collectTranslations(draft, treeJson);
    }

    private void collectTranslations(JsonObject draft, JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return;
        }
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement child : array) {
                collectTranslations(draft, child);
            }
            return;
        }
        if (!element.isJsonObject()) {
            return;
        }

        JsonObject object = element.getAsJsonObject();
        JsonElement translate = object.get("translate");
        if (translate != null && translate.isJsonPrimitive() && translate.getAsJsonPrimitive().isString()) {
            String key = normalizeKey(translate.getAsString());
            if (key != null) {
                draft.addProperty(key, "");
            }
        }
        for (var entry : object.entrySet()) {
            collectTranslations(draft, entry.getValue());
        }
    }

    private boolean writeDraft(JsonObject draft) throws IOException {
        String content = JsonHelper.pretty(draft);
        Path output = draftFile();
        if (Files.exists(output) && content.equals(Files.readString(output, StandardCharsets.UTF_8))) {
            return false;
        }
        Files.createDirectories(output.getParent());
        Files.writeString(output, content, StandardCharsets.UTF_8);
        return true;
    }

    private Path draftFile() {
        return configDir.resolve("passivestjs").resolve("generated_skilltree_lang").resolve("lang.json");
    }

    private static String normalizeKey(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
