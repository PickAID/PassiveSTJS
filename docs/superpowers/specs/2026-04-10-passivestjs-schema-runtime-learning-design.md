# PassiveSTJS Schema Runtime Learning Design

## Status

Approved in design review on April 10, 2026.

This design supersedes the content-surface and runtime portions of:

- `2026-04-08-passivestjs-registry-protocol-design.md`
- `2026-04-09-passivestjs-real-pst-registry-design.md`

The April 9 registry decision remains correct: startup registration uses PST's real Forge registries. This document replaces the remaining fake helper surface, the weak content builders, and the hard dependency on PST's built-in learning transaction.

## Scope

This design reshapes PassiveSTJS around four rules:

1. `startup_scripts` register PST types through real `StartupEvents.registry(...)` targets.
2. `server_scripts` edit tree content through `PassiveSTJSEvents.skillTreeContent(...)`.
3. public bindings expose runtime helpers, not internal content plumbing.
4. learning and resource consumption become separate services.

The goal is to make the scripting surface feel like KubeJS instead of a Java wrapper, while keeping PST compatibility where PST is already a good runtime backend.

## Goals

- Keep real PST Forge registries as the startup registration surface.
- Remove `Object` and raw JSON as normal script-facing content authoring tools.
- Keep `tree` and `skill` authoring strongly typed and easy to read.
- Make `bonus`, `requirement`, `condition`, `listener`, `value`, `multiplier`, and `itemBonus` schema-driven instead of hardcoded-helper-driven.
- Collapse public bindings down to real script helpers.
- Replace PST's hardcoded `learnSkill -> spend 1 point` transaction with a PassiveSTJS-owned learning service.
- Keep PST capability persistence, skill application, and serializer compatibility where they are already useful.
- Make runtime helpers support both query and simulation.
- Make Probe metadata, runtime field views, and content validation read from the same schema metadata.

## Non-Goals

- Do not keep helper catalogs as the primary way to discover content types.
- Do not expose internal content-writing methods in global bindings.
- Do not keep `Object` or raw JSON as fallback authoring surfaces.
- Do not make `Text` or `Resource` helpers global by default.
- Do not depend on file generation for the normal content path.
- Do not keep PST's built-in learning packet chain as the authoritative learning transaction.

## Source Facts

These facts were checked against the mapped PST 1.20.1 jar.

### Registry Facts

PST exposes these real Forge registries:

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

### Player Runtime Facts

`IPlayerSkills` and `PlayerSkills` expose:

- learned skills
- skill points
- tree reset flag
- reset tree

`PlayerSkills.learnSkill(PassiveSkill)` does two things at once:

- checks and mutates learned state
- spends exactly `1` skill point

That cost is hardcoded. PST does not model per-skill dynamic cost in this transaction.

### Skill and Tree Runtime Facts

`PassiveSkill` exposes:

- bonuses
- requirements
- connections
- tags
- title and title color
- description
- icon, frame, border
- layout position
- button size
- starting-point state

`PassiveSkillTree` exposes:

- tree id
- skill ids
- skill limitations

### Requirement Runtime Facts

`SkillRequirement<T>` is already a `Predicate<Player>`.

That means requirement runtime evaluation can directly reuse PST's `test(player)` path.

### Bonus Runtime Facts

`SkillBonus<T>` already supports:

- `merge`
- `multiply`
- `copy`
- tooltip generation
- positivity check

`SkillBonusHandler` already supports:

- reading player bonuses
- reading merged player bonuses

The problem is not missing runtime behavior. The problem is that the exposed Java API is not KubeJS-friendly.

### Sync Facts

`SyncPlayerSkillsMessage` syncs:

- learned skill ids
- skill points

This is enough to keep the normal PST screen working if PassiveSTJS continues to feed the same state shape.

## Chosen Approach

PassiveSTJS will use three script-facing layers with one shared schema core.

### 1. Startup Registration

`StartupEvents.registry(...)` remains the only registration surface for PST serializer types.

Scripts register real PST registry entries. Each entry also declares PassiveSTJS schema metadata.

### 2. Content Authoring

`PassiveSTJSEvents.skillTreeContent(...)` remains the only content authoring surface.

It is split into:

- fixed typed structure for `tree` and `skill`
- schema-driven typed nodes for `bonus`, `requirement`, `condition`, `listener`, `value`, `multiplier`, and `itemBonus`

