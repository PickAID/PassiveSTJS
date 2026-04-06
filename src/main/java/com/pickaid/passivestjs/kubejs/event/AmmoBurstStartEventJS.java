package com.pickaid.passivestjs.kubejs.event;

import com.pickaid.passiveintegration.events.ammoburst.AmmoBurstStartEvent;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.server.level.ServerPlayer;

public class AmmoBurstStartEventJS extends PlayerEventJS {
    private final AmmoBurstStartEvent event;

    public AmmoBurstStartEventJS(AmmoBurstStartEvent event) {
        this.event = event;
    }

    @Override
    public ServerPlayer getEntity() {
        return event.getPlayer();
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
