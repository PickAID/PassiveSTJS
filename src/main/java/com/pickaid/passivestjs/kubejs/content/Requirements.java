package com.pickaid.passivestjs.kubejs.content;

import com.pickaid.passivestjs.kubejs.builder.RequirementBuilder;

public final class Requirements {
    public static final Requirements INSTANCE = new Requirements();

    private Requirements() {
    }

    public RequirementBuilder learnedSkill(Object skillId) {
        return new RequirementBuilder("skilltree:learned_skill").skill(skillId);
    }

    public RequirementBuilder advancement(Object advancementId) {
        return new RequirementBuilder("skilltree:advancement").advancement(advancementId);
    }

    public RequirementBuilder statValue(Object statTypeId, Object statId, Number minValue) {
        return new RequirementBuilder("skilltree:stat_value")
                .statType(statTypeId)
                .stat(statId)
                .minValue(minValue);
    }

    public RequirementBuilder numericValue() {
        return new RequirementBuilder("skilltree:numeric_value");
    }

    public RequirementBuilder numericValue(Object valueProvider, Number requiredValue, String logic) {
        return numericValue()
                .valueProvider(valueProvider)
                .requiredValue(requiredValue)
                .logic(logic);
    }
}
