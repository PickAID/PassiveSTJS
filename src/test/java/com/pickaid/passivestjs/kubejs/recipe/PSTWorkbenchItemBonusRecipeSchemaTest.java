package com.pickaid.passivestjs.kubejs.recipe;

import com.google.gson.JsonObject;
import com.pickaid.passivestjs.kubejs.PassiveSTJSKubePlugin;
import dev.latvian.mods.kubejs.recipe.schema.RecipeNamespace;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PSTWorkbenchItemBonusRecipeSchemaTest {
    @BeforeAll
    static void bootstrapMinecraftRegistries() throws ReflectiveOperationException {
        ensureGameVersion();
        var bootstrapped = net.minecraft.server.Bootstrap.class.getDeclaredField("isBootstrapped");
        bootstrapped.setAccessible(true);
        bootstrapped.setBoolean(null, true);

        var gamePath = net.minecraftforge.fml.loading.FMLLoader.class.getDeclaredField("gamePath");
        gamePath.setAccessible(true);
        gamePath.set(null, Path.of(".").toAbsolutePath().normalize());
    }

    @AfterEach
    void clearSchemas() {
    }

    private static void ensureGameVersion() throws ReflectiveOperationException {
        try {
            net.minecraft.SharedConstants.class.getDeclaredMethod("tryDetectVersion").invoke(null);
            return;
        } catch (NoSuchMethodException ignored) {
        }
        net.minecraft.SharedConstants.class.getDeclaredMethod("m_142977_").invoke(null);
    }

    @Test
    void pluginRegistersWorkbenchItemBonusSchemaUnderSkilltreeNamespaceOnly() {
        Map<String, RecipeNamespace> namespaces = new HashMap<>();
        Map<String, ResourceLocation> mappedRecipes = new HashMap<>();

        new PassiveSTJSKubePlugin().registerRecipeSchemas(new RegisterRecipeSchemasEvent(namespaces, mappedRecipes));

        assertTrue(namespaces.containsKey("skilltree"));
        assertTrue(namespaces.get("skilltree").containsKey("workbench_item_bonus"));
        assertTrue(mappedRecipes.isEmpty());
    }

    @Test
    void workbenchItemBonusSchemaUsesZeroKeyChainDsl() {
        assertEquals(0, PSTWorkbenchItemBonusRecipeJS.SCHEMA.keys.length);
    }

    @Test
    void workbenchItemBonusRecipeDslWritesCanonicalJson() {
        PSTWorkbenchItemBonusRecipeJS recipe = new PSTWorkbenchItemBonusRecipeJS();
        recipe.json = new JsonObject();

        recipe.baseEquipmentTypeCondition("shield")
                .ingredientTag(ResourceLocation.fromNamespaceAndPath("forge", "ingots/copper").toString(), 2)
                .attributeItemBonus(bonus -> {
                    bonus.attribute(ResourceLocation.fromNamespaceAndPath("minecraft", "generic.armor").toString());
                    bonus.amount(2);
                    bonus.modifierId("8516d3f4-373e-42c3-9138-3215993b34c4");
                    bonus.bonusName("Workbench Upgrade");
                    bonus.operation(0);
                })
                .requiresPassiveSkill();

        assertEquals("skilltree:equipment_type", recipe.json.getAsJsonObject("base_item_condition").get("type").getAsString());
        assertEquals("shield", recipe.json.getAsJsonObject("base_item_condition").get("equipment_type").getAsString());
        assertEquals("forge:ingots/copper", recipe.json.getAsJsonArray("ingredients")
                .get(0).getAsJsonObject()
                .getAsJsonObject("ingredient")
                .get("tag").getAsString());
        assertEquals(2, recipe.json.getAsJsonArray("ingredients")
                .get(0).getAsJsonObject()
                .get("required_amount").getAsInt());
        assertEquals("skilltree:skill_bonus", recipe.json.getAsJsonObject("item_bonus").get("type").getAsString());
        assertEquals("skilltree:attribute", recipe.json.getAsJsonObject("item_bonus")
                .getAsJsonObject("skill_bonus")
                .get("type").getAsString());
        assertTrue(recipe.json.get("requires_passive_skill").getAsBoolean());
    }

    @Test
    void workbenchItemBonusCreateRecipeRejectsMissingBaseItemCondition() {
        PSTWorkbenchItemBonusRecipeJS recipe = new PSTWorkbenchItemBonusRecipeJS();
        recipe.json = new JsonObject();

        assertThrows(dev.latvian.mods.kubejs.recipe.RecipeExceptionJS.class, recipe::createRecipe);
    }

    @Test
    void workbenchItemBonusCreateRecipeRejectsMissingItemBonus() {
        PSTWorkbenchItemBonusRecipeJS recipe = new PSTWorkbenchItemBonusRecipeJS();
        recipe.json = new JsonObject();
        recipe.baseEquipmentTypeCondition("shield");

        assertThrows(dev.latvian.mods.kubejs.recipe.RecipeExceptionJS.class, recipe::createRecipe);
    }
}
