package com.pickaid.passivestjs.schema;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import java.util.List;

public record PSTSchemaField(
        String name,
        PSTSchemaFieldKind kind,
        boolean required,
        Component doc,
        Object defaultValue,
        ResourceLocation registryTarget,
        PSTNodeFamily nodeTarget,
        List<String> enumChoices
) {
}
