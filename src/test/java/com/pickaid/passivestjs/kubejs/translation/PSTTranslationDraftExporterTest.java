package com.pickaid.passivestjs.kubejs.translation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pickaid.passivestjs.kubejs.builder.SkillBuilder;
import com.pickaid.passivestjs.kubejs.builder.SkillTreeBuilder;
import com.pickaid.passivestjs.kubejs.content.JsonHelper;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PSTTranslationDraftExporterTest {
    @TempDir
    Path tempDir;

    @Test
    void exportsTranslatableKeysIntoLangTemplateJson() throws IOException {
        JsonObject skillJson = new JsonObject();
        skillJson.addProperty("id", "kubejs:passivestjs/root");
        skillJson.add("passivestjs$title_component", Component.Serializer.toJsonTree(Component.translatable("kubejs.passivestjs.skill.root.title")));
        skillJson.add("description", JsonHelper.array());
        skillJson.getAsJsonArray("description").add(Component.Serializer.toJsonTree(Component.translatable("kubejs.passivestjs.skill.root.desc.0")));
        skillJson.getAsJsonArray("description").add(JsonHelper.translationText("kubejs.manual.description"));
        skillJson.getAsJsonArray("description").add(Component.Serializer.toJsonTree(Component.literal("Second generated description line.")));

        new PSTTranslationDraftExporter(tempDir, "en_us").exportSkills(List.of(skillJson));

        JsonObject langJson = readJson(tempDir.resolve("passivestjs/generated_skilltree_lang/lang.json"));
        assertEquals("", langJson.get("kubejs.passivestjs.skill.root.title").getAsString());
        assertEquals("", langJson.get("kubejs.passivestjs.skill.root.desc.0").getAsString());
        assertEquals("", langJson.get("kubejs.manual.description").getAsString());
        assertEquals(3, langJson.size());
        assertFalse(Files.exists(tempDir.resolve("kubejs/assets/kubejs/lang/en_us.json")));
    }

    @Test
    void exportBuildersWritesLangTemplateWithCollectedTranslationKeys() throws IOException {
        SkillBuilder literalSkill = new SkillBuilder("kubejs:passivestjs/root", true)
                .title(Component.literal("Run Showcase Root"))
                .descriptionLine(Component.literal("Literal line"));
        SkillBuilder translatedSkill = new SkillBuilder("kubejs:passivestjs/translated", false)
                .title(Component.translatable("kubejs.passivestjs.skill.translated.title"))
                .descriptionLine(Component.translatable("kubejs.passivestjs.skill.translated.desc.0"));
        SkillTreeBuilder tree = new SkillTreeBuilder("kubejs:passivestjs_tree")
                .title(Component.translatable("kubejs.passivestjs.tree.showcase.title"));

        new PSTTranslationDraftExporter(tempDir, "zh_cn").exportBuilders(List.of(literalSkill, translatedSkill), List.of(tree));

        JsonObject langJson = readJson(tempDir.resolve("passivestjs/generated_skilltree_lang/lang.json"));
        assertEquals("", langJson.get("kubejs.passivestjs.skill.translated.title").getAsString());
        assertEquals("", langJson.get("kubejs.passivestjs.skill.translated.desc.0").getAsString());
        assertEquals("", langJson.get("kubejs.passivestjs.tree.showcase.title").getAsString());
        assertNull(langJson.get("skill.kubejs.passivestjs/root.name"));
        assertNull(langJson.get("skill.kubejs.passivestjs/root.desc.0"));
    }

    @Test
    void reexportOverwritesDraftFileWithCurrentEntriesOnly() throws IOException {
        PSTTranslationDraftExporter exporter = new PSTTranslationDraftExporter(tempDir, "zh_cn");

        assertTrue(exporter.exportBuilders(
                List.of(new SkillBuilder("kubejs:passivestjs/old", true).title(Component.translatable("kubejs.passivestjs.skill.old.title"))),
                List.of()
        ));

        assertTrue(exporter.exportBuilders(
                List.of(new SkillBuilder("kubejs:passivestjs/new", true).title(Component.translatable("kubejs.passivestjs.skill.new.title"))),
                List.of()
        ));

        JsonObject langJson = readJson(tempDir.resolve("passivestjs/generated_skilltree_lang/lang.json"));
        assertFalse(langJson.has("kubejs.passivestjs.skill.old.title"));
        assertEquals("", langJson.get("kubejs.passivestjs.skill.new.title").getAsString());
    }

    private static JsonObject readJson(Path path) throws IOException {
        return JsonParser.parseString(Files.readString(path)).getAsJsonObject();
    }
}
