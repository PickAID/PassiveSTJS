package com.pickaid.passivestjs.kubejs.event;

import com.pickaid.passiveintegration.events.ammoburst.AmmoBurstAboutToEndEvent;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.server.level.ServerPlayer;

public class AmmoBurstAboutToEndEventJS extends PlayerEventJS {
    private final AmmoBurstAboutToEndEvent event;

    public AmmoBurstAboutToEndEventJS(AmmoBurstAboutToEndEvent event) {
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

    public float getDrainThisStep() {
        return event.getDrainThisStep();
    }

    public void endNow() {
        event.endNow();
    }

    public void refundAndContinue(float amount) {
        event.refundAndContinue(amount);
    }

    public void enterZeroSustain(int startDelayTicks, int intervalTicks) {
        event.enterZeroSustain(startDelayTicks, intervalTicks);
    }
}
