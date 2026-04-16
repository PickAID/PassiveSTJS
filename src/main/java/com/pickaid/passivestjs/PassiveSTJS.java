package com.pickaid.passivestjs;

import com.pickaid.passivestjs.config.PassiveSTJSCommonConfig;
import com.pickaid.passivestjs.kubejs.probe.PassiveSTJSLegacyProbeCompat;
import com.pickaid.passivestjs.kubejs.reload.SkillTreeContentReloadListenerBridge;
import com.pickaid.passivestjs.runtime.PSTItemBonusRuntimeBridge;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

@Mod(PassiveSTJS.MOD_ID)
public final class PassiveSTJS {
    public static final String MOD_ID = "passivestjs";

    public PassiveSTJS() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PassiveSTJSCommonConfig.SPEC, "passivestjs-common.toml");
        if (ModList.get().isLoaded("probejs_legacy")) {
            PassiveSTJSLegacyProbeCompat.install();
        }
        SkillTreeContentReloadListenerBridge.register();
        MinecraftForge.EVENT_BUS.register(new PSTItemBonusRuntimeBridge());
    }
}
