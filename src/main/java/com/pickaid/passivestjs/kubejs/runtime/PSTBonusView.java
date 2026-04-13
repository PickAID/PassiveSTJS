package com.pickaid.passivestjs.kubejs.runtime;

import com.pickaid.passivestjs.runtime.PSTCustomRuntimeSkillBonus;
import com.pickaid.passivestjs.runtime.PSTRuntimeNode;
import com.pickaid.passivestjs.runtime.PSTRuntimeTypeIds;
import daripher.skilltree.skill.bonus.SkillBonus;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.network.chat.Component;

import java.util.Objects;

public final class PSTBonusView {
    private final SkillBonus<?> bonus;

    public PSTBonusView(SkillBonus<?> bonus) {
        this.bonus = Objects.requireNonNull(bonus, "bonus");
    }

    @Info("Returns the registry/type id for this bonus.")
    public String typeId() {
        return PSTRuntimeTypeIds.skillBonusId(bonus);
    }

    @Info("Returns the bonus tooltip text.")
    public Component text() {
        return bonus.getTooltip();
    }

    @Info("Returns the runtime node for this bonus when it is serializer-backed, otherwise null.")
    public PSTRuntimeNode node() {
        return bonus instanceof PSTCustomRuntimeSkillBonus runtime ? runtime.node() : null;
    }
}

