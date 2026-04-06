package com.pickaid.passivestjs.kubejs.content;

import com.pickaid.passivestjs.kubejs.builder.ListenerBuilder;

public final class Listeners {
    public static final Listeners INSTANCE = new Listeners();

    private Listeners() {
    }

    public ListenerBuilder attack() {
        return attack("enemy");
    }

    public ListenerBuilder attack(String target) {
        return new ListenerBuilder("skilltree:attack")
                .target(target)
                .whenPlayer(Conditions.INSTANCE.none())
                .whenEnemy(Conditions.INSTANCE.none())
                .whenDamage(Conditions.INSTANCE.none())
                .scalePlayer(Multipliers.INSTANCE.none())
                .scaleEnemy(Multipliers.INSTANCE.none());
    }

    public ListenerBuilder ticking() {
        return ticking(20);
    }

    public ListenerBuilder ticking(Number cooldown) {
        return new ListenerBuilder("skilltree:ticking")
                .cooldown(cooldown)
                .whenPlayer(Conditions.INSTANCE.none())
                .scalePlayer(Multipliers.INSTANCE.none());
    }
}
