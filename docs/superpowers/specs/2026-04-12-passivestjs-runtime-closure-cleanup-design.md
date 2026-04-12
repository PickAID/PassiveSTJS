# PassiveSTJS Runtime Closure And Structural Cleanup Design

Date: 2026-04-12
Project: PassiveSTJS
Status: Approved direction, implementation not started

## Why This Design Exists

PassiveSTJS has crossed the point where feature growth alone is no longer enough.

The current codebase has two separate problems:

- some custom PST registry families still do not form a complete runtime loop
- the structure across many files feels assembled instead of intentionally designed

The second problem is not cosmetic. It directly causes the first one.

When responsibilities are blurred, PassiveSTJS keeps producing half-real surfaces:

- schema and Probe metadata exist, but runtime does not
- tree content is visible, but effect execution is incomplete
- helper APIs exist, but do not answer the actual scripting questions
- generated lang works, but its ownership model is easy to misunderstand

This design treats structural cleanup as a product requirement, not as optional refactoring.

## Target Outcome

A custom PST registry entry must form one closed chain:

1. it can be registered through the real PST Forge registry
2. it can expose schema, docs, and Probe completion
3. it can appear correctly in tree content and tooltips
4. it can resolve translation keys or generated defaults predictably
5. it can be queried at runtime from scripts
6. it can execute real gameplay effects
7. it can be covered by automated tests and run scripts

If any one of those steps is missing, the surface is not complete and must not be presented as complete.

## Scope

This design covers:

- work that belongs in the `PassiveSTJS` repository rather than `PassiveSkillTreeIntegration`
- structural cleanup around runtime, registry, lang, JSON, and public API boundaries
- true runtime support for custom `event_listeners`
- true runtime support for custom `item_bonuses`
- expansion of runtime helper APIs so scripts can inspect active runtime state
- safer `generated_lang` defaults and clearer ownership warnings
- run-script and automated test coverage for the full custom registry lifecycle

This design does not cover:

- unrelated UI redesign of the skill tree screen
- unrelated PassiveIntegration feature work
- replacing PST capability persistence
- replacing PST serializers where PST's existing serializer contracts are already correct

## Source Facts

These facts have already been verified against the current mapped PST dependency and the PassiveSTJS codebase.

### PST Registry Facts

PST exposes real Forge registries for:

- `skilltree:skill_bonuses`
- `skilltree:skill_bonus_multipliers`
- `skilltree:living_conditions`
- `skilltree:damage_conditions`
- `skilltree:item_conditions`
- `skilltree:enchantment_conditions`
- `skilltree:event_listeners`
- `skilltree:numeric_value_providers`
- `skilltree:skill_requirements`
- `skilltree:item_bonuses`

### Event Listener Runtime Fact

PST does not dispatch event listeners generically.

`SkillBonusHandler` gathers `EventListenerBonus` instances and then branches by concrete built-in listener classes such as:

- `AttackEventListener`
- `DamageTakenEventListener`
- `CritEventListener`
- `BlockEventListener`
- `ItemUsedEventListener`
- `KillEventListener`
- `EvasionEventListener`

That means a custom listener serializer can deserialize successfully and still never run unless PassiveSTJS adds its own dispatch bridge.

### Item Bonus Runtime Fact

PST deserializes item bonuses through the registry serializer path, which is good.

However, PST's built-in item bonus runtime handling only applies certain built-in runtime behaviors directly, especially `SkillBonusItemBonus` attribute application on equipment changes. A custom item bonus can therefore deserialize and display tooltips correctly while still having no gameplay effect unless PassiveSTJS bridges that runtime too.

### Generated Lang Fact

`generated_lang` currently sits on a dangerous edge:

- it is useful for default seeding
- it is not the same thing as user-owned translation authoring

The default must therefore be safe. Automatic export must be opt-in, and the config text must explicitly state that it may update or replace generated entries in the chosen target file.

### JsonHelper Fact

