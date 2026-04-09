# PassiveSTJS Real PST Registry Design

## Status

Approved in design review on April 9, 2026.

This design supersedes `2026-04-08-passivestjs-registry-protocol-design.md` for registry architecture and script-facing type registration.

## Scope

This design replaces the current fake `PassiveSTJS` registry layer with the real Passive Skill Tree Forge registries. `startup_scripts` become the only script phase that registers new PST types. `server_scripts` continue to build and edit trees, but they consume registered types instead of defining them.

The goal is to make type registration, type consumption, Probe metadata, and reload behavior all describe the same system.

## Goals

- Use PST's actual Forge registries as the script-facing registration surface.
- Remove the fake flattened `bonus / condition / requirement / value / listener` registry model as the main API.
- Use `RegistryInfo` and `BuilderBase` in the normal KubeJS way.
- Make all content-side `type` fields registry-backed instead of loose `string` or `object` placeholders.
- Keep the tree-content pipeline in memory and in one reload path. No normal-path file generation.
- Preserve KubeJS support for tree creation and editing.
- Keep PassiveIntegration optional. PassiveSTJS must still run without it.
- Keep legacy ProbeJS support, but wire it to real registry metadata instead of fake exported JSON.

## Non-Goals

- Do not keep `PassiveSTJSEvents.registry` as a primary registration API.
- Do not keep `PSTRegistryKind` as the source of truth.
- Do not invent a second registry system on top of the PST registries.
- Do not use helper catalogs as the primary way to select bonus, condition, listener, or requirement types.
- Do not solve every editor UX feature in this step.

## Source Facts

PST already exposes these real Forge registries:

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

These registries are the correct registration target for startup KubeJS support.

## Chosen Approach

PassiveSTJS will expose each real PST registry through KubeJS `RegistryInfo`. Script authors register new PST serializers in `startup_scripts` through `StartupEvents.registry(...)`. PassiveSTJS will no longer post a custom server event to collect type definitions.

On the content side, builders and tree-edit APIs will treat type references as registry-backed values. Internally, tree JSON still stores resource ids because that is what PST consumes. The public script surface, however, will no longer model those fields as generic `string` or `object`.

## Rejected Approaches

### Keep the flattened 5-kind facade

This keeps the API wrong. It hides real registry boundaries, makes Probe weaker, and keeps KubeJS code disconnected from PST's own type model.

### Patch only Probe and typings

This improves the surface but leaves the architecture wrong. It would keep the fake registry event and the fake registry service alive.

## Architecture

### 1. Startup Registration Layer

`PassiveSTJSKubePlugin` will create one `RegistryInfo` view for each PST Forge registry. Each view will register one default `BuilderBase` implementation for its serializer family.

The registration model is:

- registry target: real PST Forge registry
- KubeJS builder type: `BuilderBase` subclass owned by PassiveSTJS
- created object: real PST serializer object

This means KubeJS startup registration becomes standard:

```js
StartupEvents.registry('skilltree:skill_bonuses', event => {
  event.create('kubejs:bleed_bonus')
    // builder config here
})
```

If later we need multiple builder flavors for one registry, they can be added as extra builder types on the same `RegistryInfo`. The first implementation should keep one default builder per real registry to stay simple.

### 2. Registry Mapping

The runtime design uses these builder families:

- `SkillBonusSerializerBuilder` for `skilltree:skill_bonuses`
- `LivingMultiplierSerializerBuilder` for `skilltree:skill_bonus_multipliers`
- `LivingConditionSerializerBuilder` for `skilltree:living_conditions`
- `DamageConditionSerializerBuilder` for `skilltree:damage_conditions`
- `ItemConditionSerializerBuilder` for `skilltree:item_conditions`
- `EnchantmentConditionSerializerBuilder` for `skilltree:enchantment_conditions`
- `EventListenerSerializerBuilder` for `skilltree:event_listeners`
- `FloatFunctionSerializerBuilder` for `skilltree:numeric_value_providers`
- `SkillRequirementSerializerBuilder` for `skilltree:skill_requirements`
- `ItemBonusSerializerBuilder` for `skilltree:item_bonuses`

These names are design targets. Final class names may vary, but the split is fixed: one builder family per real PST registry.

### 3. Content Layer

`server_scripts` keep the `skillTreeContent` event. That event remains responsible for:

- creating trees
- editing trees
- creating skills
- editing skills
- adding bonuses, conditions, listeners, requirements, and layout data

It does not register new types.

Content builders must consume registered types through registry-backed parameters. A bonus builder, for example, should be constructed around a real skill bonus serializer id from `skilltree:skill_bonuses`, not around a fake `kind + id` registry handle.

The important contract is:

- startup registers serializer types
- content references those serializer types
- runtime reload resolves those references once

