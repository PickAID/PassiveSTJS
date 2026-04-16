package com.pickaid.passivestjs.kubejs.recipe.builder;

import com.pickaid.passivestjs.kubejs.builder.TypedJsonBuilder;
import com.pickaid.passivestjs.kubejs.id.PSTAttributeId;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.HideFromJS;

public final class PSTAttributeSkillBonusBuilder extends TypedJsonBuilder<PSTAttributeSkillBonusBuilder> {
    public PSTAttributeSkillBonusBuilder() {
        super("skilltree:attribute");
    }

    public PSTAttributeSkillBonusBuilder amount(Number value) {
        return number("amount", value);
    }

    public PSTAttributeSkillBonusBuilder operation(Number value) {
        return integer("operation", value);
    }

    @HideFromJS
    public PSTAttributeSkillBonusBuilder attribute(String value) {
        return attribute(PSTAttributeId.parse(value));
    }

    @Info(value = "Sets the target attribute for this built-in attribute skill bonus.", params = {
            @Param(name = "attributeId", value = "The attribute id.")
    })
    public PSTAttributeSkillBonusBuilder attribute(PSTAttributeId attributeId) {
        return string("attribute", attributeId.id());
    }

    public PSTAttributeSkillBonusBuilder modifierId(String value) {
        return string("id", value);
    }

    public PSTAttributeSkillBonusBuilder bonusName(String value) {
        return string("name", value);
    }
}