`JsonHelper` is not dead code.

It is still used by:

- runtime payload copying
- typed JSON builder output
- lang exporter serialization
- content state copying and merging
- builder normalization

It should not be deleted. It should be re-homed and renamed only if doing so improves boundaries.

## Design Principles

### 1. No Fake Capability

If a registry family is exposed as custom-runtime-capable, it must really execute.

PassiveSTJS must stop shipping surfaces that are only:

- metadata-capable
- editor-capable
- Probe-capable

while lacking actual runtime behavior.

### 2. Structure Is A Feature

This codebase needs cleaner boundaries because the current organization leaks internal mechanics into public APIs and duplicates responsibility across packages.

The goal is not stylistic purity. The goal is to make every layer legible:

- startup registration
- content authoring
- runtime serialization
- runtime dispatch
- runtime querying
- language export

### 3. Runtime Helpers Must Answer Real Questions

The public runtime surface must help scripts answer questions such as:

- does this player currently have any active instance of custom bonus `kubejs:smoke_bonus`
- which learned skills are contributing those instances
- what payload and multiplier are attached to each active instance
- what is the aggregated numeric degree if this runtime family defines one
- can this player currently learn this skill
- which requirements are blocking that learn

If the runtime cannot honestly provide a single numeric answer, it should return instances or views instead of inventing one.

### 4. Translation Must Be Predictable

Custom types need a clean story for:

- tree display
- tooltips
- generated defaults
- manual translation ownership

PassiveSTJS must support translation-backed behavior without forcing script authors into brittle literal-text fallbacks.

## Chosen Architecture

PassiveSTJS will be reorganized around five explicit layers.

### Layer 1: Registration Layer

Startup scripts remain the only place where custom PST serializer types are registered.

This layer is responsible for:

- real Forge registry entry creation
- schema metadata
- Probe completion metadata
- runtime callback registration
- translation metadata defaults for the registered type

This layer must not own:

- tree content editing
- player runtime mutation
- event dispatch

### Layer 2: Content Layer

`PassiveSTJSEvents.skillTreeContent(...)` remains the only tree content authoring surface.

This layer is responsible for:

- tree and skill structure
- translated title and description seeding
- references to registered PST types
- content-time editing of bonus, requirement, listener, multiplier, condition, value, and item bonus nodes

This layer must only compose registered types. It must not implement runtime behavior itself.

### Layer 3: Runtime Serialization Layer

This layer turns registered serializer metadata and callbacks into real runtime instances.

It already covers most custom families and will be expanded to complete the remaining two:

- `event_listeners`
- `item_bonuses`

This layer is responsible for:

- preserving payload through JSON, NBT, and network serialization
- producing real runtime instances instead of placeholders
- exposing stable runtime nodes to the dispatch and query layers

### Layer 4: Runtime Dispatch Layer

This is the missing closure layer.

PassiveSTJS will introduce a dedicated runtime bridge that subscribes to the same relevant Forge events as PST and dispatches custom runtime listeners and custom runtime item bonuses through a generic PassiveSTJS-owned protocol.

This layer is responsible for:

- attack and damage listener dispatch
- crit, block, kill, use-item, ticking, learned, removed, and evasion listener dispatch
- item-bonus runtime hooks such as tooltip, equip-change, and any declared active gameplay effect path
- avoiding duplicate execution of PST built-in listener behavior

This layer must filter strictly for PassiveSTJS custom runtime implementations. Built-in PST listeners must remain on PST's native path.

### Layer 5: Runtime Query Layer

This is the public KubeJS runtime helper surface.

It is responsible for answering gameplay questions from scripts in a KubeJS-friendly way.

It will extend the current `PST` binding with read and mutation helpers that are actually useful during normal scripting and effect application.

## Structural Cleanup Targets

The current code should be cleaned so the package structure reflects the layers above.

### Public Surface Rules

Public KubeJS-facing classes should live under one clearly named API surface and stay small.

