package com.pickaid.passivestjs.kubejs.registry;

import com.pickaid.passivestjs.kubejs.builder.BonusBuilder;
import com.pickaid.passivestjs.kubejs.id.PSTSkillBonusId;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public final class PSTSkillBonusRegistryView extends PSTRegistryView<BonusBuilder, PSTSkillBonusId> {
    PSTSkillBonusRegistryView(
            String registryId,
            Supplier<? extends IForgeRegistry<?>> registrySupplier,
            PSTRegistryBuilderFactory<BonusBuilder> builderFactory
    ) {
        super(registryId, PSTSerializerFamily.SKILL_BONUSES, registrySupplier, builderFactory);
    }

    @Info(value = "Returns whether this registry currently contains the provided skill bonus type id.", params = {
            @Param(name = "id", value = "The skill bonus type id to check.")
    })
    public boolean has(PSTSkillBonusId id) {
        return hasEntry(id);
    }

    @Info(value = "Looks up a skill bonus type handle by id and returns a builder-capable handle. Prefer has(id) before get(id); get(id) throws when the id is missing from the live PST registry.", params = {
            @Param(name = "id", value = "The skill bonus type id to resolve.")
    })
    public PSTRegistryTypeHandle<BonusBuilder> get(PSTSkillBonusId id) {
        return getEntry(id);
    }
}