### 4. Reload Layer

The first-load and reload failures came from split logic and drift between PST state, KubeJS state, and screen state. This design keeps one content assembly path:

1. PST loads its base data.
2. PassiveSTJS collects KubeJS tree-content edits in memory.
3. PassiveSTJS merges built-in trees, KubeJS edits, and clear-default-tree rules into one in-memory plan.
4. PassiveSTJS commits that plan into the same runtime caches that normal PST screens read.

The normal skill tree screen and the editor must read the same resolved tree graph. Reload must rebuild that graph in memory instead of depending on file output.

## Public API Changes

### Deprecated

These become deprecated compatibility paths and should not remain the main API:

- `PassiveSTJSEvents.registry`
- `PSTRegistryEventJS`
- `PSTRegistryExtraEventJS`
- `PSTRegistryKind`
- `PassiveSTJS.registry(kind)`
- `PSTRegistryBindings`
- helper catalogs used as a fake registry source

### Kept

These stay, but with a narrower role:

- `PassiveSTJSEvents.skillTreeContent`
- tree and skill builders
- in-memory reload bridge
- optional PassiveIntegration ammo burst integration

### Compatibility Policy

If a thin compatibility layer is kept for old scripts, it must delegate into the real registry-backed system. It may not remain a second implementation path.

## Typing Rules

### Registry-Backed Type Fields

Every content-side `type` field that points to a PST serializer must become registry-backed in Java signatures and generated docs.

Examples:

- bonus type -> `skilltree:skill_bonuses`
- player multiplier type -> `skilltree:skill_bonus_multipliers`
- living condition type -> `skilltree:living_conditions`
- damage condition type -> `skilltree:damage_conditions`
- item condition type -> `skilltree:item_conditions`
- enchantment condition type -> `skilltree:enchantment_conditions`
- event listener type -> `skilltree:event_listeners`
- numeric value type -> `skilltree:numeric_value_providers`
- requirement type -> `skilltree:skill_requirements`
- item bonus type -> `skilltree:item_bonuses`

The generated JS surface should describe these as registry-backed references, not as loose `String` and not as raw `Object`.

### Enum-Like Fields

Fixed PST enums such as frame, target, logic, and operation should be represented as real typed values or well-documented constants. They are not registries and do not need helper catalogs that pretend they are. Small helper accessors may remain, but only as convenience.

### Text Fields

User-facing text such as descriptions must accept translation-backed values. String-only text APIs remain insufficient.

## ProbeJS Legacy Strategy

PassiveSTJS must target the installed legacy ProbeJS stack, not the newer `zzzank` plugin API.

The Probe work should follow these rules:

- metadata must come from real `RegistryInfo` and real builder types
- registry-backed parameters must expose useful hover text and completion targets
- generated docs must describe actual PST registries and actual content contracts
- exported fake metadata JSON is not the source of truth

If extra legacy Probe hooks are needed, they must be attached to the real registration model. They must not recreate the discarded fake registry layer.

## Built-In Content Helpers

`Bonuses`, `Conditions`, `Listeners`, `Requirements`, `Values`, and `Multipliers` should no longer act as the authoritative registry catalog.

They may continue to exist only if they are reduced to one of these roles:

- convenience templates for built-in PST content
- small adapters that build content from registry-backed inputs
- compatibility shims during migration

They must not remain the source of truth for what types exist.

## Testing Strategy

### Unit and Contract Tests

- verify each real PST registry is exposed through `RegistryInfo`
- verify each registry has the expected default builder type
- verify deprecated fake registry classes are no longer needed for registration
- verify content builders no longer expose loose `string` or `object` signatures for registry-backed type slots

### Integration Tests

- startup script registers a new bonus serializer into `skilltree:skill_bonuses`
- server script consumes that bonus type in a tree
- first world entry shows the tree without reload
- reload keeps the same tree visible
- editor view and normal tree view resolve the same graph

### Compat Tests

- PassiveSTJS runs without PassiveIntegration
- when PassiveIntegration is present, ammo burst content hooks still load through the same registry-backed content path

## Implementation Order

1. Expose the 10 real PST registries through `RegistryInfo` in the KubeJS plugin.
2. Introduce the 10 serializer builder families.
3. Move startup registration tests to `StartupEvents.registry(...)`.
4. Rework content-side typing so serializer references are registry-backed.
5. Remove the fake registry event path from the main flow.
6. Rewire Probe legacy support to the real registry model.
7. Keep or delete compatibility shims based on migration cost.

## Decision Summary

PassiveSTJS will stop pretending it has its own type registry. The real PST Forge registries are the registry API. `startup_scripts` register real PST serializer types there. `server_scripts` build trees from those types. Probe and typing follow the same model. The fake flattened registry layer is deprecated and should leave the main path.
