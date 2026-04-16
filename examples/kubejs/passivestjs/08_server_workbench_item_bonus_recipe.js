// Sample only.
// Put this into kubejs/server_scripts/ when you want a RecipeJS wrapper for `skilltree:workbench_item_bonus`.
//
// This example uses PST's built-in `skilltree:skill_bonus` item bonus, which is the native path
// for a workbench socket that actually grants item behavior.
// This checked-in sample is the canonical reference for the RecipeJS wrapper.

ServerEvents.recipes(event => {
  event.recipes.skilltree.workbench_item_bonus()
    .baseEquipmentTypeCondition('shield')
    .ingredientTag('forge:ingots/copper', 2)
    .attributeItemBonus(bonus => {
      bonus.attribute('minecraft:generic.armor')
      bonus.amount(2)
      bonus.modifierId('8516d3f4-373e-42c3-9138-3215993b34c4')
      bonus.bonusName('Workbench Upgrade')
      bonus.operation(0)
    })
    .requiresPassiveSkill()
    .id('kubejs:passivestjs_showcase_shield_socket')
})
