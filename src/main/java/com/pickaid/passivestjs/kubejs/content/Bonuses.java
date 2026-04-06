package com.pickaid.passivestjs.kubejs.content;

import com.google.gson.JsonObject;
import com.pickaid.passivestjs.kubejs.builder.BonusBuilder;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class Bonuses {
    public static final Bonuses INSTANCE = new Bonuses();

    private static final String ATTRIBUTE_BONUS_TYPE = "skilltree:attribute";
    private static final int ADDITION_OPERATION = 0;
    private static final String PASSIVE_INTEGRATION_NAMESPACE = "passiveintegration";
    private static final String GUN_AMMO_FREE_UNLOCK_TYPE = PASSIVE_INTEGRATION_NAMESPACE + ":gun_ammo_free_unlock";
    private static final String TACZ_UNLOCK_TYPE = PASSIVE_INTEGRATION_NAMESPACE + ":tacz_unlock";
    private static final String AMMO_BURST_UNLOCK_ATTRIBUTE = PASSIVE_INTEGRATION_NAMESPACE + ":ammo_burst_unlocked";
    private static final String AMMO_BURST_MAX_ENERGY_ATTRIBUTE =
            PASSIVE_INTEGRATION_NAMESPACE + ":ammo_burst_max_energy_multiplier";
    private static final String AMMO_BURST_REGEN_ATTRIBUTE =
            PASSIVE_INTEGRATION_NAMESPACE + ":ammo_burst_energy_regen_multiplier";
    private static final String AMMO_BURST_DRAIN_ATTRIBUTE =
            PASSIVE_INTEGRATION_NAMESPACE + ":ammo_burst_drain_multiplier";
    private static final String AMMO_BURST_ACTIVATION_COST_ATTRIBUTE =
            PASSIVE_INTEGRATION_NAMESPACE + ":ammo_burst_activation_cost_multiplier";

    private Bonuses() {
    }

    public BonusBuilder damage() {
        return new BonusBuilder("skilltree:damage")
                .scalePlayer(Multipliers.INSTANCE.none())
                .scaleEnemy(Multipliers.INSTANCE.none())
                .whenPlayer(Conditions.INSTANCE.none())
                .whenDamage(Conditions.INSTANCE.none())
                .whenTarget(Conditions.INSTANCE.none());
    }

    public BonusBuilder critChance() {
        return new BonusBuilder("skilltree:crit_chance")
                .scalePlayer(Multipliers.INSTANCE.none())
                .scaleEnemy(Multipliers.INSTANCE.none())
                .whenPlayer(Conditions.INSTANCE.none())
                .whenDamage(Conditions.INSTANCE.none())
                .whenTarget(Conditions.INSTANCE.none());
    }

    public BonusBuilder critDamage() {
        return new BonusBuilder("skilltree:crit_damage")
                .scalePlayer(Multipliers.INSTANCE.none())
                .scaleEnemy(Multipliers.INSTANCE.none())
                .whenPlayer(Conditions.INSTANCE.none())
                .whenDamage(Conditions.INSTANCE.none())
                .whenTarget(Conditions.INSTANCE.none());
    }

    public BonusBuilder damageTaken() {
        return new BonusBuilder("skilltree:damage_taken")
                .scalePlayer(Multipliers.INSTANCE.none())
                .scaleAttacker(Multipliers.INSTANCE.none())
                .whenPlayer(Conditions.INSTANCE.none())
                .whenDamage(Conditions.INSTANCE.none())
                .whenAttacker(Conditions.INSTANCE.none());
    }

    public BonusBuilder damageAvoidance() {
        return new BonusBuilder("skilltree:damage_avoidance")
                .scalePlayer(Multipliers.INSTANCE.none())
                .scaleAttacker(Multipliers.INSTANCE.none())
                .whenPlayer(Conditions.INSTANCE.none())
                .whenDamage(Conditions.INSTANCE.none())
                .whenAttacker(Conditions.INSTANCE.none());
    }

    public BonusBuilder effectDuration() {
        return new BonusBuilder("skilltree:effect_duration")
                .scalePlayer(Multipliers.INSTANCE.none())
                .scaleEnemy(Multipliers.INSTANCE.none())
                .whenPlayer(Conditions.INSTANCE.none())
                .whenEnemy(Conditions.INSTANCE.none());
    }

    public BonusBuilder inflictEffect() {
        return new BonusBuilder("skilltree:inflict_effect")
                .eventListener(Listeners.INSTANCE.attack());
    }

    public BonusBuilder projectileSpeed() {
        return new BonusBuilder("skilltree:projectile_speed")
                .scalePlayer(Multipliers.INSTANCE.none())
                .whenPlayer(Conditions.INSTANCE.none());
    }

    public BonusBuilder projectileDuplication() {
        return new BonusBuilder("skilltree:projectile_duplication")
                .whenPlayer(Conditions.INSTANCE.none());
    }

    public BonusBuilder itemUsageSpeed() {
        return new BonusBuilder("skilltree:item_usage_speed")
                .scalePlayer(Multipliers.INSTANCE.none())
                .whenPlayer(Conditions.INSTANCE.none())
                .whenItem(Conditions.INSTANCE.none());
    }

    public BonusBuilder itemUseMovementSpeed() {
        return new BonusBuilder("skilltree:item_use_movement_speed")
                .scalePlayer(Multipliers.INSTANCE.none())
                .whenPlayer(Conditions.INSTANCE.none())
                .whenItem(Conditions.INSTANCE.none());
    }

    public BonusBuilder incomingHealing() {
        return new BonusBuilder("skilltree:incoming_healing")
                .scalePlayer(Multipliers.INSTANCE.none())
                .whenPlayer(Conditions.INSTANCE.none());
    }

    public BonusBuilder arrowRetrieval() {
        return new BonusBuilder("skilltree:arrow_retrieval");
    }

    public BonusBuilder gunAmmoFreeUnlockBuilder() {
        return createAttributeBonusBuilder(
                AMMO_BURST_UNLOCK_ATTRIBUTE,
                "ammo_burst_unlock",
                "Ammo Burst Unlock",
                1.0D
        );
    }

    public Object gunAmmoFreeUnlock() {
        return gunAmmoFreeUnlockBuilder().json();
    }

    public BonusBuilder gunAmmoBurstMaxEnergy() {
        return createAttributeBonusBuilder(
                AMMO_BURST_MAX_ENERGY_ATTRIBUTE,
                "ammo_burst_max_energy",
                "Ammo Burst Max Energy",
                null
        );
    }

    public JsonObject gunAmmoBurstMaxEnergy(double amount) {
        return createScaledPlayerAttributeBonus(
                AMMO_BURST_MAX_ENERGY_ATTRIBUTE,
                "ammo_burst_max_energy",
                "Ammo Burst Max Energy",
                amount,
                null,
                null
        );
    }

    public JsonObject gunAmmoBurstMaxEnergy(double amount, Object playerCondition) {
        return createScaledPlayerAttributeBonus(
                AMMO_BURST_MAX_ENERGY_ATTRIBUTE,
                "ammo_burst_max_energy",
                "Ammo Burst Max Energy",
                amount,
                playerCondition,
                null
        );
    }

    public JsonObject gunAmmoBurstMaxEnergy(double amount, Object playerCondition, Object playerMultiplier) {
        return createScaledPlayerAttributeBonus(
                AMMO_BURST_MAX_ENERGY_ATTRIBUTE,
                "ammo_burst_max_energy",
                "Ammo Burst Max Energy",
                amount,
                playerCondition,
                playerMultiplier
        );
    }

    public BonusBuilder gunAmmoFreeDuration() {
        return gunAmmoBurstMaxEnergy();
    }

    public JsonObject gunAmmoFreeDuration(double amount) {
        return gunAmmoBurstMaxEnergy(amount);
    }

    public JsonObject gunAmmoFreeDuration(double amount, Object playerCondition) {
        return gunAmmoBurstMaxEnergy(amount, playerCondition);
    }

    public JsonObject gunAmmoFreeDuration(double amount, Object playerCondition, Object playerMultiplier) {
        return gunAmmoBurstMaxEnergy(amount, playerCondition, playerMultiplier);
    }

    public BonusBuilder gunAmmoBurstEnergyRegen() {
        return createAttributeBonusBuilder(
                AMMO_BURST_REGEN_ATTRIBUTE,
                "ammo_burst_energy_regen",
                "Ammo Burst Energy Regen",
                null
        );
    }

    public JsonObject gunAmmoBurstEnergyRegen(double amount) {
        return createScaledPlayerAttributeBonus(
                AMMO_BURST_REGEN_ATTRIBUTE,
                "ammo_burst_energy_regen",
                "Ammo Burst Energy Regen",
                amount,
                null,
                null
        );
    }

    public JsonObject gunAmmoBurstEnergyRegen(double amount, Object playerCondition) {
        return createScaledPlayerAttributeBonus(
                AMMO_BURST_REGEN_ATTRIBUTE,
                "ammo_burst_energy_regen",
                "Ammo Burst Energy Regen",
                amount,
                playerCondition,
                null
        );
    }

    public JsonObject gunAmmoBurstEnergyRegen(double amount, Object playerCondition, Object playerMultiplier) {
        return createScaledPlayerAttributeBonus(
                AMMO_BURST_REGEN_ATTRIBUTE,
                "ammo_burst_energy_regen",
                "Ammo Burst Energy Regen",
                amount,
                playerCondition,
                playerMultiplier
        );
    }

    public BonusBuilder gunAmmoBurstDrainReduction() {
        return createAttributeBonusBuilder(
                AMMO_BURST_DRAIN_ATTRIBUTE,
                "ammo_burst_drain",
                "Ammo Burst Drain",
                null
        );
    }

    public JsonObject gunAmmoBurstDrainReduction(double amount) {
        return createScaledPlayerAttributeBonus(
                AMMO_BURST_DRAIN_ATTRIBUTE,
                "ammo_burst_drain",
                "Ammo Burst Drain",
                -amount,
                null,
                null
        );
    }

    public JsonObject gunAmmoBurstDrainReduction(double amount, Object playerCondition) {
        return createScaledPlayerAttributeBonus(
                AMMO_BURST_DRAIN_ATTRIBUTE,
                "ammo_burst_drain",
                "Ammo Burst Drain",
                -amount,
                playerCondition,
                null
        );
    }

    public JsonObject gunAmmoBurstDrainReduction(double amount, Object playerCondition, Object playerMultiplier) {
        return createScaledPlayerAttributeBonus(
                AMMO_BURST_DRAIN_ATTRIBUTE,
                "ammo_burst_drain",
                "Ammo Burst Drain",
                -amount,
                playerCondition,
                playerMultiplier
        );
    }

    public BonusBuilder gunAmmoBurstActivationCostMultiplier() {
        return createAttributeBonusBuilder(
                AMMO_BURST_ACTIVATION_COST_ATTRIBUTE,
                "ammo_burst_activation_cost",
                "Ammo Burst Activation Cost",
                null
        );
    }

    public JsonObject gunAmmoBurstActivationCostMultiplier(double amount) {
        return createScaledPlayerAttributeBonus(
                AMMO_BURST_ACTIVATION_COST_ATTRIBUTE,
                "ammo_burst_activation_cost",
                "Ammo Burst Activation Cost",
                amount,
                null,
                null
        );
    }

    public JsonObject gunAmmoBurstActivationCostMultiplier(double amount, Object playerCondition) {
        return createScaledPlayerAttributeBonus(
                AMMO_BURST_ACTIVATION_COST_ATTRIBUTE,
                "ammo_burst_activation_cost",
                "Ammo Burst Activation Cost",
                amount,
                playerCondition,
                null
        );
    }

    public JsonObject gunAmmoBurstActivationCostMultiplier(
            double amount,
            Object playerCondition,
            Object playerMultiplier
    ) {
        return createScaledPlayerAttributeBonus(
                AMMO_BURST_ACTIVATION_COST_ATTRIBUTE,
                "ammo_burst_activation_cost",
                "Ammo Burst Activation Cost",
                amount,
                playerCondition,
                playerMultiplier
        );
    }

    public BonusBuilder gunAmmoFreeCooldownReduction() {
        return gunAmmoBurstEnergyRegen();
    }

    public JsonObject gunAmmoFreeCooldownReduction(double amount) {
        return gunAmmoBurstEnergyRegen(amount);
    }

    public JsonObject gunAmmoFreeCooldownReduction(double amount, Object playerCondition) {
        return gunAmmoBurstEnergyRegen(amount, playerCondition);
    }

    public JsonObject gunAmmoFreeCooldownReduction(double amount, Object playerCondition, Object playerMultiplier) {
        return gunAmmoBurstEnergyRegen(amount, playerCondition, playerMultiplier);
    }

    public BonusBuilder gunAmmoFreeDrainReduction() {
        return gunAmmoBurstDrainReduction();
    }

    public JsonObject gunAmmoFreeDrainReduction(double amount) {
        return gunAmmoBurstDrainReduction(amount);
    }

    public JsonObject gunAmmoFreeDrainReduction(double amount, Object playerCondition) {
        return gunAmmoBurstDrainReduction(amount, playerCondition);
    }

    public JsonObject gunAmmoFreeDrainReduction(double amount, Object playerCondition, Object playerMultiplier) {
        return gunAmmoBurstDrainReduction(amount, playerCondition, playerMultiplier);
    }

    public BonusBuilder taczUnlock() {
        return new BonusBuilder(TACZ_UNLOCK_TYPE);
    }

    public BonusBuilder taczGunUnlock() {
        return taczUnlock().contentType("gun");
    }

    public JsonObject taczGunUnlock(String id) {
        return taczGunUnlock().contentId(id).json();
    }

    public BonusBuilder taczAttachmentUnlock() {
        return taczUnlock().contentType("attachment");
    }

    public JsonObject taczAttachmentUnlock(String id) {
        return taczAttachmentUnlock().contentId(id).json();
    }

    public BonusBuilder taczAccessoryUnlock() {
        return taczAttachmentUnlock();
    }

    public JsonObject taczAccessoryUnlock(String id) {
        return taczAttachmentUnlock(id);
    }

    private static JsonObject createScaledPlayerAttributeBonus(
            String attributeId,
            String modifierKey,
            String modifierName,
            double amount,
            Object playerCondition,
            Object playerMultiplier
    ) {
        return createAttributeBonusBuilder(attributeId, modifierKey, modifierName, amount)
                .modifierId(stableModifierId(
                        attributeId
                                + ":"
                                + modifierKey
                                + ":"
                                + String.valueOf(JsonHelper.optionalElement(playerCondition))
                                + ":"
                                + String.valueOf(JsonHelper.optionalElement(playerMultiplier))
                ))
                .whenPlayer(playerCondition)
                .scalePlayer(playerMultiplier)
                .json();
    }

    private static BonusBuilder createAttributeBonusBuilder(
            String attributeId,
            String modifierKey,
            String modifierName,
            Double amount
    ) {
        BonusBuilder builder = new BonusBuilder(ATTRIBUTE_BONUS_TYPE)
                .attribute(attributeId)
                .modifierId(stableModifierId(attributeId + ":" + modifierKey))
                .bonusName(modifierName)
                .operation(ADDITION_OPERATION);
        if (amount != null) {
            builder.amount(amount);
        }
        return builder;
    }

    private static String stableModifierId(String seed) {
        return UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8)).toString();
    }
}
