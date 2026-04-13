package com.pickaid.passivestjs.runtime.tooltip;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Optional;

public record PSTTooltipSpec(
        PSTTooltipFactory effectFactory,
        PSTTooltipFactory prefixFactory,
        PSTTooltipFactory requirementFactory
) {
    public boolean isEmpty() {
        return effectFactory == null && prefixFactory == null && requirementFactory == null;
    }

    public Optional<MutableComponent> render(PSTTooltipFragmentKind kind, PSTTooltipRenderContext context) {
        PSTTooltipFactory factory = switch (kind) {
            case EFFECT -> effectFactory;
            case PREFIX -> prefixFactory;
            case REQUIREMENT -> requirementFactory;
        };
        if (factory == null) {
            return Optional.empty();
        }
        Component component = factory.create(context);
        if (component == null) {
            return Optional.empty();
        }
        return Optional.of(component.copy());
    }
}
