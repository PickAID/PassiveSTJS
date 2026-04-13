package com.pickaid.passivestjs.runtime.tooltip;

import com.pickaid.passivestjs.runtime.PSTRuntimeNode;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.predicate.living.FloatFunctionEntityPredicate;

public record PSTTooltipRenderContext(
        PSTRuntimeNode node,
        SkillBonus.Target target,
        FloatFunctionEntityPredicate.Logic logic,
        Double numericValue,
        String subtype
) {
    public static PSTTooltipRenderContext of(PSTRuntimeNode node) {
        return new PSTTooltipRenderContext(node, null, null, null, null);
    }

    public PSTTooltipRenderContext withTarget(SkillBonus.Target value) {
        return new PSTTooltipRenderContext(node, value, logic, numericValue, subtype);
    }

    public PSTTooltipRenderContext withLogic(FloatFunctionEntityPredicate.Logic value) {
        return new PSTTooltipRenderContext(node, target, value, numericValue, subtype);
    }

    public PSTTooltipRenderContext withNumericValue(Double value) {
        return new PSTTooltipRenderContext(node, target, logic, value, subtype);
    }

    public PSTTooltipRenderContext withSubtype(String value) {
        return new PSTTooltipRenderContext(node, target, logic, numericValue, value);
    }
}
