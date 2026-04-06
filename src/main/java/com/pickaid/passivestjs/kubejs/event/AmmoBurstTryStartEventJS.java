package com.pickaid.passivestjs.kubejs.event;

import com.pickaid.passiveintegration.events.ammoburst.AmmoBurstTryStartEvent;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.server.level.ServerPlayer;

public class AmmoBurstTryStartEventJS extends PlayerEventJS {
    private final AmmoBurstTryStartEvent event;

    public AmmoBurstTryStartEventJS(AmmoBurstTryStartEvent event) {
        this.event = event;
    }

    @Override
    public ServerPlayer getEntity() {
        return event.getPlayer();
    }

    public Object cancel() {
        event.setCanceled(true);
        return null;
    }

    public float getCurrentEnergy() {
        return event.getCurrentEnergy();
    }

    public void setCurrentEnergy(float value) {
        event.setCurrentEnergy(value);
    }

    public float getMaxEnergy() {
        return event.getMaxEnergy();
    }

    public void setMaxEnergy(float value) {
        event.setMaxEnergy(value);
    }

    public float getRegenPerSecond() {
        return event.getRegenPerSecond();
    }

    public void setRegenPerSecond(float value) {
        event.setRegenPerSecond(value);
    }

    public float getDrainPerSecond() {
        return event.getDrainPerSecond();
    }

    public void setDrainPerSecond(float value) {
        event.setDrainPerSecond(value);
    }

    public float getActivationCost() {
        return event.getActivationCost();
    }

    public void setActivationCost(float value) {
        event.setActivationCost(value);
    }

    public void setIgnoreUnlockRequirement(boolean value) {
        event.setIgnoreUnlockRequirement(value);
    }

    public void setIgnoreSupportedGunRequirement(boolean value) {
        event.setIgnoreSupportedGunRequirement(value);
    }
}