### 3. Runtime Helper Surface

`PST` becomes the single public binding.

`PST` exposes runtime query, runtime mutation, and simulation helpers. It does not expose internal content-writing methods.

## Public API Shape

## Startup Scripts

Startup remains standard KubeJS:

```js
StartupEvents.registry('skilltree:skill_bonuses', event => {
  event.create('kubejs:bleed_bonus')
  // serializer builder config
  // schema metadata config
})
```

Startup registration is the source of truth for dynamic type metadata.

## Server Scripts

Tree content stays in one event:

```js
PassiveSTJSEvents.skillTreeContent(event => {
  const tree = event.editTree('kubejs:combat')

  tree.startingSkill('kubejs:root', skill => {
    skill.titleKey('kubejs.skill.root')
    skill.position(0, 0)
    skill.bonus('skilltree:damage', bonus => {
      bonus.amount(2)
      bonus.operation('ADDITION')
    })
  })
})
```

The author never supplies raw JSON or generic `Object` payloads.

## Bindings

Global bindings are reduced to one public helper root:

- `PST`

`PST` is for runtime and simulation only.

These do not remain public binding responsibilities:

- writing skill JSON
- writing tree JSON
- exposing internal registry views as helper catalogs
- exposing internal content builder factories

## Content DSL

### Fixed Typed Layer

`tree` and `skill` keep a complete, explicit DSL because their structure is stable.

This layer covers:

- ids
- title and title color
- translation-backed descriptions
- icon, frame, border
- button size
- direct, long, and one-way connections
- tags and tree tag limits
- position
- alternate layout helpers such as distance and angle when added
- starting-point state

These APIs must be complete enough that normal skill and tree authoring never needs a raw escape hatch.

### Schema-Driven Node Layer

The following nodes are dynamic:

- bonus
- requirement
- living condition
- damage condition
- item condition
- enchantment condition
- event listener
- numeric value
- multiplier
- item bonus

These nodes use one pattern:

```js
skill.bonus('namespace:type', node => {
  node.someField(...)
})
```

The callback is not a loose object writer. It is a schema-backed field writer.

The schema determines:

- allowed fields
- field types
- enum choices
- registry references
- defaults
- documentation
- Probe completion

No `raw(...)` or generic object fallback is kept in the public API.

## Shared Schema Core

Each startup-registered PST type carries one PassiveSTJS schema definition.

That schema drives three systems:

- content authoring validation
- runtime view export
- Probe metadata

This removes the current drift where Java builders, runtime helpers, and Probe docs each describe the API differently.

### Schema Responsibilities

The schema layer must describe:

- scalar fields
- booleans
- ints and doubles
- resource locations
- text values
- enums
- nested registry references
- lists
- optional fields
- defaults
- documentation text

### Schema Boundaries

Not everything should be dynamic.

- `tree` and `skill` remain fixed typed shells
- extensible content nodes become schema-driven

This keeps the API readable while still supporting custom PST types without hardcoded Java helper growth.

## Probe Strategy

Probe must read the same schema metadata that runtime authoring and runtime view export use.

That means:

- callback parameter completion for dynamic nodes comes from the registered type schema
- docs and snippets come from the same schema metadata
- content-side completion is not driven by fake helper catalogs

Legacy ProbeJS remains the supported Probe target.

## Learning Architecture

PassiveSTJS takes ownership of the learning transaction.

The existing PST capability, skill application hooks, and sync shape remain useful, but `PlayerSkills.learnSkill(...)` is no longer the authoritative transaction.

### Learn Service

`LearnService` becomes the only learning entrypoint for:

- learn skill
- remove skill
- reset tree

Its job is:

1. resolve the target skill
2. verify learned state
3. verify requirements
4. compute cost
5. ask the resource controller to spend or refund resources
6. mutate learned skill state
7. call PST's `skill.learn(player, true)` or `skill.remove(player)` hooks
8. sync the final player skill state

### Resource Controller

Resource spending is split out into its own interface.

The default implementation still uses PST skill points, but only as one resource backend.

That means:

- skill point support remains
- the learning transaction no longer hardcodes skill point logic
- future resource systems can plug in without pretending PST already models them

### Learn Simulation

Learning also exposes a simulation layer for:

- `canLearn`
- `previewLearn`
- `previewCost`
- `previewRefund`
- requirement failure reporting

The same simulation result should be reusable by:

