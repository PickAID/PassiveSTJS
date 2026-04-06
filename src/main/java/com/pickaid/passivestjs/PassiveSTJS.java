package com.pickaid.passivestjs;

import com.pickaid.passivestjs.compat.passiveintegration.AmmoBurstBridge;
import com.pickaid.passivestjs.compat.passiveintegration.PassiveIntegrationCompat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(PassiveSTJS.MOD_ID)
public final class PassiveSTJS {
    public static final String MOD_ID = "passivestjs";

    public PassiveSTJS() {
        if (PassiveIntegrationCompat.isLoaded()) {
            MinecraftForge.EVENT_BUS.register(new AmmoBurstBridge());
        }
    }
}
