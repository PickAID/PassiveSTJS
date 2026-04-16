# PassiveSTJS Sample Scripts

These files are reference samples only.

They are intentionally placed under `examples/`, so KubeJS will not auto-load them.

Copy the parts you need into:

- `startup_scripts/` for PST registry registration
- `server_scripts/` for `PassiveSTJSEvents.skillTreeContent(...)`
- `server_scripts/` for runtime helpers such as `PassiveSkillTreeJS.player(...)` and `PassiveSkillTreeJS.item(...)`

Files:

- `01_startup_registry_runtime.js`
  - custom `skill_bonus`
  - custom `living_condition`
  - custom `event_listener`
  - custom `skill_requirement`
  - translation-driven tooltip fragments
  - schema with nested `NODE` fields
- `02_server_skill_tree_content.js`
  - tree and skill content using the registered custom types
  - `Component`-based title and description lines
  - nested schema-driven listener and condition payloads
  - includes an item-bonus recipe-flow node so the tree can point users at the workbench socket test path
- `03_server_runtime_api.js`
  - `PassiveSkillTreeJS.player(...)`, `PassiveSkillTreeJS.skill(...)`, `PassiveSkillTreeJS.tree(...)`, `PassiveSkillTreeJS.item(...)`
  - skill point grant / consume
  - canonical learn path and no-cost learn path
  - main-hand item trigger example
- `05_server_requirement_listener_chain.js`
  - focused sample for custom requirement + custom listener on the same skill
  - useful when you only want the linked pattern, not the whole showcase tree
- `06_client_lang_entries.js`
  - matching `ClientEvents.lang(...)` entries
  - includes tooltip join templates and translated fragments
- `07_server_runtime_helper_samples.js`
  - focused `PassiveSkillTreeJS.player(...).skill(...)` usage
  - mirrors the runtime helper ids used throughout the checked-in runtime helper guide
  - shows `learned`, `canLearn`, `bonuses`, `listeners`, and `requirements`
- `08_server_workbench_item_bonus_recipe.js`
  - `event.recipes.skilltree.workbench_item_bonus()` chain-first canonical path
  - includes built-in helpers such as `.baseEquipmentTypeCondition(...)`, `.ingredientTag(...)`, `.skillBonusItemBonus(...)`, `.attributeItemBonus(...)`, and `.itemBonusList(...)`
  - built-in PST item bonus paths are the recommended route for workbench usage
  - still keeps typed-id fallback for custom `baseItemCondition(...)` and `itemBonus(...)`
- `09_server_item_bonus_runtime_api.js`
  - `PassiveSkillTreeJS.item(stack)` runtime helper
  - direct item-bonus reads, appends, and clears on live `ItemStack`s
  - useful when the stack should gain or lose PST item bonuses outside the workbench flow
