package com.pickaid.passivestjs.kubejs.registry;

import com.pickaid.passivestjs.schema.PSTSchema;
import com.pickaid.passivestjs.schema.PSTSchemaBuilder;
import com.pickaid.passivestjs.schema.PSTSchemaRegistry;
import com.pickaid.passivestjs.runtime.PSTSerializerObjectIndex;
import com.pickaid.passivestjs.runtime.tooltip.PSTTooltipSpec;
import com.pickaid.passivestjs.runtime.tooltip.PSTTooltipSpecBuilder;
import com.pickaid.passivestjs.runtime.tooltip.PSTTooltipSpecRegistry;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public abstract class AbstractPSTSerializerBuilder<T> extends BuilderBase<T> {
    private final PSTSerializerFamily family;
    private final PSTSchemaBuilder schemaBuilder;
    private final PSTTooltipSpecBuilder tooltipBuilder;

    protected AbstractPSTSerializerBuilder(ResourceLocation id, PSTSerializerFamily family) {
        super(id);
        this.family = family;
        this.schemaBuilder = PSTSchema.builder(new PSTSerializerMetadata(family, id).nodeFamily());
        this.tooltipBuilder = new PSTTooltipSpecBuilder();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final RegistryInfo<T> getRegistryType() {
        return (RegistryInfo<T>) family.registryInfo();
    }

    public final AbstractPSTSerializerBuilder<T> schema(Consumer<PSTSchemaBuilder> consumer) {
        consumer.accept(schemaBuilder);
        return this;
    }

    public final AbstractPSTSerializerBuilder<T> tooltip(Consumer<PSTTooltipSpecBuilder> consumer) {
        consumer.accept(tooltipBuilder);
        return this;
    }

    @Override
    public final T createObject() {
        publishSchema();
        publishTooltipSpec();
        PSTSerializerMetadata metadata = PSTSerializerMetadataIndex.remember(new PSTSerializerMetadata(family, id));
        return PSTSerializerObjectIndex.remember(metadata, createSerializer(metadata));
    }

    protected final PSTSchema publishSchema() {
        PSTSchema schema = schemaBuilder.build(id);
        PSTSchemaRegistry.remember(schema);
        return schema;
    }

    protected final PSTTooltipSpec publishTooltipSpec() {
        PSTTooltipSpec spec = tooltipBuilder.build();
        PSTTooltipSpecRegistry.remember(new PSTSerializerMetadata(family, id).nodeFamily(), id, spec);
        return spec;
    }

    protected final PSTTooltipSpecBuilder tooltipBuilder() {
        return tooltipBuilder;
    }

    protected abstract T createSerializer(PSTSerializerMetadata metadata);

    public final PSTSerializerFamily family() {
        return family;
    }
}
