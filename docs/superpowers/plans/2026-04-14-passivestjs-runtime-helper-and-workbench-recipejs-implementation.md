# PassiveSTJS Runtime Helper And Workbench RecipeJS Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a player-bound PST runtime helper surface and a real KubeJS RecipeJS adapter for `skilltree:workbench_item_bonus` without inventing fake wrapper APIs.

**Architecture:** Extend `PST.player(...)` with a dedicated `PSTPlayerSkillView` plus small `bonus`, `listener`, and `requirement` views backed by `PSTRuntimeNode` only when the underlying runtime object is PassiveSTJS-managed. For RecipeJS, register a zero-argument fluent builder on the real `skilltree:workbench_item_bonus` serializer and expose a top-level alias `event.recipes.workbenchItemBonus()`: KubeJS source confirms `mapRecipe(...)` creates top-level aliases, while namespace access stays tied to the real recipe namespace.

**Tech Stack:** Java 17, Forge 1.20.1, KubeJS Forge 2001.6.5, Passive Skill Tree 0.7.4, JUnit 5

---

## Scope Check

This plan keeps three coupled pieces in one batch:

- player-bound runtime helper views
- workbench item-bonus RecipeJS support
- examples and regression tests for both surfaces

They stay together because they serve one scripting story: register a real custom PST type, inspect it from `PST.player(...)`, and author item-bonus recipes from KubeJS without raw JSON.

## File Structure

### Runtime View Surface

- Modify: `src/main/java/com/pickaid/passivestjs/kubejs/Bindings.java`
  - Preserve `PST.player(...)` as the entrypoint, but pass the actual `Player` into the runtime view.
- Modify: `src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTPlayerView.java`
  - Add `skill(PSTSkillId)` returning a player-bound runtime skill view.
- Create: `src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTPlayerSkillView.java`
  - Learned state, learn/remove methods, requirement listing, bonus listing, and listener listing for one skill from one player's perspective.
- Create: `src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTBonusView.java`
  - JS-facing wrapper for one effect-side learned bonus instance.
- Create: `src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTListenerView.java`
  - JS-facing wrapper for one trigger-side learned listener instance.
- Create: `src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTRequirementView.java`
  - JS-facing wrapper for one learn-gate requirement instance.

### Runtime Introspection Helpers

- Create: `src/main/java/com/pickaid/passivestjs/runtime/PSTCustomRuntimeSkillBonus.java`
  - Marker interface exposing `node()` for custom runtime skill bonuses.
- Create: `src/main/java/com/pickaid/passivestjs/runtime/PSTCustomRuntimeSkillRequirement.java`
  - Marker interface exposing `node()` for custom runtime skill requirements.
- Create: `src/main/java/com/pickaid/passivestjs/runtime/PSTRuntimeTypeIds.java`
  - Resolves registry ids for bonus, listener, and requirement runtime instances.
- Modify: `src/main/java/com/pickaid/passivestjs/runtime/serializer/PSTCustomRuntimeSerializers.java`
  - Make the private runtime skill-bonus and runtime requirement classes implement the new marker interfaces.

### RecipeJS Adapter

- Modify: `src/main/java/com/pickaid/passivestjs/kubejs/PassiveSTJSKubePlugin.java`
  - Register the recipe schema and alias.
- Create: `src/main/java/com/pickaid/passivestjs/kubejs/recipe/PSTWorkbenchItemBonusRecipeJS.java`
  - A zero-argument fluent `RecipeJS` subclass for `skilltree:workbench_item_bonus`.
- Create: `src/main/java/com/pickaid/passivestjs/kubejs/recipe/PSTWorkbenchItemBonusRecipeSchemas.java`
  - Holds the `RecipeSchema`, explicit zero-arg constructor, and alias registration.

### Tests

- Modify: `src/test/java/com/pickaid/passivestjs/kubejs/PublicApiSurfaceTest.java`
  - Lock the new runtime helper methods and return types.
- Modify: `src/test/java/com/pickaid/passivestjs/kubejs/BindingsRuntimeViewTest.java`
  - Lock real player-skill runtime behavior.
- Create: `src/test/java/com/pickaid/passivestjs/runtime/PSTRuntimeTypeIdsTest.java`
  - Lock id resolution and `node()` nullability for built-in vs custom runtime objects.
- Create: `src/test/java/com/pickaid/passivestjs/kubejs/recipe/PSTWorkbenchItemBonusRecipeSchemaTest.java`
  - Lock schema registration, alias registration, and fluent JSON output.
- Create: `src/test/java/com/pickaid/passivestjs/kubejs/KubeJsRuntimeHelperExamplesTest.java`
  - Lock the example files against the intended public API.

### Examples

- Modify: `examples/kubejs/passivestjs/04_startup_item_bonus_metadata.js`
  - Point to the new recipe-side usage.