These include:

- bindings
- id wrappers
- runtime views
- builder entrypoints
- KubeJS event entrypoints

Public classes should not depend on internal serializer plumbing more than necessary.

### Internal Runtime Rules

Internal runtime code should be grouped by responsibility, not by accidental growth.

Target internal grouping:

- `runtime.model`
  - runtime node and runtime instance views
- `runtime.serializer`
  - serializer-backed runtime instance creation
- `runtime.dispatch`
  - Forge-event bridges and dispatch services
- `runtime.query`
  - aggregation and matching services for scripts
- `util.json`
  - JSON copy and normalization helpers

These package groups are the target structure for the implementation pass. If an existing package already matches one of these responsibilities cleanly, it may be retained; otherwise the code should be moved until the boundary is obvious from the tree.

### Builder Rules

Builders must stay content-facing.

They must not drift into runtime query responsibilities or generic object-dumping APIs.

The current rule remains:

- normal script entrypoints must not expose raw `Object` or raw JSON as the intended authoring path

### Placeholder Rules

A registry family may only keep placeholder serializers when the family is explicitly metadata-only by design.

That is not true for `event_listeners` or `item_bonuses`.

Their placeholder path must be removed once real runtime support lands.

## Custom Registry Lifecycle

Every custom registry family must follow the same lifecycle.

### Step 1: Registration

The script registers a real PST type and declares:

- schema
- docs
- translation defaults or translation keys
- runtime behavior callbacks

Example shape:

```js
StartupEvents.registry('skilltree:skill_bonuses', event => {
  event.create('kubejs:smoke_bonus')
    .schema(schema => {
      schema.number('duration')
      schema.number('amplifier')
    })
    .runtime(runtime => {
      runtime.onLearn(ctx => {})
      runtime.onApply(ctx => {})
    })
})
```

The first implementation must keep the current registry-builder style and add explicit runtime-builder methods to it. It must not invent a second registration entrypoint.

### Step 2: Content Composition

The registered type is referenced from `skillTreeContent`.

Example shape:

```js
PassiveSTJSEvents.skillTreeContent(event => {
  event.editTree('kubejs:smoke_tree')
    .startingSkill('kubejs:smoke_root')
    .title('Smoke Root')
    .bonus('kubejs:smoke_bonus', bonus => {
      bonus.duration(80)
      bonus.amplifier(1)
      bonus.eventListener('kubejs:smoke_listener', listener => {
        listener.target('enemy')
      })
    })
})
```

### Step 3: Translation Resolution

The type and the content that references it must both support translation cleanly.

Requirements:

- skill and tree content must continue to support generated or manual translation keys
- custom registry metadata must be able to provide translatable name and description defaults
- tooltip text for custom runtime types must not be trapped behind literal-only strings

### Step 4: Runtime Activation

When the player learns the skill or equips an item carrying the custom effect, the runtime instance must become discoverable and executable.

That means:

- the skill bonus runtime must exist
- the listener runtime must exist
- any associated multiplier, requirement, condition, or value runtime must exist
- custom item bonus runtime must exist when the effect is routed through item bonuses

### Step 5: Runtime Query

Scripts must be able to inspect the active runtime state without re-parsing content JSON manually.

### Step 6: Runtime Effect Application

Scripts and runtime bridges must be able to use the active state to apply the actual gameplay effect.

This is the step that turns a custom type like `kubejs:smoke_bonus` from a schema artifact into a real game mechanic.

## Runtime Helper Design

The current `Bindings` surface is too weak. It exposes basic player, skill, and tree views, but it does not expose enough active runtime state to drive custom effects reliably.

### Player Runtime Queries

`PST.player(player)` must grow into a real runtime helper root.

Required capabilities:

