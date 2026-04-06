package com.pickaid.passivestjs.kubejs.content;

import com.pickaid.passivestjs.kubejs.builder.ValueBuilder;

public final class Values {
    public static final Values INSTANCE = new Values();

    private Values() {
    }

    public ValueBuilder distanceToTarget() {
        return new ValueBuilder("skilltree:distance_to_target");
    }

    public ValueBuilder effectAmount(String effectType) {
        return new ValueBuilder("skilltree:effect_amount").effectType(effectType);
    }

    public ValueBuilder attributeValue(String attributeId) {
        return new ValueBuilder("skilltree:attribute_value").attribute(attributeId);
    }

    public ValueBuilder foodLevel() {
        return foodLevel(true, false);
    }

    public ValueBuilder foodLevel(boolean percentage, boolean missing) {
        return new ValueBuilder("skilltree:food_level")
                .percentage(percentage)
                .missing(missing);
    }

    public ValueBuilder healthLevel() {
        return healthLevel(true, false);
    }

    public ValueBuilder healthLevel(boolean percentage, boolean missing) {
        return new ValueBuilder("skilltree:health_level")
                .percentage(percentage)
                .missing(missing);
    }
}
