package com.pickaid.passivestjs.kubejs.id;

import com.pickaid.passivestjs.kubejs.builder.Ids;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record PSTNumericValueProviderId(ResourceLocation location) implements PSTRegistryEntryId {
    public PSTNumericValueProviderId {
        Objects.requireNonNull(location, "location");
    }

    public static PSTNumericValueProviderId of(ResourceLocation location) {
        return new PSTNumericValueProviderId(location);
    }

    public static PSTNumericValueProviderId parse(Object value) {
        if (value instanceof PSTNumericValueProviderId id) {
            return id;
        }
        return new PSTNumericValueProviderId(Ids.parse(value, "numericValueProviderId"));
    }

    @Override
    public PSTSerializerFamily family() {
        return PSTSerializerFamily.FLOAT_FUNCTIONS;
    }

    @Override
    public String toString() {
        return id();
    }
}
