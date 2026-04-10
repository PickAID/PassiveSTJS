package com.pickaid.passivestjs.schema;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record PSTSchemaField(
        String name,
        PSTSchemaFieldKind kind,
        boolean required,
        String doc,
        Object defaultValue,
        ResourceLocation registryTarget,
        List<String> enumChoices
) {
}
