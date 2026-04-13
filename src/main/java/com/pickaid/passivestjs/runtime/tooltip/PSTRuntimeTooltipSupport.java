package com.pickaid.passivestjs.runtime.tooltip;

import com.pickaid.passivestjs.runtime.PSTRuntimeNode;
import com.pickaid.passivestjs.runtime.serializer.PSTRuntimeTranslationKeys;
import daripher.skilltree.skill.bonus.SkillBonus;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class PSTRuntimeTooltipSupport {
    private PSTRuntimeTooltipSupport() {
    }

    public static MutableComponent skillBonusTooltip(PSTRuntimeNode node) {
        Component effect = node.metadata().tooltipSpec()
                .flatMap(spec -> spec.render(PSTTooltipFragmentKind.EFFECT, PSTTooltipRenderContext.of(node)))
                .orElseGet(() -> PSTRuntimeTranslationKeys.describe(node.metadata()));
        return PSTTooltipComposer.renderEffect(PSTTooltipNodeWalker.collectPrefixes(node), effect);
    }

    public static MutableComponent skillRequirementTooltip(PSTRuntimeNode node) {
        ArrayList<Component> requirements = new ArrayList<>();
        requirements.add(node.metadata().tooltipSpec()
                .flatMap(spec -> spec.render(PSTTooltipFragmentKind.REQUIREMENT, PSTTooltipRenderContext.of(node)))
                .orElseGet(() -> PSTRuntimeTranslationKeys.describe(node.metadata())));
        requirements.addAll(PSTTooltipNodeWalker.collectRequirements(node));
        return PSTTooltipComposer.renderRequirement(requirements);
    }

    public static MutableComponent wrappedPrefixTooltip(
            PSTRuntimeNode node,
            Component inner,
            SkillBonus.Target target,
            Supplier<MutableComponent> fallback
    ) {
        return node.metadata().tooltipSpec()
                .flatMap(spec -> spec.render(
                        PSTTooltipFragmentKind.PREFIX,
                        PSTTooltipRenderContext.of(node).withTarget(target)
                ))
                .map(prefix -> PSTTooltipComposer.renderEffect(List.of(prefix), inner))
                .orElseGet(fallback);
    }

    public static MutableComponent standalonePrefixTooltip(PSTRuntimeNode node) {
        return node.metadata().tooltipSpec()
                .flatMap(spec -> spec.render(PSTTooltipFragmentKind.PREFIX, PSTTooltipRenderContext.of(node)))
                .orElseGet(() -> PSTRuntimeTranslationKeys.describe(node.metadata()));
    }

    public static MutableComponent standaloneEffectTooltip(PSTRuntimeNode node) {
        return node.metadata().tooltipSpec()
                .flatMap(spec -> spec.render(PSTTooltipFragmentKind.EFFECT, PSTTooltipRenderContext.of(node)))
                .orElseGet(() -> PSTRuntimeTranslationKeys.describe(node.metadata()));
    }
}
