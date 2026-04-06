package com.pickaid.passivestjs.kubejs.event;

import com.pickaid.passiveintegration.events.ammoburst.AmmoBurstSustainEvent;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.server.level.ServerPlayer;

public class AmmoBurstSustainEventJS extends PlayerEventJS {
    private final AmmoBurstSustainEvent event;

    public AmmoBurstSustainEventJS(AmmoBurstSustainEvent event) {
        this.event = event;
    }

    @Override
    public ServerPlayer getEntity() {
        return event.getPlayer();
    }

    public String getSourceEndReason() {
        return event.getSourceEndReason().name();
    }

    public float getActivationCost() {
        return event.getActivationCost();
    }

    public float getDrainPerSecond() {
        return event.getDrainPerSecond();
    }

    public float getDrainThisStep() {
        return event.getDrainThisStep();
    }

    public int getRunIndex() {
        return event.getRunIndex();
    }

    public int getSustainAgeTicks() {
        return event.getSustainAgeTicks();
    }

    public void continueSustain() {
        event.continueSustain();
    }

    public void terminate() {
        event.terminate();
    }

    public void exitWithRefund(float amount) {
        event.exitWithRefund(amount);
    }
}
