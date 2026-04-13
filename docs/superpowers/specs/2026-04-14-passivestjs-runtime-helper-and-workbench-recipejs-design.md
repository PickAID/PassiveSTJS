# PassiveSTJS Runtime Helper And Workbench RecipeJS Design

Date: 2026-04-14
Project: PassiveSTJS
Status: Approved direction, implementation not started

## Why This Design Exists

PassiveSTJS now has enough real registry and runtime plumbing that the next problem is not "can this type exist" but "how does a script author actually use it without fighting the API".

Two surfaces need to be made explicit:

- a small runtime helper API for learned skill state, custom bonus inspection, listener inspection, and requirement inspection
- a thin RecipeJS layer for PST's `skilltree:workbench_item_bonus` recipe so users do not have to hand-write raw datapack JSON for normal cases

This design also fixes a recurring conceptual problem: `skill bonus`, `listener`, `requirement`, and `item bonus` are not the same kind of thing and should not be exposed as if they were interchangeable.

## User Goals

- let KubeJS scripts inspect learned PST skills in arbitrary events without adding fake wrapper APIs
- make `bonus` the main effect-side runtime object and `listener` the trigger-side runtime object
- keep `requirement` as a learn gate, not as a generic mutable gameplay toggle
- support `skilltree:workbench_item_bonus` through RecipeJS in a way that reuses the existing node-writing model
- keep translation and display text compatible with the current Component-oriented direction
- avoid exposing item bonus as if it were a tree-side node when it is really part of PST's stack and workbench pipeline

## Scope

This design covers:

- a player-bound runtime helper entrypoint built on top of `PST.player(player)`
- a dedicated player-skill runtime view with `learned`, `canLearn`, `learn`, `remove`, `requirements`, `bonuses`, and `listeners`
- minimal runtime view objects for `bonus`, `listener`, and `requirement`
- first-phase RecipeJS support for `skilltree:workbench_item_bonus`
- clear boundary rules for when scripts should use `bonus`, `listener`, `requirement`, or raw `PSTRuntimeNode`

This design does not cover:

- a full PST recipe DSL for every PST recipe type
- a pure wrapper surface such as `PST.skillBonus()` or `PST.requirementType()`
- a generic writable requirement API such as `requirement.pass()` or `pst.forceRequirement()`
- a first-phase stack runtime API such as `PST.stack(stack).itemBonuses()`
- hiding all registry ids behind wrapper objects when a direct id string is enough

## Source Facts

These facts have already been verified against the current PassiveSTJS codebase and the mapped PST dependency in Gradle cache.

### Existing Runtime Entrypoints Already Exist

`Bindings` already exposes:

- `PST.player(player)`
- `PST.skill(id)`
- `PST.tree(id)`

`PSTPlayerView` already owns real runtime mutations such as learning, removing, resetting, and skill-point changes.

`PSTRuntimeNode` already provides the generic escape hatch for custom payload access and node evaluation, including:

- `testLivingCondition`
- `testDamageCondition`
- `testItemCondition`
- `testEnchantmentCondition`
- `testSkillRequirement`
- `getLivingMultiplier`
- `getNumericValue`
- `skillBonus`
- `skillBonuses`
- `eventListener`
- `itemBonus`
- `itemBonuses`

This means the missing work is not inventing a second runtime model. The missing work is exposing the current model through a smaller and more legible public surface.

### Listener Is A Trigger, Not An Effect

PST does not dispatch custom listeners generically. PassiveSTJS has to bridge real dispatch points itself.

That means `listener` should be treated as trigger metadata attached to a learned skill. It is not the main effect object a script reads in arbitrary external events.

The fixed dispatch points remain:

- `onSkillLearned`
- `onSkillRemoved`
- `onTick`
- `onAttack`
- `onDamageTaken`
- `onCriticalHit`
- `onBlock`
- `onItemUsed`
- `onKill`

Custom payload fields such as `mode` stay listener-specific schema, not global listener API.

