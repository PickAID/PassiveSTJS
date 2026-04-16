// Sample only.
// Put this into kubejs/startup_scripts/ when you want to use it.

const PST_SAMPLE_SCHEMA_FIELD_KIND = Java.loadClass('com.pickaid.passivestjs.schema.PSTSchemaFieldKind')
const PST_SAMPLE_NODE_FAMILY = Java.loadClass('com.pickaid.passivestjs.schema.PSTNodeFamily')
const PST_SAMPLE_MOB_EFFECT_INSTANCE = Java.loadClass('net.minecraft.world.effect.MobEffectInstance')
const PST_SAMPLE_RESOURCE_LOCATION = Java.loadClass('net.minecraft.resources.ResourceLocation')
const PST_SAMPLE_FORGE_REGISTRIES = Java.loadClass('net.minecraftforge.registries.ForgeRegistries')
const PST_SAMPLE_FLUIDS = Java.loadClass('net.minecraft.world.level.material.Fluids')

const PST_SAMPLE_DEFAULT_EFFECT = 'minecraft:regeneration'

StartupEvents.registry('skilltree:living_conditions', event => {
  event.create('kubejs:sample_combat_state')
    // context: PSTTooltipRenderContext
    .prefix(context => {
      const state = String(context.node().string('state').orElse('crouching'))
      return Text.translate(`living_condition.kubejs.sample_combat_state.${state}`)
    })
    .schema(schema => {
      schema.field('state', field => field.kind(PST_SAMPLE_SCHEMA_FIELD_KIND.ENUM)
        .required()
        .enumChoice('crouching')
        .enumChoice('underwater'))
    })
    // context: PSTCustomRuntimeContexts.LivingConditionContext
    .test(context => {
      const entity = context.entity()
      const state = String(context.node().string('state').orElse('crouching'))
      if (entity == null) {
        return false
      }
      if (state === 'crouching') {
        return entity.isCrouching()
      }
      if (state === 'underwater') {
        return entity.getEyeInFluidType() == PST_SAMPLE_FLUIDS.WATER.getFluidType()
      }
      return false
    })
})

StartupEvents.registry('skilltree:skill_bonus_multipliers', event => {
  event.create('kubejs:sample_missing_health_multiplier')
    // context: PSTCustomRuntimeContexts.LivingMultiplierContext
    .value(context => {
      const entity = context.entity()
      if (entity == null) {
        return 1
      }
      return entity.getHealth() <= (entity.getMaxHealth() / 2) ? 2 : 1
    })
})

StartupEvents.registry('skilltree:event_listeners', event => {
  event.create('kubejs:sample_attack_or_tick')
    // context: PSTTooltipRenderContext
    .prefix(context => {
      const mode = String(context.node().string('mode').orElse('attack'))
      return Text.translate(`event_listener.kubejs.sample_attack_or_tick.${mode}`)
    })
    .schema(schema => {
      schema.field('mode', field => field.kind(PST_SAMPLE_SCHEMA_FIELD_KIND.ENUM)
        .required()
        .enumChoice('attack')
        .enumChoice('tick'))
      schema.field('target', field => field.kind(PST_SAMPLE_SCHEMA_FIELD_KIND.ENUM)
        .defaultValue('enemy')
        .enumChoice('player')
        .enumChoice('enemy'))
      schema.field('cooldown', field => field.kind(PST_SAMPLE_SCHEMA_FIELD_KIND.INT).defaultValue(20))
      schema.field('player_condition', field => field.kind(PST_SAMPLE_SCHEMA_FIELD_KIND.NODE).nodeTarget(PST_SAMPLE_NODE_FAMILY.LIVING_CONDITION))
      schema.field('player_multiplier', field => field.kind(PST_SAMPLE_SCHEMA_FIELD_KIND.NODE).nodeTarget(PST_SAMPLE_NODE_FAMILY.MULTIPLIER))
    })
    // context: PSTCustomRuntimeContexts.AttackListenerContext
    .onAttack(context => {
      const player = context.player()
      const enemy = context.enemy()
      if (player == null || enemy == null || player.level == null || player.level.isClientSide()) {
        return
      }
      if (String(context.node().string('mode').orElse('attack')) !== 'attack') {
        return
      }
      if (!context.node().testLivingCondition('player_condition', player, true)) {
        return
      }

      const multiplier = context.node().getLivingMultiplier('player_multiplier', player, 1.0)
      context.apply(enemy, multiplier)
    })
    // context: PSTCustomRuntimeContexts.TickListenerContext
    .onTick(context => {
      const player = context.player()
      if (player == null || player.level == null || player.level.isClientSide()) {
        return
      }
      if (String(context.node().string('mode').orElse('attack')) !== 'tick') {
        return
      }

      const cooldown = Math.max(1, context.node().integer('cooldown').orElse(20))
      if ((context.tickCount() % cooldown) !== 0) {
        return
      }
      if (!context.node().testLivingCondition('player_condition', player, true)) {
        return
      }

      const multiplier = context.node().getLivingMultiplier('player_multiplier', player, 1.0)
      context.apply(player, multiplier)
    })
})

