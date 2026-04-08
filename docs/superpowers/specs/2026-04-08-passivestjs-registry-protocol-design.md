# PassiveSTJS Registry Protocol Design

## Status

Approved in design review on April 8, 2026.

## Scope

This design replaces the current hardcoded KubeJS content helpers with a registry-driven protocol for `PassiveSTJS`. The new protocol must support script-defined `bonus`, `condition`, `requirement`, `value`, and `listener` types, keep Passive Skill Tree compatibility, remove runtime file generation from the main path, and fix the current first-load and reload instability around skill trees.

The design does not treat KubeJS as a thin wrapper around PST's Forge registries. Instead, `PassiveSTJS` becomes the script-facing authority, and PST's existing registries remain a lower compatibility layer.

## Goals

- Replace hardcoded public helpers such as fixed bonus factory methods with a unified registry protocol.
- Register all script-facing types through one dedicated event instead of scattered methods.
- Support all five type kinds:
  - `bonus`
  - `condition`
  - `requirement`
  - `value`
  - `listener`
- Support both data-only types and script-executed runtime types.
- Support both `cached` and `dynamic` execution, with `cached` as the default.
- Make KubeJS tree content available on first world entry without requiring manual reload.
- Keep PST built-in types and Java-side extensions compatible.
- Make ProbeJS autocomplete and docs come from registry metadata instead of hardcoded Java members.
- Keep editor and normal skill tree views on one final data source.

## Non-Goals

- Do not make `PST.Bonus.bleedChance`, `event.damage()`, or similar named methods the public source of truth.
- Do not use PST's Forge registries as the primary script API.
- Do not keep runtime content generation on the file system as the normal execution path.
- Do not allow late registration after the registry phase is frozen.
- Do not redesign the entire PST editor UX in this stage.

## Problem Summary

The current `PassiveSTJS` state has three structural problems:

- The public KubeJS surface still relies on hardcoded Java content helpers, so the real type system is not registry-driven.
- Script trees and default PST trees do not enter the same reload pipeline cleanly, which causes first-load empty trees, reload-only visibility, and cache drift between screens.
- ProbeJS metadata, docs, and runtime behavior do not come from one shared source, so the API remains thin, brittle, and incomplete.

The new protocol fixes all three by introducing a single authoritative registry and moving KubeJS content into PST's own reload flow.

## Core Architecture

### Main Layers

The protocol is split into four layers.

1. `PassiveSTJS` reload registry
   - The script-facing authority.
   - Built on immutable snapshots assembled during reload.
2. PST compatibility bridge
   - Lets PST built-in types and existing Java serializer-based extensions continue to work.
   - Used as a fallback layer, not as the main script surface.
3. Reload integration layer
   - Merges PST data, script data, default-tree rules, and editor-visible results into one plan.
4. Probe and docs layer
   - Generates autocomplete and docs from the same metadata that drives runtime behavior.

### Registry Snapshot

Each reload builds one immutable registry snapshot that contains all known type definitions for the current server state. A snapshot includes:

- type kind
- full resource id
- source metadata
- optional `extra` namespace
- schema definition
- builder metadata
- ProbeJS and doc metadata
- execution mode
- runtime adapter

Once the snapshot is frozen, runtime code can only read from it.

### Source Model

A registry entry may come from one of three sources:

- `pst_builtin`
  - PST built-in types mirrored into the `PassiveSTJS` registry.
- `java_extension`
  - Java-side `PassiveSTJS` or compat modules that contribute type providers.
- `script`
  - KubeJS registrations from `PSTEvents.registry`.

The source label is stored for logging, docs, and debugging.

## Public KubeJS API

### Registration Event

All script-defined types are registered through one dedicated event:

```js
PSTEvents.registry(event => {
  event.register('bonus', 'skilltree:damage', type => {
    type.mode('cached')
    type.schema(schema => {
      schema.double('amount')
      schema.enum('operation', PST.helpers.operation())
      schema.condition('player_condition').optional()
      schema.multiplier('player_multiplier').optional()
    })
  })
})
```

This event is the only script-facing registration entrypoint.

### Extra Scope

The registry event also supports `Extra` scopes modeled after PMMOJS and KubeJSSanity style namespace grouping:

```js
PSTEvents.registry(event => {
  event.extra('passiveintegration', extra => {
    extra.register('bonus', 'ammo_burst_unlocked', type => {
      type.mode('cached')
      type.schema(schema => {
        schema.double('value')
      })
    })
  })
})
```

`extra.register('bonus', 'ammo_burst_unlocked', ...)` resolves to `passiveintegration:ammo_burst_unlocked`.

`Extra` is a scoped registration helper, not a second registry system.

### Consumption API

Consumers never instantiate named hardcoded factories directly. They first resolve a registered type handle, then create a builder:

```js
const damage = PST.registry('bonus').get('skilltree:damage').create()
```

