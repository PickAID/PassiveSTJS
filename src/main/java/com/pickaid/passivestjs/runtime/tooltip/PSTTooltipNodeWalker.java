package com.pickaid.passivestjs.runtime.tooltip;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pickaid.passivestjs.kubejs.registry.PSTRegistryTargets;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadata;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadataIndex;
import com.pickaid.passivestjs.runtime.PSTRuntimeNode;
import com.pickaid.passivestjs.schema.PSTNodeFamily;
import com.pickaid.passivestjs.schema.PSTSchema;
import com.pickaid.passivestjs.schema.PSTSchemaField;
import com.pickaid.passivestjs.schema.PSTSchemaFieldKind;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class PSTTooltipNodeWalker {
    private PSTTooltipNodeWalker() {
    }

    public static List<Component> collectPrefixes(PSTRuntimeNode node) {
        ArrayList<Component> prefixes = new ArrayList<>();
        collectPrefixes(node, prefixes);
        return List.copyOf(prefixes);
    }

    public static List<Component> collectRequirements(PSTRuntimeNode node) {
        ArrayList<Component> requirements = new ArrayList<>();
        collectRequirements(node, requirements);
        return List.copyOf(requirements);
    }

    private static void collectPrefixes(PSTRuntimeNode node, List<Component> prefixes) {
        for (PSTRuntimeNode child : childNodes(node)) {
            child.metadata().tooltipSpec()
                    .flatMap(spec -> spec.render(PSTTooltipFragmentKind.PREFIX, PSTTooltipRenderContext.of(child)))
                    .ifPresent(prefixes::add);
            collectPrefixes(child, prefixes);
        }
    }

    private static void collectRequirements(PSTRuntimeNode node, List<Component> requirements) {
        for (PSTRuntimeNode child : childNodes(node)) {
            child.metadata().tooltipSpec()
                    .flatMap(spec -> spec.render(PSTTooltipFragmentKind.REQUIREMENT, PSTTooltipRenderContext.of(child)))
                    .ifPresent(requirements::add);
            collectRequirements(child, requirements);
        }
    }

    private static List<PSTRuntimeNode> childNodes(PSTRuntimeNode node) {
        Optional<PSTSchema> schema = node.metadata().schema();
        if (schema.isEmpty()) {
            return List.of();
        }
        JsonObject payload = node.payload();
        ArrayList<PSTRuntimeNode> children = new ArrayList<>();
        for (PSTSchemaField field : schema.get().fields().values()) {
            if (field.kind() == PSTSchemaFieldKind.NODE) {
                addChild(field, payload.get(field.name()), children);
            } else if (field.kind() == PSTSchemaFieldKind.NODE_LIST) {
                JsonElement element = payload.get(field.name());
                if (element != null && element.isJsonArray()) {
                    for (JsonElement child : element.getAsJsonArray()) {
                        addChild(field, child, children);
                    }
                }
            }
        }
        return children;
    }

    private static void addChild(PSTSchemaField field, JsonElement element, List<PSTRuntimeNode> children) {
        if (element == null || !element.isJsonObject()) {
            return;
        }
        JsonObject childJson = element.getAsJsonObject();
        if (!childJson.has("type")) {
            return;
        }
        ResourceLocation typeId = ResourceLocation.tryParse(childJson.get("type").getAsString());
        PSTNodeFamily family = resolveNodeFamily(field);
        if (typeId == null || family == null) {
            return;
        }
        PSTSerializerMetadata metadata = PSTSerializerMetadataIndex.find(family, typeId)
                .orElseGet(() -> new PSTSerializerMetadata(serializerFamily(family), family, typeId));
        JsonObject payload = childJson.deepCopy();
        payload.remove("type");
        children.add(new PSTRuntimeNode(metadata, payload));
    }

    private static PSTNodeFamily resolveNodeFamily(PSTSchemaField field) {
        if (field.nodeTarget() != null) {
            return field.nodeTarget();
        }
        ResourceLocation target = field.registryTarget();
        if (target == null) {
            return null;
        }
        if (target.equals(PSTRegistryTargets.SKILL_BONUSES.location())) {
            return PSTNodeFamily.SKILL_BONUS;
        }
        if (target.equals(PSTRegistryTargets.LIVING_MULTIPLIERS.location())) {
            return PSTNodeFamily.MULTIPLIER;
        }
        if (target.equals(PSTRegistryTargets.LIVING_CONDITIONS.location())) {
            return PSTNodeFamily.LIVING_CONDITION;
        }
        if (target.equals(PSTRegistryTargets.DAMAGE_CONDITIONS.location())) {
            return PSTNodeFamily.DAMAGE_CONDITION;
        }
        if (target.equals(PSTRegistryTargets.ITEM_CONDITIONS.location())) {
            return PSTNodeFamily.ITEM_CONDITION;
        }
        if (target.equals(PSTRegistryTargets.ENCHANTMENT_CONDITIONS.location())) {
            return PSTNodeFamily.ENCHANTMENT_CONDITION;
        }
        if (target.equals(PSTRegistryTargets.EVENT_LISTENERS.location())) {
            return PSTNodeFamily.EVENT_LISTENER;
        }
        if (target.equals(PSTRegistryTargets.FLOAT_FUNCTIONS.location())) {
            return PSTNodeFamily.NUMERIC_VALUE;
        }
        if (target.equals(PSTRegistryTargets.SKILL_REQUIREMENTS.location())) {
            return PSTNodeFamily.SKILL_REQUIREMENT;
        }
        if (target.equals(PSTRegistryTargets.ITEM_BONUSES.location())) {
            return PSTNodeFamily.ITEM_BONUS;
        }
        return null;
    }

    private static PSTSerializerFamily serializerFamily(PSTNodeFamily family) {
        return switch (family) {
            case SKILL_BONUS -> PSTSerializerFamily.SKILL_BONUSES;
            case MULTIPLIER -> PSTSerializerFamily.LIVING_MULTIPLIERS;
            case LIVING_CONDITION -> PSTSerializerFamily.LIVING_CONDITIONS;
            case DAMAGE_CONDITION -> PSTSerializerFamily.DAMAGE_CONDITIONS;
            case ITEM_CONDITION -> PSTSerializerFamily.ITEM_CONDITIONS;
            case ENCHANTMENT_CONDITION -> PSTSerializerFamily.ENCHANTMENT_CONDITIONS;
            case EVENT_LISTENER -> PSTSerializerFamily.EVENT_LISTENERS;
            case NUMERIC_VALUE -> PSTSerializerFamily.FLOAT_FUNCTIONS;
            case SKILL_REQUIREMENT -> PSTSerializerFamily.SKILL_REQUIREMENTS;
            case ITEM_BONUS -> PSTSerializerFamily.ITEM_BONUSES;
        };
    }
}
