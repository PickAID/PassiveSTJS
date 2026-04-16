// Sample only.
// Put this into kubejs/server_scripts/ when you want a focused requirement + listener chain example.
//
// Expected tooltip shape after matching lang entries:
//   attack time / listener prefix + condition prefix + effect text
//   requirement text on the same skill

const PST_SAMPLE_CHAIN_TREE_ID = 'kubejs:sample_requirement_listener_tree'
const PST_SAMPLE_CHAIN_ROOT_ID = 'kubejs:sample_requirement_listener_tree/root'
const PST_SAMPLE_CHAIN_SKILL_ID = 'kubejs:sample_requirement_listener_tree/execution_window'

PassiveSTJSEvents.skillTreeContent(event => {
  const tree = event.editTree(PST_SAMPLE_CHAIN_TREE_ID)
    .title(Text.translate('kubejs.passivestjs.sample.chain.tree.title'))

  tree.startingSkill(PST_SAMPLE_CHAIN_ROOT_ID)
    .title(Text.translate('kubejs.passivestjs.sample.chain.root.title'))
    .descriptionLine(Text.translate('kubejs.passivestjs.sample.chain.root.desc.0'))
    .icon('minecraft:textures/item/amethyst_shard.png')
    .position(0, 0)

  tree.skill(PST_SAMPLE_CHAIN_SKILL_ID)
    .title(Text.translate('kubejs.passivestjs.sample.chain.skill.title'))
    .descriptionLine(Text.translate('kubejs.passivestjs.sample.chain.skill.desc.0'))
    .descriptionLine(Text.translate('kubejs.passivestjs.sample.chain.skill.desc.1'))
    .icon('minecraft:textures/item/diamond_sword.png')
    .requirement('kubejs:sample_low_health_requirement', requirement => {
      requirement.number('ratio', 0.4)
    })
    .bonus('kubejs:sample_runtime_bonus', bonus => {
      bonus.effect('minecraft:glowing')
      bonus.duration(100)
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
    .connect(PST_SAMPLE_CHAIN_ROOT_ID)
})
