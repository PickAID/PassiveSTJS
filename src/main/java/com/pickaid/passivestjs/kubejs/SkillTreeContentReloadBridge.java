package com.pickaid.passivestjs.kubejs;

import com.pickaid.passivestjs.kubejs.event.SkillTreeContentEventJS;
import dev.latvian.mods.kubejs.script.data.VirtualKubeJSDataPack;
import net.minecraft.server.packs.resources.MultiPackResourceManager;

public final class SkillTreeContentReloadBridge {
    private SkillTreeContentReloadBridge() {
    }

    public static void postDuringServerReload(
            VirtualKubeJSDataPack virtualDataPack,
            MultiPackResourceManager wrappedManager
    ) {
        PassiveSTJSKubeEvents.postSkillTreeContent(
                new SkillTreeContentEventJS(virtualDataPack, wrappedManager)
        );
    }
}
