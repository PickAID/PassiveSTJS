package com.pickaid.passivestjs.kubejs.builder;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.pickaid.passivestjs.kubejs.content.JsonHelper;
import com.pickaid.passivestjs.schema.PSTNodeFamily;
import com.pickaid.passivestjs.schema.PSTSchema;
import com.pickaid.passivestjs.schema.PSTSchemaField;
import com.pickaid.passivestjs.schema.PSTSchemaRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class PSTNodeWriter implements JsonFragment {
    private final PSTSchema schema;
    private final JsonObject json;

    public PSTNodeWriter(PSTNodeFamily family, ResourceLocation typeId) {
        ResourceLocation normalizedTypeId = Objects.requireNonNull(typeId, "typeId");
        this.schema = PSTSchemaRegistry.find(Objects.requireNonNull(family, "family"), normalizedTypeId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Missing schema for " + normalizedTypeId + " in " + family
                ));
        this.json = JsonHelper.typedObject(normalizedTypeId.toString());
    }

    public PSTNodeWriter set(String fieldName, Object value) {
        PSTSchemaField field = schema.field(fieldName);
        var element = JsonHelper.requireElement(value, fieldName);
        validate(field, element);
        json.add(fieldName, element);
        return this;
    }

    @Override
    public JsonObject toJson() {
        return JsonHelper.copy(json).getAsJsonObject();
    }

    private static void validate(PSTSchemaField field, com.google.gson.JsonElement element) {
        boolean valid = switch (field.kind()) {
            case BOOLEAN -> isBoolean(element);
            case INT -> isIntegerNumber(element);
            case DOUBLE -> isNumber(element);
            case STRING, TEXT -> isString(element);
            case ENUM -> isEnum(element, field);
            case RESOURCE_LOCATION, REGISTRY_ID -> isResourceLocation(element);
            case NODE -> isNode(element);
            case NODE_LIST -> isNodeList(element);
            case LIST -> element.isJsonArray();
        };

        if (!valid) {
            throw new IllegalArgumentException("Invalid value for schema field '" + field.name() + "' (" + field.kind() + ")");
        }
    }

    private static boolean isBoolean(com.google.gson.JsonElement element) {
        if (!element.isJsonPrimitive()) {
            return false;
        }
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        return primitive.isBoolean();
    }

    private static boolean isNumber(com.google.gson.JsonElement element) {
        if (!element.isJsonPrimitive()) {
            return false;
        }
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        return primitive.isNumber();
    }

    private static boolean isIntegerNumber(com.google.gson.JsonElement element) {
        if (!isNumber(element)) {
            return false;
        }
        double value = element.getAsDouble();
        return Math.rint(value) == value;
    }

    private static boolean isString(com.google.gson.JsonElement element) {
        if (!element.isJsonPrimitive()) {
            return false;
        }
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        return primitive.isString();
    }

    private static boolean isEnum(com.google.gson.JsonElement element, PSTSchemaField field) {
        if (!isString(element)) {
            return false;
        }
        if (field.enumChoices() == null || field.enumChoices().isEmpty()) {
            return true;
        }
        return field.enumChoices().contains(element.getAsString());
    }

    private static boolean isResourceLocation(com.google.gson.JsonElement element) {
        if (!isString(element)) {
            return false;
        }
        return ResourceLocation.tryParse(element.getAsString()) != null;
    }

    private static boolean isNode(com.google.gson.JsonElement element) {
        if (!element.isJsonObject()) {
            return false;
        }
        com.google.gson.JsonObject object = element.getAsJsonObject();
        if (!object.has("type") || !object.get("type").isJsonPrimitive()) {
            return false;
        }
        return ResourceLocation.tryParse(object.get("type").getAsString()) != null;
    }

    private static boolean isNodeList(com.google.gson.JsonElement element) {
        if (!element.isJsonArray()) {
            return false;
        }
        for (com.google.gson.JsonElement child : element.getAsJsonArray()) {
            if (!isNode(child)) {
                return false;
            }
        }
        return true;
    }
}
