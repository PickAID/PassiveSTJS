package com.pickaid.passivestjs.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;

public class PassiveSTJSKubePlugin extends KubeJSPlugin {
    @Override
    public void registerEvents() {
        PassiveSTJSKubeEvents.GROUP.register();
    }
}
