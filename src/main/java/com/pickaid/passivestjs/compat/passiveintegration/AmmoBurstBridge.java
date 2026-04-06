package com.pickaid.passivestjs.compat.passiveintegration;

import com.pickaid.passiveintegration.events.ammoburst.AmmoBurstAboutToEndEvent;
import com.pickaid.passiveintegration.events.ammoburst.AmmoBurstEndEvent;
import com.pickaid.passiveintegration.events.ammoburst.AmmoBurstFailEvent;
import com.pickaid.passiveintegration.events.ammoburst.AmmoBurstStartEvent;
import com.pickaid.passiveintegration.events.ammoburst.AmmoBurstSustainEvent;
import com.pickaid.passiveintegration.events.ammoburst.AmmoBurstTryStartEvent;
import com.pickaid.passivestjs.kubejs.PassiveSTJSKubeEvents;
import com.pickaid.passivestjs.kubejs.event.AmmoBurstAboutToEndEventJS;
import com.pickaid.passivestjs.kubejs.event.AmmoBurstEndEventJS;
import com.pickaid.passivestjs.kubejs.event.AmmoBurstFailEventJS;
import com.pickaid.passivestjs.kubejs.event.AmmoBurstStartEventJS;
import com.pickaid.passivestjs.kubejs.event.AmmoBurstSustainEventJS;
import com.pickaid.passivestjs.kubejs.event.AmmoBurstTryStartEventJS;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class AmmoBurstBridge {
    @SubscribeEvent
    public void onTryStart(AmmoBurstTryStartEvent event) {
        PassiveSTJSKubeEvents.postTryStart(new AmmoBurstTryStartEventJS(event));
    }

    @SubscribeEvent
    public void onStart(AmmoBurstStartEvent event) {
        PassiveSTJSKubeEvents.postStart(new AmmoBurstStartEventJS(event));
    }

    @SubscribeEvent
    public void onFail(AmmoBurstFailEvent event) {
        PassiveSTJSKubeEvents.postFail(new AmmoBurstFailEventJS(event));
    }

    @SubscribeEvent
    public void onAboutToEnd(AmmoBurstAboutToEndEvent event) {
        PassiveSTJSKubeEvents.postAboutToEnd(new AmmoBurstAboutToEndEventJS(event));
    }

    @SubscribeEvent
    public void onSustain(AmmoBurstSustainEvent event) {
        PassiveSTJSKubeEvents.postSustain(new AmmoBurstSustainEventJS(event));
    }

    @SubscribeEvent
    public void onEnd(AmmoBurstEndEvent event) {
        PassiveSTJSKubeEvents.postEnd(new AmmoBurstEndEventJS(event));
    }
}