- Create: `examples/kubejs/passivestjs/05_server_runtime_helper_samples.js`
  - Show `PST.player(...).skill(...)` plus `bonus`, `listener`, and `requirement` usage.
- Create: `examples/kubejs/passivestjs/06_server_workbench_item_bonus_recipe.js`
  - Show `event.recipes.workbenchItemBonus()` with a custom item bonus shell.

## Task 1: Lock The Runtime Helper Public Contract

**Files:**
- Modify: `src/test/java/com/pickaid/passivestjs/kubejs/PublicApiSurfaceTest.java`
- Modify: `src/test/java/com/pickaid/passivestjs/kubejs/BindingsRuntimeViewTest.java`

- [ ] **Step 1: Add reflection coverage for the new player-bound runtime view**

```java
import com.pickaid.passivestjs.kubejs.runtime.PSTBonusView;
import com.pickaid.passivestjs.kubejs.runtime.PSTListenerView;
import com.pickaid.passivestjs.kubejs.runtime.PSTPlayerSkillView;
import com.pickaid.passivestjs.kubejs.runtime.PSTRequirementView;
import com.pickaid.passivestjs.runtime.PSTRuntimeNode;

assertNotNull(PSTPlayerView.class.getDeclaredMethod("skill", PSTSkillId.class));
assertEquals(PSTPlayerSkillView.class, PSTPlayerView.class.getDeclaredMethod("skill", PSTSkillId.class).getReturnType());

assertNotNull(PSTPlayerSkillView.class.getDeclaredMethod("learned"));
assertNotNull(PSTPlayerSkillView.class.getDeclaredMethod("canLearn"));
assertNotNull(PSTPlayerSkillView.class.getDeclaredMethod("learn"));
assertNotNull(PSTPlayerSkillView.class.getDeclaredMethod("remove"));
assertNotNull(PSTPlayerSkillView.class.getDeclaredMethod("requirements"));
assertNotNull(PSTPlayerSkillView.class.getDeclaredMethod("bonuses"));
assertNotNull(PSTPlayerSkillView.class.getDeclaredMethod("listeners"));

assertEquals(Component.class, PSTBonusView.class.getDeclaredMethod("text").getReturnType());
assertEquals(PSTRuntimeNode.class, PSTBonusView.class.getDeclaredMethod("node").getReturnType());
assertEquals(Component.class, PSTListenerView.class.getDeclaredMethod("text").getReturnType());
assertEquals(PSTRuntimeNode.class, PSTListenerView.class.getDeclaredMethod("node").getReturnType());
assertEquals(Component.class, PSTRequirementView.class.getDeclaredMethod("text").getReturnType());
assertEquals(PSTRuntimeNode.class, PSTRequirementView.class.getDeclaredMethod("node").getReturnType());
```

- [ ] **Step 2: Add a behavior test that exercises learned state, custom bonus views, custom listener views, and custom requirement views**

