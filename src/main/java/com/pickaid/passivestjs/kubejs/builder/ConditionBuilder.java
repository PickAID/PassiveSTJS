package com.pickaid.passivestjs.kubejs.builder;

import com.pickaid.passivestjs.kubejs.id.PSTItemConditionId;
import com.pickaid.passivestjs.kubejs.id.PSTItemId;
import com.pickaid.passivestjs.kubejs.id.PSTItemTagId;
import com.pickaid.passivestjs.kubejs.id.PSTMobEffectId;
import com.pickaid.passivestjs.kubejs.id.PSTNumericValueProviderId;
import com.pickaid.passivestjs.kubejs.id.PSTPotionId;
import com.pickaid.passivestjs.kubejs.type.PSTComparisonLogic;
import com.pickaid.passivestjs.kubejs.type.PSTEquipmentType;
import com.pickaid.passivestjs.schema.PSTNodeFamily;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.function.Consumer;

public class ConditionBuilder
        extends TypedJsonBuilder<ConditionBuilder> {
    public ConditionBuilder(String type) {
        super(type);
    }

    @HideFromJS
    public ConditionBuilder itemCondition(Object value) {
        return optionalRaw("item_condition", value);
    }

    @Info(value = "Configures nested item condition via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The item condition type id."),
            @Param(name = "consumer", value = "The callback that configures the created item condition builder.")
    })
    public ConditionBuilder itemCondition(PSTItemConditionId typeId, Consumer<ConditionBuilder> consumer) {
        ConditionBuilder builder = new ConditionBuilder(PSTItemConditionId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return itemCondition(builder);
    }

    @Info(value = "Configures nested item condition through the schema writer for the given item condition id.", params = {
            @Param(name = "typeId", value = "The item condition type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields.")
    })
    public ConditionBuilder itemConditionSchema(PSTItemConditionId typeId, Consumer<PSTNodeWriter> consumer) {
        return itemCondition(node(PSTNodeFamily.ITEM_CONDITION, PSTItemConditionId.parse(typeId).location(), consumer));
    }

    @Info("Adds a prebuilt nested item condition builder.")
    public ConditionBuilder itemCondition(ConditionBuilder value) {
        return itemCondition((Object) value);
    }

    @HideFromJS
    public ConditionBuilder valueProvider(Object value) {
        return optionalRaw("value_provider", value);
    }

    @Info(value = "Configures value provider via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The numeric value type id."),
            @Param(name = "consumer", value = "The callback that configures the created numeric value builder.")
    })
    public ConditionBuilder valueProvider(PSTNumericValueProviderId typeId, Consumer<ValueBuilder> consumer) {
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
    public ConditionBuilder valueProviderSchema(PSTNumericValueProviderId typeId, Consumer<PSTNodeWriter> consumer) {
        return valueProvider(node(PSTNodeFamily.NUMERIC_VALUE, PSTNumericValueProviderId.parse(typeId).location(), consumer));
    }

    @Info("Adds a prebuilt numeric value provider builder.")
    public ConditionBuilder valueProvider(ValueBuilder value) {
        return valueProvider((Object) value);
    }

    public ConditionBuilder requiredValue(Number value) {
        return number("required_value", value);
    }

    @HideFromJS
    public ConditionBuilder logic(String value) {
        return logic(PSTComparisonLogic.parse(value));
    }

    @Info(value = "Sets the numeric comparison logic for this condition.", params = {
            @Param(name = "logic", value = "The comparison logic.")
    })
    public ConditionBuilder logic(PSTComparisonLogic logic) {
        return string("logic", logic.serializedName());
    }

    @HideFromJS
    public ConditionBuilder effect(String value) {
        return effect(PSTMobEffectId.parse(value));
    }

    @Info(value = "Sets the mob effect id for this condition.", params = {
            @Param(name = "effectId", value = "The mob effect id.")
    })
    public ConditionBuilder effect(PSTMobEffectId effectId) {
        return string("effect", effectId.id());
    }

    public ConditionBuilder amplifier(Number value) {
        return integer("amplifier", value);
    }

    @HideFromJS
    public ConditionBuilder equipmentType(String value) {
        return equipmentType(PSTEquipmentType.parse(value));
    }

    @Info(value = "Sets the equipment type used by this condition.", params = {
            @Param(name = "equipmentType", value = "The equipment type.")
    })
    public ConditionBuilder equipmentType(PSTEquipmentType equipmentType) {
        return string("equipment_type", equipmentType.id());
    }

    @HideFromJS
    public ConditionBuilder potionType(String value) {
        return potionType(PSTPotionId.parse(value));
    }

    @Info(value = "Sets the potion id for this condition.", params = {
            @Param(name = "potionId", value = "The potion id.")
    })
    public ConditionBuilder potionType(PSTPotionId potionId) {
        return string("potion_type", potionId.id());
    }

    @HideFromJS
    public ConditionBuilder tagId(String value) {
        return tagId(PSTItemTagId.parse(value));
    }

    @Info(value = "Sets the item tag id for this condition.", params = {
            @Param(name = "tagId", value = "The item tag id.")
    })
    public ConditionBuilder tagId(PSTItemTagId tagId) {
        return string("tag_id", tagId.id());
    }

    @HideFromJS
    public ConditionBuilder itemId(String value) {
        return itemId(PSTItemId.parse(value));
    }

    @Info(value = "Sets the item id for this condition.", params = {
            @Param(name = "itemId", value = "The item id.")
    })
    public ConditionBuilder itemId(PSTItemId itemId) {
        return string("id", itemId.id());
    }

    private static PSTNodeWriter node(PSTNodeFamily family, net.minecraft.resources.ResourceLocation typeId, Consumer<PSTNodeWriter> consumer) {
        PSTNodeWriter writer = new PSTNodeWriter(family, typeId);
        if (consumer != null) {
            consumer.accept(writer);
        }
        return writer;
    }
}
