package com.pickaid.passivestjs.runtime;

import com.google.gson.JsonObject;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadataIndex;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillBonusSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillRequirementSerializerBuilder;
import com.pickaid.passivestjs.runtime.tooltip.PSTTooltipSpecRegistry;
import com.pickaid.passivestjs.schema.PSTSchemaRegistry;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.requirement.SkillRequirement;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PSTRuntimeTypeIdsTest {
    @BeforeAll
    static void bootstrapMinecraftRegistries() throws ReflectiveOperationException {
        var bootstrapped = net.minecraft.server.Bootstrap.class.getDeclaredField("isBootstrapped");
        bootstrapped.setAccessible(true);
        bootstrapped.setBoolean(null, true);
    }

    @AfterEach
    void clearIndexes() {
        PSTSchemaRegistry.clear();
        PSTSerializerMetadataIndex.clear();
        PSTSerializerObjectIndex.clear();
        PSTTooltipSpecRegistry.clear();
    }

    @Test
    void customRuntimeTypesExposeRegistryIdsAndNodes() {
        SkillBonus.Serializer bonusSerializer = new PSTSkillBonusSerializerBuilder(
                new ResourceLocation("kubejs", "typed_bonus")
        ).createObject();
        SkillRequirement.Serializer requirementSerializer = new PSTSkillRequirementSerializerBuilder(
                new ResourceLocation("kubejs", "typed_requirement")
        ).test(context -> true).createObject();

        JsonObject bonusJson = new JsonObject();
        bonusJson.addProperty("type", "kubejs:typed_bonus");
        SkillBonus<?> bonus = bonusSerializer.deserialize(bonusJson);

        JsonObject requirementJson = new JsonObject();
        requirementJson.addProperty("type", "kubejs:typed_requirement");
        SkillRequirement<?> requirement = requirementSerializer.deserialize(requirementJson);

        assertEquals("kubejs:typed_bonus", PSTRuntimeTypeIds.skillBonusId(bonus));
        assertEquals("kubejs:typed_requirement", PSTRuntimeTypeIds.skillRequirementId(requirement));
        assertNotNull(((PSTCustomRuntimeSkillBonus) bonus).node());
        assertNotNull(((PSTCustomRuntimeSkillRequirement) requirement).node());
    }
}
