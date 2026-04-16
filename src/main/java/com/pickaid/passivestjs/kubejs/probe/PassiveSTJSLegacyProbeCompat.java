package com.pickaid.passivestjs.kubejs.probe;

import zzzank.probejs.plugin.ProbeJSPlugins;

public final class PassiveSTJSLegacyProbeCompat {
    private static boolean installed;

    private PassiveSTJSLegacyProbeCompat() {
    }

    public static synchronized void install() {
        if (installed) {
            return;
        }
        installed = true;
        ProbeJSPlugins.remove(PassiveSTJSLegacyProbePlugin.class);
        ProbeJSPlugins.register(new PassiveSTJSLegacyProbePlugin());
    }
}
