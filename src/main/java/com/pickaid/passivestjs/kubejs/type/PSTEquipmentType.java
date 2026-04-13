package com.pickaid.passivestjs.kubejs.type;

import dev.latvian.mods.rhino.Wrapper;

import java.util.Arrays;
import java.util.Locale;

public enum PSTEquipmentType {
    ANY("any"),
    HELMET("helmet"),
    CHESTPLATE("chestplate"),
    LEGGINGS("leggings"),
    BOOTS("boots"),
    ARMOR("armor"),
    SHIELD("shield"),
    WEAPON("weapon"),
    SWORD("sword"),
    AXE("axe"),
    TRIDENT("trident"),
    MELEE_WEAPON("melee_weapon"),
    BOW("bow"),
    CROSSBOW("crossbow"),
    RANGED_WEAPON("ranged_weapon"),
    PICKAXE("pickaxe"),
    HOE("hoe"),
    SHOVEL("shovel"),
    TOOL("tool");

    private final String id;

    PSTEquipmentType(String id) {
        this.id = id;
    }

    public static PSTEquipmentType parse(Object value) {
        if (value instanceof PSTEquipmentType equipmentType) {
            return equipmentType;
        }

        Object unwrapped = Wrapper.unwrapped(value);
        if (unwrapped instanceof CharSequence charSequence) {
            String normalized = charSequence.toString().trim().toLowerCase(Locale.ROOT);
            return Arrays.stream(values())
                    .filter(equipmentType -> equipmentType.id.equals(normalized))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown equipment type: " + charSequence));
        }

        throw new IllegalArgumentException("Unsupported equipment type: " + unwrapped);
    }

    public String id() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }
}
