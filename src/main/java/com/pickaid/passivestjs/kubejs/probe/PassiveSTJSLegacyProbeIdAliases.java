package com.pickaid.passivestjs.kubejs.probe;

import com.pickaid.passivestjs.kubejs.content.ManagedContent;
import com.pickaid.passivestjs.kubejs.id.PSTAttributeId;
import com.pickaid.passivestjs.kubejs.id.PSTDamageConditionId;
import com.pickaid.passivestjs.kubejs.id.PSTEnchantmentConditionId;
import com.pickaid.passivestjs.kubejs.id.PSTEventListenerId;
import com.pickaid.passivestjs.kubejs.id.PSTItemBonusId;
import com.pickaid.passivestjs.kubejs.id.PSTItemConditionId;
import com.pickaid.passivestjs.kubejs.id.PSTItemId;
import com.pickaid.passivestjs.kubejs.id.PSTItemTagId;
import com.pickaid.passivestjs.kubejs.id.PSTLivingConditionId;
import com.pickaid.passivestjs.kubejs.id.PSTLivingMultiplierId;
import com.pickaid.passivestjs.kubejs.id.PSTMobEffectId;
import com.pickaid.passivestjs.kubejs.id.PSTNumericValueProviderId;
import com.pickaid.passivestjs.kubejs.id.PSTPotionId;
import com.pickaid.passivestjs.kubejs.id.PSTSkillBonusId;
import com.pickaid.passivestjs.kubejs.id.PSTSkillId;
import com.pickaid.passivestjs.kubejs.id.PSTSkillRequirementId;
import com.pickaid.passivestjs.kubejs.id.PSTStatTypeId;
import com.pickaid.passivestjs.kubejs.id.PSTTreeId;
import com.pickaid.passivestjs.kubejs.texture.PSTSkillFrameType;
import com.pickaid.passivestjs.kubejs.texture.PSTTexture;
import com.pickaid.passivestjs.kubejs.texture.PSTTooltipFrameType;
import com.pickaid.passivestjs.kubejs.type.PSTComparisonLogic;
import com.pickaid.passivestjs.kubejs.type.PSTEquipmentType;
import com.pickaid.passivestjs.kubejs.type.PSTSkillTarget;
import daripher.skilltree.data.reloader.SkillTreesReloader;
import daripher.skilltree.data.reloader.SkillsReloader;
import daripher.skilltree.init.PSTRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

