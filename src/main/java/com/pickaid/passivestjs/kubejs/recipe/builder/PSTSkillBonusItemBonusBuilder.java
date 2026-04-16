package com.pickaid.passivestjs.kubejs.recipe.builder;

import com.pickaid.passivestjs.kubejs.builder.BonusBuilder;
import com.pickaid.passivestjs.kubejs.builder.PSTNodeWriter;
import com.pickaid.passivestjs.kubejs.builder.TypedJsonBuilder;
import com.pickaid.passivestjs.kubejs.id.PSTSkillBonusId;
import com.pickaid.passivestjs.schema.PSTNodeFamily;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;

import java.util.function.Consumer;

public final class PSTSkillBonusItemBonusBuilder extends TypedJsonBuilder<PSTSkillBonusItemBonusBuilder> {
    public PSTSkillBonusItemBonusBuilder() {
        super("skilltree:skill_bonus");
    }

    @Info(value = "Configures the nested skill bonus inside the built-in skilltree:skill_bonus item bonus.", params = {
            @Param(name = "typeId", value = "The nested skill bonus type id."),
            @Param(name = "consumer", value = "The callback that configures the nested skill bonus builder.")
    })
    public PSTSkillBonusItemBonusBuilder skillBonus(PSTSkillBonusId typeId, Consumer<BonusBuilder> consumer) {
        BonusBuilder builder = new BonusBuilder(PSTSkillBonusId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return skillBonus(builder);
    }

    @Info(value = "Configures the nested skill bonus through the schema writer for the given skill bonus id.", params = {
            @Param(name = "typeId", value = "The nested skill bonus type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields.")
    })
    public PSTSkillBonusItemBonusBuilder skillBonusSchema(PSTSkillBonusId typeId, Consumer<PSTNodeWriter> consumer) {
        PSTNodeWriter writer = new PSTNodeWriter(PSTNodeFamily.SKILL_BONUS, PSTSkillBonusId.parse(typeId).location());
        if (consumer != null) {
            consumer.accept(writer);
        }
        return skillBonus(writer);
    }

    @Info("Adds a prebuilt nested skill bonus builder to the built-in skilltree:skill_bonus item bonus.")
    public PSTSkillBonusItemBonusBuilder skillBonus(BonusBuilder value) {
        return skillBonus((Object) value);
    }

    @Info("Adds a built-in skilltree:attribute skill bonus inside this skilltree:skill_bonus item bonus.")
    public PSTSkillBonusItemBonusBuilder attribute(Consumer<PSTAttributeSkillBonusBuilder> consumer) {
        PSTAttributeSkillBonusBuilder builder = new PSTAttributeSkillBonusBuilder();
        if (consumer != null) {
            consumer.accept(builder);
        }
        return skillBonus(builder);
    }

    @Info("Adds a prebuilt built-in skilltree:attribute skill bonus inside this skilltree:skill_bonus item bonus.")
    public PSTSkillBonusItemBonusBuilder attribute(PSTAttributeSkillBonusBuilder value) {
        return skillBonus(value);
    }

    private PSTSkillBonusItemBonusBuilder skillBonus(Object value) {
        return optionalRaw("skill_bonus", value);
    }
}
