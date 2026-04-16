// Sample only.
// Put this into kubejs/server_scripts/ when you want to use it.

const PST_SAMPLE_TREE_ID = 'kubejs:sample_runtime_tree'
const PST_SAMPLE_ROOT_ID = 'kubejs:sample_runtime_tree/root'
const PST_SAMPLE_ATTACK_ID = 'kubejs:sample_runtime_tree/attack_pulse'
const PST_SAMPLE_WATER_ID = 'kubejs:sample_runtime_tree/water_pulse'
const PST_SAMPLE_REQ_ID = 'kubejs:sample_runtime_tree/last_stand'
const PST_SAMPLE_SOCKET_ID = 'kubejs:sample_runtime_tree/item_bonus_socket'

PassiveSTJSEvents.skillTreeContent(event => {
  const tree = event.editTree(PST_SAMPLE_TREE_ID)
    .title(Text.translate('kubejs.passivestjs.sample.tree.title'))

  tree.startingSkill(PST_SAMPLE_ROOT_ID)
    .title(Text.translate('kubejs.passivestjs.sample.root.title'))
    .descriptionLine(Text.translate('kubejs.passivestjs.sample.root.desc.0'))
    .icon('minecraft:textures/item/nether_star.png')
    .position(0, 0)

  tree.skill(PST_SAMPLE_ATTACK_ID)
    .title(Text.translate('kubejs.passivestjs.sample.attack.title'))
    .descriptionLine(Text.translate('kubejs.passivestjs.sample.attack.desc.0'))
    .icon('minecraft:textures/item/iron_sword.png')
    .bonus('kubejs:sample_runtime_bonus', bonus => {
      bonus.effect('minecraft:glowing')
      bonus.duration(80)
      bonus.amplifier(0)
      bonus.eventListener('kubejs:sample_attack_or_tick', listener => {
        listener.string('mode', 'attack')
        listener.target('enemy')
        listener.whenPlayerSchema('kubejs:sample_combat_state', condition => {
          condition.set('state', 'crouching')
        })
        listener.scalePlayer('kubejs:sample_missing_health_multiplier', multiplier => {})
      })
    })
    .positionPolar(72, 0)
    .connect(PST_SAMPLE_ROOT_ID)

  tree.skill(PST_SAMPLE_WATER_ID)
    .title(Text.translate('kubejs.passivestjs.sample.water.title'))
    .descriptionLine(Text.translate('kubejs.passivestjs.sample.water.desc.0'))
    .icon('minecraft:textures/item/heart_of_the_sea.png')
    .bonus('kubejs:sample_runtime_bonus', bonus => {
      bonus.effect('minecraft:regeneration')
      bonus.duration(40)
      bonus.amplifier(0)
      bonus.eventListener('kubejs:sample_attack_or_tick', listener => {
        listener.string('mode', 'tick')
        listener.target('player')
        listener.cooldown(20)
        listener.whenPlayerSchema('kubejs:sample_combat_state', condition => {
          condition.set('state', 'underwater')
        })
      })
    })
    .positionPolar(72, 90)
    .connect(PST_SAMPLE_ROOT_ID)

  tree.skill(PST_SAMPLE_REQ_ID)
    .title(Text.translate('kubejs.passivestjs.sample.requirement.title'))
    .descriptionLine(Text.translate('kubejs.passivestjs.sample.requirement.desc.0'))
    .icon('minecraft:textures/item/golden_apple.png')
    .requirement('kubejs:sample_low_health_requirement', requirement => {
      requirement.number('ratio', 0.5)
    })
    .bonus('skilltree:damage', bonus => {
      bonus.amount(2)
      bonus.operation(0)
    })
    .positionPolar(72, 180)
    .connect(PST_SAMPLE_ROOT_ID)

  tree.skill(PST_SAMPLE_SOCKET_ID)
    .title(Text.translate('kubejs.passivestjs.sample.socket.title'))
    .descriptionLine(Text.translate('kubejs.passivestjs.sample.socket.desc.0'))
    .descriptionLine(Text.translate('kubejs.passivestjs.sample.socket.desc.1'))
    .icon('minecraft:textures/item/copper_ingot.png')
    .positionPolar(72, 270)
    .connect(PST_SAMPLE_ROOT_ID)
})
