package com.pickaid.passivestjs.kubejs.builder;

import com.pickaid.passivestjs.kubejs.content.Listeners;

public class BonusBuilder
        extends TypedJsonBuilder<BonusBuilder> {
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

    public BonusBuilder target(String value) {
        return string("target", value);
    }

    public BonusBuilder effect(String value) {
        return string("effect", value);
    }

    public BonusBuilder effectType(String value) {
        return string("effect_type", value);
    }

    public BonusBuilder lootType(String value) {
        return string("loot_type", value);
    }

    public BonusBuilder experienceSource(String value) {
        return string("experience_source", value);
    }

    public BonusBuilder attribute(String value) {
        return string("attribute", value);
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

    public BonusBuilder whenPlayer(Object value) {
        return optionalRaw("player_condition", value);
    }

    public BonusBuilder whenEnemy(Object value) {
        return optionalRaw("enemy_condition", value);
    }

    public BonusBuilder whenTarget(Object value) {
        return optionalRaw("target_condition", value);
    }

    public BonusBuilder whenAttacker(Object value) {
        return optionalRaw("attacker_condition", value);
    }

    public BonusBuilder whenDamage(Object value) {
        return optionalRaw("damage_condition", value);
    }

    public BonusBuilder whenItem(Object value) {
        return optionalRaw("item_condition", value);
    }

    public BonusBuilder scalePlayer(Object value) {
        return optionalRaw("player_multiplier", value);
    }

    public BonusBuilder scaleEnemy(Object value) {
        return optionalRaw("enemy_multiplier", value);
    }

    public BonusBuilder scaleTarget(Object value) {
        return optionalRaw("target_multiplier", value);
    }

    public BonusBuilder scaleAttacker(Object value) {
        return optionalRaw("attacker_multiplier", value);
    }

    public BonusBuilder eventListener(Object value) {
        return optionalRaw("event_listener", value);
    }

    public BonusBuilder onAttack() {
        return eventListener(Listeners.INSTANCE.attack());
    }

    public BonusBuilder onAttack(String target) {
        return eventListener(Listeners.INSTANCE.attack(target));
    }

    public BonusBuilder onTick(Number cooldown) {
        return eventListener(Listeners.INSTANCE.ticking(cooldown));
    }
}
