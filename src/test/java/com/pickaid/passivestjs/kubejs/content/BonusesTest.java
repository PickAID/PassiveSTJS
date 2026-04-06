package com.pickaid.passivestjs.kubejs.content;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BonusesTest {
    @Test
    void ammoBurstUnlockUsesSkillTreeAttributeBonus() {
        JsonObject json = (JsonObject) Bonuses.INSTANCE.gunAmmoFreeUnlock();

        assertEquals("skilltree:attribute", json.get("type").getAsString());
        assertEquals("passiveintegration:ammo_burst_unlocked", json.get("attribute").getAsString());
        assertEquals(1.0D, json.get("amount").getAsDouble());
        assertEquals(0, json.get("operation").getAsInt());
    }

    @Test
    void ammoBurstMaxEnergyUsesManagedAttributeBonus() {
        JsonObject json = Bonuses.INSTANCE.gunAmmoBurstMaxEnergy(0.5D);

        assertEquals("skilltree:attribute", json.get("type").getAsString());
        assertEquals("passiveintegration:ammo_burst_max_energy_multiplier", json.get("attribute").getAsString());
        assertEquals(0.5D, json.get("amount").getAsDouble());
    }

    @Test
    void ammoBurstEnergyRegenUsesManagedAttributeBonus() {
        JsonObject json = Bonuses.INSTANCE.gunAmmoBurstEnergyRegen(0.25D);

        assertEquals("skilltree:attribute", json.get("type").getAsString());
        assertEquals("passiveintegration:ammo_burst_energy_regen_multiplier", json.get("attribute").getAsString());
        assertEquals(0.25D, json.get("amount").getAsDouble());
    }

    @Test
    void ammoBurstDrainReductionLowersDrainMultiplierAttribute() {
        JsonObject json = Bonuses.INSTANCE.gunAmmoBurstDrainReduction(0.2D);

        assertEquals("skilltree:attribute", json.get("type").getAsString());
        assertEquals("passiveintegration:ammo_burst_drain_multiplier", json.get("attribute").getAsString());
        assertEquals(-0.2D, json.get("amount").getAsDouble());
    }

    @Test
    void taczGunUnlockProducesTypedTargetedJson() {
        JsonObject json = Bonuses.INSTANCE.taczGunUnlock("tacz:ai_awm");

        assertEquals("passiveintegration:tacz_unlock", json.get("type").getAsString());
        assertEquals("gun", json.get("content_type").getAsString());
        assertEquals("tacz:ai_awm", json.get("content_id").getAsString());
    }

    @Test
    void taczAttachmentUnlockProducesTypedTargetedJson() {
        JsonObject json = Bonuses.INSTANCE.taczAttachmentUnlock("tacz:acog_4x");

        assertEquals("passiveintegration:tacz_unlock", json.get("type").getAsString());
        assertEquals("attachment", json.get("content_type").getAsString());
        assertEquals("tacz:acog_4x", json.get("content_id").getAsString());
    }
}
