package com.pickaid.passivestjs.kubejs.builder;

public class ListenerBuilder
        extends TypedJsonBuilder<ListenerBuilder> {
    public ListenerBuilder(String type) {
        super(type);
    }

    public ListenerBuilder cooldown(Number value) {
        return integer("cooldown", value);
    }

    public ListenerBuilder target(String value) {
        return string("target", value);
    }

    public ListenerBuilder whenPlayer(Object value) {
        return optionalRaw("player_condition", value);
    }

    public ListenerBuilder whenEnemy(Object value) {
        return optionalRaw("enemy_condition", value);
    }

    public ListenerBuilder whenDamage(Object value) {
        return optionalRaw("damage_condition", value);
    }

    public ListenerBuilder scalePlayer(Object value) {
        return optionalRaw("player_multiplier", value);
    }

    public ListenerBuilder scaleEnemy(Object value) {
        return optionalRaw("enemy_multiplier", value);
    }
}
