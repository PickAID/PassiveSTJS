package com.pickaid.passivestjs.kubejs.builder;

public class ConditionBuilder
        extends TypedJsonBuilder<ConditionBuilder> {
    public ConditionBuilder(String type) {
        super(type);
    }

    public ConditionBuilder itemCondition(Object value) {
        return optionalRaw("item_condition", value);
    }

    public ConditionBuilder valueProvider(Object value) {
        return optionalRaw("value_provider", value);
    }

    public ConditionBuilder requiredValue(Number value) {
        return number("required_value", value);
    }

    public ConditionBuilder logic(String value) {
        return string("logic", value);
    }

    public ConditionBuilder effect(String value) {
        return string("effect", value);
    }

    public ConditionBuilder amplifier(Number value) {
        return integer("amplifier", value);
    }

    public ConditionBuilder equipmentType(String value) {
        return string("equipment_type", value);
    }

    public ConditionBuilder potionType(String value) {
        return string("potion_type", value);
    }

    public ConditionBuilder tagId(String value) {
        return string("tag_id", value);
    }

    public ConditionBuilder itemId(String value) {
        return string("id", value);
    }
}
