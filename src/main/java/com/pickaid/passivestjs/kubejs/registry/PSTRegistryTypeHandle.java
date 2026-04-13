package com.pickaid.passivestjs.kubejs.registry;

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Consumer;

public final class PSTRegistryTypeHandle<T> {
    private final ResourceLocation id;
    private final String registryId;
    private final PSTRegistryBuilderFactory<T> builderFactory;

    PSTRegistryTypeHandle(ResourceLocation id, String registryId, PSTRegistryBuilderFactory<T> builderFactory) {
        this.id = Objects.requireNonNull(id, "id");
        this.registryId = Objects.requireNonNull(registryId, "registryId");
        this.builderFactory = Objects.requireNonNull(builderFactory, "builderFactory");
    }

    @Info("Returns the type id represented by this registry handle.")
    public String id() {
        return id.toString();
    }

    @Info("Returns the registry id that owns this type handle.")
    public String registry() {
        return registryId;
    }

    @Info("Creates a new builder for this registry type.")
    public T create() {
        return builderFactory.create(id);
    }

    @Info(value = "Creates a new builder for this registry type and configures it inline.", params = {
            @Param(name = "consumer", value = "The callback that mutates the new builder.")
    })
    public T create(Consumer<T> consumer) {
        T builder = create();
        if (consumer != null) {
            consumer.accept(builder);
        }
        return builder;
    }
}
