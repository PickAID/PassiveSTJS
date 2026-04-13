package com.pickaid.passivestjs.kubejs.registry;

import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
interface PSTRegistryBuilderFactory<T> {
    T create(ResourceLocation id);
}
