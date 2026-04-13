package com.pickaid.passivestjs.kubejs.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.pickaid.passivestjs.kubejs.content.JsonHelper;
import com.pickaid.passivestjs.kubejs.id.PSTItemBonusId;
import com.pickaid.passivestjs.kubejs.id.PSTSkillBonusId;
import com.pickaid.passivestjs.schema.PSTNodeFamily;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.function.Consumer;

public class ItemBonusBuilder extends TypedJsonBuilder<ItemBonusBuilder> {
    public ItemBonusBuilder(String type) {
        super(type);
    }

    @HideFromJS
    public ItemBonusBuilder skillBonus(Object value) {
        return optionalRaw("skill_bonus", value);
    }

    @Info(value = "Configures nested skill bonus via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The skill bonus type id."),
            @Param(name = "consumer", value = "The callback that configures the created skill bonus builder.")
    })
    public ItemBonusBuilder skillBonus(PSTSkillBonusId typeId, Consumer<BonusBuilder> consumer) {
        BonusBuilder builder = new BonusBuilder(PSTSkillBonusId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return skillBonus(builder);
    }

    @Info(value = "Configures nested skill bonus through the schema writer for the given skill bonus id.", params = {
            @Param(name = "typeId", value = "The skill bonus type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields.")
    })
    public ItemBonusBuilder skillBonusSchema(PSTSkillBonusId typeId, Consumer<PSTNodeWriter> consumer) {
        PSTNodeWriter writer = new PSTNodeWriter(PSTNodeFamily.SKILL_BONUS, PSTSkillBonusId.parse(typeId).location());
        if (consumer != null) {
            consumer.accept(writer);
        }
        return skillBonus(writer);
    }

    @Info("Adds a prebuilt nested skill bonus builder.")
    public ItemBonusBuilder skillBonus(BonusBuilder value) {
        return skillBonus((Object) value);
    }

    @HideFromJS
    public ItemBonusBuilder itemBonuses(Object value) {
        JsonElement element = JsonHelper.optionalElement(value);
        if (element == null) {
            json.remove("inner_bonuses");
            return this;
        }

        JsonArray bonuses = json.has("inner_bonuses") && json.get("inner_bonuses").isJsonArray()
                ? json.getAsJsonArray("inner_bonuses")
                : new JsonArray();
        bonuses.add(element);
        json.add("inner_bonuses", bonuses);
        return this;
    }

    @Info(value = "Configures nested item bonus via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The item bonus type id."),
            @Param(name = "consumer", value = "The callback that configures the created item bonus builder.")
    })
    public ItemBonusBuilder itemBonuses(PSTItemBonusId typeId, Consumer<ItemBonusBuilder> consumer) {
        ItemBonusBuilder builder = new ItemBonusBuilder(PSTItemBonusId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return itemBonuses(builder);
    }

    @Info(value = "Configures nested item bonus through the schema writer for the given item bonus id.", params = {
            @Param(name = "typeId", value = "The item bonus type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields.")
    })
    public ItemBonusBuilder itemBonusesSchema(PSTItemBonusId typeId, Consumer<PSTNodeWriter> consumer) {
        PSTNodeWriter writer = new PSTNodeWriter(PSTNodeFamily.ITEM_BONUS, PSTItemBonusId.parse(typeId).location());
        if (consumer != null) {
            consumer.accept(writer);
        }
        return itemBonuses(writer);
    }

    @Info("Adds a prebuilt nested item bonus builder.")
    public ItemBonusBuilder itemBonuses(ItemBonusBuilder value) {
        return itemBonuses((Object) value);
    }
}