```java
@Test
void playerSkillViewExposesLearnedCanLearnBonusListenerAndRequirementState() {
    ResourceLocation rootId = ResourceLocation.fromNamespaceAndPath("kubejs", "runtime_root");
    ResourceLocation branchId = ResourceLocation.fromNamespaceAndPath("kubejs", "runtime_branch");

    new com.pickaid.passivestjs.kubejs.registry.builder.PSTEventListenerSerializerBuilder(
            ResourceLocation.fromNamespaceAndPath("kubejs", "smoke_listener")
    ).createObject();

    SkillBonus.Serializer effectBonusSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillBonusSerializerBuilder(
            ResourceLocation.fromNamespaceAndPath("kubejs", "smoke_bonus")
    ).createObject();

    SkillBonus.Serializer triggerBonusSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillBonusSerializerBuilder(
            ResourceLocation.fromNamespaceAndPath("kubejs", "trigger_bonus")
    ).createObject();

    SkillRequirement.Serializer requirementSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillRequirementSerializerBuilder(
            ResourceLocation.fromNamespaceAndPath("kubejs", "smoke_requirement")
    ).test(context -> context.node().bool("allow").orElse(false)).createObject();

    PassiveSkill root = skill("kubejs:runtime_root");
    PassiveSkill branch = skill("kubejs:runtime_branch");
    root.getDirectConnections().add(branchId);

    JsonObject effectBonusJson = new JsonObject();
    effectBonusJson.addProperty("type", "kubejs:smoke_bonus");
    effectBonusJson.addProperty("amount", 2.5D);
    root.getBonuses().add(effectBonusSerializer.deserialize(effectBonusJson));

    JsonObject triggerBonusJson = new JsonObject();
    triggerBonusJson.addProperty("type", "kubejs:trigger_bonus");
    JsonObject listenerJson = new JsonObject();
    listenerJson.addProperty("type", "kubejs:smoke_listener");
    listenerJson.addProperty("mode", "attack");
    triggerBonusJson.add("event_listener", listenerJson);
    root.getBonuses().add(triggerBonusSerializer.deserialize(triggerBonusJson));

    JsonObject requirementJson = new JsonObject();
    requirementJson.addProperty("type", "kubejs:smoke_requirement");
    requirementJson.addProperty("allow", true);
    branch.getRequirements().add(requirementSerializer.deserialize(requirementJson));

    SkillsReloader.getSkills().put(rootId, root);
    SkillsReloader.getSkills().put(branchId, branch);

    PassiveSkillTree tree = new PassiveSkillTree(ResourceLocation.fromNamespaceAndPath("kubejs", "runtime_tree"));
    tree.getSkillIds().add(rootId);
    tree.getSkillIds().add(branchId);
    SkillTreesReloader.getSkillTrees().put(tree.getId(), tree);

    PlayerSkills playerSkills = new PlayerSkills();
    playerSkills.setSkillPoints(1);
    playerSkills.getPlayerSkills().add(root);

    PSTPlayerView playerView = new PSTPlayerView(playerSkills);
    PSTPlayerSkillView rootView = playerView.skill(PSTSkillId.of(rootId));
    PSTPlayerSkillView branchView = playerView.skill(PSTSkillId.of(branchId));

    assertTrue(rootView.learned());
    assertFalse(branchView.learned());
    assertTrue(branchView.canLearn());

    assertEquals(1, rootView.bonuses().size());
    assertEquals("kubejs:smoke_bonus", rootView.bonuses().get(0).typeId());
    assertEquals(2.5D, rootView.bonuses().get(0).node().number("amount").orElseThrow(), 0.0001D);

    assertEquals(1, rootView.listeners().size());
    assertEquals("kubejs:smoke_listener", rootView.listeners().get(0).typeId());
    assertEquals("attack", rootView.listeners().get(0).node().string("mode").orElseThrow());

    assertEquals(1, branchView.requirements().size());
    assertEquals("kubejs:smoke_requirement", branchView.requirements().get(0).typeId());
    assertTrue(branchView.requirements().get(0).passed());
    assertTrue(branchView.requirements().get(0).node().bool("allow").orElseThrow());
}
```

- [ ] **Step 3: Run the targeted runtime-helper tests and verify they fail**

Run:

```bash
cd /Users/gedwen/Documents/programing/MC/PassiveSTJS
./gradlew test \
  --tests com.pickaid.passivestjs.kubejs.PublicApiSurfaceTest \
  --tests com.pickaid.passivestjs.kubejs.BindingsRuntimeViewTest \
  --console=plain
```

Expected:

- reflection assertions fail because `PSTPlayerView.skill(...)` and the new runtime view classes do not exist yet
- the behavior test fails to compile or fails at runtime because there is no player-skill runtime surface yet

## Task 2: Implement The Player-Bound Runtime Helper

**Files:**
- Modify: `src/main/java/com/pickaid/passivestjs/kubejs/Bindings.java`
- Modify: `src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTPlayerView.java`
- Create: `src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTPlayerSkillView.java`
- Create: `src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTBonusView.java`
- Create: `src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTListenerView.java`
- Create: `src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTRequirementView.java`
- Create: `src/main/java/com/pickaid/passivestjs/runtime/PSTCustomRuntimeSkillBonus.java`
- Create: `src/main/java/com/pickaid/passivestjs/runtime/PSTCustomRuntimeSkillRequirement.java`
- Create: `src/main/java/com/pickaid/passivestjs/runtime/PSTRuntimeTypeIds.java`
- Modify: `src/main/java/com/pickaid/passivestjs/runtime/serializer/PSTCustomRuntimeSerializers.java`

- [ ] **Step 1: Add marker interfaces for custom runtime bonus and requirement nodes**

```java
package com.pickaid.passivestjs.runtime;

public interface PSTCustomRuntimeSkillBonus {
    PSTRuntimeNode node();
}
```

```java
package com.pickaid.passivestjs.runtime;

public interface PSTCustomRuntimeSkillRequirement {
    PSTRuntimeNode node();
}
```

- [ ] **Step 2: Make the serializer-backed runtime classes implement the new interfaces**

```java
private static final class RuntimeSkillRequirement implements SkillRequirement<RuntimeSkillRequirement>, PSTCustomRuntimeSkillRequirement {
    private final PSTRuntimeNode node;

    @Override
    public PSTRuntimeNode node() {
        return node;
    }
}
```

```java
private static final class RuntimeSkillBonus implements EventListenerBonus<RuntimeSkillBonus>, PSTCustomRuntimeSkillBonus {
    private final PSTRuntimeNode node;

    @Override
    public PSTRuntimeNode node() {
        return node;
    }
}
```

