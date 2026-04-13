package com.pickaid.passivestjs.kubejs.registry;

import com.pickaid.passivestjs.kubejs.builder.ConditionBuilder;
import com.pickaid.passivestjs.kubejs.id.PSTEnchantmentConditionId;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public final class PSTEnchantmentConditionRegistryView extends PSTRegistryView<ConditionBuilder, PSTEnchantmentConditionId> {
    PSTEnchantmentConditionRegistryView(
            String registryId,
            Supplier<? extends IForgeRegistry<?>> registrySupplier,
            PSTRegistryBuilderFactory<ConditionBuilder> builderFactory
    ) {
        super(registryId, PSTSerializerFamily.ENCHANTMENT_CONDITIONS, registrySupplier, builderFactory);
    }

    @Info(value = "Returns whether this registry currently contains the provided enchantment condition type id.", params = {
            @Param(name = "id", value = "The enchantment condition type id to check.")
    })
    public boolean has(PSTEnchantmentConditionId id) {
        return hasEntry(id);
    }

    @Info(value = "Looks up an enchantment condition type handle by id and returns a builder-capable handle. Prefer has(id) before get(id); get(id) throws when the id is missing from the live PST registry.", params = {
            @Param(name = "id", value = "The enchantment condition type id to resolve.")
    })
    public PSTRegistryTypeHandle<ConditionBuilder> get(PSTEnchantmentConditionId id) {
        return getEntry(id);
    }
}
