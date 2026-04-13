package com.pickaid.passivestjs.kubejs.registry;

import com.pickaid.passivestjs.kubejs.id.PSTRegistryEntryId;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class PSTRegistryView<T, I extends PSTRegistryEntryId> {
    private final String registryId;
    private final PSTSerializerFamily family;
    private final Supplier<? extends IForgeRegistry<?>> registrySupplier;
    private final PSTRegistryBuilderFactory<T> builderFactory;

    protected PSTRegistryView(
            String registryId,
            PSTSerializerFamily family,
            Supplier<? extends IForgeRegistry<?>> registrySupplier,
            PSTRegistryBuilderFactory<T> builderFactory
    ) {
        this.registryId = Objects.requireNonNull(registryId, "registryId");
        this.family = Objects.requireNonNull(family, "family");
        this.registrySupplier = Objects.requireNonNull(registrySupplier, "registrySupplier");
        this.builderFactory = Objects.requireNonNull(builderFactory, "builderFactory");
    }

    @Info("Returns the real PST registry id represented by this view.")
    public String id() {
        return registryId;
    }

    protected final boolean hasEntry(I id) {
        ResourceLocation normalizedId = requireId(id);
        IForgeRegistry<?> registry = registry();
        if (registry == null) {
            return false;
        }
        return registry.getValue(normalizedId) != null;
    }

    protected final PSTRegistryTypeHandle<T> getEntry(I id) {
        ResourceLocation normalizedId = requireId(id);
        IForgeRegistry<?> registry = registry();
        if (registry != null && registry.getValue(normalizedId) == null) {
            throw new IllegalArgumentException(
                    "Unknown PST registry entry '" + normalizedId + "' in '" + registryId + "'. " +
                            "Check view.has(" + normalizedId + ") before calling get(...)."
            );
        }
        return new PSTRegistryTypeHandle<>(normalizedId, registryId, builderFactory);
    }

    private IForgeRegistry<?> registry() {
        try {
            return registrySupplier.get();
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private ResourceLocation requireId(I id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        if (id.family() != family) {
            throw new IllegalArgumentException("Expected id for " + family + " but received " + id.family());
        }
        return id.location();
    }
}