- [ ] **Step 3: Add a shared type-id resolver for bonus, listener, and requirement instances**

```java
package com.pickaid.passivestjs.runtime;

import daripher.skilltree.init.PSTRegistries;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.requirement.SkillRequirement;
import net.minecraft.resources.ResourceLocation;

public final class PSTRuntimeTypeIds {
    private PSTRuntimeTypeIds() {
    }

    public static String skillBonusId(SkillBonus<?> bonus) {
        if (bonus instanceof PSTCustomRuntimeSkillBonus runtime) {
            return runtime.node().id().toString();
        }
        ResourceLocation id = PSTRegistries.SKILL_BONUSES.get().getKey(bonus.getSerializer());
        return id == null ? "<unknown>" : id.toString();
    }

    public static String eventListenerId(EventListenerBonus<?> bonus) {
        SkillEventListener listener = bonus.getEventListener();
        if (listener instanceof PSTCustomRuntimeEventListener runtime) {
            return runtime.node().id().toString();
        }
        ResourceLocation id = PSTRegistries.EVENT_LISTENERS.get().getKey(listener.getSerializer());
        return id == null ? "<unknown>" : id.toString();
    }

    public static String skillRequirementId(SkillRequirement<?> requirement) {
        if (requirement instanceof PSTCustomRuntimeSkillRequirement runtime) {
            return runtime.node().id().toString();
        }
        ResourceLocation id = PSTRegistries.SKILL_REQUIREMENTS.get().getKey(requirement.getSerializer());
        return id == null ? "<unknown>" : id.toString();
    }
}
```

- [ ] **Step 4: Add the new JS-facing runtime view classes and wire them from `PSTPlayerView`**

```java
public final class PSTPlayerSkillView {
    private final Player player;
    private final IPlayerSkills playerSkills;
    private final PassiveSkill skill;
    private final PSTPlayerView owner;

    public boolean learned() {
        return owner.hasSkill(PSTSkillId.of(skill.getId()));
    }

    public boolean canLearn() {
        return player != null
                ? PSTSkillLearningRules.canLearn(player, playerSkills, skill)
                : PSTSkillLearningRules.canLearn(playerSkills, skill);
    }

    public boolean learn() {
        return owner.learn(PSTSkillId.of(skill.getId()));
    }

    public boolean remove() {
        return owner.remove(PSTSkillId.of(skill.getId()));
    }

    public List<PSTBonusView> bonuses() {
        return skill.getBonuses().stream()
                .filter(bonus -> !(bonus instanceof EventListenerBonus<?>))
                .map(PSTBonusView::new)
                .toList();
    }

    public List<PSTListenerView> listeners() {
        return skill.getBonuses().stream()
                .filter(EventListenerBonus.class::isInstance)
                .map(EventListenerBonus.class::cast)
                .map(PSTListenerView::new)
                .toList();
    }

    public List<PSTRequirementView> requirements() {
        return skill.getRequirements().stream()
                .map(requirement -> new PSTRequirementView(player, playerSkills, requirement))
                .toList();
    }
}
```

```java
public final class PSTBonusView {
    private final SkillBonus<?> bonus;

    public String typeId() {
        return PSTRuntimeTypeIds.skillBonusId(bonus);
    }

    public Component text() {
        return bonus.getTooltip();
    }

    public PSTRuntimeNode node() {
        return bonus instanceof PSTCustomRuntimeSkillBonus runtime ? runtime.node() : null;
    }
}
```

```java
public final class PSTListenerView {
    private final EventListenerBonus<?> bonus;

    public String typeId() {
        return PSTRuntimeTypeIds.eventListenerId(bonus);
    }

    public Component text() {
        return bonus.getTooltip();
    }

    public PSTRuntimeNode node() {
        SkillEventListener listener = bonus.getEventListener();
        return listener instanceof PSTCustomRuntimeEventListener runtime ? runtime.node() : null;
    }
}
```

```java
public final class PSTRequirementView {
    private final Player player;
    private final IPlayerSkills playerSkills;
    private final SkillRequirement<?> requirement;

    public boolean passed() {
        if (requirement instanceof LearnedSkillRequirement learnedRequirement) {
            return playerSkills.getPlayerSkills().stream()
                    .map(PassiveSkill::getId)
                    .anyMatch(learnedRequirement.getSkillId()::equals);
        }
        return player != null && requirement.test(player);
    }

    public String typeId() {
        return PSTRuntimeTypeIds.skillRequirementId(requirement);
    }

    public Component text() {
        return requirement.getTooltip();
    }

    public PSTRuntimeNode node() {
        return requirement instanceof PSTCustomRuntimeSkillRequirement runtime ? runtime.node() : null;
    }
}
```

