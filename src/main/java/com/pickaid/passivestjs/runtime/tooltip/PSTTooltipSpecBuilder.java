package com.pickaid.passivestjs.runtime.tooltip;

import net.minecraft.network.chat.Component;

public final class PSTTooltipSpecBuilder {
    private PSTTooltipFactory effectFactory;
    private PSTTooltipFactory prefixFactory;
    private PSTTooltipFactory requirementFactory;

    public PSTTooltipSpecBuilder effectText(Component component) {
        this.effectFactory = constant(component);
        return this;
    }

    public PSTTooltipSpecBuilder effect(PSTTooltipFactory factory) {
        this.effectFactory = factory;
        return this;
    }

    public PSTTooltipSpecBuilder prefixText(Component component) {
        this.prefixFactory = constant(component);
        return this;
    }

    public PSTTooltipSpecBuilder prefix(PSTTooltipFactory factory) {
        this.prefixFactory = factory;
        return this;
    }

    public PSTTooltipSpecBuilder requirementText(Component component) {
        this.requirementFactory = constant(component);
        return this;
    }

    public PSTTooltipSpecBuilder requirement(PSTTooltipFactory factory) {
        this.requirementFactory = factory;
        return this;
    }

    public boolean isEmpty() {
        return effectFactory == null && prefixFactory == null && requirementFactory == null;
    }

    public PSTTooltipSpec build() {
        return new PSTTooltipSpec(effectFactory, prefixFactory, requirementFactory);
    }

    private static PSTTooltipFactory constant(Component component) {
        Component copy = component == null ? Component.empty() : component.copy();
        return context -> copy.copy();
    }
}