- `hasSkill(id)`
- `learn(id)`
- `learnWithoutSkillPointCost(id)`
- `learnWithSkillPointCost(id, cost)`
- `grantSkillPoints(amount)`
- `consumeSkillPoints(amount)`
- `canLearn(id)`
- `missingRequirements(id)`
- `hasBonus(typeId)`
- `bonusInstances(typeId)`
- `bonusMagnitude(typeId)` when the runtime family declares a safe numeric aggregation rule
- `itemBonusInstances(typeId)`
- `listenerInstances(typeId)`

The important rule is:

- boolean helpers answer presence
- instance helpers answer detail
- magnitude helpers exist only where the runtime family actually defines a meaningful numeric degree

### Runtime Instance Views

Runtime instance views must expose enough information to let scripts act without dropping back to raw JSON.

Minimum fields:

- type id
- source skill id or source item context when available
- payload view
- effective multiplier
- whether the instance came from a skill or item bonus path

For bonus instances specifically, scripts need to be able to answer:

- which skill granted this
- what payload configured it
- what the current effective strength is

### Requirement Evaluation Helpers

Requirements are not an always-active buff family, so their helper model should be skill-centric rather than fake-active-instance-centric.

Required capabilities:

- `canLearn(skillId)`
- `missingRequirements(skillId)`
- `requirementsFor(skillId)`

This keeps the surface honest.

## Event Listener Runtime Design

Custom event listeners must become first-class runtime types.

### Dispatch Model

PassiveSTJS will add its own Forge-event bridge and dispatch only custom runtime listeners.

This bridge will:

- collect active `EventListenerBonus` instances from the player
- filter listeners that belong to the PassiveSTJS custom runtime protocol
- construct a typed runtime context
- apply the listener effect through the associated runtime bonus

Built-in PST listeners stay on PST's own path.

This avoids both fake support and duplicate execution.

### Event Context Model

`PSTCustomRuntimeContexts` will expand to include the missing event-time data needed by real custom listeners.

Target context coverage includes:

- player
- target
- attacker
- damage source
- item stack
- server player
- server level when applicable
- source runtime node
- effective multiplier

The current `damageSource = null` gap in generic apply contexts must be fixed where the underlying event actually provides a damage source.

### Listener Translation And Tooltip Model

Custom listeners need:

- translatable type name
- translatable tooltip description
- content-aware tooltip formatting when relevant

This should come from metadata and runtime context, not hardcoded English literals.

## Item Bonus Runtime Design

Custom item bonuses must also become first-class runtime types.

### Supported Models

PassiveSTJS will support two item-bonus models:

1. wrapper item bonus
   - the item bonus wraps a skill bonus and forwards runtime behavior
2. direct custom item bonus runtime
   - the item bonus defines its own runtime effect and query surface

### Runtime Hook Model

Custom item bonuses must support the hooks needed for actual gameplay use, not just tooltip display.

Minimum hooks:

- tooltip contribution
- equip-change activation path
- equip-change removal path
- runtime query visibility

Additional hooks may be added later, but the first implementation must already make custom item bonuses real enough for gameplay, not just serialization.

## Translation Design

### Generated Lang Default

`generated_lang.enabled` must default to `false`.

Reason:

- exporting generated defaults is useful
- silently mutating user-owned lang files is not a safe default

### Config Warning

The config comments must explicitly state:

- generated export writes to local KubeJS assets
- it may add, update, or replace generated entries in the target locale file
- users should not treat the generated target file as a safe hand-edited source unless they accept that ownership model

### Custom Type Translation

Custom PST registry metadata must support translation-backed display for:

- type name
- tooltip summary
- optional field-level docs where appropriate

This support should work whether the translation comes from:

- manual lang authoring
- generated defaults

## Run Script Coverage

The `run/client` environment must contain a full vertical smoke test for the custom runtime chain.

### Required Example Types

The run scripts must register and exercise at least:

- `kubejs:smoke_bonus`
- `kubejs:smoke_listener`
- `kubejs:smoke_item_bonus`
- `kubejs:smoke_requirement`
- `kubejs:smoke_multiplier`
- `kubejs:smoke_value_provider`
- `kubejs:smoke_living_condition`
- `kubejs:smoke_damage_condition`