```java
public final class PSTPlayerView {
    private final Player player;
    private final ServerPlayer serverPlayer;
    private final IPlayerSkills playerSkills;

    public PSTPlayerView(IPlayerSkills playerSkills) {
        this(null, playerSkills);
    }

    public PSTPlayerView(Player player, IPlayerSkills playerSkills) {
        this.player = player;
        this.serverPlayer = player instanceof ServerPlayer sp ? sp : null;
        this.playerSkills = Objects.requireNonNull(playerSkills, "playerSkills");
    }

    public PSTPlayerSkillView skill(PSTSkillId id) {
        if (id == null) {
            return null;
        }
        PassiveSkill skill = SkillsReloader.getSkillById(id.location());
        return skill == null ? null : new PSTPlayerSkillView(player, playerSkills, skill, this);
    }
}
```

```java
public PSTPlayerView player(Player player) {
    if (player == null || !PlayerSkillsProvider.hasSkills(player)) {
        return null;
    }
    return new PSTPlayerView(player, PlayerSkillsProvider.get(player));
}
```

- [ ] **Step 5: Run the targeted tests and verify they pass**

Run:

```bash
cd /Users/gedwen/Documents/programing/MC/PassiveSTJS
./gradlew test \
  --tests com.pickaid.passivestjs.kubejs.PublicApiSurfaceTest \
  --tests com.pickaid.passivestjs.kubejs.BindingsRuntimeViewTest \
  --console=plain
```

Expected:

- `PublicApiSurfaceTest` passes with the new runtime view signatures
- `BindingsRuntimeViewTest` passes and confirms learned state, type ids, and payload access

- [ ] **Step 6: Commit the runtime-helper slice**

```bash
cd /Users/gedwen/Documents/programing/MC/PassiveSTJS
git add \
  src/main/java/com/pickaid/passivestjs/kubejs/Bindings.java \
  src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTPlayerView.java \
  src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTPlayerSkillView.java \
  src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTBonusView.java \
  src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTListenerView.java \
  src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTRequirementView.java \
  src/main/java/com/pickaid/passivestjs/runtime/PSTCustomRuntimeSkillBonus.java \
  src/main/java/com/pickaid/passivestjs/runtime/PSTCustomRuntimeSkillRequirement.java \
  src/main/java/com/pickaid/passivestjs/runtime/PSTRuntimeTypeIds.java \
  src/main/java/com/pickaid/passivestjs/runtime/serializer/PSTCustomRuntimeSerializers.java \
  src/test/java/com/pickaid/passivestjs/kubejs/PublicApiSurfaceTest.java \
  src/test/java/com/pickaid/passivestjs/kubejs/BindingsRuntimeViewTest.java
git commit -m "feat(passivestjs): add player-bound runtime helper views"
```

## Task 3: Lock The RecipeJS Contract Against KubeJS Source Reality

**Files:**
- Create: `src/test/java/com/pickaid/passivestjs/kubejs/recipe/PSTWorkbenchItemBonusRecipeSchemaTest.java`

- [ ] **Step 1: Add a failing schema-registration test for the real serializer id and the mapped alias**

```java
package com.pickaid.passivestjs.kubejs.recipe;

import com.pickaid.passivestjs.kubejs.PassiveSTJSKubePlugin;
import dev.latvian.mods.kubejs.recipe.schema.RecipeNamespace;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PSTWorkbenchItemBonusRecipeSchemaTest {
    @Test
    void pluginRegistersWorkbenchItemBonusSchemaAndMappedAlias() {
        Map<String, RecipeNamespace> namespaces = new HashMap<>();
        Map<String, ResourceLocation> mappedRecipes = new HashMap<>();

        new PassiveSTJSKubePlugin().registerRecipeSchemas(new RegisterRecipeSchemasEvent(namespaces, mappedRecipes));

        assertTrue(namespaces.containsKey("skilltree"));
        assertTrue(namespaces.get("skilltree").containsKey("workbench_item_bonus"));
        assertEquals(
                ResourceLocation.fromNamespaceAndPath("skilltree", "workbench_item_bonus"),
                mappedRecipes.get("workbenchItemBonus")
        );
    }
}
```

- [ ] **Step 2: Add a failing fluent-builder output test**

