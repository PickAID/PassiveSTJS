package com.pickaid.passivestjs.schema;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;

public record PSTSchema(ResourceLocation id, PSTNodeFamily family, Map<String, PSTSchemaField> fields) {
    public static PSTSchemaBuilder builder(PSTNodeFamily family) {
        return new PSTSchemaBuilder(family);
    }

    public PSTSchemaField field(String name) {
        return Optional.ofNullable(fields.get(name))
                .orElseThrow(() -> new IllegalArgumentException("Unknown schema field: " + name));
    }
}
