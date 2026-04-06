package com.pickaid.passivestjs.kubejs.content;

import com.pickaid.passivestjs.kubejs.builder.ConditionBuilder;

public final class Conditions {
    public static final Conditions INSTANCE = new Conditions();

    private static final String GUN_DAMAGE_TYPE = "passiveintegration:gun_damage";

    private Conditions() {
    }

    public ConditionBuilder none() {
        return new ConditionBuilder("skilltree:none");
    }

    public ConditionBuilder gunDamage() {
        return new ConditionBuilder(GUN_DAMAGE_TYPE);
    }

    public ConditionBuilder projectileDamage() {
        return new ConditionBuilder("skilltree:projectile");
    }

    public ConditionBuilder magicDamage() {
        return new ConditionBuilder("skilltree:magic");
    }

    public ConditionBuilder hasEffect(String effectId) {
        return new ConditionBuilder("skilltree:has_effect").effect(effectId);
    }

    public ConditionBuilder crouching() {
        return new ConditionBuilder("skilltree:crouching");
    }

    public ConditionBuilder underwater() {
        return new ConditionBuilder("skilltree:underwater");
    }

    public ConditionBuilder unarmed() {
        return new ConditionBuilder("skilltree:unarmed");
    }

    public ConditionBuilder hasItemEquipped(Object itemCondition) {
        return new ConditionBuilder("skilltree:has_item_equipped").itemCondition(itemCondition);
    }

    public ConditionBuilder hasItemInHand(Object itemCondition) {
        return new ConditionBuilder("skilltree:has_item_in_hand").itemCondition(itemCondition);
    }

    public ConditionBuilder itemTag(String tagId) {
        return new ConditionBuilder("skilltree:tag").tagId(tagId);
    }

    public ConditionBuilder itemId(String itemId) {
        return new ConditionBuilder("skilltree:item_id").itemId(itemId);
    }

    public ConditionBuilder equipmentType(String equipmentType) {
        return new ConditionBuilder("skilltree:equipment_type").equipmentType(equipmentType);
    }

    public ConditionBuilder foodItem() {
        return new ConditionBuilder("skilltree:food");
    }

    public ConditionBuilder potionItem(String potionType) {
        return new ConditionBuilder("skilltree:potion").potionType(potionType);
    }

    public ConditionBuilder numericValue() {
        return new ConditionBuilder("skilltree:numeric_value");
    }

    public ConditionBuilder numericValue(Object valueProvider, Number requiredValue, String logic) {
        return numericValue()
                .valueProvider(valueProvider)
                .requiredValue(requiredValue)
                .logic(logic);
    }
}