### Requirement Is A Learn Gate

Requirement evaluation belongs to skill learning rules.

A requirement view should answer:

- does it currently pass
- what text should be shown
- what custom type is it
- what runtime payload node does it carry

It should not pretend to be a universal mutable state container. If a user later wants a manual gate, that should be implemented as a custom requirement type that reads custom state, not as a generic `pass()` mutation method on every requirement.

### Item Bonus Is Stack-Side, Not Tree-Side

PST item bonuses live in `PSTRegistries.ITEM_BONUSES` and are used by the workbench and item stack upgrade pipeline.

Built-in item bonus types include:

- `skilltree:skill_bonus`
- `skilltree:item_bonus_list`

The tree-side bridge is the built-in skill bonus `skilltree:more_item_bonuses`.

That means the relationship is:

1. a skill can grant more item-bonus capacity through `skilltree:more_item_bonuses`
2. a workbench recipe writes an item bonus onto an item stack
3. PST and PassiveSTJS runtime then use that item-bonus structure on the item side

PassiveSTJS should therefore document and support item bonus authoring clearly, but should not blur it into the tree node API.

### PST Already Has A Native Workbench Item Bonus Recipe

PST ships a dedicated recipe type:

- `skilltree:workbench_item_bonus`

Its builder already models:

- base item condition
- ingredients
- whether a passive skill is required
- the item bonus payload to write

PassiveSTJS should expose a thin RecipeJS adapter for this native PST recipe instead of forcing users to author raw JSON for common cases.

## Alternatives Considered

### 1. Pure Wrapper-First API

Example direction:

```js
PST.skillBonus('kubejs:smoke_bonus')
PST.listener('kubejs:smoke_listener')
```

This was rejected.

It adds autocomplete-oriented wrappers without answering actual gameplay questions. It also pushes the user toward ids and wrapper objects instead of toward the learned player state that matters at runtime.

### 2. Flat Toolbox API

Example direction:

```js
pst.canLearn('kubejs:demo/root')
pst.hasBonus('kubejs:smoke_bonus')
pst.activeBonuses()
```

This was rejected for the first phase.

It looks simple, but it mixes several different concepts:

- learned state
- effect instances
- trigger instances
- context-sensitive condition checks
- aggregated queries that are easy to misname

The earlier `activeBonuses` naming problem came from exactly this flattening.

### 3. Player-Bound Skill Runtime View Plus Thin Recipe Adapter

This is the chosen approach.

It keeps the runtime entrypoint on the player, keeps skill-local objects grouped under the skill that owns them, and keeps RecipeJS support narrow enough that it does not become a second content system.

## Chosen Runtime Model

### Binding Surface

The public runtime entrypoint remains:

```js
const pst = PST.player(player)
```

`PST.skill(id)` and `PST.tree(id)` stay as read-only definition lookups.

`PST.player(player)` becomes the player-bound runtime entrypoint for learned state and learn/remove operations.

### Player-Bound Skill View

`PST.player(player)` should expose:

```js
const skill = pst.skill('kubejs:demo/root')
```

This returns a dedicated player-skill runtime view rather than overloading the existing definition-only `PSTSkillView`.

The skill runtime view should expose:

- `learned()`
- `canLearn()`
- `learn()`
- `remove()`
- `requirements()`
- `bonuses()`
- `listeners()`

It may also expose `definition()` as a bridge back to the read-only `PSTSkillView`, but the required first-phase methods are the seven listed above.

This keeps the public shape honest:

- `PST.skill(...)` is a definition lookup
- `pst.skill(...)` is a player-bound runtime lookup

### Bonus Runtime View

`skill.bonuses()` returns the learned bonus instances attached to that learned skill.

Each bonus view should expose:

- `typeId()`
- `text()`
- `node()`

`text()` should return a `Component`, not a raw string.

`node()` returns `PSTRuntimeNode`. This is the intentional escape hatch for advanced custom logic in arbitrary KubeJS events.

