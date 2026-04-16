package com.pickaid.passivestjs.mixin;

import daripher.skilltree.network.message.LearnSkillMessage;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = LearnSkillMessage.class, remap = false)
public interface LearnSkillMessageAccessor {
    @Accessor("skillId")
    ResourceLocation passivestjs$getSkillId();
}
