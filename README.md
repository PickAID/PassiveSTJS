# PassiveSTJS

KubeJS integration for Passive Skill Tree on Minecraft `1.20.1` Forge.

`PassiveSTJS` exposes Passive Skill Tree content generation, typed runtime helpers, registry integration, and a RecipeJS wrapper for PST's workbench item-bonus recipe so packs can script PST content instead of hand-authoring every JSON file.

## What It Covers

- `PassiveSTJSEvents.skillTreeContent(...)` for managed skill tree and skill generation during server datapack reload
- `PassiveSkillTreeJS.player(...)`, `PassiveSkillTreeJS.skill(...)`, `PassiveSkillTreeJS.tree(...)`, and `PassiveSkillTreeJS.item(...)` for runtime and read-only PST access from scripts
- startup registry support for PST serializer registries such as `skill_bonuses`, `living_conditions`, `event_listeners`, `numeric_value_providers`, `skill_requirements`, and `item_bonuses`
- `event.recipes.skilltree.workbench_item_bonus()` for PST workbench socketing recipes
- typed ids, enums, textures, and ProbeJS legacy completions for common PST authoring paths

## Supported Stack

- Minecraft `1.20.1`
- Forge `47.4.18`
- KubeJS `2001.6.5-build.22`
- Rhino `2001.2.2-build.17`
- Passive Skill Tree for the `1.20.1` line

## Documentation

The authored docs live in [`docs`](./docs):

- [`docs/overview.mdx`](./docs/overview.mdx)

Sample scripts live in [`examples/kubejs/passivestjs`](./examples/kubejs/passivestjs).

## Common Config

Shared config lives in `config/passivestjs-common.toml`.

Available switches:

- `editor.disabled`
- `translation_draft_export.enabled`

`editor.disabled` defaults to `true`, because PassiveSTJS is designed around generated or scripted content and explicitly disables Passive Skill Tree's built-in editor entry points unless you opt back in.

## Development Notes

- `PassiveSTJSEvents.skillTreeContent(...)` is posted from a reload listener, so generated content is applied on world load and datapack reload.
- ProbeJS legacy compatibility is installed only when `probejs_legacy` is present.
- The item-bonus runtime bridge is registered on the Forge event bus at mod startup.
- Translation draft export writes discovered keys to `config/passivestjs/generated_skilltree_lang/lang.json` when `translation_draft_export.enabled=true`.
