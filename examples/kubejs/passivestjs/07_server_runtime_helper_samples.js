// Sample only.
// Put this into kubejs/server_scripts/ when you want a focused player-bound runtime helper example.
//
// This mirrors the runtime helper ids used throughout the runtime helpers guide.

var pstShowcaseRuntimeHelperSampleComponent = Java.loadClass('net.minecraft.network.chat.Component')

var sparkSkillId = 'kubejs:passivestjs/runtime_spark'
var pulseSkillId = 'kubejs:passivestjs/runtime_pulse'
var thresholdSkillId = 'kubejs:passivestjs/runtime_threshold'

ServerEvents.entityHurt(event => {
  var player = event.source.player
  if (!player) {
    return
  }

  var playerView = PassiveSkillTreeJS.player(player)
  if (!playerView) {
    return
  }

  var sparkSkill = playerView.skill(sparkSkillId)
  var pulseSkill = playerView.skill(pulseSkillId)
  var thresholdSkill = playerView.skill(thresholdSkillId)
  var lines = []

  if (thresholdSkill && !thresholdSkill.learned()) {
    lines.push(`threshold canLearn=${thresholdSkill.canLearn()} requirementCount=${thresholdSkill.requirements().length}`)
    for (var requirement of thresholdSkill.requirements()) {
      lines.push(`requirement ${requirement.typeId()} passed=${requirement.passed()}`)
    }
  }

  if (sparkSkill && sparkSkill.learned()) {
    for (var listener of sparkSkill.listeners()) {
      var node = listener.node()
      lines.push(`spark listener ${listener.typeId()} mode=${node ? node.string('mode').orElse('<missing>') : '<none>'}`)
    }
  }

  if (pulseSkill && pulseSkill.learned()) {
    for (var bonus of pulseSkill.bonuses()) {
      if (bonus.typeId() !== 'kubejs:smoke_bonus') {
        continue
      }

      var node = bonus.node()
      lines.push(`pulse bonus ${bonus.typeId()} effect=${node ? node.string('effect').orElse('<missing>') : '<none>'} duration=${node ? node.integer('duration').orElse(80) : -1}`)
    }

    for (var listener of pulseSkill.listeners()) {
      var node = listener.node()
      lines.push(`pulse listener ${listener.typeId()} mode=${node ? node.string('mode').orElse('<missing>') : '<none>'}`)
    }
  }

  for (var line of lines) {
    player.sendSystemMessage(pstShowcaseRuntimeHelperSampleComponent.literal(`[PassiveSTJS Showcase Runtime Helper] ${line}`))
  }
})
