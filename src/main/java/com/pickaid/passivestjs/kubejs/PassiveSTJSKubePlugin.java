package com.pickaid.passivestjs.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;

public class PassiveSTJSKubePlugin extends KubeJSPlugin {
    @Override
    public void registerEvents() {
        PassiveSTJSKubeEvents.registerCore();
        PassiveSTJSKubeEvents.GROUP.register();
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("PassiveSTJS", Bindings.INSTANCE);
    }
}
