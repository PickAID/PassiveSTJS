package com.pickaid.passivestjs.kubejs.event;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pickaid.passivestjs.config.PassiveSTJSCommonConfig;
import com.pickaid.passivestjs.kubejs.builder.SkillBuilder;
import com.pickaid.passivestjs.kubejs.builder.SkillTreeBuilder;
import com.pickaid.passivestjs.kubejs.content.JsonHelper;
import com.pickaid.passivestjs.kubejs.content.SkillTreeContentState;
import com.pickaid.passivestjs.kubejs.content.Skills;
import com.pickaid.passivestjs.kubejs.id.PSTSkillId;
import com.pickaid.passivestjs.kubejs.id.PSTTreeId;
import com.pickaid.passivestjs.kubejs.translation.PSTTranslationDraftExporter;
import com.pickaid.passivestjs.kubejs.registry.PSTRegistriesApi;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.script.data.DataPackEventJS;
import dev.latvian.mods.kubejs.script.data.VirtualKubeJSDataPack;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraftforge.fml.loading.FMLPaths;

import java.lang.reflect.Field;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

public class SkillTreeContentEventJS extends DataPackEventJS {
    private static final Field VIRTUAL_DATA_PACK_FIELD = field("virtualDataPack");
    private static final Field WRAPPED_MANAGER_FIELD = field("wrappedManager");

    private final MultiPackResourceManager resourceManager;
    private final Map<ResourceLocation, SkillTreeBuilder> trees = new LinkedHashMap<>();
    private final Map<ResourceLocation, SkillBuilder> looseSkills = new LinkedHashMap<>();
    private final SkillTreeContentState contentState = new SkillTreeContentState();

    public SkillTreeContentEventJS(VirtualKubeJSDataPack dataPack, MultiPackResourceManager resourceManager) {
        super(dataPack, resourceManager);
        this.resourceManager = resourceManager;
    }

    @HideFromJS
    public static SkillTreeContentEventJS wrap(DataPackEventJS event) {
        if (event instanceof SkillTreeContentEventJS wrapped) {
            return wrapped;
        }

        return new SkillTreeContentEventJS(readVirtualDataPack(event), readWrappedManager(event));
    }

    @HideFromJS
    public static SkillTreeContentEventJS synthetic() {
        return new SkillTreeContentEventJS(new VirtualKubeJSDataPack(true), null);
    }

    @Info("Returns the real PST registry handle API.")
    public PSTRegistriesApi registries() {
        return PSTRegistriesApi.INSTANCE;
    }

    @Info("Returns the shared skill and skill tree factory API.")
    @HideFromJS
    public Skills skills() {
        return Skills.INSTANCE;
    }

    private SkillTreeBuilder createTree(String id) {
        return createTree(PSTTreeId.parse(id));
    }

    private SkillTreeBuilder createTree(PSTTreeId id) {
        SkillTreeBuilder template = Skills.INSTANCE.createTree(id.location());
        return trees.computeIfAbsent(template.idLocation(), ignored -> template);
    }

    private SkillTreeBuilder editTree(String id) {
        return createTree(PSTTreeId.parse(id));
    }

    @Info(value = "Creates or edits a managed skill tree builder.", params = {
            @Param(name = "id", value = "The skill tree resource location.")
    })
    public SkillTreeBuilder editTree(PSTTreeId id) {
        return createTree(id);
    }

    private SkillBuilder createSkill(String id) {
        return createSkill(PSTSkillId.parse(id));
    }

    private SkillBuilder createSkill(PSTSkillId id) {
        SkillBuilder existingTreeSkill = findTreeSkill(id.location());
        if (existingTreeSkill != null) {
            return existingTreeSkill;
        }

        SkillBuilder template = Skills.INSTANCE.createSkill(id.location());
        return looseSkills.computeIfAbsent(template.idLocation(), ignored -> template);
    }

    private SkillBuilder editSkill(String id) {
        return createSkill(PSTSkillId.parse(id));
    }

    @Info(value = "Creates or edits a managed loose skill builder.", params = {
            @Param(name = "id", value = "The skill resource location.")
    })
    public SkillBuilder editSkill(PSTSkillId id) {
        return createSkill(id);
    }

    private SkillBuilder createStartingSkill(String id) {
        return createStartingSkill(PSTSkillId.parse(id));
    }

    private SkillBuilder createStartingSkill(PSTSkillId id) {
        SkillBuilder skill = createSkill(id);
        skill.startingPoint(true);
        return skill;
    }

    @Info(value = "Creates or edits a managed starting skill builder.", params = {
            @Param(name = "id", value = "The skill resource location.")
    })
    public SkillBuilder editStartingSkill(PSTSkillId id) {
        return createStartingSkill(id);
    }

    @Info(value = "Converts builders, arrays, objects, or primitives into a JsonElement.", params = {
            @Param(name = "value", value = "The value to convert into JSON.")
    })
    @HideFromJS
    public JsonElement json(Object value) {
        return JsonHelper.requireElement(value, "value");
    }

