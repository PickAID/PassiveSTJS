package com.pickaid.passivestjs.kubejs.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pickaid.passivestjs.kubejs.builder.BonusBuilder;
import com.pickaid.passivestjs.kubejs.builder.ConditionBuilder;
import com.pickaid.passivestjs.kubejs.builder.ItemBonusBuilder;
import com.pickaid.passivestjs.kubejs.builder.PSTNodeWriter;
import com.pickaid.passivestjs.kubejs.content.JsonHelper;
import com.pickaid.passivestjs.kubejs.id.PSTItemBonusId;
import com.pickaid.passivestjs.kubejs.id.PSTItemConditionId;
import com.pickaid.passivestjs.kubejs.id.PSTItemId;
import com.pickaid.passivestjs.kubejs.id.PSTItemTagId;
import com.pickaid.passivestjs.kubejs.id.PSTPotionId;
import com.pickaid.passivestjs.kubejs.id.PSTSkillBonusId;
import com.pickaid.passivestjs.kubejs.recipe.builder.PSTAttributeSkillBonusBuilder;
import com.pickaid.passivestjs.kubejs.recipe.builder.PSTItemBonusListBuilder;
import com.pickaid.passivestjs.kubejs.recipe.builder.PSTSkillBonusItemBonusBuilder;
import com.pickaid.passivestjs.kubejs.type.PSTEquipmentType;
import com.pickaid.passivestjs.schema.PSTNodeFamily;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public final class PSTWorkbenchItemBonusRecipeJS extends RecipeJS {
    public static final ResourceLocation TYPE = new ResourceLocation("skilltree", "workbench_item_bonus");
    public static final RecipeSchema SCHEMA = new RecipeSchema(
            PSTWorkbenchItemBonusRecipeJS.class,
            PSTWorkbenchItemBonusRecipeJS::new
    );

    @Info(value = "Sets the base item condition using a typed item condition id.", params = {
            @Param(name = "typeId", value = "The base item condition type id."),
            @Param(name = "consumer", value = "The callback that configures the base item condition builder.")
    })
    public PSTWorkbenchItemBonusRecipeJS baseItemCondition(PSTItemConditionId typeId, Consumer<ConditionBuilder> consumer) {
        ConditionBuilder builder = new ConditionBuilder(PSTItemConditionId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return baseItemCondition(builder);
    }

    @Info(value = "Sets the base item condition through the schema writer for the given item condition id.", params = {
            @Param(name = "typeId", value = "The base item condition type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields.")
    })
    public PSTWorkbenchItemBonusRecipeJS baseItemConditionSchema(PSTItemConditionId typeId, Consumer<PSTNodeWriter> consumer) {
        PSTNodeWriter writer = new PSTNodeWriter(PSTNodeFamily.ITEM_CONDITION, PSTItemConditionId.parse(typeId).location());
        if (consumer != null) {
            consumer.accept(writer);
        }
        return setNode("base_item_condition", writer);
    }

    @Info("Sets a prebuilt base item condition builder.")
    public PSTWorkbenchItemBonusRecipeJS baseItemCondition(ConditionBuilder value) {
        return setNode("base_item_condition", value);
    }

    @Info("Marks this recipe as matching any item by using PST's built-in skilltree:none item condition.")
    public PSTWorkbenchItemBonusRecipeJS noBaseCondition() {
        return baseItemCondition(new ConditionBuilder("skilltree:none"));
    }

    @HideFromJS
    public PSTWorkbenchItemBonusRecipeJS baseEquipmentTypeCondition(String value) {
        return baseEquipmentTypeCondition(PSTEquipmentType.parse(value));
    }

    @Info(value = "Sets PST's built-in skilltree:equipment_type base item condition.", params = {
            @Param(name = "equipmentType", value = "The required equipment type.")
    })
    public PSTWorkbenchItemBonusRecipeJS baseEquipmentTypeCondition(PSTEquipmentType equipmentType) {
        return baseItemCondition(new ConditionBuilder("skilltree:equipment_type").equipmentType(equipmentType));
    }

    @HideFromJS
    public PSTWorkbenchItemBonusRecipeJS baseItemIdCondition(String value) {
        return baseItemIdCondition(PSTItemId.parse(value));
    }

    @Info(value = "Sets PST's built-in skilltree:item_id base item condition.", params = {
            @Param(name = "itemId", value = "The required item id.")
    })
    public PSTWorkbenchItemBonusRecipeJS baseItemIdCondition(PSTItemId itemId) {
        return baseItemCondition(new ConditionBuilder("skilltree:item_id").itemId(itemId));
    }

    @HideFromJS
    public PSTWorkbenchItemBonusRecipeJS baseTagCondition(String value) {
        return baseTagCondition(PSTItemTagId.parse(value));
    }

    @Info(value = "Sets PST's built-in skilltree:tag base item condition.", params = {
            @Param(name = "tagId", value = "The required item tag id.")
    })
    public PSTWorkbenchItemBonusRecipeJS baseTagCondition(PSTItemTagId tagId) {
        return baseItemCondition(new ConditionBuilder("skilltree:tag").tagId(tagId));
    }

    @HideFromJS
    public PSTWorkbenchItemBonusRecipeJS basePotionCondition(String value) {
        return basePotionCondition(PSTPotionId.parse(value));
    }

    @Info(value = "Sets PST's built-in skilltree:potion base item condition.", params = {
            @Param(name = "potionId", value = "The required potion id.")
    })
    public PSTWorkbenchItemBonusRecipeJS basePotionCondition(PSTPotionId potionId) {
        return baseItemCondition(new ConditionBuilder("skilltree:potion").potionType(potionId));
    }

    @Info("Sets PST's built-in skilltree:food base item condition.")
    public PSTWorkbenchItemBonusRecipeJS baseFoodCondition() {
        return baseItemCondition(new ConditionBuilder("skilltree:food"));
    }

    @Info("Sets PST's built-in skilltree:enchanted base item condition.")
    public PSTWorkbenchItemBonusRecipeJS baseEnchantedCondition() {
        return baseItemCondition(new ConditionBuilder("skilltree:enchanted"));
    }

    @Info(value = "Sets the item bonus using a typed item bonus id.", params = {
            @Param(name = "typeId", value = "The item bonus type id."),
            @Param(name = "consumer", value = "The callback that configures the item bonus builder.")
    })
    public PSTWorkbenchItemBonusRecipeJS itemBonus(PSTItemBonusId typeId, Consumer<ItemBonusBuilder> consumer) {
        ItemBonusBuilder builder = new ItemBonusBuilder(PSTItemBonusId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return itemBonus(builder);
    }

    @Info(value = "Sets the item bonus through the schema writer for the given item bonus id.", params = {
            @Param(name = "typeId", value = "The item bonus type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields.")
    })
    public PSTWorkbenchItemBonusRecipeJS itemBonusSchema(PSTItemBonusId typeId, Consumer<PSTNodeWriter> consumer) {
        PSTNodeWriter writer = new PSTNodeWriter(PSTNodeFamily.ITEM_BONUS, PSTItemBonusId.parse(typeId).location());
        if (consumer != null) {
            consumer.accept(writer);
        }
        return setNode("item_bonus", writer);
    }

    @Info("Sets a prebuilt item bonus builder.")
    public PSTWorkbenchItemBonusRecipeJS itemBonus(ItemBonusBuilder value) {
        return setNode("item_bonus", value);
    }

    @Info("Sets a prebuilt built-in skilltree:skill_bonus item bonus builder.")
    public PSTWorkbenchItemBonusRecipeJS itemBonus(PSTSkillBonusItemBonusBuilder value) {
        return setNode("item_bonus", value);
    }

    @Info("Sets a prebuilt built-in skilltree:item_bonus_list item bonus builder.")
    public PSTWorkbenchItemBonusRecipeJS itemBonus(PSTItemBonusListBuilder value) {
        return setNode("item_bonus", value);
    }

    @Info(value = "Sets PST's built-in skilltree:skill_bonus item bonus with a typed nested skill bonus id.", params = {
            @Param(name = "typeId", value = "The nested skill bonus type id."),
            @Param(name = "consumer", value = "The callback that configures the nested skill bonus builder.")
    })
    public PSTWorkbenchItemBonusRecipeJS skillBonusItemBonus(PSTSkillBonusId typeId, Consumer<BonusBuilder> consumer) {
        return itemBonus(new PSTSkillBonusItemBonusBuilder().skillBonus(typeId, consumer));
    }

    @Info(value = "Sets PST's built-in skilltree:skill_bonus item bonus using schema-writer configuration for the nested skill bonus.", params = {
            @Param(name = "typeId", value = "The nested skill bonus type id."),
            @Param(name = "consumer", value = "The callback that sets nested schema fields.")
    })
    public PSTWorkbenchItemBonusRecipeJS skillBonusItemBonusSchema(PSTSkillBonusId typeId, Consumer<PSTNodeWriter> consumer) {
        return itemBonus(new PSTSkillBonusItemBonusBuilder().skillBonusSchema(typeId, consumer));
    }

    @Info("Sets PST's built-in skilltree:skill_bonus item bonus with a built-in skilltree:attribute nested skill bonus.")
    public PSTWorkbenchItemBonusRecipeJS attributeItemBonus(Consumer<PSTAttributeSkillBonusBuilder> consumer) {
        return itemBonus(new PSTSkillBonusItemBonusBuilder().attribute(consumer));
    }

    @Info("Sets PST's built-in skilltree:item_bonus_list item bonus.")
    public PSTWorkbenchItemBonusRecipeJS itemBonusList(Consumer<PSTItemBonusListBuilder> consumer) {
        PSTItemBonusListBuilder builder = new PSTItemBonusListBuilder();
        if (consumer != null) {
            consumer.accept(builder);
        }
        return itemBonus(builder);
    }

    @Info(value = "Adds an ingredient to this workbench item bonus recipe.", params = {
            @Param(name = "ingredient", value = "The ingredient input."),
            @Param(name = "requiredAmount", value = "The required ingredient amount.")
    })
    public PSTWorkbenchItemBonusRecipeJS ingredient(InputItem ingredient, Number requiredAmount) {
        return appendIngredient(ingredient.kjs$asIngredient().toJson(), requiredAmount);
    }

    @Info("Adds an ingredient to this workbench item bonus recipe with a required amount of 1.")
    public PSTWorkbenchItemBonusRecipeJS ingredient(InputItem ingredient) {
        return ingredient(ingredient, 1);
    }

    @HideFromJS
    public PSTWorkbenchItemBonusRecipeJS ingredientItem(String value, Number requiredAmount) {
        return ingredientItem(PSTItemId.parse(value), requiredAmount);
    }

    @Info(value = "Adds a direct item ingredient to this workbench item bonus recipe.", params = {
            @Param(name = "itemId", value = "The required item id."),
            @Param(name = "requiredAmount", value = "The required ingredient amount.")
    })
    public PSTWorkbenchItemBonusRecipeJS ingredientItem(PSTItemId itemId, Number requiredAmount) {
        return appendIngredient(itemId.id(), requiredAmount);
    }

    @Info("Adds a direct item ingredient to this workbench item bonus recipe with a required amount of 1.")
    public PSTWorkbenchItemBonusRecipeJS ingredientItem(PSTItemId itemId) {
        return ingredientItem(itemId, 1);
    }

    @HideFromJS
    public PSTWorkbenchItemBonusRecipeJS ingredientTag(String value, Number requiredAmount) {
        return ingredientTag(PSTItemTagId.parse(value), requiredAmount);
    }

    @Info(value = "Adds a tag ingredient to this workbench item bonus recipe.", params = {
            @Param(name = "tagId", value = "The required item tag id."),
            @Param(name = "requiredAmount", value = "The required ingredient amount.")
    })
    public PSTWorkbenchItemBonusRecipeJS ingredientTag(PSTItemTagId tagId, Number requiredAmount) {
        return appendIngredient(Map.of("tag", tagId.id()), requiredAmount);
    }

    @Info("Adds a tag ingredient to this workbench item bonus recipe with a required amount of 1.")
    public PSTWorkbenchItemBonusRecipeJS ingredientTag(PSTItemTagId tagId) {
        return ingredientTag(tagId, 1);
    }

    @Info(value = "Marks whether the PST workbench should require the passive skill before socketing this item bonus.", params = {
            @Param(name = "value", value = "Whether the passive skill is required.")
    })
    public PSTWorkbenchItemBonusRecipeJS requiresPassiveSkill(boolean value) {
        ensureJson().addProperty("requires_passive_skill", value);
        save();
        return this;
    }

    @Info("Marks this recipe as requiring the passive skill before socketing the item bonus.")
    public PSTWorkbenchItemBonusRecipeJS requiresPassiveSkill() {
        return requiresPassiveSkill(true);
    }

    @Override
    public void afterLoaded() {
        JsonObject recipeJson = ensureJson();
        if (!recipeJson.has("type")) {
            recipeJson.addProperty("type", TYPE.toString());
        }
        if (!recipeJson.has("requires_passive_skill")) {
            recipeJson.addProperty("requires_passive_skill", false);
        }
        if (!recipeJson.has("ingredients")) {
            recipeJson.add("ingredients", JsonHelper.array());
        }
        super.afterLoaded();
    }

    @Override
    @Nullable
    public Recipe<?> createRecipe() {
        if (removed) {
            return null;
        }
        JsonObject recipeJson = ensureJson();
        if (!recipeJson.has("type")) {
            recipeJson.addProperty("type", TYPE.toString());
        }
        if (!recipeJson.has("requires_passive_skill")) {
            recipeJson.addProperty("requires_passive_skill", false);
        }
        if (!recipeJson.has("ingredients")) {
            recipeJson.add("ingredients", JsonHelper.array());
        }
        if (!recipeJson.has("base_item_condition") || !recipeJson.get("base_item_condition").isJsonObject()) {
            throw new RecipeExceptionJS("skilltree:workbench_item_bonus requires base_item_condition");
        }
        if (!recipeJson.has("item_bonus") || !recipeJson.get("item_bonus").isJsonObject()) {
            throw new RecipeExceptionJS("skilltree:workbench_item_bonus requires item_bonus");
        }
        return super.createRecipe();
    }

    private PSTWorkbenchItemBonusRecipeJS appendIngredient(Object ingredientValue, Number requiredAmount) {
        JsonObject recipeJson = ensureJson();
        JsonArray ingredients = recipeJson.has("ingredients") && recipeJson.get("ingredients").isJsonArray()
                ? JsonHelper.copy(recipeJson.getAsJsonArray("ingredients")).getAsJsonArray()
                : JsonHelper.array();
        JsonObject entry = new JsonObject();
        entry.add("ingredient", JsonHelper.requireElement(ingredientValue, "ingredient"));
        entry.addProperty("required_amount", requiredAmount == null ? 1 : requiredAmount.intValue());
        ingredients.add(entry);
        recipeJson.add("ingredients", ingredients);
        save();
        return this;
    }

    private PSTWorkbenchItemBonusRecipeJS setNode(String key, Object value) {
        ensureJson().add(key, JsonHelper.requireObject(value, key));
        save();
        return this;
    }

    private JsonObject ensureJson() {
        if (json == null) {
            json = new JsonObject();
        }
        return json;
    }
}