```java
@Test
void workbenchItemBonusRecipeBuilderWritesNativePstJson() {
    PSTWorkbenchItemBonusRecipeJS recipe = new PSTWorkbenchItemBonusRecipeJS();
    recipe.json = new JsonObject();
    recipe.json.addProperty("type", "skilltree:workbench_item_bonus");

    recipe.baseItemConditionSchema(PSTItemConditionId.parse("skilltree:equipment_type"), condition -> {
        condition.set("equipment_type", "shield");
    });
    recipe.ingredient(Map.of("tag", "forge:ingots/copper"), 2);
    recipe.requiresPassiveSkill(true);
    recipe.itemBonus(PSTItemBonusId.parse("kubejs:socket_shell"), itemBonus -> {
        itemBonus.int("tier", 1);
    });

    assertEquals("skilltree:equipment_type", recipe.json.getAsJsonObject("base_item_condition").get("type").getAsString());
    assertEquals("shield", recipe.json.getAsJsonObject("base_item_condition").get("equipment_type").getAsString());
    assertEquals(1, recipe.json.getAsJsonArray("ingredients").size());
    assertEquals(2, recipe.json.getAsJsonArray("ingredients").get(0).getAsJsonObject().get("required_amount").getAsInt());
    assertTrue(recipe.json.get("requires_passive_skill").getAsBoolean());
    assertEquals("kubejs:socket_shell", recipe.json.getAsJsonObject("item_bonus").get("type").getAsString());
}
```

- [ ] **Step 3: Run the targeted recipe tests and verify they fail**

Run:

```bash
cd /Users/gedwen/Documents/programing/MC/PassiveSTJS
./gradlew test \
  --tests com.pickaid.passivestjs.kubejs.recipe.PSTWorkbenchItemBonusRecipeSchemaTest \
  --console=plain
```

Expected:

- the schema-registration test fails because the plugin does not yet register a recipe schema or alias
- the builder-output test fails to compile because `PSTWorkbenchItemBonusRecipeJS` does not exist yet

## Task 4: Implement The Workbench Item Bonus RecipeJS Adapter

**Files:**
- Modify: `src/main/java/com/pickaid/passivestjs/kubejs/PassiveSTJSKubePlugin.java`
- Create: `src/main/java/com/pickaid/passivestjs/kubejs/recipe/PSTWorkbenchItemBonusRecipeJS.java`
- Create: `src/main/java/com/pickaid/passivestjs/kubejs/recipe/PSTWorkbenchItemBonusRecipeSchemas.java`
- Create: `src/test/java/com/pickaid/passivestjs/runtime/PSTRuntimeTypeIdsTest.java`
- Create: `src/test/java/com/pickaid/passivestjs/kubejs/recipe/PSTWorkbenchItemBonusRecipeSchemaTest.java`

- [ ] **Step 1: Add a focused regression test for custom vs built-in type-id and `node()` behavior**

```java
package com.pickaid.passivestjs.runtime;

import com.google.gson.JsonObject;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillBonusSerializerBuilder;
import com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillRequirementSerializerBuilder;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.requirement.SkillRequirement;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PSTRuntimeTypeIdsTest {
    @Test
    void customRuntimeTypesExposeRegistryIdsAndNodes() {
        SkillBonus.Serializer bonusSerializer = new PSTSkillBonusSerializerBuilder(
                ResourceLocation.fromNamespaceAndPath("kubejs", "typed_bonus")
        ).createObject();
        SkillRequirement.Serializer requirementSerializer = new PSTSkillRequirementSerializerBuilder(
                ResourceLocation.fromNamespaceAndPath("kubejs", "typed_requirement")
        ).test(context -> true).createObject();

        JsonObject bonusJson = new JsonObject();
        bonusJson.addProperty("type", "kubejs:typed_bonus");
        SkillBonus<?> bonus = bonusSerializer.deserialize(bonusJson);

        JsonObject requirementJson = new JsonObject();
        requirementJson.addProperty("type", "kubejs:typed_requirement");
        SkillRequirement<?> requirement = requirementSerializer.deserialize(requirementJson);

        assertEquals("kubejs:typed_bonus", PSTRuntimeTypeIds.skillBonusId(bonus));
        assertEquals("kubejs:typed_requirement", PSTRuntimeTypeIds.skillRequirementId(requirement));
        assertNotNull(((PSTCustomRuntimeSkillBonus) bonus).node());
        assertNotNull(((PSTCustomRuntimeSkillRequirement) requirement).node());
    }
}
```

- [ ] **Step 2: Implement the zero-argument fluent `RecipeJS` subclass**