    @Info(value = "Pretty-prints builders, arrays, objects, or primitives as JSON text.", params = {
            @Param(name = "value", value = "The value to convert into formatted JSON.")
    })
    @HideFromJS
    public String prettyJson(Object value) {
        return JsonHelper.pretty(value);
    }

    private JsonObject skill(String id) {
        return skill(PSTSkillId.parse(id));
    }

    @Info(value = "Builds raw PST skill JSON for the given skill id.", params = {
            @Param(name = "id", value = "The skill resource location.")
    })
    @HideFromJS
    public JsonObject skill(PSTSkillId id) {
        return Skills.INSTANCE.skill(id.id());
    }

    private JsonObject startingSkill(String id) {
        return startingSkill(PSTSkillId.parse(id));
    }

    @Info(value = "Builds raw PST starting skill JSON for the given skill id.", params = {
            @Param(name = "id", value = "The skill resource location.")
    })
    @HideFromJS
    public JsonObject startingSkill(PSTSkillId id) {
        return Skills.INSTANCE.startingSkill(id.id());
    }

    private JsonObject skillTree(String id) {
        return skillTree(PSTTreeId.parse(id));
    }

    @Info(value = "Builds raw PST skill tree JSON for the given tree id.", params = {
            @Param(name = "id", value = "The skill tree resource location.")
    })
    @HideFromJS
    public JsonObject skillTree(PSTTreeId id) {
        return Skills.INSTANCE.skillTree(id.id());
    }

    private void addSkill(String id, Object overrides) {
        addSkill(PSTSkillId.parse(id), overrides);
    }

    @HideFromJS
    @Info(value = "Writes a loose skill JSON file immediately.", params = {
            @Param(name = "id", value = "The skill resource location."),
            @Param(name = "overrides", value = "Raw JSON-like overrides merged into the generated skill template.")
    })
    public void addSkill(PSTSkillId id, Object overrides) {
        JsonObject json = Skills.INSTANCE.buildSkillJson(id.id(), overrides);
        contentState.putSkill(id.location(), json);
    }

    @Info(value = "Writes a loose skill JSON file immediately by configuring a typed skill builder.", params = {
            @Param(name = "id", value = "The skill resource location."),
            @Param(name = "consumer", value = "The callback that configures the created skill builder.")
    })
    public void addSkill(PSTSkillId id, Consumer<SkillBuilder> consumer) {
        SkillBuilder skill = Skills.INSTANCE.createSkill(id.location());
        if (consumer != null) {
            consumer.accept(skill);
        }
        looseSkills.put(skill.idLocation(), skill);
    }

    private void addStartingSkill(String id, Object overrides) {
        addStartingSkill(PSTSkillId.parse(id), overrides);
    }

    @HideFromJS
    @Info(value = "Writes a starting skill JSON file immediately.", params = {
            @Param(name = "id", value = "The skill resource location."),
            @Param(name = "overrides", value = "Raw JSON-like overrides merged into the generated starting skill template.")
    })
    public void addStartingSkill(PSTSkillId id, Object overrides) {
        JsonObject json = Skills.INSTANCE.buildStartingSkillJson(id.id(), overrides);
        contentState.putSkill(id.location(), json);
    }

    @Info(value = "Writes a starting skill JSON file immediately by configuring a typed skill builder.", params = {
            @Param(name = "id", value = "The skill resource location."),
            @Param(name = "consumer", value = "The callback that configures the created starting skill builder.")
    })
    public void addStartingSkill(PSTSkillId id, Consumer<SkillBuilder> consumer) {
        SkillBuilder skill = Skills.INSTANCE.createStartingSkill(id.location());
        if (consumer != null) {
            consumer.accept(skill);
        }
        looseSkills.put(skill.idLocation(), skill);
    }

    private void addSkillTree(String id, Object overrides) {
        addSkillTree(PSTTreeId.parse(id), overrides);
    }

    @HideFromJS
    @Info(value = "Writes a skill tree JSON file immediately.", params = {
            @Param(name = "id", value = "The skill tree resource location."),
            @Param(name = "overrides", value = "Raw JSON-like overrides merged into the generated skill tree template.")
    })
    public void addSkillTree(PSTTreeId id, Object overrides) {
        JsonObject json = Skills.INSTANCE.buildSkillTreeJson(id.id(), overrides);
        contentState.putTree(id.location(), json);
    }

    @Info(value = "Writes a skill tree JSON file immediately by configuring a typed skill tree builder.", params = {
            @Param(name = "id", value = "The skill tree resource location."),
            @Param(name = "consumer", value = "The callback that configures the created skill tree builder.")
    })
    public void addSkillTree(PSTTreeId id, Consumer<SkillTreeBuilder> consumer) {
        SkillTreeBuilder tree = Skills.INSTANCE.createTree(id.location());
        if (consumer != null) {
            consumer.accept(tree);
        }
        trees.put(tree.idLocation(), tree);
    }

