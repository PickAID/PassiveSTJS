package com.pickaid.passivestjs.kubejs.registry;

import com.pickaid.passivestjs.kubejs.builder.ValueBuilder;
import com.pickaid.passivestjs.kubejs.id.PSTNumericValueProviderId;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public final class PSTNumericValueRegistryView extends PSTRegistryView<ValueBuilder, PSTNumericValueProviderId> {
    PSTNumericValueRegistryView(
            String registryId,
            Supplier<? extends IForgeRegistry<?>> registrySupplier,
            PSTRegistryBuilderFactory<ValueBuilder> builderFactory
    ) {
        super(registryId, PSTSerializerFamily.FLOAT_FUNCTIONS, registrySupplier, builderFactory);
    }

    @Info(value = "Returns whether this registry currently contains the provided numeric value provider type id.", params = {
            @Param(name = "id", value = "The numeric value provider type id to check.")
    })
    public boolean has(PSTNumericValueProviderId id) {
        return hasEntry(id);
    }

    @Info(value = "Looks up a numeric value provider type handle by id and returns a builder-capable handle. Prefer has(id) before get(id); get(id) throws when the id is missing from the live PST registry.", params = {
            @Param(name = "id", value = "The numeric value provider type id to resolve.")
    })
    public PSTRegistryTypeHandle<ValueBuilder> get(PSTNumericValueProviderId id) {
        return getEntry(id);
    }
}