```java
package com.pickaid.passivestjs.kubejs.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pickaid.passivestjs.kubejs.builder.ItemBonusBuilder;
import com.pickaid.passivestjs.kubejs.builder.PSTNodeWriter;
import com.pickaid.passivestjs.kubejs.id.PSTItemBonusId;
import com.pickaid.passivestjs.kubejs.id.PSTItemConditionId;
import com.pickaid.passivestjs.schema.PSTNodeFamily;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public final class PSTWorkbenchItemBonusRecipeJS extends RecipeJS {
    public PSTWorkbenchItemBonusRecipeJS baseItemConditionSchema(PSTItemConditionId typeId, Consumer<PSTNodeWriter> consumer) {
        PSTNodeWriter writer = new PSTNodeWriter(PSTNodeFamily.ITEM_CONDITION, PSTItemConditionId.parse(typeId).location());
        if (consumer != null) {
            consumer.accept(writer);
        }
        json.add("base_item_condition", writer.json());
        save();
        return this;
    }

    public PSTWorkbenchItemBonusRecipeJS ingredient(Object ingredient, int requiredAmount) {
        InputItem input = readInputItem(ingredient);
        JsonObject entry = new JsonObject();
        entry.add("ingredient", writeInputItem(input));
        entry.addProperty("required_amount", Math.max(1, requiredAmount));
        JsonArray ingredients = json.has("ingredients") && json.get("ingredients").isJsonArray()
                ? json.getAsJsonArray("ingredients")
                : new JsonArray();
        ingredients.add(entry);
        json.add("ingredients", ingredients);
        save();
        return this;
    }

    public PSTWorkbenchItemBonusRecipeJS requiresPassiveSkill(boolean required) {
        json.addProperty("requires_passive_skill", required);
        save();
        return this;
    }

    public PSTWorkbenchItemBonusRecipeJS itemBonus(PSTItemBonusId typeId, Consumer<ItemBonusBuilder> consumer) {
        ItemBonusBuilder builder = new ItemBonusBuilder(PSTItemBonusId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        json.add("item_bonus", builder.json());
        save();
        return this;
    }

    @Override
    public void afterLoaded() {
        super.afterLoaded();
        if (!json.has("type")) {
            json.addProperty("type", "skilltree:workbench_item_bonus");
        }
        if (!json.has("base_item_condition")) {
            throw new RecipeExceptionJS("skilltree:workbench_item_bonus requires base_item_condition");
        }
        if (!json.has("item_bonus")) {
            throw new RecipeExceptionJS("skilltree:workbench_item_bonus requires item_bonus");
        }
    }
}
```

- [ ] **Step 3: Register the real schema on `skilltree:workbench_item_bonus` and add the top-level alias**

```java
package com.pickaid.passivestjs.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import net.minecraft.resources.ResourceLocation;

public final class PSTWorkbenchItemBonusRecipeSchemas {
    public static final ResourceLocation TYPE = ResourceLocation.fromNamespaceAndPath("skilltree", "workbench_item_bonus");
    public static final RecipeSchema SCHEMA = new RecipeSchema(
            PSTWorkbenchItemBonusRecipeJS.class,
            PSTWorkbenchItemBonusRecipeJS::new
    ).constructor();

    private PSTWorkbenchItemBonusRecipeSchemas() {
    }

    public static void register(RegisterRecipeSchemasEvent event) {
        event.register(TYPE, SCHEMA);
        event.mapRecipe("workbenchItemBonus", TYPE);
    }
}
```

```java
@Override
public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
    PSTWorkbenchItemBonusRecipeSchemas.register(event);
}
```

- [ ] **Step 4: Run the runtime-id and recipe tests and verify they pass**

Run:

```bash
cd /Users/gedwen/Documents/programing/MC/PassiveSTJS
./gradlew test \
  --tests com.pickaid.passivestjs.runtime.PSTRuntimeTypeIdsTest \
  --tests com.pickaid.passivestjs.kubejs.recipe.PSTWorkbenchItemBonusRecipeSchemaTest \
  --console=plain
```

Expected:

- the type-id test passes for custom runtime bonus and requirement nodes
- the plugin registers `skilltree:workbench_item_bonus` plus the mapped alias `workbenchItemBonus`
- the fluent builder writes the expected native PST JSON

- [ ] **Step 5: Commit the recipe adapter slice**

```bash
cd /Users/gedwen/Documents/programing/MC/PassiveSTJS
git add \
  src/main/java/com/pickaid/passivestjs/kubejs/PassiveSTJSKubePlugin.java \
  src/main/java/com/pickaid/passivestjs/kubejs/recipe/PSTWorkbenchItemBonusRecipeJS.java \
  src/main/java/com/pickaid/passivestjs/kubejs/recipe/PSTWorkbenchItemBonusRecipeSchemas.java \
  src/main/java/com/pickaid/passivestjs/runtime/PSTRuntimeTypeIds.java \
  src/test/java/com/pickaid/passivestjs/runtime/PSTRuntimeTypeIdsTest.java \
  src/test/java/com/pickaid/passivestjs/kubejs/recipe/PSTWorkbenchItemBonusRecipeSchemaTest.java
git commit -m "feat(passivestjs): add workbench item bonus recipejs adapter"
```

## Task 5: Add Examples And Lock The Public Usage Story

