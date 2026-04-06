package com.pickaid.passivestjs.kubejs.builder;

public class RequirementBuilder
        extends TypedJsonBuilder<RequirementBuilder> {
    public RequirementBuilder(String type) {
        super(type);
    }

    public RequirementBuilder skill(Object skillId) {
        return string("skillId", Ids.stringify(skillId, "skillId"));
    }

    public RequirementBuilder advancement(Object advancementId) {
        return string("advancementId", Ids.stringify(advancementId, "advancementId"));
    }

    public RequirementBuilder statType(Object statTypeId) {
        return string("statTypeId", Ids.stringify(statTypeId, "statTypeId"));
    }

    public RequirementBuilder stat(Object statId) {
        return string("statId", Ids.stringify(statId, "statId"));
    }

    public RequirementBuilder minValue(Number value) {
        return integer("minValue", value);
    }

    public RequirementBuilder valueProvider(Object value) {
        return optionalRaw("value_provider", value);
    }

    public RequirementBuilder requiredValue(Number value) {
        return number("required_value", value);
    }

    public RequirementBuilder logic(String value) {
        return string("logic", value);
    }
}
