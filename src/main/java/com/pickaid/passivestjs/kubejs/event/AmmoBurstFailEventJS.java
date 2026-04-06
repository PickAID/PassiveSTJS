package com.pickaid.passivestjs.kubejs.event;

import com.pickaid.passiveintegration.events.ammoburst.AmmoBurstFailEvent;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.server.level.ServerPlayer;

public class AmmoBurstFailEventJS extends PlayerEventJS {
    private final AmmoBurstFailEvent event;

    public AmmoBurstFailEventJS(AmmoBurstFailEvent event) {
        this.event = event;
    }

    @Override
    public ServerPlayer getEntity() {
        return event.getPlayer();
    }

    public String getReason() {
        return event.getReason().name();
    }

    public float getCurrentEnergy() {
        return event.getCurrentEnergy();
    }

    public float getMaxEnergy() {
        return event.getMaxEnergy();
    }

    public float getRegenPerSecond() {
        return event.getRegenPerSecond();
    }

    public float getDrainPerSecond() {
        return event.getDrainPerSecond();
    }

    public float getActivationCost() {
        return event.getActivationCost();
    }
}
