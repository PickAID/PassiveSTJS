package com.pickaid.passivestjs.kubejs.registry;

import com.pickaid.passivestjs.kubejs.PassiveSTJSKubePlugin;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTDamageConditionSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTEnchantmentConditionSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTEventListenerSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTFloatFunctionSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTItemBonusSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTItemConditionSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTLivingConditionSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTLivingMultiplierSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillBonusSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillRequirementSerializerBuilder;
import com.pickaid.passivestjs.runtime.PSTSerializerObjectIndex;
import com.pickaid.passivestjs.schema.PSTNodeFamily;
import com.pickaid.passivestjs.schema.PSTSchemaFieldKind;
import com.pickaid.passivestjs.schema.PSTSchemaRegistry;
import org.junit.jupiter.api.AfterEach;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class PSTRegistryInfosTest {
    @AfterEach
    void clearMetadata() {
        PSTSchemaRegistry.clear();
        PSTSerializerMetadataIndex.clear();
        PSTSerializerObjectIndex.clear();
    }

    @BeforeAll
    static void initPlugin() throws ReflectiveOperationException {
        Field bootstrapped = net.minecraft.server.Bootstrap.class.getDeclaredField("isBootstrapped");
        bootstrapped.setAccessible(true);
        bootstrapped.setBoolean(null, true);
        new PassiveSTJSKubePlugin().init();
    }

    @Test
    void registersAllRealPstRegistriesAsKubejsRegistryInfos() {
        assertSame(PSTRegistryInfos.SKILL_BONUSES, RegistryInfo.of(PSTRegistryTargets.SKILL_BONUSES));
        assertSame(PSTRegistryInfos.LIVING_MULTIPLIERS, RegistryInfo.of(PSTRegistryTargets.LIVING_MULTIPLIERS));
        assertSame(PSTRegistryInfos.LIVING_CONDITIONS, RegistryInfo.of(PSTRegistryTargets.LIVING_CONDITIONS));
        assertSame(PSTRegistryInfos.DAMAGE_CONDITIONS, RegistryInfo.of(PSTRegistryTargets.DAMAGE_CONDITIONS));
        assertSame(PSTRegistryInfos.ITEM_CONDITIONS, RegistryInfo.of(PSTRegistryTargets.ITEM_CONDITIONS));
        assertSame(PSTRegistryInfos.ENCHANTMENT_CONDITIONS, RegistryInfo.of(PSTRegistryTargets.ENCHANTMENT_CONDITIONS));
        assertSame(PSTRegistryInfos.EVENT_LISTENERS, RegistryInfo.of(PSTRegistryTargets.EVENT_LISTENERS));
        assertSame(PSTRegistryInfos.FLOAT_FUNCTIONS, RegistryInfo.of(PSTRegistryTargets.FLOAT_FUNCTIONS));
        assertSame(PSTRegistryInfos.SKILL_REQUIREMENTS, RegistryInfo.of(PSTRegistryTargets.SKILL_REQUIREMENTS));
        assertSame(PSTRegistryInfos.ITEM_BONUSES, RegistryInfo.of(PSTRegistryTargets.ITEM_BONUSES));
    }

    @Test
    void installsDefaultBuilderForEachRealPstRegistry() {
        assertDefaultType(PSTRegistryInfos.SKILL_BONUSES, PSTSkillBonusSerializerBuilder.class);
        assertDefaultType(PSTRegistryInfos.LIVING_MULTIPLIERS, PSTLivingMultiplierSerializerBuilder.class);
        assertDefaultType(PSTRegistryInfos.LIVING_CONDITIONS, PSTLivingConditionSerializerBuilder.class);
        assertDefaultType(PSTRegistryInfos.DAMAGE_CONDITIONS, PSTDamageConditionSerializerBuilder.class);
        assertDefaultType(PSTRegistryInfos.ITEM_CONDITIONS, PSTItemConditionSerializerBuilder.class);
        assertDefaultType(PSTRegistryInfos.ENCHANTMENT_CONDITIONS, PSTEnchantmentConditionSerializerBuilder.class);
        assertDefaultType(PSTRegistryInfos.EVENT_LISTENERS, PSTEventListenerSerializerBuilder.class);
        assertDefaultType(PSTRegistryInfos.FLOAT_FUNCTIONS, PSTFloatFunctionSerializerBuilder.class);
        assertDefaultType(PSTRegistryInfos.SKILL_REQUIREMENTS, PSTSkillRequirementSerializerBuilder.class);
        assertDefaultType(PSTRegistryInfos.ITEM_BONUSES, PSTItemBonusSerializerBuilder.class);
    }

    @Test
    void defaultBuildersPublishSchemaMetadataWhenCreated() throws ReflectiveOperationException {
        for (var fixture : List.of(
                new RegistryFixture(PSTRegistryInfos.SKILL_BONUSES, PSTNodeFamily.SKILL_BONUS, "skill_bonus"),
                new RegistryFixture(PSTRegistryInfos.LIVING_MULTIPLIERS, PSTNodeFamily.MULTIPLIER, "living_multiplier"),
                new RegistryFixture(PSTRegistryInfos.LIVING_CONDITIONS, PSTNodeFamily.LIVING_CONDITION, "living_condition"),
                new RegistryFixture(PSTRegistryInfos.DAMAGE_CONDITIONS, PSTNodeFamily.DAMAGE_CONDITION, "damage_condition"),
                new RegistryFixture(PSTRegistryInfos.ITEM_CONDITIONS, PSTNodeFamily.ITEM_CONDITION, "item_condition"),
                new RegistryFixture(PSTRegistryInfos.ENCHANTMENT_CONDITIONS, PSTNodeFamily.ENCHANTMENT_CONDITION, "enchantment_condition"),
                new RegistryFixture(PSTRegistryInfos.EVENT_LISTENERS, PSTNodeFamily.EVENT_LISTENER, "event_listener"),
                new RegistryFixture(PSTRegistryInfos.FLOAT_FUNCTIONS, PSTNodeFamily.NUMERIC_VALUE, "float_function"),
                new RegistryFixture(PSTRegistryInfos.SKILL_REQUIREMENTS, PSTNodeFamily.SKILL_REQUIREMENT, "skill_requirement"),
                new RegistryFixture(PSTRegistryInfos.ITEM_BONUSES, PSTNodeFamily.ITEM_BONUS, "item_bonus")
        )) {
            var id = new ResourceLocation("kubejs", fixture.path());
            AbstractPSTSerializerBuilder<?> builder = newBuilderFromRegisteredFactory(fixture.registryInfo(), id);
            builder.schema(schema -> schema.field("value", field -> field.kind(PSTSchemaFieldKind.STRING).required()));

            builder.createObject();

            assertEquals(
                    PSTSchemaFieldKind.STRING,
                    PSTSchemaRegistry.find(fixture.nodeFamily(), id).orElseThrow().field("value").kind()
            );
        }
    }

    private static void assertDefaultType(RegistryInfo<?> registryInfo, Class<?> builderClass) {
        assertNotNull(registryInfo.getDefaultType());
        assertEquals("default", registryInfo.getDefaultType().type());
        assertSame(builderClass, registryInfo.getDefaultType().builderClass());
    }

    @SuppressWarnings("unchecked")
    private static AbstractPSTSerializerBuilder<?> newBuilderFromRegisteredFactory(RegistryInfo<?> registryInfo, ResourceLocation id) {
        BuilderBase<?> builder = registryInfo.getDefaultType().factory().createBuilder(id);
        return (AbstractPSTSerializerBuilder<?>) builder;
    }

    private record RegistryFixture(RegistryInfo<?> registryInfo, PSTNodeFamily nodeFamily, String path) {
    }
}
