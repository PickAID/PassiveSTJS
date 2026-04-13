package com.pickaid.passivestjs.runtime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerFamily;
import com.pickaid.passivestjs.schema.PSTNodeFamily;
import com.pickaid.passivestjs.schema.PSTSchemaField;
import com.pickaid.passivestjs.schema.PSTSchemaFieldKind;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.item.ItemBonusHandler;
import daripher.skilltree.skill.bonus.item.ItemBonusListItemBonus;
import daripher.skilltree.skill.bonus.item.SkillBonusItemBonus;
import daripher.skilltree.skill.bonus.player.AttributeBonus;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public final class PSTItemBonusRuntimeBridge {
    @SubscribeEvent
    public void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof net.minecraft.world.entity.player.Player player)) {
            return;
        }
        removeAttributeBonuses(player::getAttribute, collectCustomAttributeBonuses(event.getFrom()));
        addAttributeBonuses(player::getAttribute, collectCustomAttributeBonuses(event.getTo()));
    }

    static List<AttributeBonus> collectCustomAttributeBonuses(ItemStack stack) {
        return collectCustomAttributeBonuses(ItemBonusHandler.getItemBonuses(stack));
    }

    static List<AttributeBonus> collectCustomAttributeBonuses(Iterable<? extends ItemBonus<?>> bonuses) {
        ArrayList<AttributeBonus> collected = new ArrayList<>();
        int rootIndex = 0;
        for (ItemBonus<?> bonus : bonuses) {
            if (bonus instanceof PSTCustomRuntimeItemBonus runtimeBonus) {
                collectCustomAttributeBonuses(runtimeBonus.node(), "root[" + rootIndex + "]:" + runtimeBonus.node().id(), collected);
            }
            rootIndex++;
        }
        return List.copyOf(collected);
    }

    static void addAttributeBonuses(
            Function<Attribute, AttributeInstance> resolver,
            Iterable<AttributeBonus> bonuses
    ) {
        for (AttributeBonus bonus : bonuses) {
            if (bonus.isDynamic()) {
                continue;
            }
            AttributeInstance instance = resolver.apply(bonus.getAttribute());
            if (instance == null || instance.hasModifier(bonus.getModifier())) {
                continue;
            }
            instance.addTransientModifier(bonus.getModifier());
        }
    }

    static void removeAttributeBonuses(
            Function<Attribute, AttributeInstance> resolver,
            Iterable<AttributeBonus> bonuses
    ) {
        for (AttributeBonus bonus : bonuses) {
            AttributeInstance instance = resolver.apply(bonus.getAttribute());
            if (instance == null) {
                continue;
            }
            instance.removeModifier(bonus.getModifier().getId());
        }
    }

    private static void collectCustomAttributeBonuses(PSTRuntimeNode node, String path, List<AttributeBonus> collected) {
        if (node.metadata().schema().isEmpty()) {
            return;
        }

        JsonObject payload = node.payload();
        for (PSTSchemaField field : node.metadata().requireSchema().fields().values()) {
            if (field.kind() == PSTSchemaFieldKind.NODE) {
                JsonObject child = childObject(payload.get(field.name()));
                if (child == null) {
                    continue;
                }
                String childPath = path + "/" + field.name();
                if (field.nodeTarget() == PSTNodeFamily.SKILL_BONUS) {
                    collectCustomAttributeBonusesFromSkillBonus(child, childPath, collected);
                    continue;
                }
                if (field.nodeTarget() == PSTNodeFamily.ITEM_BONUS) {
                    collectCustomAttributeBonusesFromItemBonus(child, childPath, collected);
                }
                continue;
            }

            if (field.kind() != PSTSchemaFieldKind.NODE_LIST) {
                continue;
            }

            JsonElement element = payload.get(field.name());
            if (element == null || !element.isJsonArray()) {
                continue;
            }

            int index = 0;
            for (JsonElement childElement : element.getAsJsonArray()) {
                JsonObject child = childObject(childElement);
                if (child == null) {
                    index++;
                    continue;
                }
                String childPath = path + "/" + field.name() + "[" + index + "]";
                if (field.nodeTarget() == PSTNodeFamily.SKILL_BONUS) {
                    collectCustomAttributeBonusesFromSkillBonus(child, childPath, collected);
                } else if (field.nodeTarget() == PSTNodeFamily.ITEM_BONUS) {
                    collectCustomAttributeBonusesFromItemBonus(child, childPath, collected);
                }
                index++;
            }
        }
    }

    private static void collectCustomAttributeBonusesFromSkillBonus(
            JsonObject json,
            String path,
            List<AttributeBonus> collected
    ) {
        SkillBonus<?> skillBonus = PSTRuntimeNode.deserializeTypedNode(json, PSTSerializerFamily.SKILL_BONUSES);
        if (skillBonus instanceof AttributeBonus attributeBonus) {
            collected.add(stabilizeAttributeBonus(attributeBonus, path));
        }
    }

    private static void collectCustomAttributeBonusesFromItemBonus(
            JsonObject json,
            String path,
            List<AttributeBonus> collected
    ) {
        ItemBonus<?> itemBonus = PSTRuntimeNode.deserializeTypedNode(json, PSTSerializerFamily.ITEM_BONUSES);
        if (itemBonus == null) {
            return;
        }
        collectCustomAttributeBonuses(itemBonus, path, collected);
    }

    private static void collectCustomAttributeBonuses(ItemBonus<?> bonus, String path, List<AttributeBonus> collected) {
        if (bonus instanceof PSTCustomRuntimeItemBonus runtimeBonus) {
            collectCustomAttributeBonuses(runtimeBonus.node(), path + "/" + runtimeBonus.node().id(), collected);
            return;
        }
        if (bonus instanceof SkillBonusItemBonus skillBonusItemBonus) {
            if (skillBonusItemBonus.skillBonus() instanceof AttributeBonus attributeBonus) {
                collected.add(stabilizeAttributeBonus(attributeBonus, path + "/skill_bonus"));
            }
            return;
        }
        if (bonus instanceof ItemBonusListItemBonus itemBonusList) {
            int index = 0;
            for (ItemBonus<?> innerBonus : itemBonusList.innerBonuses()) {
                collectCustomAttributeBonuses(innerBonus, path + "/inner[" + index + "]", collected);
                index++;
            }
        }
    }

    private static AttributeBonus stabilizeAttributeBonus(AttributeBonus bonus, String path) {
        if (bonus.isDynamic()) {
            return bonus;
        }

        AttributeModifier modifier = bonus.getModifier();
        UUID stableId = UUID.nameUUIDFromBytes((
                path
                        + "|" + bonus.getAttribute().getDescriptionId()
                        + "|" + modifier.getName()
                        + "|" + modifier.getAmount()
                        + "|" + modifier.getOperation()
        ).getBytes(StandardCharsets.UTF_8));
        if (stableId.equals(modifier.getId())) {
            return bonus;
        }
        return new AttributeBonus(
                bonus.getAttribute(),
                new AttributeModifier(stableId, modifier.getName(), modifier.getAmount(), modifier.getOperation())
        );
    }

    private static JsonObject childObject(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return null;
        }
        return element.getAsJsonObject();
    }
}
