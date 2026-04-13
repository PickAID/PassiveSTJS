package com.pickaid.passivestjs.kubejs.builder;

import com.pickaid.passivestjs.kubejs.id.PSTDamageConditionId;
import com.pickaid.passivestjs.kubejs.id.PSTEventListenerId;
import com.pickaid.passivestjs.kubejs.id.PSTAttributeId;
import com.pickaid.passivestjs.kubejs.id.PSTItemConditionId;
import com.pickaid.passivestjs.kubejs.id.PSTLivingConditionId;
import com.pickaid.passivestjs.kubejs.id.PSTLivingMultiplierId;
import com.pickaid.passivestjs.kubejs.id.PSTMobEffectId;
import com.pickaid.passivestjs.kubejs.type.PSTSkillTarget;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import com.pickaid.passivestjs.schema.PSTNodeFamily;

import java.util.function.Consumer;

public class BonusBuilder
        extends TypedJsonBuilder<BonusBuilder> {
    private static final String NONE_TYPE = "skilltree:none";
    private static final String ATTACK_LISTENER_TYPE = "skilltree:attack";
    private static final String TICKING_LISTENER_TYPE = "skilltree:ticking";

    public BonusBuilder(String type) {
        super(type);
    }

    public BonusBuilder amount(Number value) {
        return number("amount", value);
    }

    public BonusBuilder chance(Number value) {
        return number("chance", value);
    }

    public BonusBuilder multiplier(Number value) {
        return number("multiplier", value);
    }

    public BonusBuilder duration(Number value) {
        return integer("duration", value);
    }

    public BonusBuilder cooldown(Number value) {
        return integer("cooldown", value);
    }

    public BonusBuilder amplifier(Number value) {
        return integer("amplifier", value);
    }

    public BonusBuilder maxStacks(Number value) {
        return integer("max_stacks", value);
    }

    public BonusBuilder operation(Number value) {
        return integer("operation", value);
    }

    @HideFromJS
    public BonusBuilder target(String value) {
        return target(PSTSkillTarget.parse(value));
    }

    @Info(value = "Sets which side this bonus applies to.", params = {
            @Param(name = "target", value = "The target side for this bonus.")
    })
    public BonusBuilder target(PSTSkillTarget target) {
        return string("target", target.id());
    }

    @HideFromJS
    public BonusBuilder effect(String value) {
        return effect(PSTMobEffectId.parse(value));
    }

    @Info(value = "Sets the mob effect id for this bonus.", params = {
            @Param(name = "effectId", value = "The mob effect id.")
    })
    public BonusBuilder effect(PSTMobEffectId effectId) {
        return string("effect", effectId.id());
    }

    @HideFromJS
    public BonusBuilder effectType(String value) {
        return effectType(PSTMobEffectId.parse(value));
    }

    @Info(value = "Sets the mob effect id used by this bonus type.", params = {
            @Param(name = "effectId", value = "The mob effect id.")
    })
    public BonusBuilder effectType(PSTMobEffectId effectId) {
        return string("effect_type", effectId.id());
    }

    public BonusBuilder lootType(String value) {
        return string("loot_type", value);
    }

    public BonusBuilder experienceSource(String value) {
        return string("experience_source", value);
    }

    @HideFromJS
    public BonusBuilder attribute(String value) {
        return attribute(PSTAttributeId.parse(value));
    }

    @Info(value = "Sets the attribute id for this bonus.", params = {
            @Param(name = "attributeId", value = "The attribute id.")
    })
    public BonusBuilder attribute(PSTAttributeId attributeId) {
        return string("attribute", attributeId.id());
    }

    public BonusBuilder contentType(String value) {
        return string("content_type", value);
    }

    public BonusBuilder contentId(String value) {
        return string("content_id", value);
    }

    public BonusBuilder modifierId(String value) {
        return string("id", value);
    }

    public BonusBuilder bonusName(String value) {
        return string("name", value);
    }

    public BonusBuilder percentageHealing(boolean value) {
        return bool("percentage_healing", value);
    }

    @HideFromJS
    public BonusBuilder whenPlayer(Object value) {
        return optionalRaw("player_condition", value);
    }

    @Info(value = "Configures player condition via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The living condition type id."),
            @Param(name = "consumer", value = "The callback that configures the created player condition builder.")
    })
    public BonusBuilder whenPlayer(PSTLivingConditionId typeId, Consumer<ConditionBuilder> consumer) {
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
    public BonusBuilder whenPlayerSchema(PSTLivingConditionId typeId, Consumer<PSTNodeWriter> consumer) {
        return whenPlayer(node(PSTNodeFamily.LIVING_CONDITION, PSTLivingConditionId.parse(typeId).location(), consumer));
    }

    @Info("Adds a prebuilt player condition builder.")
    public BonusBuilder whenPlayer(ConditionBuilder value) {
        return whenPlayer((Object) value);
    }

    @HideFromJS
    public BonusBuilder whenEnemy(Object value) {
        return optionalRaw("enemy_condition", value);
    }

    @Info(value = "Configures enemy condition via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The living condition type id."),
            @Param(name = "consumer", value = "The callback that configures the created enemy condition builder.")
    })
    public BonusBuilder whenEnemy(PSTLivingConditionId typeId, Consumer<ConditionBuilder> consumer) {
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
    public BonusBuilder whenEnemySchema(PSTLivingConditionId typeId, Consumer<PSTNodeWriter> consumer) {
        return whenEnemy(node(PSTNodeFamily.LIVING_CONDITION, PSTLivingConditionId.parse(typeId).location(), consumer));
    }

    @Info("Adds a prebuilt enemy condition builder.")
    public BonusBuilder whenEnemy(ConditionBuilder value) {
        return whenEnemy((Object) value);
    }

    @HideFromJS
    public BonusBuilder whenTarget(Object value) {
        return optionalRaw("target_condition", value);
    }

    @Info(value = "Configures target condition via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The living condition type id."),
            @Param(name = "consumer", value = "The callback that configures the created target condition builder.")
    })
    public BonusBuilder whenTarget(PSTLivingConditionId typeId, Consumer<ConditionBuilder> consumer) {
        ConditionBuilder builder = new ConditionBuilder(PSTLivingConditionId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return whenTarget(builder);
    }

    @Info(value = "Configures target condition through the schema writer for the given living condition id.", params = {
            @Param(name = "typeId", value = "The living condition type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields.")
    })
    public BonusBuilder whenTargetSchema(PSTLivingConditionId typeId, Consumer<PSTNodeWriter> consumer) {
        return whenTarget(node(PSTNodeFamily.LIVING_CONDITION, PSTLivingConditionId.parse(typeId).location(), consumer));
    }

    @Info("Adds a prebuilt target condition builder.")
    public BonusBuilder whenTarget(ConditionBuilder value) {
        return whenTarget((Object) value);
    }

    @HideFromJS
    public BonusBuilder whenAttacker(Object value) {
        return optionalRaw("attacker_condition", value);
    }

    @Info(value = "Configures attacker condition via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The living condition type id."),
            @Param(name = "consumer", value = "The callback that configures the created attacker condition builder.")
    })
    public BonusBuilder whenAttacker(PSTLivingConditionId typeId, Consumer<ConditionBuilder> consumer) {
        ConditionBuilder builder = new ConditionBuilder(PSTLivingConditionId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return whenAttacker(builder);
    }

    @Info(value = "Configures attacker condition through the schema writer for the given living condition id.", params = {
            @Param(name = "typeId", value = "The living condition type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields.")
    })
    public BonusBuilder whenAttackerSchema(PSTLivingConditionId typeId, Consumer<PSTNodeWriter> consumer) {
        return whenAttacker(node(PSTNodeFamily.LIVING_CONDITION, PSTLivingConditionId.parse(typeId).location(), consumer));
    }

    @Info("Adds a prebuilt attacker condition builder.")
    public BonusBuilder whenAttacker(ConditionBuilder value) {
        return whenAttacker((Object) value);
    }

    @HideFromJS
    public BonusBuilder whenDamage(Object value) {
        return optionalRaw("damage_condition", value);
    }

    @Info(value = "Configures damage condition via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The damage condition type id."),
            @Param(name = "consumer", value = "The callback that configures the created damage condition builder.")
    })
    public BonusBuilder whenDamage(PSTDamageConditionId typeId, Consumer<ConditionBuilder> consumer) {
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
    public BonusBuilder whenDamageSchema(PSTDamageConditionId typeId, Consumer<PSTNodeWriter> consumer) {
        return whenDamage(node(PSTNodeFamily.DAMAGE_CONDITION, PSTDamageConditionId.parse(typeId).location(), consumer));
    }

    @Info("Adds a prebuilt damage condition builder.")
    public BonusBuilder whenDamage(ConditionBuilder value) {
        return whenDamage((Object) value);
    }

    @HideFromJS
    public BonusBuilder whenItem(Object value) {
        return optionalRaw("item_condition", value);
    }

    @Info(value = "Configures item condition via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The item condition type id."),
            @Param(name = "consumer", value = "The callback that configures the created item condition builder.")
    })
    public BonusBuilder whenItem(PSTItemConditionId typeId, Consumer<ConditionBuilder> consumer) {
        ConditionBuilder builder = new ConditionBuilder(PSTItemConditionId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return whenItem(builder);
    }

    @Info(value = "Configures item condition through the schema writer for the given item condition id.", params = {
            @Param(name = "typeId", value = "The item condition type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields.")
    })
    public BonusBuilder whenItemSchema(PSTItemConditionId typeId, Consumer<PSTNodeWriter> consumer) {
        return whenItem(node(PSTNodeFamily.ITEM_CONDITION, PSTItemConditionId.parse(typeId).location(), consumer));
    }

    @Info("Adds a prebuilt item condition builder.")
    public BonusBuilder whenItem(ConditionBuilder value) {
        return whenItem((Object) value);
    }

    @HideFromJS
    public BonusBuilder scalePlayer(Object value) {
        return optionalRaw("player_multiplier", value);
    }

    @Info(value = "Configures player multiplier via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The multiplier type id."),
            @Param(name = "consumer", value = "The callback that configures the created player multiplier builder.")
    })
    public BonusBuilder scalePlayer(PSTLivingMultiplierId typeId, Consumer<MultiplierBuilder> consumer) {
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
    public BonusBuilder scalePlayerSchema(PSTLivingMultiplierId typeId, Consumer<PSTNodeWriter> consumer) {
        return scalePlayer(node(PSTNodeFamily.MULTIPLIER, PSTLivingMultiplierId.parse(typeId).location(), consumer));
    }

    @Info("Adds a prebuilt player multiplier builder.")
    public BonusBuilder scalePlayer(MultiplierBuilder value) {
        return scalePlayer((Object) value);
    }

    @HideFromJS
    public BonusBuilder scaleEnemy(Object value) {
        return optionalRaw("enemy_multiplier", value);
    }

    @Info(value = "Configures enemy multiplier via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The multiplier type id."),
            @Param(name = "consumer", value = "The callback that configures the created enemy multiplier builder.")
    })
    public BonusBuilder scaleEnemy(PSTLivingMultiplierId typeId, Consumer<MultiplierBuilder> consumer) {
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
    public BonusBuilder scaleEnemySchema(PSTLivingMultiplierId typeId, Consumer<PSTNodeWriter> consumer) {
        return scaleEnemy(node(PSTNodeFamily.MULTIPLIER, PSTLivingMultiplierId.parse(typeId).location(), consumer));
    }

    @Info("Adds a prebuilt enemy multiplier builder.")
    public BonusBuilder scaleEnemy(MultiplierBuilder value) {
        return scaleEnemy((Object) value);
    }

    @HideFromJS
    public BonusBuilder scaleTarget(Object value) {
        return optionalRaw("target_multiplier", value);
    }

    @Info(value = "Configures target multiplier via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The multiplier type id."),
            @Param(name = "consumer", value = "The callback that configures the created target multiplier builder.")
    })
    public BonusBuilder scaleTarget(PSTLivingMultiplierId typeId, Consumer<MultiplierBuilder> consumer) {
        MultiplierBuilder builder = new MultiplierBuilder(PSTLivingMultiplierId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return scaleTarget(builder);
    }

    @Info(value = "Configures target multiplier through the schema writer for the given multiplier id.", params = {
            @Param(name = "typeId", value = "The multiplier type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields.")
    })
    public BonusBuilder scaleTargetSchema(PSTLivingMultiplierId typeId, Consumer<PSTNodeWriter> consumer) {
        return scaleTarget(node(PSTNodeFamily.MULTIPLIER, PSTLivingMultiplierId.parse(typeId).location(), consumer));
    }

    @Info("Adds a prebuilt target multiplier builder.")
    public BonusBuilder scaleTarget(MultiplierBuilder value) {
        return scaleTarget((Object) value);
    }

    @HideFromJS
    public BonusBuilder scaleAttacker(Object value) {
        return optionalRaw("attacker_multiplier", value);
    }

    @Info(value = "Configures attacker multiplier via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The multiplier type id."),
            @Param(name = "consumer", value = "The callback that configures the created attacker multiplier builder.")
    })
    public BonusBuilder scaleAttacker(PSTLivingMultiplierId typeId, Consumer<MultiplierBuilder> consumer) {
        MultiplierBuilder builder = new MultiplierBuilder(PSTLivingMultiplierId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return scaleAttacker(builder);
    }

    @Info(value = "Configures attacker multiplier through the schema writer for the given multiplier id.", params = {
            @Param(name = "typeId", value = "The multiplier type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields.")
    })
    public BonusBuilder scaleAttackerSchema(PSTLivingMultiplierId typeId, Consumer<PSTNodeWriter> consumer) {
        return scaleAttacker(node(PSTNodeFamily.MULTIPLIER, PSTLivingMultiplierId.parse(typeId).location(), consumer));
    }

    @Info("Adds a prebuilt attacker multiplier builder.")
    public BonusBuilder scaleAttacker(MultiplierBuilder value) {
        return scaleAttacker((Object) value);
    }

    @HideFromJS
    public BonusBuilder eventListener(Object value) {
        return optionalRaw("event_listener", value);
    }

    @Info(value = "Configures event listener via schema-backed type id.", params = {
            @Param(name = "typeId", value = "The event listener type id."),
            @Param(name = "consumer", value = "The callback that configures the created event listener builder.")
    })
    public BonusBuilder eventListener(PSTEventListenerId typeId, Consumer<ListenerBuilder> consumer) {
        ListenerBuilder builder = new ListenerBuilder(PSTEventListenerId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return eventListener(builder);
    }

    @Info(value = "Configures event listener through the schema writer for the given event listener id.", params = {
            @Param(name = "typeId", value = "The event listener type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields.")
    })
    public BonusBuilder eventListenerSchema(PSTEventListenerId typeId, Consumer<PSTNodeWriter> consumer) {
        return eventListener(node(PSTNodeFamily.EVENT_LISTENER, PSTEventListenerId.parse(typeId).location(), consumer));
    }

    @Info("Adds a prebuilt event listener builder.")
    public BonusBuilder eventListener(ListenerBuilder value) {
        return eventListener((Object) value);
    }

    @HideFromJS
    public BonusBuilder onAttack() {
        return onAttack("enemy");
    }

    @HideFromJS
    public BonusBuilder onAttack(String target) {
        return onAttack(PSTSkillTarget.parse(target));
    }

    @Info(value = "Adds the built-in attack event listener targeting the selected side.", params = {
            @Param(name = "target", value = "The side that receives the attack-triggered bonus.")
    })
    public BonusBuilder onAttack(PSTSkillTarget target) {
        ListenerBuilder listener = new ListenerBuilder(ATTACK_LISTENER_TYPE)
                .target(target)
                .whenPlayer(new ConditionBuilder(NONE_TYPE))
                .whenEnemy(new ConditionBuilder(NONE_TYPE))
                .whenDamage(new ConditionBuilder(NONE_TYPE))
                .scalePlayer(new MultiplierBuilder(NONE_TYPE))
                .scaleEnemy(new MultiplierBuilder(NONE_TYPE));
        return eventListener(listener);
    }

    @HideFromJS
    public BonusBuilder onTick(Number cooldown) {
        ListenerBuilder listener = new ListenerBuilder(TICKING_LISTENER_TYPE)
                .cooldown(cooldown)
                .whenPlayer(new ConditionBuilder(NONE_TYPE))
                .scalePlayer(new MultiplierBuilder(NONE_TYPE));
        return eventListener(listener);
    }

    private static PSTNodeWriter node(PSTNodeFamily family, net.minecraft.resources.ResourceLocation typeId, Consumer<PSTNodeWriter> consumer) {
        PSTNodeWriter writer = new PSTNodeWriter(family, typeId);
        if (consumer != null) {
            consumer.accept(writer);
        }
        return writer;
    }
}
