package com.pickaid.passivestjs.kubejs.id;

import com.pickaid.passivestjs.kubejs.builder.Ids;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PSTSkillRequirementId(ResourceLocation location) implements PSTRegistryEntryId {
    public PSTSkillRequirementId {
        Objects.requireNonNull(location, "location");
    }

    public static PSTSkillRequirementId of(ResourceLocation location) {
        return new PSTSkillRequirementId(location);
    }

    public static PSTSkillRequirementId parse(Object value) {
        if (value instanceof PSTSkillRequirementId id) {
            return id;
        }
        return new PSTSkillRequirementId(Ids.parse(value, "skillRequirementId"));
    }

    @Override
    public PSTSerializerFamily family() {
        return PSTSerializerFamily.SKILL_REQUIREMENTS;
    }

    @Override
    public String toString() {
        return id();
    }
}
