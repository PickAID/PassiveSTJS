package com.pickaid.passivestjs.kubejs.registry;

import com.pickaid.passivestjs.kubejs.builder.ListenerBuilder;
import com.pickaid.passivestjs.kubejs.id.PSTEventListenerId;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public final class PSTEventListenerRegistryView extends PSTRegistryView<ListenerBuilder, PSTEventListenerId> {
    PSTEventListenerRegistryView(
            String registryId,
            Supplier<? extends IForgeRegistry<?>> registrySupplier,
            PSTRegistryBuilderFactory<ListenerBuilder> builderFactory
    ) {
        super(registryId, PSTSerializerFamily.EVENT_LISTENERS, registrySupplier, builderFactory);
    }

    @Info(value = "Returns whether this registry currently contains the provided event listener type id.", params = {
            @Param(name = "id", value = "The event listener type id to check.")
    })
    public boolean has(PSTEventListenerId id) {
        return hasEntry(id);
    }

    @Info(value = "Looks up an event listener type handle by id and returns a builder-capable handle. Prefer has(id) before get(id); get(id) throws when the id is missing from the live PST registry.", params = {
            @Param(name = "id", value = "The event listener type id to resolve.")
    })
    public PSTRegistryTypeHandle<ListenerBuilder> get(PSTEventListenerId id) {
        return getEntry(id);
    }
}
