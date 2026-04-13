package com.pickaid.passivestjs.kubejs.builder;

import com.pickaid.passivestjs.kubejs.id.PSTDamageConditionId;
import com.pickaid.passivestjs.kubejs.id.PSTLivingConditionId;
import com.pickaid.passivestjs.kubejs.id.PSTLivingMultiplierId;
import com.pickaid.passivestjs.kubejs.type.PSTSkillTarget;
import com.pickaid.passivestjs.schema.PSTNodeFamily;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.function.Consumer;

public class ListenerBuilder
        extends TypedJsonBuilder<ListenerBuilder> {
    public ListenerBuilder(String type) {
        super(type);
    }

    public ListenerBuilder cooldown(Number value) {
        return integer("cooldown", value);
    }

    @HideFromJS
    public ListenerBuilder target(String value) {
        return target(PSTSkillTarget.parse(value));
    }

    @Info(value = "Sets which side this listener applies bonuses to.", params = {
            @Param(name = "target", value = "The target side for this listener.")
    })
    public ListenerBuilder target(PSTSkillTarget target) {
        return string("target", target.id());
    }

    @HideFromJS
    public ListenerBuilder whenPlayer(Object value) {
        return optionalRaw("player_condition", value);
    }

    @Info(value = "Configures player condition via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The living condition type id."),
            @Param(name = "consumer", value = "The callback that configures the created player condition builder.")
    })
    public ListenerBuilder whenPlayer(PSTLivingConditionId typeId, Consumer<ConditionBuilder> consumer) {
        ConditionBuilder builder = new ConditionBuilder(PSTLivingConditionId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return whenPlayer(builder);
    }

    @Info(value = "Configures player condition through the schema writer for the given living condition id.", params = {
            @Param(name = "typeId", value = "The living condition type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields.")
    })
    public ListenerBuilder whenPlayerSchema(PSTLivingConditionId typeId, Consumer<PSTNodeWriter> consumer) {
        return whenPlayer(node(PSTNodeFamily.LIVING_CONDITION, PSTLivingConditionId.parse(typeId).location(), consumer));
    }

    @Info("Adds a prebuilt player condition builder.")
    public ListenerBuilder whenPlayer(ConditionBuilder value) {
        return whenPlayer((Object) value);
    }

    @HideFromJS
    public ListenerBuilder whenEnemy(Object value) {
        return optionalRaw("enemy_condition", value);
    }

    @Info(value = "Configures enemy condition via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The living condition type id."),
            @Param(name = "consumer", value = "The callback that configures the created enemy condition builder.")
    })
    public ListenerBuilder whenEnemy(PSTLivingConditionId typeId, Consumer<ConditionBuilder> consumer) {
        ConditionBuilder builder = new ConditionBuilder(PSTLivingConditionId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return whenEnemy(builder);
    }

    @Info(value = "Configures enemy condition through the schema writer for the given living condition id.", params = {
            @Param(name = "typeId", value = "The living condition type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields.")
    })
    public ListenerBuilder whenEnemySchema(PSTLivingConditionId typeId, Consumer<PSTNodeWriter> consumer) {
        return whenEnemy(node(PSTNodeFamily.LIVING_CONDITION, PSTLivingConditionId.parse(typeId).location(), consumer));
    }

    @Info("Adds a prebuilt enemy condition builder.")
    public ListenerBuilder whenEnemy(ConditionBuilder value) {
        return whenEnemy((Object) value);
    }

    @HideFromJS
    public ListenerBuilder whenDamage(Object value) {
        return optionalRaw("damage_condition", value);
    }

    @Info(value = "Configures damage condition via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The damage condition type id."),
            @Param(name = "consumer", value = "The callback that configures the created damage condition builder.")
    })
    public ListenerBuilder whenDamage(PSTDamageConditionId typeId, Consumer<ConditionBuilder> consumer) {
        ConditionBuilder builder = new ConditionBuilder(PSTDamageConditionId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return whenDamage(builder);
    }

    @Info(value = "Configures damage condition through the schema writer for the given damage condition id.", params = {
            @Param(name = "typeId", value = "The damage condition type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields.")
    })
    public ListenerBuilder whenDamageSchema(PSTDamageConditionId typeId, Consumer<PSTNodeWriter> consumer) {
        return whenDamage(node(PSTNodeFamily.DAMAGE_CONDITION, PSTDamageConditionId.parse(typeId).location(), consumer));
    }

    @Info("Adds a prebuilt damage condition builder.")
    public ListenerBuilder whenDamage(ConditionBuilder value) {
        return whenDamage((Object) value);
    }

    @HideFromJS
    public ListenerBuilder scalePlayer(Object value) {
        return optionalRaw("player_multiplier", value);
    }

    @Info(value = "Configures player multiplier via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The multiplier type id."),
            @Param(name = "consumer", value = "The callback that configures the created player multiplier builder.")
    })
    public ListenerBuilder scalePlayer(PSTLivingMultiplierId typeId, Consumer<MultiplierBuilder> consumer) {
        MultiplierBuilder builder = new MultiplierBuilder(PSTLivingMultiplierId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return scalePlayer(builder);
    }

    @Info(value = "Configures player multiplier through the schema writer for the given multiplier id.", params = {
            @Param(name = "typeId", value = "The multiplier type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields.")
    })
    public ListenerBuilder scalePlayerSchema(PSTLivingMultiplierId typeId, Consumer<PSTNodeWriter> consumer) {
        return scalePlayer(node(PSTNodeFamily.MULTIPLIER, PSTLivingMultiplierId.parse(typeId).location(), consumer));
    }

    @Info("Adds a prebuilt player multiplier builder.")
    public ListenerBuilder scalePlayer(MultiplierBuilder value) {
        return scalePlayer((Object) value);
    }

    @HideFromJS
    public ListenerBuilder scaleEnemy(Object value) {
        return optionalRaw("enemy_multiplier", value);
    }

    @Info(value = "Configures enemy multiplier via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The multiplier type id."),
            @Param(name = "consumer", value = "The callback that configures the created enemy multiplier builder.")
    })
    public ListenerBuilder scaleEnemy(PSTLivingMultiplierId typeId, Consumer<MultiplierBuilder> consumer) {
        MultiplierBuilder builder = new MultiplierBuilder(PSTLivingMultiplierId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return scaleEnemy(builder);
    }

    @Info(value = "Configures enemy multiplier through the schema writer for the given multiplier id.", params = {
            @Param(name = "typeId", value = "The multiplier type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields.")
    })
    public ListenerBuilder scaleEnemySchema(PSTLivingMultiplierId typeId, Consumer<PSTNodeWriter> consumer) {
        return scaleEnemy(node(PSTNodeFamily.MULTIPLIER, PSTLivingMultiplierId.parse(typeId).location(), consumer));
    }

    @Info("Adds a prebuilt enemy multiplier builder.")
    public ListenerBuilder scaleEnemy(MultiplierBuilder value) {
        return scaleEnemy((Object) value);
    }

    private static PSTNodeWriter node(PSTNodeFamily family, net.minecraft.resources.ResourceLocation typeId, Consumer<PSTNodeWriter> consumer) {
        PSTNodeWriter writer = new PSTNodeWriter(family, typeId);
        if (consumer != null) {
            consumer.accept(writer);
        }
        return writer;
    }
}