- KubeJS scripts
- UI decisions
- future compat systems

## Runtime Helper Surface

`PST` exposes runtime helpers. It does not expose content builders.

### Player-Centered Helper

`PST.player(player)` is the main entrypoint.

It should cover:

- points
- learned skills
- `hasSkill(id)`
- `learn(id)`
- `remove(id)`
- `reset()`
- `canLearn(id)`
- `previewLearn(id)`
- tree reset state
- bonus queries by type id

### Definition Queries

`PST.skill(id)` and `PST.tree(id)` should also exist.

They provide read-only definition views for:

- bonuses
- requirements
- connections
- tags
- descriptions
- layout
- tree limits
- default tree lookup

### Simulation Helpers

`PST.simulate(...)` covers operations that are not tied to one player's current state.

This includes:

- bonus merge
- bonus multiply
- requirement test
- learned-state previews
- resource previews

## Runtime Views

Public runtime helpers should not dump raw PST Java objects into KubeJS and force script authors to know internal subclasses.

Instead, PassiveSTJS wraps them into uniform runtime views.

### Bonus View

A bonus view should expose:

- type id
- tooltip
- positivity
- schema fields
- field lookup by name
- serialized json view
- merge
- multiply

### Requirement View

A requirement view should expose:

- type id
- tooltip
- schema fields
- field lookup by name
- serialized json view
- `test(player)`

### Predicate and Listener Views

Condition and listener views should expose the same kind of uniform metadata:

- type id
- tooltip
- serialized json view
- schema field access

The wrapped object may still be the real PST instance internally. The public surface is the normalized PassiveSTJS view.

## Serializer Use

PST serializer interfaces already support JSON serialization.

PassiveSTJS should use those serializers as the base export path for runtime views instead of writing custom ad hoc field dumping code for every subtype.

Schema metadata still matters because serializer output alone is not enough for:

- field typing
- Probe metadata
- ergonomic lookup
- enum descriptions

## Required Script UX Rules

- no public `Object` catch-all for normal content authoring
- no public raw JSON path for normal content authoring
- no fake helper catalog as the primary source of valid type choices
- no public binding that exists only because the Java internals happen to use it
- text support must be built into content builders
- Probe completion must describe the same field model that runtime authoring enforces

## Compatibility Rules

- PassiveSTJS must work with SkillTree and KubeJS alone
- PassiveIntegration remains optional
- ammo burst compat remains in compat-owned packages and services
- compat must plug into the same runtime helper and learning architecture instead of creating a second scripting model

## Migration Rules

- old internal-heavy bindings are removed or hidden from JS
- old helper catalogs are removed or reduced to private compatibility code
- any temporary compatibility adapter must delegate into the new schema and runtime systems
- no adapter may remain a second authoritative path

## Acceptance Criteria

This design is accepted only if all of these become true.

### Content Authoring

- a script author can build complex trees without writing raw JSON
- a script author can use custom registered dynamic node types without writing raw JSON
- text fields support translation-backed content directly in the builder
- `skillTreeContent` becomes the only content entrypoint

### Bindings

- public global bindings collapse to runtime-focused helpers
- `PST` no longer exposes internal content plumbing
- `Text` and `Resource` do not remain global helpers unless later review proves they are necessary

### Learning

- learning no longer depends on PST's hardcoded spend-one-point transaction
- resource spending is pluggable
- skill point support still works through the default resource controller
- sync still feeds the same learned-skills and skill-points state expected by the normal PST screen

### Runtime Helpers

- scripts can inspect player skill state without touching PST capability internals
- scripts can inspect bonus and requirement runtime state without passing Java `Class` objects
- scripts can simulate merge, multiply, and learn outcomes through a normalized helper surface

### Shared Metadata

- Probe, runtime field views, and content validation all read from the same schema metadata
- there is no separate fake metadata source for docs

## Risks

- schema completeness is the hard part; if the schema layer misses fields, the public API becomes dishonest
- runtime views for some PST subtypes may need small adapters where PST does not expose enough getters
- learning takeover must preserve screen sync and bonus application order

## Decision Summary

PassiveSTJS stops trying to solve content authoring with helper catalogs and loose objects. It uses:

- real PST startup registries
- a fixed typed shell for tree and skill content
- schema-driven dynamic nodes
- one public runtime helper root
- a PassiveSTJS-owned learning transaction with pluggable resources

That is the smallest architecture that is still complete.
