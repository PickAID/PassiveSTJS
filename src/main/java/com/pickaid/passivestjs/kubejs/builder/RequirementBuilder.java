package com.pickaid.passivestjs.kubejs.builder;

import com.pickaid.passivestjs.kubejs.id.PSTNumericValueProviderId;
import com.pickaid.passivestjs.kubejs.id.PSTSkillId;
import com.pickaid.passivestjs.kubejs.id.PSTStatTypeId;
import com.pickaid.passivestjs.kubejs.type.PSTComparisonLogic;
import com.pickaid.passivestjs.schema.PSTNodeFamily;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.function.Consumer;

public class RequirementBuilder
        extends TypedJsonBuilder<RequirementBuilder> {
    public RequirementBuilder(String type) {
        super(type);
    }

    @HideFromJS
    public RequirementBuilder skill(Object skillId) {
        return string("skill_id", Ids.stringify(skillId, "skillId"));
    }

    @Info(value = "Sets the required learned skill id.", params = {
            @Param(name = "skillId", value = "The required skill id.")
    })
    public RequirementBuilder skill(PSTSkillId skillId) {
        return string("skill_id", skillId.id());
    }

    public RequirementBuilder advancement(Object advancementId) {
        return string("advancement", Ids.stringify(advancementId, "advancementId"));
    }

    @HideFromJS
    public RequirementBuilder statType(Object statTypeId) {
        return string("statTypeId", Ids.stringify(statTypeId, "statTypeId"));
    }

    @Info(value = "Sets the stat type id for this requirement.", params = {
            @Param(name = "statTypeId", value = "The stat type id.")
    })
    public RequirementBuilder statType(PSTStatTypeId statTypeId) {
        return string("statTypeId", statTypeId.id());
    }

    public RequirementBuilder stat(Object statId) {
        return string("statId", Ids.stringify(statId, "statId"));
    }

    public RequirementBuilder minValue(Number value) {
        return integer("minValue", value);
    }

    @HideFromJS
    public RequirementBuilder valueProvider(Object value) {
        return optionalRaw("value_provider", value);
    }

    @Info("Adds a prebuilt numeric value provider builder.")
    public RequirementBuilder valueProvider(ValueBuilder value) {
        return valueProvider((Object) value);
    }

    @Info(value = "Configures value provider via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The numeric value type id."),
            @Param(name = "consumer", value = "The callback that configures the created numeric value builder.")
    })
    public RequirementBuilder valueProvider(PSTNumericValueProviderId typeId, Consumer<ValueBuilder> consumer) {
        ValueBuilder builder = new ValueBuilder(PSTNumericValueProviderId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return valueProvider(builder);
    }

    @Info(value = "Configures value provider through the schema writer for the given numeric value id.", params = {
            @Param(name = "typeId", value = "The numeric value type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields.")
    })
    public RequirementBuilder valueProviderSchema(PSTNumericValueProviderId typeId, Consumer<PSTNodeWriter> consumer) {
        PSTNodeWriter writer = new PSTNodeWriter(PSTNodeFamily.NUMERIC_VALUE, PSTNumericValueProviderId.parse(typeId).location());
        if (consumer != null) {
            consumer.accept(writer);
        }
        return valueProvider(writer);
    }

    public RequirementBuilder requiredValue(Number value) {
        return number("required_value", value);
    }

    @HideFromJS
    public RequirementBuilder logic(String value) {
        return logic(PSTComparisonLogic.parse(value));
    }

    @Info(value = "Sets the numeric comparison logic for this requirement.", params = {
            @Param(name = "logic", value = "The comparison logic.")
    })
    public RequirementBuilder logic(PSTComparisonLogic logic) {
        return string("logic", logic.serializedName());
    }
}
