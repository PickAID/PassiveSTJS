package com.pickaid.passivestjs.schema;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

public final class PSTSchemaBuilder {
    private final PSTNodeFamily family;
    private final LinkedHashMap<String, PSTSchemaField> fields = new LinkedHashMap<>();

    public PSTSchemaBuilder(PSTNodeFamily family) {
        this.family = family;
    }

    public PSTSchemaBuilder field(String name, Consumer<FieldBuilder> consumer) {
        FieldBuilder field = new FieldBuilder(name);
        consumer.accept(field);
        fields.put(name, field.build());
        return this;
    }

    public PSTSchema build(ResourceLocation id) {
        return new PSTSchema(id, family, java.util.Map.copyOf(fields));
    }

    public static final class FieldBuilder {
        private final String name;
        private PSTSchemaFieldKind kind = PSTSchemaFieldKind.STRING;
        private boolean required;
        private String doc = "";
        private Object defaultValue;
        private ResourceLocation registryTarget;
        private final List<String> enumChoices = new ArrayList<>();

        public FieldBuilder(String name) {
            this.name = name;
        }

        public FieldBuilder kind(PSTSchemaFieldKind value) {
            this.kind = value;
            return this;
        }

        public FieldBuilder required() {
            this.required = true;
            return this;
        }

        public FieldBuilder doc(String value) {
            this.doc = value;
            return this;
        }

        public FieldBuilder defaultValue(Object value) {
            this.defaultValue = value;
            return this;
        }

        public FieldBuilder registryTarget(ResourceLocation value) {
            this.registryTarget = value;
            return this;
        }

        public FieldBuilder enumChoice(String value) {
            this.enumChoices.add(value);
            return this;
        }

        private PSTSchemaField build() {
            return new PSTSchemaField(name, kind, required, doc, defaultValue, registryTarget, List.copyOf(enumChoices));
        }
    }
}