The semantics are:

- `registry(kind)` selects one registry view.
- `get(id)` resolves a registered type handle.
- `create()` creates a fresh builder instance for that type.

This keeps lookup and instantiation separate.

### Content Event

Tree and skill content stay in a separate event:

```js
PSTEvents.skillTreeContent(event => {
  const damage = PST.registry('bonus').get('skilltree:damage').create()
    .amount(2)
    .operation(PST.helpers.operation().add())

  event.editSkill('kubejs:root').bonus(damage)
})
```

The content event may consume registered types, but it may not register new ones.

## Registry Lifecycle

### Reload Sequence

The registry is assembled in this order:

1. Start reload assembly.
2. Load mirrored PST built-ins.
3. Load Java-side `PassiveSTJS` and compat contributors.
4. Fire `PSTEvents.registry`.
5. Validate all definitions.
6. Freeze the registry snapshot.
7. Fire `PSTEvents.skillTreeContent`.
8. Build one `SkillTreeReloadPlan`.
9. Commit the final plan into PST runtime caches.

### Freeze Rule

The registry freezes immediately after the registry event finishes. After that point:

- no new types may be registered
- no schema may change
- no execution mode may change

Late registration is an error, not a warning.

This rule exists to keep Probe output, tree deserialization, and runtime execution in sync.

## Type Definition Model

### Shared Definition Contract

Every definition, regardless of kind, exposes the same top-level contract:

- `id`
- `kind`
- `source`
- `extraScope`
- `mode`
- `schema`
- `builder`
- `runtime`
- `docs`

### Schema

The schema is the source of truth for field shape. It supports:

- scalar fields such as `double`, `int`, `boolean`, `string`, and `resourceLocation`
- text fields that accept KubeJS-friendly text inputs, including translation-backed forms
- references to other registry kinds, such as `condition`, `value`, and `listener`
- enum-like helper-backed fields such as frame, operation, target, logic, and effect type
- optional fields
- repeated fields
- object fields

The schema drives:

- runtime validation
- builder generation
- ProbeJS typings
- docs generation

### Builder Surface

The runtime implementation may use one generic schema-backed builder internally, but the public script UX must behave like a type-specific builder. In practice:

- the type handle's `create()` method returns a builder proxy for that schema
- schema fields are exposed as concrete methods such as `.amount(2)` and `.operation(...)`
- a generic fallback setter may exist internally for unknown or debug paths, but it is not the primary API surface

This keeps the runtime simple while still giving the user field-level autocomplete.

### Text Handling

Fields such as `title`, `description`, and other text-bearing properties must support KubeJS-friendly text values. At minimum, the schema must support:

- raw strings
- translation key payloads
- component-like objects that can be converted into `MutableComponent`

This replaces the current fragile string-only approach for user-facing text.

## Execution Model

### Modes

Each registered type declares one mode:

- `cached`
- `dynamic`

The default is `cached`.

### Cached Mode

`cached` mode is the normal path. During reload:

- schema access is pre-resolved
- type handles are compiled into immutable runtime adapters
- builder metadata is flattened into stable runtime structures
- script callbacks are bound once if they are needed at all

At runtime, execution uses these cached adapters directly. This is the stable and performant path.

### Dynamic Mode

`dynamic` mode exists for highly flexible script behavior. It may execute JS-driven logic at runtime, but it must be explicit.

Rules:

- script authors must opt into `dynamic`
- non-development environments may warn when `dynamic` is used
- configuration may disable `dynamic` entirely

This keeps the safe path as the default while still allowing advanced packs to use script execution where needed.

### Data-Only and Runtime-Executed Types

The protocol supports two registration shapes:

- data-only types
  - define schema, builder, docs, and adapter metadata
  - runtime resolves to existing compiled behavior
- runtime-executed types
  - define schema, builder, docs, and explicit runtime hooks
  - may use either `cached` or `dynamic`

Both shapes use the same event and the same registry surface.

## PST Compatibility Layer

### PST Built-ins

All PST built-in types are mirrored into the `PassiveSTJS` registry as builtin entries. They stay addressable by their original ids, such as `skilltree:damage`.

Mirroring has three purposes:

- it gives Probe and docs full visibility into built-in types
- it lets built-ins participate in the same `get(id).create()` flow as script types
- it prevents built-in support from living in hardcoded KubeJS helper classes

### PST Forge Registry Fallback

PST already uses Forge registries for serializer lookup. `PassiveSTJS` does not remove that system. Instead, the parser order becomes:

1. look up the type in the frozen `PassiveSTJS` registry
2. if found, use the `PassiveSTJS` runtime adapter
3. if not found, fall back to PST's original Forge registry lookup

This preserves compatibility with Java-side PST extensions that register serializers directly.

### Why Script Registration Does Not Use PST Forge Registries Directly

