package com.pickaid.passivestjs.kubejs.registry;

import com.pickaid.passivestjs.kubejs.builder.ConditionBuilder;
import com.pickaid.passivestjs.kubejs.id.PSTLivingConditionId;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public final class PSTLivingConditionRegistryView extends PSTRegistryView<ConditionBuilder, PSTLivingConditionId> {
    PSTLivingConditionRegistryView(
            String registryId,
            Supplier<? extends IForgeRegistry<?>> registrySupplier,
            PSTRegistryBuilderFactory<ConditionBuilder> builderFactory
    ) {
        super(registryId, PSTSerializerFamily.LIVING_CONDITIONS, registrySupplier, builderFactory);
    }

    @Info(value = "Returns whether this registry currently contains the provided living condition type id.", params = {
            @Param(name = "id", value = "The living condition type id to check.")
    })
    public boolean has(PSTLivingConditionId id) {
        return hasEntry(id);
    }

    @Info(value = "Looks up a living condition type handle by id and returns a builder-capable handle. Prefer has(id) before get(id); get(id) throws when the id is missing from the live PST registry.", params = {
            @Param(name = "id", value = "The living condition type id to resolve.")
    })
    public PSTRegistryTypeHandle<ConditionBuilder> get(PSTLivingConditionId id) {
        return getEntry(id);
    }
}
