package com.pickaid.passivestjs.kubejs.registry;

import com.pickaid.passivestjs.kubejs.builder.MultiplierBuilder;
import com.pickaid.passivestjs.kubejs.id.PSTLivingMultiplierId;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public final class PSTLivingMultiplierRegistryView extends PSTRegistryView<MultiplierBuilder, PSTLivingMultiplierId> {
    PSTLivingMultiplierRegistryView(
            String registryId,
            Supplier<? extends IForgeRegistry<?>> registrySupplier,
            PSTRegistryBuilderFactory<MultiplierBuilder> builderFactory
    ) {
        super(registryId, PSTSerializerFamily.LIVING_MULTIPLIERS, registrySupplier, builderFactory);
    }

    @Info(value = "Returns whether this registry currently contains the provided living multiplier type id.", params = {
            @Param(name = "id", value = "The living multiplier type id to check.")
    })
    public boolean has(PSTLivingMultiplierId id) {
        return hasEntry(id);
    }

    @Info(value = "Looks up a living multiplier type handle by id and returns a builder-capable handle. Prefer has(id) before get(id); get(id) throws when the id is missing from the live PST registry.", params = {
            @Param(name = "id", value = "The living multiplier type id to resolve.")
    })
    public PSTRegistryTypeHandle<MultiplierBuilder> get(PSTLivingMultiplierId id) {
        return getEntry(id);
    }
}