**Files:**
- Modify: `examples/kubejs/passivestjs/04_startup_item_bonus_metadata.js`
- Create: `examples/kubejs/passivestjs/05_server_runtime_helper_samples.js`
- Create: `examples/kubejs/passivestjs/06_server_workbench_item_bonus_recipe.js`
- Create: `src/test/java/com/pickaid/passivestjs/kubejs/KubeJsRuntimeHelperExamplesTest.java`

- [ ] **Step 1: Add example coverage for runtime helper usage**

```js
// examples/kubejs/passivestjs/05_server_runtime_helper_samples.js
ServerEvents.entityHurt(event => {
  const player = event.source.player
  if (!player) return

  const pst = PST.player(player)
  if (!pst) return

  const skill = pst.skill('kubejs:demo/root')
  if (!skill || !skill.learned()) return

  for (const bonus of skill.bonuses()) {
    if (bonus.typeId() !== 'kubejs:smoke_bonus') continue
    const node = bonus.node()
    if (node && !node.testLivingCondition('player_condition', player, true)) continue
    event.entity.setSecondsOnFire(3)
  }

  for (const listener of skill.listeners()) {
    if (listener.typeId() !== 'kubejs:smoke_listener') continue
    console.info(`listener mode=${listener.node() ? listener.node().string('mode').orElse('<missing>') : '<none>'}`)
  }

  for (const requirement of skill.requirements()) {
    console.info(`requirement ${requirement.typeId()} passed=${requirement.passed()}`)
  }
})
```

- [ ] **Step 2: Add example coverage for the fluent `workbenchItemBonus()` recipe**

```js
// examples/kubejs/passivestjs/06_server_workbench_item_bonus_recipe.js
ServerEvents.recipes(event => {
  event.recipes.workbenchItemBonus()
    .id('kubejs:shield_socket')
    .baseItemConditionSchema('skilltree:equipment_type', condition => {
      condition.set('equipment_type', 'shield')
    })
    .ingredient({ tag: 'forge:ingots/copper' }, 2)
    .requiresPassiveSkill(true)
    .itemBonus('kubejs:socket_shell', itemBonus => {
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
```

- [ ] **Step 3: Add an example regression test that locks the intended public API**

```java
package com.pickaid.passivestjs.kubejs;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class KubeJsRuntimeHelperExamplesTest {
    @Test
    void runtimeHelperExampleUsesPlayerBoundSkillViews() throws IOException {
        String text = Files.readString(Path.of("examples/kubejs/passivestjs/05_server_runtime_helper_samples.js").toAbsolutePath());

        assertTrue(text.contains("PST.player("));
        assertTrue(text.contains(".skill('kubejs:demo/root')"));
        assertTrue(text.contains(".bonuses()"));
        assertTrue(text.contains(".listeners()"));
        assertTrue(text.contains(".requirements()"));
    }

    @Test
    void workbenchRecipeExampleUsesMappedAliasAndFluentBuilder() throws IOException {
        String text = Files.readString(Path.of("examples/kubejs/passivestjs/06_server_workbench_item_bonus_recipe.js").toAbsolutePath());

        assertTrue(text.contains("event.recipes.workbenchItemBonus()"));
        assertTrue(text.contains(".baseItemConditionSchema("));
        assertTrue(text.contains(".ingredient("));
        assertTrue(text.contains(".requiresPassiveSkill(true)"));
        assertTrue(text.contains(".itemBonus("));
    }
}
```

- [ ] **Step 4: Run the example regression tests and the full focused batch**

Run:

```bash
cd /Users/gedwen/Documents/programing/MC/PassiveSTJS
./gradlew test \
  --tests com.pickaid.passivestjs.kubejs.PublicApiSurfaceTest \
  --tests com.pickaid.passivestjs.kubejs.BindingsRuntimeViewTest \
  --tests com.pickaid.passivestjs.runtime.PSTRuntimeTypeIdsTest \
  --tests com.pickaid.passivestjs.kubejs.recipe.PSTWorkbenchItemBonusRecipeSchemaTest \
  --tests com.pickaid.passivestjs.kubejs.KubeJsRuntimeHelperExamplesTest \
  --console=plain
```

Expected:

- all runtime-helper contract tests pass
- all recipe-schema tests pass
- example files are present and reference the intended public APIs

- [ ] **Step 5: Commit the examples and docs slice**

```bash
cd /Users/gedwen/Documents/programing/MC/PassiveSTJS
git add \
  examples/kubejs/passivestjs/04_startup_item_bonus_metadata.js \
  examples/kubejs/passivestjs/05_server_runtime_helper_samples.js \
  examples/kubejs/passivestjs/06_server_workbench_item_bonus_recipe.js \
  src/test/java/com/pickaid/passivestjs/kubejs/KubeJsRuntimeHelperExamplesTest.java
git commit -m "docs(passivestjs): add runtime helper and recipejs examples"
```
