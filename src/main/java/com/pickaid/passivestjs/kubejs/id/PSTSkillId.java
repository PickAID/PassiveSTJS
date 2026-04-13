package com.pickaid.passivestjs.kubejs.id;

import com.pickaid.passivestjs.kubejs.builder.Ids;
import com.pickaid.passivestjs.kubejs.builder.SkillBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PSTSkillId(ResourceLocation location) {
    public PSTSkillId {
        Objects.requireNonNull(location, "location");
    }

    public static PSTSkillId of(ResourceLocation location) {
        return new PSTSkillId(location);
    }

    public static PSTSkillId parse(Object value) {
        if (value instanceof PSTSkillId skillId) {
            return skillId;
        }
        if (value instanceof SkillBuilder builder) {
            return new PSTSkillId(builder.idLocation());
        }
        return new PSTSkillId(Ids.parse(value, "skillId"));
    }

    public String id() {
        return location.toString();
    }

    @Override
    public String toString() {
        return id();
    }
}
