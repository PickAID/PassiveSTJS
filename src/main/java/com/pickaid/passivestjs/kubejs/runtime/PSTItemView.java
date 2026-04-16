package com.pickaid.passivestjs.kubejs.runtime;

import com.google.gson.JsonObject;
import com.pickaid.passivestjs.kubejs.builder.BonusBuilder;
import com.pickaid.passivestjs.kubejs.builder.ItemBonusBuilder;
import com.pickaid.passivestjs.kubejs.content.JsonHelper;
import com.pickaid.passivestjs.kubejs.id.PSTItemBonusId;
import com.pickaid.passivestjs.kubejs.id.PSTSkillBonusId;
import com.pickaid.passivestjs.kubejs.recipe.builder.PSTAttributeSkillBonusBuilder;
import com.pickaid.passivestjs.kubejs.recipe.builder.PSTItemBonusListBuilder;
import com.pickaid.passivestjs.kubejs.recipe.builder.PSTSkillBonusItemBonusBuilder;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import com.pickaid.passivestjs.runtime.PSTRuntimeTypeIds;
import daripher.skilltree.data.serializers.Serializer;
import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemBonusHandler;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class PSTItemView {
    private static final String SKILL_BONUSES_KEY = "SkillBonuses";
    private final ItemStack stack;

    public PSTItemView(ItemStack stack) {
        this.stack = Objects.requireNonNull(stack, "stack");
    }

    @Info("Returns the current number of PST item bonuses stored on this stack.")
    public int bonusCount() {
        return readBonuses().size();
    }

    @Info("Returns read-only runtime item bonus helper views for this stack.")
    public List<PSTItemBonusView> bonuses() {
        List<PSTItemBonusView> views = new ArrayList<>();
        for (ItemBonus<?> bonus : readBonuses()) {
            if (bonus != null) {
                views.add(new PSTItemBonusView(bonus));
            }
        }
        return List.copyOf(views);
    }

    @Info("Removes all PST item bonuses from this stack and returns the number removed.")
    public int clearBonuses() {
        int count = bonusCount();
        try {
            ItemBonusHandler.removeItemBonuses(stack);
        } catch (Throwable ignored) {
            if (stack.hasTag()) {
                stack.getOrCreateTag().remove(SKILL_BONUSES_KEY);
            }
        }
        return count;
    }

    @Info(value = "Adds an item bonus built from a typed item bonus id and returns this stack view.", params = {
            @Param(name = "typeId", value = "The PST item bonus type id."),
            @Param(name = "consumer", value = "The callback that configures the created item bonus builder.")
    })
    public PSTItemView addItemBonus(PSTItemBonusId typeId, Consumer<ItemBonusBuilder> consumer) {
        ItemBonusBuilder builder = new ItemBonusBuilder(PSTItemBonusId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return addItemBonus(builder);
    }

    @Info("Adds a prebuilt item bonus builder to this stack and returns this stack view.")
    public PSTItemView addItemBonus(ItemBonusBuilder value) {
        return appendBonus(value);
    }

    @Info("Adds a built-in skilltree:skill_bonus item bonus to this stack and returns this stack view.")
    public PSTItemView addSkillBonusItemBonus(PSTSkillBonusId typeId, Consumer<BonusBuilder> consumer) {
        return appendBonus(new PSTSkillBonusItemBonusBuilder().skillBonus(typeId, consumer));
    }

    @Info("Adds a built-in skilltree:attribute-backed item bonus to this stack and returns this stack view.")
    public PSTItemView addAttributeItemBonus(Consumer<PSTAttributeSkillBonusBuilder> consumer) {
        return appendBonus(new PSTSkillBonusItemBonusBuilder().attribute(consumer));
    }

    @Info("Adds a built-in skilltree:item_bonus_list item bonus to this stack and returns this stack view.")
    public PSTItemView addItemBonusList(Consumer<PSTItemBonusListBuilder> consumer) {
        PSTItemBonusListBuilder builder = new PSTItemBonusListBuilder();
        if (consumer != null) {
            consumer.accept(builder);
        }
        return appendBonus(builder);
    }

    private PSTItemView appendBonus(Object value) {
        JsonObject json = JsonHelper.requireObject(value, "itemBonus");
        ItemBonus<?> itemBonus = deserializeItemBonus(json);
        if (itemBonus == null) {
            throw new IllegalArgumentException("Unknown item bonus type: " + json);
        }

        List<ItemBonus<?>> bonuses = new ArrayList<>(readBonuses());
        bonuses.add(itemBonus);
        writeBonuses(bonuses);
        return this;
    }

    private List<ItemBonus<?>> readBonuses() {
        try {
            return ItemBonusHandler.getItemBonuses(stack);
        } catch (Throwable ignored) {
            return readBonusesFallback();
        }
    }

    private void writeBonuses(List<ItemBonus<?>> bonuses) {
        try {
            ItemBonusHandler.setItemBonuses(stack, bonuses);
            return;
        } catch (Throwable ignored) {
        }

        CompoundTag serializedBonuses = new CompoundTag();
        int index = 0;
        for (ItemBonus<?> bonus : bonuses) {
            CompoundTag tag = serializeBonusFallback(bonus);
            if (tag != null) {
                serializedBonuses.put(String.valueOf(index), tag);
                index++;
            }
        }
        stack.getOrCreateTag().put(SKILL_BONUSES_KEY, serializedBonuses);
    }

    private List<ItemBonus<?>> readBonusesFallback() {
        if (!stack.hasTag()) {
            return List.of();
        }

        List<ItemBonus<?>> bonuses = new ArrayList<>();
        CompoundTag root = stack.getOrCreateTag().getCompound(SKILL_BONUSES_KEY);
        for (int index = 0; root.contains(String.valueOf(index)); index++) {
            CompoundTag child = root.getCompound(String.valueOf(index));
            ItemBonus<?> bonus = deserializeItemBonus(child);
            if (bonus != null) {
                bonuses.add(bonus);
            }
        }
        return List.copyOf(bonuses);
    }

    @SuppressWarnings("unchecked")
    private static ItemBonus<?> deserializeItemBonus(JsonObject json) {
        ResourceLocation typeId = ResourceLocation.tryParse(json.get("type").getAsString());
        if (typeId == null) {
            return null;
        }

        Object serializer = com.pickaid.passivestjs.runtime.PSTSerializerObjectIndex.find(PSTSerializerFamily.ITEM_BONUSES, typeId)
                .orElseGet(() -> {
                    try {
                        return PSTRegistries.ITEM_BONUSES.get().getValue(typeId);
                    } catch (Throwable ignored) {
                        return null;
                    }
                });
        if (!(serializer instanceof Serializer<?> typedSerializer)) {
            return null;
        }
        return ((Serializer<ItemBonus<?>>) typedSerializer).deserialize(JsonHelper.copy(json).getAsJsonObject());
    }

    @SuppressWarnings("unchecked")
    private static ItemBonus<?> deserializeItemBonus(CompoundTag tag) {
        ResourceLocation typeId = ResourceLocation.tryParse(tag.getString("type"));
        if (typeId == null) {
            return null;
        }

        Object serializer = com.pickaid.passivestjs.runtime.PSTSerializerObjectIndex.find(PSTSerializerFamily.ITEM_BONUSES, typeId)
                .orElseGet(() -> {
                    try {
                        return PSTRegistries.ITEM_BONUSES.get().getValue(typeId);
                    } catch (Throwable ignored) {
                        return null;
                    }
                });
        if (!(serializer instanceof ItemBonus.Serializer typedSerializer)) {
            return null;
        }
        return ((ItemBonus.Serializer) typedSerializer).deserialize(tag);
    }

    @SuppressWarnings("unchecked")
    private static CompoundTag serializeBonusFallback(ItemBonus<?> bonus) {
        String typeId = PSTRuntimeTypeIds.itemBonusId(bonus);
        if (typeId == null || typeId.equals("<unknown>")) {
            return null;
        }

        ItemBonus.Serializer serializer = bonus.getSerializer();
        CompoundTag tag = serializer.serialize((ItemBonus) bonus);
        tag.putString("type", typeId);
        return tag;
    }
}
