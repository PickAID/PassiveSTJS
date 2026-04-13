package com.pickaid.passivestjs.kubejs.registry;

import com.pickaid.passivestjs.kubejs.builder.ConditionBuilder;
import com.pickaid.passivestjs.kubejs.id.PSTItemConditionId;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public final class PSTItemConditionRegistryView extends PSTRegistryView<ConditionBuilder, PSTItemConditionId> {
    PSTItemConditionRegistryView(
            String registryId,
            Supplier<? extends IForgeRegistry<?>> registrySupplier,
            PSTRegistryBuilderFactory<ConditionBuilder> builderFactory
    ) {
        super(registryId, PSTSerializerFamily.ITEM_CONDITIONS, registrySupplier, builderFactory);
    }

    @Info(value = "Returns whether this registry currently contains the provided item condition type id.", params = {
            @Param(name = "id", value = "The item condition type id to check.")
    })
    public boolean has(PSTItemConditionId id) {
        return hasEntry(id);
    }

    @Info(value = "Looks up an item condition type handle by id and returns a builder-capable handle. Prefer has(id) before get(id); get(id) throws when the id is missing from the live PST registry.", params = {
            @Param(name = "id", value = "The item condition type id to resolve.")
    })
    public PSTRegistryTypeHandle<ConditionBuilder> get(PSTItemConditionId id) {
        return getEntry(id);
    }
}