    private void disableTree(String id) {
        removeTree(id);
    }

    @Info(value = "Disables or removes a skill tree by id.", params = {
            @Param(name = "id", value = "The skill tree resource location.")
    })
    @HideFromJS
    public void disableTree(PSTTreeId id) {
        removeTree(id);
    }

    @Info("Requests removal of PST default skill trees after generated content is flushed.")
    public void clearDefaultTree() {
        clearDefaultTrees();
    }

    @Info("Requests removal of PST default skill trees after generated content is flushed.")
    public void clearDefaultTrees() {
        contentState.clearDefaultTrees();
    }

    private void removeTree(String id) {
        removeTree(PSTTreeId.parse(id));
    }

    @Info(value = "Removes a skill tree by id.", params = {
            @Param(name = "id", value = "The skill tree resource location.")
    })
    public void removeTree(PSTTreeId id) {
        trees.remove(id.location());
        contentState.removeTree(id.location());
    }

    private void disableSkill(String id) {
        removeSkill(id);
    }

    @Info(value = "Disables or removes a skill by id.", params = {
            @Param(name = "id", value = "The skill resource location.")
    })
    @HideFromJS
    public void disableSkill(PSTSkillId id) {
        removeSkill(id);
    }

    private void removeSkill(String id) {
        removeSkill(PSTSkillId.parse(id));
    }

    @Info(value = "Removes a skill by id.", params = {
            @Param(name = "id", value = "The skill resource location.")
    })
    public void removeSkill(PSTSkillId id) {
        looseSkills.remove(id.location());
        contentState.removeSkill(id.location());
    }

    @Info(value = "Writes an arbitrary data JSON file into the virtual datapack.", params = {
            @Param(name = "id", value = "The resource location string of the JSON file."),
            @Param(name = "value", value = "The JSON-like content to write.")
    })
    @HideFromJS
    public void addDataJson(String id, Object value) {
        ResourceLocation resourceLocation = ResourceLocation.tryParse(id);
        if (resourceLocation == null) {
            throw new IllegalArgumentException("Invalid resource location: " + id);
        }
        addJson(resourceLocation, JsonHelper.requireElement(value, "value"));
    }

    @HideFromJS
    @Info("Flushes all queued builders and removals into the virtual datapack and runtime caches.")
    public void flushGeneratedContent() {
        Set<ResourceLocation> writtenSkillIds = new LinkedHashSet<>();
        List<SkillBuilder> exportableSkills = new ArrayList<>();
        List<SkillTreeBuilder> exportableTrees = new ArrayList<>();
        for (SkillTreeBuilder tree : trees.values()) {
            exportableTrees.add(tree);
            contentState.putTree(tree);
            for (SkillBuilder skill : tree.builtSkills()) {
                if (writtenSkillIds.add(skill.idLocation())) {
                    exportableSkills.add(skill);
                }
            }
        }
        for (SkillBuilder skill : looseSkills.values()) {
            if (writtenSkillIds.add(skill.idLocation())) {
                contentState.putLooseSkill(skill);
                exportableSkills.add(skill);
            }
        }
        contentState.writeTo(this);
        exportTranslationDraft(exportableSkills, exportableTrees);
    }

    @Override
    protected void afterPosted(EventResult result) {
        flushGeneratedContent();
        contentState.apply();
        if (resourceManager != null) {
            super.afterPosted(result);
        }
    }

    private SkillBuilder findTreeSkill(ResourceLocation id) {
        for (SkillTreeBuilder tree : trees.values()) {
            for (SkillBuilder skill : tree.builtSkills()) {
                if (skill.idLocation().equals(id)) {
                    return skill;
                }
            }
        }
        return null;
    }

    private static Field field(String name) {
        try {
            Field field = DataPackEventJS.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static VirtualKubeJSDataPack readVirtualDataPack(DataPackEventJS event) {
        try {
            return (VirtualKubeJSDataPack) VIRTUAL_DATA_PACK_FIELD.get(event);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Failed to access DataPackEventJS virtualDataPack", exception);
        }
    }

    private static MultiPackResourceManager readWrappedManager(DataPackEventJS event) {
        try {
            return (MultiPackResourceManager) WRAPPED_MANAGER_FIELD.get(event);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Failed to access DataPackEventJS wrappedManager", exception);
        }
    }

    private static void exportTranslationDraft(List<SkillBuilder> exportableSkills, List<SkillTreeBuilder> exportableTrees) {
        try {
            Path configDir = FMLPaths.CONFIGDIR.get();
            if (!shouldExportTranslationDraft(configDir, PassiveSTJSCommonConfig.translationDraftExportEnabled())) {
                return;
            }
            new PSTTranslationDraftExporter(configDir)
                    .exportBuilders(exportableSkills, exportableTrees);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to export PassiveSTJS translation draft", exception);
        }
    }

    @HideFromJS
    static boolean shouldExportTranslationDraft(Path configDir, boolean enabled) {
        return enabled && configDir != null;
    }
}
