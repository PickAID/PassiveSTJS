package com.pickaid.passivestjs.kubejs.registry.builder;

import com.pickaid.passivestjs.kubejs.registry.AbstractPSTSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadata;
import com.pickaid.passivestjs.runtime.PSTCustomRuntimeContexts;
import com.pickaid.passivestjs.runtime.serializer.PSTCustomRuntimeSerializers;
import com.pickaid.passivestjs.runtime.tooltip.PSTTooltipFactory;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class PSTEventListenerSerializerBuilder extends AbstractPSTSerializerBuilder<SkillEventListener.Serializer> {
    private Consumer<PSTCustomRuntimeContexts.SkillLearnedListenerContext> onSkillLearned = context -> {
    };
    private Consumer<PSTCustomRuntimeContexts.SkillRemovedListenerContext> onSkillRemoved = context -> {
    };
    private Consumer<PSTCustomRuntimeContexts.TickListenerContext> onTick = context -> {
    };
    private Consumer<PSTCustomRuntimeContexts.AttackListenerContext> onAttack = context -> {
    };
    private Consumer<PSTCustomRuntimeContexts.DamageTakenListenerContext> onDamageTaken = context -> {
    };
    private Consumer<PSTCustomRuntimeContexts.CriticalHitListenerContext> onCriticalHit = context -> {
    };
    private Consumer<PSTCustomRuntimeContexts.BlockListenerContext> onBlock = context -> {
    };
    private Consumer<PSTCustomRuntimeContexts.ItemUsedListenerContext> onItemUsed = context -> {
    };
    private Consumer<PSTCustomRuntimeContexts.KillListenerContext> onKill = context -> {
    };

    public PSTEventListenerSerializerBuilder(ResourceLocation id) {
        super(id, PSTSerializerFamily.EVENT_LISTENERS);
    }

    @Info(value = "Runs when a learned-skill trigger uses this custom event listener.", params = {
            @Param(name = "consumer", value = "Receives the runtime node, copied bonus, player, and first-time flag.")
    })
    public PSTEventListenerSerializerBuilder onSkillLearned(
            Consumer<PSTCustomRuntimeContexts.SkillLearnedListenerContext> consumer
    ) {
        this.onSkillLearned = consumer == null ? context -> {
        } : consumer;
        return this;
    }

    @Info(value = "Runs when a removed-skill trigger uses this custom event listener.", params = {
            @Param(name = "consumer", value = "Receives the runtime node, copied bonus, and player.")
    })
    public PSTEventListenerSerializerBuilder onSkillRemoved(
            Consumer<PSTCustomRuntimeContexts.SkillRemovedListenerContext> consumer
    ) {
        this.onSkillRemoved = consumer == null ? context -> {
        } : consumer;
        return this;
    }

    @Info(value = "Runs every server tick for bonuses that use this custom event listener.", params = {
            @Param(name = "consumer", value = "Receives the runtime node, copied bonus, and player.")
    })
    public PSTEventListenerSerializerBuilder onTick(Consumer<PSTCustomRuntimeContexts.TickListenerContext> consumer) {
        this.onTick = consumer == null ? context -> {
        } : consumer;
        return this;
    }

    @Info(value = "Runs when the owning player hurts a living target.", params = {
            @Param(name = "consumer", value = "Receives the runtime node, copied bonus, player, enemy, and damage source.")
    })
    public PSTEventListenerSerializerBuilder onAttack(Consumer<PSTCustomRuntimeContexts.AttackListenerContext> consumer) {
        this.onAttack = consumer == null ? context -> {
        } : consumer;
        return this;
    }

    @Info(value = "Runs when the owning player takes damage.", params = {
            @Param(name = "consumer", value = "Receives the runtime node, copied bonus, player, attacker, and damage source.")
    })
    public PSTEventListenerSerializerBuilder onDamageTaken(
            Consumer<PSTCustomRuntimeContexts.DamageTakenListenerContext> consumer
    ) {
        this.onDamageTaken = consumer == null ? context -> {
        } : consumer;
        return this;
    }

    @Info(value = "Runs when the owning player lands a critical hit.", params = {
            @Param(name = "consumer", value = "Receives the runtime node, copied bonus, player, and enemy.")
    })
    public PSTEventListenerSerializerBuilder onCriticalHit(
            Consumer<PSTCustomRuntimeContexts.CriticalHitListenerContext> consumer
    ) {
        this.onCriticalHit = consumer == null ? context -> {
        } : consumer;
        return this;
    }

    @Info(value = "Runs when the owning player blocks with a shield.", params = {
            @Param(name = "consumer", value = "Receives the runtime node, copied bonus, player, attacker, and damage source.")
    })
    public PSTEventListenerSerializerBuilder onBlock(Consumer<PSTCustomRuntimeContexts.BlockListenerContext> consumer) {
        this.onBlock = consumer == null ? context -> {
        } : consumer;
        return this;
    }

    @Info(value = "Runs when the owning player finishes using an item.", params = {
            @Param(name = "consumer", value = "Receives the runtime node, copied bonus, player, and item stack.")
    })
    public PSTEventListenerSerializerBuilder onItemUsed(
            Consumer<PSTCustomRuntimeContexts.ItemUsedListenerContext> consumer
    ) {
        this.onItemUsed = consumer == null ? context -> {
        } : consumer;
        return this;
    }

    @Info(value = "Runs when the owning player kills a living target.", params = {
            @Param(name = "consumer", value = "Receives the runtime node, copied bonus, player, enemy, and damage source.")
    })
    public PSTEventListenerSerializerBuilder onKill(Consumer<PSTCustomRuntimeContexts.KillListenerContext> consumer) {
        this.onKill = consumer == null ? context -> {
        } : consumer;
        return this;
    }

    @Info(value = "Sets the translated prefix fragment shown for this custom event listener type.", params = {
            @Param(name = "component", value = "The component used as the listener prefix text.")
    })
    public PSTEventListenerSerializerBuilder prefixText(Component component) {
        tooltipBuilder().prefixText(component);
        return this;
    }

    @Info(value = "Sets the dynamic prefix fragment factory shown for this custom event listener type.", params = {
            @Param(name = "factory", value = "Receives the runtime tooltip context and returns the listener prefix component.")
    })
    public PSTEventListenerSerializerBuilder prefix(PSTTooltipFactory factory) {
        tooltipBuilder().prefix(factory);
        return this;
    }

    @Override
    protected SkillEventListener.Serializer createSerializer(PSTSerializerMetadata metadata) {
        metadata.schema();
        return PSTCustomRuntimeSerializers.eventListener(
                metadata,
                onSkillLearned,
                onSkillRemoved,
                onTick,
                onAttack,
                onDamageTaken,
                onCriticalHit,
                onBlock,
                onItemUsed,
                onKill
        );
    }
}