PST's Forge registries are serializer registries. They are appropriate for Java-side startup registration, but they are the wrong script authority for this project because:

- they are startup-oriented, while `PassiveSTJS` must stay reload-friendly
- they expect Java serializer objects, not script-first metadata and Probe output
- they do not solve the first-load and reload injection problems by themselves

For that reason, PST registries stay as a compatibility layer, not as the main script API.

## Reload and First-Load Integration

### Problem Being Solved

The current system behaves like a late patch:

- script trees may appear only after reload
- default PST trees may appear on a different timeline
- editor and normal UI can see different data

This design removes the late patch model.

### New Integration Point

`PassiveSTJS` content is injected into PST's own resource reload flow. The final merge occurs before PST publishes its runtime skill and tree maps.

The merged result is represented by one in-memory object:

- `SkillTreeReloadPlan`

The plan is assembled from:

- PST-loaded trees and skills
- script-managed trees and skills
- default-tree removal rules
- optional editor-managed runtime overlays

The plan is then committed once into the final PST caches.

### Guarantees

After this change:

- first world entry sees the same final tree data as a manual reload
- default PST trees and KubeJS trees are merged in one pass
- `clearDefaultTree()` acts during plan assembly, not as a late cache deletion
- editor views and normal tree views consume the same final committed dataset

### IO Rule

`skillTreeContent` does not generate runtime files. Its output stays in memory for the reload pipeline.

Export remains allowed only as an explicit tool path, not as the default runtime path.

## ProbeJS and Docs

### Source of Truth

ProbeJS output is generated from registry metadata, not from hardcoded Java content members.

This means:

- built-in PST types
- script-defined types
- compat extra scopes

all show up through the same metadata system.

### Probe Expectations

Probe must provide:

- `kind`-aware `registry(...).get(id)` lookup autocomplete
- typed handle information
- field-level builder autocomplete after `.create()`
- helper enums and presets
- docs grouped by source and `extra` namespace

### Helpers

Helpers such as frame, operation, target, logic, effect type, and icon presets stay available, but they are helper metadata, not the type system itself.

Allowed pattern:

```js
PST.helpers.operation().add()
```

Disallowed pattern:

```js
PST.Bonus.damage()
```

### Legacy Helper Policy

Existing hardcoded content helpers remain only as deprecated compatibility wrappers during the migration period, and they must be:

- hidden from Probe-first docs
- marked as legacy
- implemented in terms of the registry protocol

They are not part of the long-term public API.

## Extra Scope Rules

`Extra` is a first-class naming and documentation scope.

Rules:

- every extra scope has one namespace
- entries registered through an extra scope become normal registry entries under that namespace
- extra scopes may attach additional docs or grouping metadata
- extra scopes may not invent a separate lookup path or bypass freeze rules

This gives compat modules a stable place to extend the registry without polluting the core surface.

## Error Handling

### Logging

If any runtime type, skill, or tree fails to deserialize or validate, the log must include:

- failing kind
- id
- pretty-printed JSON
- source
- extra scope, if any

### Failure Policy

Default behavior:

- one failing entry does not erase unrelated valid entries

Configurable behavior:

- allow fail-fast mode for strict development environments

The logging contract must make world-entry failures actionable without guessing.

## Testing Strategy

The implementation must add coverage for the following:

- registry assembly order
- freeze behavior
- builtin mirror visibility
- PST fallback behavior
- `cached` versus `dynamic` mode selection
- `Extra` namespace resolution
- Probe metadata generation
- `get(id).create()` typing contracts
- reload plan merge rules
- first-load visibility without manual reload
- `clearDefaultTree()` during plan assembly
- parity between editor-visible and normal tree-visible datasets

## Migration Plan

### New Standard

The new standard is:

- register types in `PSTEvents.registry`
- consume types in `PSTEvents.skillTreeContent`
- resolve types through `PST.registry(kind).get(id).create()`

### Transition

The existing hardcoded helper surface is demoted to legacy compatibility. New docs, Probe output, and examples use only the registry protocol.

### Compat Modules

Compat modules such as PassiveIntegration register their extensions through:

- Java-side `PassiveSTJS` contributors
- script-side `extra(...)` scopes

They do not add core hardcoded content APIs.

## Final Decision Summary

This design makes the following decisions final:

- `PassiveSTJS` owns the script-facing type registry.
- Registration uses one dedicated event: `PSTEvents.registry`.
- `Extra` is supported as a scoped registration helper.
- Consumption uses `PST.registry(kind).get(id).create()`.
- Type-specific builder UX comes from schema metadata and Probe generation, not from hardcoded Java methods.
- `cached` is the default mode.
- `dynamic` is opt-in and may be restricted.
- PST's original Forge registries remain as a compatibility fallback.
- KubeJS content is injected into PST's reload pipeline before final cache publication.
- Runtime skill tree generation does not depend on writing files to disk.

This is the base protocol for the next implementation phase.
