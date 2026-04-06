package com.pickaid.passivestjs.kubejs.event;

import com.pickaid.passiveintegration.events.ammoburst.AmmoBurstEndEvent;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.server.level.ServerPlayer;

public class AmmoBurstEndEventJS extends PlayerEventJS {
    private final AmmoBurstEndEvent event;

    public AmmoBurstEndEventJS(AmmoBurstEndEvent event) {
        this.event = event;
    }

    @Override
    public ServerPlayer getEntity() {
        return event.getPlayer();
    }

    public String getFinalReason() {
        return event.getFinalReason().name();
    }

    public String getSourceReason() {
        return event.getSourceReason().name();
    }

    public boolean isPassedThroughZeroSustain() {
        return event.isPassedThroughZeroSustain();
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