Important semantic rule:

`bonus` is an effect instance attached to a learned skill. It is not silently filtered by current event context. If a custom bonus has conditions or multipliers, scripts read them through `node()` and decide how to evaluate them for the current event.

### Listener Runtime View

`skill.listeners()` returns the learned listener instances attached to that learned skill.

Each listener view should expose:

- `typeId()`
- `text()`
- `node()`

`listener` is trigger-side metadata. It exists mainly for:

- inspecting what custom trigger logic is attached to a skill
- exposing listener data to scripts or debug tools
- future dispatch bridging

It is not the primary effect object for arbitrary external events. In those events, scripts should usually look at `bonus` first.

### Requirement Runtime View

`skill.requirements()` returns the current learn requirements for that skill from the perspective of the bound player.

Each requirement view should expose:

- `passed()`
- `text()`
- `typeId()`
- `node()`

`text()` should return a `Component`.

`passed()` is read-only. There is no generic writable requirement API in this design.

### Query Semantics

The public runtime model must keep these meanings stable:

- `bonus` means learned effect-side data
- `listener` means learned trigger-side data
- `requirement` means current learn gate state
- `node` means raw typed runtime payload plus condition, multiplier, nested bonus, and value-provider helpers

The first phase should not add player-level aggregate APIs such as:

- `activeBonuses()`
- `activeListeners()`
- `failedRequirements()`

Those names sound simple but hide context-sensitive meaning. Scripts can already aggregate explicitly by iterating learned skills and their attached views.

## Using The Runtime In Arbitrary Events

The intended usage in user-owned KubeJS events is:

1. get the player runtime view with `PST.player(player)`
2. get a learned skill view with `pst.skill(id)` or iterate learned skills
3. inspect `skill.bonuses()` for effect logic
4. inspect `skill.listeners()` only when trigger metadata matters
5. use `bonus.node()` or `listener.node()` for actual custom payload, condition, multiplier, and nested-node access

Example shape:

```js
const pst = PST.player(player)
const skill = pst.skill('kubejs:demo/root')

if (skill && skill.learned()) {
  for (const bonus of skill.bonuses()) {
    if (bonus.typeId() !== 'kubejs:smoke_bonus') continue
    const node = bonus.node()
    if (!node.testLivingCondition('player_condition', player, true)) continue
    const duration = node.integer('duration').orElse(100)
    const amplifier = node.integer('amplifier').orElse(0)
    target.potionEffects.add('minecraft:poison', duration, amplifier)
  }
}
```

This keeps PassiveSTJS responsible for structured runtime access while keeping effect ownership in the user's event code when they want that freedom.

## RecipeJS Support For Workbench Item Bonus

### Design Goal

A normal `skilltree:workbench_item_bonus` recipe should be writable from KubeJS without dropping to `event.custom(...)` for every field.

The adapter should be thin. It should reuse the existing node-writing model instead of inventing a separate PST recipe DSL.

### Chosen Shape

PassiveSTJS should add:

```js
ServerEvents.recipes(event => {
  event.recipes.passiveStjs.workbenchItemBonus('kubejs:shield_socket', recipe => {
    recipe.baseItemConditionSchema('skilltree:equipment_type', condition => {
      condition.set('equipment_type', 'shield')
    })
    recipe.ingredient({ tag: 'forge:ingots/copper' }, 2)
    recipe.requiresPassiveSkill(true)
    recipe.itemBonus('kubejs:socket_shell', itemBonus => {
      itemBonus.int('tier', 1)
      itemBonus.skillBonus('skilltree:attribute', bonus => {
        bonus.amount(2)
        bonus.attribute('minecraft:generic.armor')
        bonus.modifierId('8516d3f4-373e-42c3-9138-3215993b34c4')
        bonus.bonusName('Socket Upgrade')
        bonus.operation(0)
      })
    })
  })
})
```

### Supported Builder Operations

First-phase support should include:

