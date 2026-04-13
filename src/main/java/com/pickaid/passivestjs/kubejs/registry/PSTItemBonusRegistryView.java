package com.pickaid.passivestjs.kubejs.registry;

import com.pickaid.passivestjs.kubejs.builder.ItemBonusBuilder;
import com.pickaid.passivestjs.kubejs.id.PSTItemBonusId;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public final class PSTItemBonusRegistryView extends PSTRegistryView<ItemBonusBuilder, PSTItemBonusId> {
    PSTItemBonusRegistryView(
            String registryId,
            Supplier<? extends IForgeRegistry<?>> registrySupplier,
            PSTRegistryBuilderFactory<ItemBonusBuilder> builderFactory
    ) {
        super(registryId, PSTSerializerFamily.ITEM_BONUSES, registrySupplier, builderFactory);
    }

    @Info(value = "Returns whether this registry currently contains the provided item bonus type id.", params = {
            @Param(name = "id", value = "The item bonus type id to check.")
    })
    public boolean has(PSTItemBonusId id) {
        return hasEntry(id);
    }

    @Info(value = "Looks up an item bonus type handle by id and returns a builder-capable handle. Prefer has(id) before get(id); get(id) throws when the id is missing from the live PST registry.", params = {
            @Param(name = "id", value = "The item bonus type id to resolve.")
    })
    public PSTRegistryTypeHandle<ItemBonusBuilder> get(PSTItemBonusId id) {
        return getEntry(id);
    }
}
