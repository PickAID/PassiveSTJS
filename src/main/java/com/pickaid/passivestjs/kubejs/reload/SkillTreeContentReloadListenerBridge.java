package com.pickaid.passivestjs.kubejs.reload;

import com.pickaid.passivestjs.kubejs.PassiveSTJSKubeEvents;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class SkillTreeContentReloadListenerBridge {
    private static final SkillTreeContentReloadListenerBridge INSTANCE = new SkillTreeContentReloadListenerBridge();
    private static boolean registered;

    private SkillTreeContentReloadListenerBridge() {
    }

    public static void register() {
        if (!registered) {
            MinecraftForge.EVENT_BUS.register(INSTANCE);
            registered = true;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<Void>() {
            @Override
            protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
                return null;
            }

            @Override
            protected void apply(Void unused, ResourceManager resourceManager, ProfilerFiller profiler) {
                PassiveSTJSKubeEvents.postSkillTreeContent();
            }

            @Override
            public String getName() {
                return "PassiveSTJS skillTreeContent bridge";
            }
        });
    }
}