- `baseItemConditionSchema(typeId, consumer)`
- `ingredient(ingredient, requiredAmount)`
- `requiresPassiveSkill(boolean)`
- `itemBonus(typeId, consumer)`

The internal implementation should reuse the same node-writing conventions already used by PassiveSTJS content builders.

### Why This Stays Thin

This adapter should not try to cover every PST recipe or every possible convenience method in the first phase.

It exists to solve one concrete UX problem:

- users should not need to hand-write `kubejs/data/.../recipes/*.json` for the common workbench item-bonus flow

If a user needs a shape not covered by the thin builder, `event.custom(...)` remains the fallback.

## Item Bonus Position In The Overall System

Item bonus support should be explained in docs and examples with this chain:

1. register or use an item bonus type
2. write a `skilltree:workbench_item_bonus` recipe that adds it to an item
3. optionally grant capacity through `skilltree:more_item_bonuses` in a skill tree
4. let PST or PassiveSTJS runtime apply the resulting item-side behavior

This avoids a common misunderstanding:

An item bonus is not a skill-tree node effect by itself. It becomes useful through the workbench item pipeline, optionally gated by skill-side item-bonus capacity.

## Translation And Display Rules

Runtime helper display text should stay aligned with the current Component direction.

The rule for this design is simple:

- user-visible runtime text surfaces return `Component`
- raw payload inspection stays on `PSTRuntimeNode`
- translation ownership remains with the existing Component and serializer-metadata path, not with a second runtime-only lang system

This means:

- `bonus.text()` returns a `Component`
- `listener.text()` returns a `Component`
- `requirement.text()` returns a `Component`

PassiveSTJS should not invent a second string-only formatting path here.

## Structural Rules

### Keep One Purpose Per View

The runtime helper layer should be split into small view types with one job:

- player view
- player-skill runtime view
- bonus view
- listener view
- requirement view

Do not collapse them into one generic "runtime object" class with mode flags.

### Reuse `PSTRuntimeNode` Instead Of Re-Wrapping It

`PSTRuntimeNode` already answers the low-level questions.

The new public views should point at it. They should not duplicate:

- payload reading
- nested node deserialization
- condition evaluation
- multiplier evaluation

### Avoid Overload Ambiguity

Rhino method ambiguity has already caused real problems in this codebase.

The new runtime and RecipeJS APIs should therefore prefer:

- one clear method per operation
- distinct method names over ambiguous overload families

Autocomplete is useful, but ambiguous overloads are not worth the scripting instability they create.

## Testing And Documentation Expectations

This design should be implemented with:

- focused runtime-view tests for player-skill, bonus, listener, and requirement views
- RecipeJS builder tests for `skilltree:workbench_item_bonus` output
- at least one example script showing bonus-side arbitrary event logic
- at least one example script showing listener-side custom metadata usage
- at least one example recipe using a custom item bonus shell plus nested built-in skill bonus

The examples should explain the boundary clearly:

- use `bonus` when you want effect logic
- use `listener` when you want trigger metadata
- use `requirement` when you want learn-gate visibility
- use item bonus recipes when you want item-side upgrades

## Non-Goals To Keep Explicit

This design deliberately does not do the following:

- it does not add `PST.skillBonus()` style wrapper registries
- it does not add `requirement.pass()` or any universal requirement mutation API
- it does not pretend `item bonus` belongs to the tree node layer
- it does not add a first-phase generic stack runtime query surface
- it does not replace raw `event.custom(...)` as an escape hatch
- it does not add a second translation system for runtime helper text

## Final Direction

PassiveSTJS should move forward with one clear runtime story:

- `PST.player(player)` is the runtime entrypoint
- `pst.skill(id)` is the player-bound skill surface
- `bonus`, `listener`, and `requirement` each keep their own meaning
- `PSTRuntimeNode` remains the low-level power tool
- `skilltree:workbench_item_bonus` gets thin RecipeJS support instead of forcing raw JSON

This keeps the API small, source-backed, and honest about what PST is actually doing underneath.
