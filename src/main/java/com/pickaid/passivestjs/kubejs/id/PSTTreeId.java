package com.pickaid.passivestjs.kubejs.id;

import com.pickaid.passivestjs.kubejs.builder.Ids;
import com.pickaid.passivestjs.kubejs.builder.SkillTreeBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PSTTreeId(ResourceLocation location) {
    public PSTTreeId {
        Objects.requireNonNull(location, "location");
    }

    public static PSTTreeId of(ResourceLocation location) {
        return new PSTTreeId(location);
    }

    public static PSTTreeId parse(Object value) {
        if (value instanceof PSTTreeId treeId) {
            return treeId;
        }
        if (value instanceof SkillTreeBuilder builder) {
            return new PSTTreeId(builder.idLocation());
        }
        return new PSTTreeId(Ids.parse(value, "treeId"));
    }

    public String id() {
        return location.toString();
    }

    @Override
    public String toString() {
        return id();
    }
}
