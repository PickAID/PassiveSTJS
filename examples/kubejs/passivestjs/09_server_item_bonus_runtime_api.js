// Sample only.
// Put this into kubejs/server_scripts/ when you want to read or mutate PST item bonuses directly on an ItemStack.

var pstSampleItemBonusRuntimeComponent = Java.loadClass('net.minecraft.network.chat.Component')

ItemEvents.rightClicked(event => {
  var stackView = PassiveSkillTreeJS.item(event.item)
  if (!stackView) {
    return
  }

  if (event.player.isCrouching()) {
    var removed = stackView.clearBonuses()
    event.player.sendSystemMessage(pstSampleItemBonusRuntimeComponent.literal(`[PassiveSTJS Sample] removed=${removed}`))
    return
  }

  if (stackView.bonusCount() === 0) {
    stackView.addItemBonus('kubejs:sample_item_bonus', bonus => {
      bonus.number('amount', 1.5)
      bonus.string('state', 'sample')
    })

    stackView.addAttributeItemBonus(bonus => {
      bonus.attribute('minecraft:generic.armor')
      bonus.amount(2)
      bonus.operation(0)
      bonus.modifierId('8516d3f4-373e-42c3-9138-3215993b34c4')
      bonus.bonusName('passivestjs.sample')
    })
  }

  for (var itemBonus of stackView.bonuses()) {
    var node = itemBonus.node()
    var amount = node ? node.number('amount').orElse(0) : 0
    event.player.sendSystemMessage(pstSampleItemBonusRuntimeComponent.literal(`[PassiveSTJS Sample] type=${itemBonus.typeId()} amount=${amount}`))
  }
})
