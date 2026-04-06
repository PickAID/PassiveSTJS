package com.pickaid.passivestjs.compat.passiveintegration;

import net.minecraftforge.fml.ModList;

public final class PassiveIntegrationCompat {
    public static final String MOD_ID = "passiveintegration";

    private PassiveIntegrationCompat() {
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(MOD_ID);
    }
}
