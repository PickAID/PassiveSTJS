package com.pickaid.passivestjs.kubejs.runtime;

import com.pickaid.passivestjs.runtime.PSTCustomRuntimeItemBonus;
import com.pickaid.passivestjs.runtime.PSTRuntimeNode;
import com.pickaid.passivestjs.runtime.PSTRuntimeTypeIds;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PSTItemBonusView {
    private final ItemBonus<?> bonus;

    public PSTItemBonusView(ItemBonus<?> bonus) {
        this.bonus = Objects.requireNonNull(bonus, "bonus");
    }

    @Info("Returns the registry/type id for this item bonus.")
    public String typeId() {
        return PSTRuntimeTypeIds.itemBonusId(bonus);
    }

    @Info("Returns the rendered tooltip text for this item bonus.")
    public Component text() {
        List<MutableComponent> lines = new ArrayList<>();
        bonus.addTooltip(lines::add);
        if (lines.isEmpty()) {
            return Component.empty();
        }

        MutableComponent text = lines.get(0).copy();
        for (int index = 1; index < lines.size(); index++) {
            text.append("\n").append(lines.get(index));
        }
        return text;
    }

    @Info("Returns the runtime node for this item bonus when it is serializer-backed, otherwise null.")
    public PSTRuntimeNode node() {
        return bonus instanceof PSTCustomRuntimeItemBonus runtime ? runtime.node() : null;
    }
}