StartupEvents.registry('skilltree:skill_bonuses', event => {
  event.create('kubejs:sample_runtime_bonus')
    // context: PSTTooltipRenderContext
    .effect(context => {
      const effectId = String(context.node().string('effect').orElse(PST_SAMPLE_DEFAULT_EFFECT))
      return Text.translate(
        'skill_bonus.kubejs.sample_runtime_bonus',
        Text.translate(`effect.${effectId.replace(':', '.')}`)
      )
    })
    .schema(schema => {
      schema.field('effect', field => field.kind(PST_SAMPLE_SCHEMA_FIELD_KIND.RESOURCE_LOCATION).required())
      schema.field('duration', field => field.kind(PST_SAMPLE_SCHEMA_FIELD_KIND.INT).defaultValue(80))
      schema.field('amplifier', field => field.kind(PST_SAMPLE_SCHEMA_FIELD_KIND.INT).defaultValue(0))
      schema.field('event_listener', field => field.kind(PST_SAMPLE_SCHEMA_FIELD_KIND.NODE).nodeTarget(PST_SAMPLE_NODE_FAMILY.EVENT_LISTENER))
    })
    // context: PSTCustomRuntimeContexts.SkillBonusApplyContext
    .onApply(context => {
      const target = context.target()
      if (target == null || target.level == null || target.level.isClientSide()) {
        return
      }

      const effectId = String(context.node().string('effect').orElse(PST_SAMPLE_DEFAULT_EFFECT))
      const effect = PST_SAMPLE_FORGE_REGISTRIES.MOB_EFFECTS.getValue(PST_SAMPLE_RESOURCE_LOCATION.tryParse(effectId))
      if (effect == null) {
        return
      }

      const duration = Math.max(1, Math.floor(context.node().integer('duration').orElse(80) * context.multiplier()))
      const amplifier = Math.max(0, context.node().integer('amplifier').orElse(0))
      target.addEffect(new PST_SAMPLE_MOB_EFFECT_INSTANCE(effect, duration, amplifier, false, true, true))
    })
})

StartupEvents.registry('skilltree:skill_requirements', event => {
  event.create('kubejs:sample_low_health_requirement')
    .requirementText(Text.translate('skill_requirements.kubejs.sample_low_health_requirement'))
    .schema(schema => {
      schema.field('ratio', field => field.kind(PST_SAMPLE_SCHEMA_FIELD_KIND.DOUBLE).defaultValue(0.5))
    })
    // context: PSTCustomRuntimeContexts.SkillRequirementContext
    .test(context => {
      const player = context.player()
      if (player == null) {
        return false
      }
      const ratio = context.node().number('ratio').orElse(0.5)
      return player.getHealth() <= (player.getMaxHealth() * ratio)
    })
})
