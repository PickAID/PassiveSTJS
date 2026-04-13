package com.pickaid.passivestjs.runtime.tooltip;

import net.minecraft.network.chat.Component;

@FunctionalInterface
public interface PSTTooltipFactory {
    Component create(PSTTooltipRenderContext context);
}
