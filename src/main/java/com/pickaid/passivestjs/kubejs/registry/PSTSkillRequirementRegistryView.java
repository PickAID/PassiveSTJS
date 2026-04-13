package com.pickaid.passivestjs.kubejs.registry;

import com.pickaid.passivestjs.kubejs.builder.RequirementBuilder;
import com.pickaid.passivestjs.kubejs.id.PSTSkillRequirementId;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public final class PSTSkillRequirementRegistryView extends PSTRegistryView<RequirementBuilder, PSTSkillRequirementId> {
    PSTSkillRequirementRegistryView(
            String registryId,
            Supplier<? extends IForgeRegistry<?>> registrySupplier,
            PSTRegistryBuilderFactory<RequirementBuilder> builderFactory
    ) {
        super(registryId, PSTSerializerFamily.SKILL_REQUIREMENTS, registrySupplier, builderFactory);
    }

    @Info(value = "Returns whether this registry currently contains the provided skill requirement type id.", params = {
            @Param(name = "id", value = "The skill requirement type id to check.")
    })
    public boolean has(PSTSkillRequirementId id) {
        return hasEntry(id);
    }

    @Info(value = "Looks up a skill requirement type handle by id and returns a builder-capable handle. Prefer has(id) before get(id); get(id) throws when the id is missing from the live PST registry.", params = {
            @Param(name = "id", value = "The skill requirement type id to resolve.")
    })
    public PSTRegistryTypeHandle<RequirementBuilder> get(PSTSkillRequirementId id) {
        return getEntry(id);
    }
}
