package com.pickaid.passivestjs.kubejs.recipe.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.pickaid.passivestjs.kubejs.builder.BonusBuilder;
import com.pickaid.passivestjs.kubejs.builder.ItemBonusBuilder;
import com.pickaid.passivestjs.kubejs.builder.PSTNodeWriter;
import com.pickaid.passivestjs.kubejs.builder.TypedJsonBuilder;
import com.pickaid.passivestjs.kubejs.content.JsonHelper;
import com.pickaid.passivestjs.kubejs.id.PSTItemBonusId;
import com.pickaid.passivestjs.kubejs.id.PSTSkillBonusId;
import com.pickaid.passivestjs.schema.PSTNodeFamily;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;

import java.util.function.Consumer;

public final class PSTItemBonusListBuilder extends TypedJsonBuilder<PSTItemBonusListBuilder> {
    public PSTItemBonusListBuilder() {
        super("skilltree:item_bonus_list");
    }

    @Info(value = "Adds a nested item bonus to the built-in skilltree:item_bonus_list item bonus.", params = {
            @Param(name = "typeId", value = "The nested item bonus type id."),
            @Param(name = "consumer", value = "The callback that configures the nested item bonus builder.")
    })
    public PSTItemBonusListBuilder itemBonus(PSTItemBonusId typeId, Consumer<ItemBonusBuilder> consumer) {
        ItemBonusBuilder builder = new ItemBonusBuilder(PSTItemBonusId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return itemBonus(builder);
    }

    @Info(value = "Adds a nested item bonus through the schema writer for the given item bonus id.", params = {
            @Param(name = "typeId", value = "The nested item bonus type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields.")
    })
    public PSTItemBonusListBuilder itemBonusSchema(PSTItemBonusId typeId, Consumer<PSTNodeWriter> consumer) {
        PSTNodeWriter writer = new PSTNodeWriter(PSTNodeFamily.ITEM_BONUS, PSTItemBonusId.parse(typeId).location());
        if (consumer != null) {
            consumer.accept(writer);
        }
        return itemBonus(writer);
    }

    @Info("Adds a prebuilt nested item bonus builder to the built-in skilltree:item_bonus_list item bonus.")
    public PSTItemBonusListBuilder itemBonus(ItemBonusBuilder value) {
        return itemBonus((Object) value);
    }

    @Info("Adds a built-in skilltree:skill_bonus item bonus to this built-in skilltree:item_bonus_list item bonus.")
    public PSTItemBonusListBuilder skillBonusItemBonus(PSTSkillBonusId typeId, Consumer<BonusBuilder> consumer) {
        return itemBonus(new PSTSkillBonusItemBonusBuilder().skillBonus(typeId, consumer));
    }

    @Info("Adds a built-in skilltree:skill_bonus item bonus using schema-writer configuration for the nested skill bonus.")
    public PSTItemBonusListBuilder skillBonusItemBonusSchema(PSTSkillBonusId typeId, Consumer<PSTNodeWriter> consumer) {
        return itemBonus(new PSTSkillBonusItemBonusBuilder().skillBonusSchema(typeId, consumer));
    }

    @Info("Adds a built-in skilltree:attribute skill bonus item bonus to this built-in skilltree:item_bonus_list item bonus.")
    public PSTItemBonusListBuilder attributeItemBonus(Consumer<PSTAttributeSkillBonusBuilder> consumer) {
        return itemBonus(new PSTSkillBonusItemBonusBuilder().attribute(consumer));
    }

    @Info("Adds a prebuilt built-in skilltree:attribute skill bonus item bonus to this built-in skilltree:item_bonus_list item bonus.")
    public PSTItemBonusListBuilder attributeItemBonus(PSTAttributeSkillBonusBuilder value) {
        return itemBonus(new PSTSkillBonusItemBonusBuilder().attribute(value));
    }

    public PSTItemBonusListBuilder itemBonus(PSTSkillBonusItemBonusBuilder value) {
        return itemBonus((Object) value);
    }

    private PSTItemBonusListBuilder itemBonus(Object value) {
        JsonElement element = JsonHelper.optionalElement(value);
        if (element == null) {
            return this;
        }

        JsonArray bonuses = json.has("inner_bonuses") && json.get("inner_bonuses").isJsonArray()
                ? json.getAsJsonArray("inner_bonuses")
                : new JsonArray();
        bonuses.add(element);
        json.add("inner_bonuses", bonuses);
        return this;
    }
}
