package com.pickaid.passivestjs.compat.passiveintegration;

import com.pickaid.passiveintegration.events.ammoburst.AmmoBurstAboutToEndDecision;
import com.pickaid.passiveintegration.events.ammoburst.AmmoBurstAboutToEndEvent;
import com.pickaid.passiveintegration.events.ammoburst.AmmoBurstFinalReason;
import com.pickaid.passivestjs.kubejs.event.AmmoBurstAboutToEndEventJS;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AmmoBurstBridgeTest {
    @Test
    void aboutToEndJsWrapperMutatesUnderlyingForgeEvent() {
        AmmoBurstAboutToEndEvent forgeEvent = new AmmoBurstAboutToEndEvent(
                null,
                AmmoBurstFinalReason.ENERGY_DEPLETED,
                0.0F,
                100.0F,
                1.0F,
                5.0F,
                50.0F,
                5.0F
        );

        AmmoBurstAboutToEndEventJS jsEvent = new AmmoBurstAboutToEndEventJS(forgeEvent);
        jsEvent.enterZeroSustain(10, 20);

        assertEquals(AmmoBurstAboutToEndDecision.ENTER_ZERO_SUSTAIN, forgeEvent.getDecision());
        assertEquals(10, forgeEvent.getSustainStartDelayTicks());
        assertEquals(20, forgeEvent.getSustainIntervalTicks());
    }
}