final class PassiveSTJSLegacyProbeIdAliases {
    private static final List<IdAlias> ID_ALIASES = List.of(
            new IdAlias("PSTSkillId", PSTSkillId.class, PassiveSTJSLegacyProbeIdAliases::skillIds),
            new IdAlias("PSTTreeId", PSTTreeId.class, PassiveSTJSLegacyProbeIdAliases::treeIds),
            new IdAlias("PSTSkillBonusId", PSTSkillBonusId.class, () -> registryIds(PSTRegistries.SKILL_BONUSES)),
            new IdAlias("PSTLivingMultiplierId", PSTLivingMultiplierId.class, () -> registryIds(PSTRegistries.LIVING_MULTIPLIERS)),
            new IdAlias("PSTLivingConditionId", PSTLivingConditionId.class, () -> registryIds(PSTRegistries.LIVING_CONDITIONS)),
            new IdAlias("PSTDamageConditionId", PSTDamageConditionId.class, () -> registryIds(PSTRegistries.DAMAGE_CONDITIONS)),
            new IdAlias("PSTItemConditionId", PSTItemConditionId.class, () -> registryIds(PSTRegistries.ITEM_CONDITIONS)),
            new IdAlias("PSTEnchantmentConditionId", PSTEnchantmentConditionId.class, () -> registryIds(PSTRegistries.ENCHANTMENT_CONDITIONS)),
            new IdAlias("PSTEventListenerId", PSTEventListenerId.class, () -> registryIds(PSTRegistries.EVENT_LISTENERS)),
            new IdAlias("PSTNumericValueProviderId", PSTNumericValueProviderId.class, () -> registryIds(PSTRegistries.FLOAT_FUNCTIONS)),
            new IdAlias("PSTSkillRequirementId", PSTSkillRequirementId.class, () -> registryIds(PSTRegistries.SKILL_REQUIREMENTS)),
            new IdAlias("PSTItemBonusId", PSTItemBonusId.class, () -> registryIds(PSTRegistries.ITEM_BONUSES)),
            IdAlias.rawType("PSTMobEffectId", PSTMobEffectId.class, "MobEffect"),
            IdAlias.rawType("PSTAttributeId", PSTAttributeId.class, "Attribute"),
            IdAlias.rawType("PSTItemId", PSTItemId.class, "Item"),
            IdAlias.rawType("PSTItemTagId", PSTItemTagId.class, "ItemTag"),
            IdAlias.rawType("PSTPotionId", PSTPotionId.class, "Potion"),
            IdAlias.rawType("PSTStatTypeId", PSTStatTypeId.class, "StatType"),
            IdAlias.rawType("PSTTexture", PSTTexture.class, "RawTexture"),
            new IdAlias("PSTSkillFrameType", PSTSkillFrameType.class, () -> List.of("lesser", "class", "notable", "keystone", "gateway", "recipe")),
            new IdAlias("PSTTooltipFrameType", PSTTooltipFrameType.class, () -> List.of("lesser", "notable", "keystone", "gateway")),
            new IdAlias("PSTSkillTarget", PSTSkillTarget.class, () -> List.of("player", "enemy")),
            new IdAlias("PSTComparisonLogic", PSTComparisonLogic.class, () -> List.of("more", "less", "equal", "at_least", "at_most")),
            new IdAlias("PSTEquipmentType", PSTEquipmentType.class, () -> List.of(
                    "any",
                    "helmet",
                    "chestplate",
                    "leggings",
                    "boots",
                    "armor",
                    "shield",
                    "weapon",
                    "sword",
                    "axe",
                    "trident",
                    "melee_weapon",
                    "bow",
                    "crossbow",
                    "ranged_weapon",
                    "pickaxe",
                    "hoe",
                    "shovel",
                    "tool"
            ))
    );

    private PassiveSTJSLegacyProbeIdAliases() {
    }

    static List<IdAlias> all() {
        return ID_ALIASES;
    }

    private static List<String> skillIds() {
        return resourceLocationIds(() -> merge(keys(SkillsReloader.getSkills()), ManagedContent.skillIds()));
    }

    private static List<String> treeIds() {
        return resourceLocationIds(() -> merge(keys(SkillTreesReloader.getSkillTrees()), ManagedContent.treeIds()));
    }

    private static List<String> registryIds(Supplier<? extends IForgeRegistry<?>> registrySupplier) {
        return resourceLocationIds(() -> {
            IForgeRegistry<?> registry = registrySupplier.get();
            return registry == null ? List.of() : registry.getKeys();
        });
    }

    private static Collection<ResourceLocation> keys(Map<ResourceLocation, ?> map) {
        return map == null ? List.of() : map.keySet();
    }

    private static Collection<ResourceLocation> merge(Collection<ResourceLocation> first, Collection<ResourceLocation> second) {
        LinkedHashSet<ResourceLocation> merged = new LinkedHashSet<>();
        if (first != null) {
            merged.addAll(first);
        }
        if (second != null) {
            merged.addAll(second);
        }
        return merged;
    }

    private static List<String> resourceLocationIds(Supplier<? extends Collection<ResourceLocation>> supplier) {
        try {
            Collection<ResourceLocation> values = supplier.get();
            if (values == null || values.isEmpty()) {
                return List.of();
            }
            return values.stream()
                    .filter(java.util.Objects::nonNull)
                    .map(ResourceLocation::toString)
                    .sorted(Comparator.naturalOrder())
                    .distinct()
                    .toList();
        } catch (RuntimeException exception) {
            return List.of();
        }
    }

    record IdAlias(String alias, Class<?> wrapperClass, Supplier<List<String>> ids, String specialTypeReference) {
        IdAlias(String alias, Class<?> wrapperClass, Supplier<List<String>> ids) {
            this(alias, wrapperClass, ids, null);
        }

        static IdAlias rawType(String alias, Class<?> wrapperClass, String specialTypeReference) {
            return new IdAlias(alias, wrapperClass, () -> List.of(), specialTypeReference);
        }
    }
}
