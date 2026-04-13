package com.pickaid.passivestjs.kubejs.runtime;

import com.pickaid.passivestjs.runtime.PSTCustomRuntimeEventListener;
import com.pickaid.passivestjs.runtime.PSTRuntimeNode;
import com.pickaid.passivestjs.runtime.PSTRuntimeTypeIds;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.network.chat.Component;

import java.util.Objects;

public final class PSTListenerView {
    private final EventListenerBonus<?> bonus;

    public PSTListenerView(EventListenerBonus<?> bonus) {
        this.bonus = Objects.requireNonNull(bonus, "bonus");
    }

    @Info("Returns the registry/type id for this event listener.")
    public String typeId() {
        return PSTRuntimeTypeIds.eventListenerId(bonus);
    }

    @Info("Returns the listener bonus tooltip text.")
    public Component text() {
        return bonus.getTooltip();
    }

    @Info("Returns the runtime node for this event listener when it is serializer-backed, otherwise null.")
    public PSTRuntimeNode node() {
        SkillEventListener listener = bonus.getEventListener();
        return listener instanceof PSTCustomRuntimeEventListener runtime ? runtime.node() : null;
    }
}