### Required Example Tree Behavior

The test tree must prove:

- translated title and description display
- skill learning activates the custom runtime bonus
- the custom listener runs during the intended event
- the custom effect can be detected through runtime helpers
- the effect survives reload and world re-entry consistently

### Required Runtime Script Behavior

Runtime scripts must log enough information that the maintainer can validate behavior with minimal manual work.

Required log categories:

- registration loaded
- content loaded
- player learned target skill
- active custom bonus instances
- active listener instances
- effect execution events

## Testing Requirements

### Unit Tests

Must cover:

- custom event listener serializer round-trip
- custom item bonus serializer round-trip
- custom event listener runtime dispatch
- custom item bonus runtime dispatch
- runtime helper queries for active custom bonus instances
- generated lang default disabled
- generated lang config warning text expectations

### Integration-Like Tests

Must cover:

- a learned custom bonus becomes queryable from `PST.player(player)`
- a custom listener actually fires through the bridge
- a custom item bonus actually produces runtime-visible behavior
- translation-backed metadata survives the content pipeline correctly

### Run Tests

The run environment must be usable for direct manual confirmation without authoring additional scripts.

That means the maintainer should be able to:

1. enter the world
2. learn or auto-grant the smoke skill
3. trigger the intended event
4. observe the effect
5. read the runtime log lines

## Migration Plan

### Phase 1: Safe Surface Corrections

- change `generated_lang.enabled` default to `false`
- add explicit overwrite-risk wording to config comments
- document that `JsonHelper` is infrastructure, not dead code
- identify and trim public APIs that still imply fake capability

### Phase 2: Structural Boundary Cleanup

- move runtime internals behind clearer package boundaries
- keep public KubeJS entrypoints thin
- remove leftover placeholder-dependent exposure for families that are becoming real

### Phase 3: Item Bonus Runtime Closure

- replace placeholder item bonus runtime with real runtime instances
- add query support
- add run-script coverage

### Phase 4: Event Listener Runtime Closure

- introduce the custom listener dispatch bridge
- expand event contexts
- add query support
- add run-script coverage

### Phase 5: Runtime Helper Expansion

- expose active custom bonus and listener views
- expose requirement evaluation helpers
- expose meaningful numeric aggregation where valid

### Phase 6: Test And Example Finalization

- complete automated tests
- complete run smoke scripts
- verify reload and re-entry behavior

## Risks

### Risk 1: Duplicate Execution

If the bridge does not cleanly distinguish custom listeners from PST built-ins, effects may double-apply.

Mitigation:

- dispatch only PassiveSTJS custom runtime listener implementations
- leave PST built-ins on PST's native path

### Risk 2: Fake Magnitude APIs

If runtime helper APIs invent a numeric degree for a family that does not define one, the public API becomes misleading.

Mitigation:

- return instance views by default
- expose numeric aggregation only where the semantics are explicit

### Risk 3: Translation Ownership Confusion

If generated export remains ambiguous, users will keep losing confidence in the lang workflow.

Mitigation:

- default off
- explicit warning text
- generated output treated as opt-in managed content

## Final Decision

PassiveSTJS will move forward with the full closure model.

That means:

- `event_listeners` will become true runtime-capable through a PassiveSTJS dispatch bridge
- `item_bonuses` will become true runtime-capable through real runtime objects and bridges
- runtime helpers will be extended so scripts can query active custom effects directly
- structural cleanup will be treated as part of the feature, not postponed until after the feature
- generated lang will default to safe-off behavior with explicit overwrite warnings

This is the only direction that satisfies the current requirement:

- custom PST registries must support metadata
- custom PST registries must support translation
- custom PST registries must support tree content
- custom PST registries must support real gameplay effect execution
- custom PST registries must support runtime querying and testing
