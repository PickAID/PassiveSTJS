// Sample only.
// Put this into kubejs/server_scripts/ when you want to use it.

var pstSampleRuntimeComponent = Java.loadClass('net.minecraft.network.chat.Component')
var pstSampleRuntimeInteractionHand = Java.loadClass('net.minecraft.world.InteractionHand')

var triggerItem = 'minecraft:blaze_rod'
var treeId = 'kubejs:sample_runtime_tree'
var skillId = 'kubejs:sample_runtime_tree/water_pulse'

// event.player: Internal.ServerPlayer
// PassiveSkillTreeJS.player(event.player): PSTPlayerView
ItemEvents.rightClicked(triggerItem, event => {
  if (event.hand == null || !event.hand.equals(pstSampleRuntimeInteractionHand.MAIN_HAND)) {
    return
  }

  var playerView = PassiveSkillTreeJS.player(event.player)
  if (playerView == null) {
    return
  }

  var treeView = PassiveSkillTreeJS.tree(treeId)
  var skillView = PassiveSkillTreeJS.skill(skillId)

  var beforePoints = playerView.skillPoints()
  var hadSkill = playerView.hasSkill(skillId)

  playerView.grantSkillPoints(3)
  var learned = playerView.learnWithoutSkillPointCost(skillId)

  var learnedIds = playerView.learnedSkillIds().join(', ')
  var treeHasSkill = treeView != null && treeView.hasSkill(skillId)
  var skillTitle = skillView == null ? pstSampleRuntimeComponent.literal('<missing>') : skillView.title()

  event.player.sendSystemMessage(pstSampleRuntimeComponent.literal('[PassiveSTJS Sample Runtime] '))
  event.player.sendSystemMessage(pstSampleRuntimeComponent.literal(`beforePoints=${beforePoints} afterPoints=${playerView.skillPoints()} hadSkill=${hadSkill} learned=${learned}`))
  event.player.sendSystemMessage(pstSampleRuntimeComponent.literal(`treeHasSkill=${treeHasSkill} learnedSkills=[${learnedIds}]`))
  event.player.sendSystemMessage(skillTitle)
})

ServerEvents.commandRegistry(event => {
  var Commands = event.commands

  event.register(
    Commands.literal('pst_sample_reset')
      .requires(source => source.hasPermission(2))
      .executes(context => {
        const player = context.source.playerOrException
        const playerView = PassiveSkillTreeJS.player(player)
        if (playerView == null) {
          return 0
        }

        playerView.reset()
        playerView.consumeSkillPoints(playerView.skillPoints())
        player.sendSystemMessage(pstSampleRuntimeComponent.literal('[PassiveSTJS Sample Runtime] reset complete'))
        return 1
      })
  )
})
