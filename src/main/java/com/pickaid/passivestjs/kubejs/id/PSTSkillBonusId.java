package com.pickaid.passivestjs.kubejs.id;

import com.pickaid.passivestjs.kubejs.builder.Ids;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PSTSkillBonusId(ResourceLocation location) implements PSTRegistryEntryId {
    public PSTSkillBonusId {
        Objects.requireNonNull(location, "location");
    }

    public static PSTSkillBonusId of(ResourceLocation location) {
        return new PSTSkillBonusId(location);
    }

    public static PSTSkillBonusId parse(Object value) {
        if (value instanceof PSTSkillBonusId id) {
            return id;
        }
        return new PSTSkillBonusId(Ids.parse(value, "skillBonusId"));
    }

    @Override
    public PSTSerializerFamily family() {
        return PSTSerializerFamily.SKILL_BONUSES;
    }

    @Override
    public String toString() {
        return id();
    }
}
