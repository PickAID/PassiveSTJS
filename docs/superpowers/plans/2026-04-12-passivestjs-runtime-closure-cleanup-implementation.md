# PassiveSTJS Runtime Closure Cleanup Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Close the remaining fake custom-runtime gaps in PassiveSTJS by making custom `event_listeners` and `item_bonuses` execute for real, expanding runtime helper queries so scripts can inspect active custom effects, and cleaning the structure enough that the runtime path no longer feels stitched together.

**Architecture:** Keep the existing real PST registry and schema direction, but finish the missing runtime loop in three layers: serializer-backed runtime objects, PassiveSTJS-owned runtime dispatch/query services, and a KubeJS-friendly runtime view surface on `PST.player(...)`. Structural cleanup is part of the feature: move JSON and runtime internals behind clearer package boundaries, default generated lang back to safe-off, and update run scripts so registration, translation, tree composition, runtime queries, and real effect execution are tested as one chain.

**Tech Stack:** Java 17, Forge 1.20.1, KubeJS Forge 2001.6.5, Passive Skill Tree 0.7.4, legacy ProbeJS, JUnit 5

---

## Scope Check

This plan keeps five coupled pieces in one implementation batch:

- generated-lang safety
- runtime helper expansion
- custom `item_bonuses`
- custom `event_listeners`
- structural cleanup around runtime and JSON helpers

They stay together because they all sit on the same boundary: custom PST registry entries must stop being metadata-only and become queryable, translatable, executable runtime objects. Splitting them would force temporary duplicate APIs and more half-real surfaces.

## File Structure

### Config And Public Runtime Surface

- Modify: `src/main/java/com/pickaid/passivestjs/config/PassiveSTJSClientConfig.java`
  - Default generated lang back to safe-off and add explicit overwrite-risk comments.
- Modify: `src/main/java/com/pickaid/passivestjs/kubejs/Bindings.java`
  - Keep the binding root thin while expanding the runtime view returned by `player(...)`.
- Modify: `src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTPlayerView.java`
  - Add real active-runtime query methods.
- Create: `src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTActiveBonusView.java`
  - Public JS-facing view for one active custom skill-bonus instance.
- Create: `src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTActiveItemBonusView.java`
  - Public JS-facing view for one active custom item-bonus instance.
- Create: `src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTActiveListenerView.java`
  - Public JS-facing view for one active custom listener instance.

### Internal Runtime Query And Model

- Create: `src/main/java/com/pickaid/passivestjs/runtime/model/PSTCustomSkillBonusRuntime.java`
  - Marker and accessor contract for custom runtime skill bonuses.
- Create: `src/main/java/com/pickaid/passivestjs/runtime/model/PSTCustomEventListenerRuntime.java`
  - Marker and accessor contract for custom runtime listeners.
- Create: `src/main/java/com/pickaid/passivestjs/runtime/model/PSTCustomItemBonusRuntime.java`
  - Marker and accessor contract for custom runtime item bonuses.
- Create: `src/main/java/com/pickaid/passivestjs/runtime/query/PSTRuntimeQueryService.java`
  - Collects active custom bonus, listener, and item-bonus instances from player state and item state.

### Runtime Serialization And Dispatch

- Modify: `src/main/java/com/pickaid/passivestjs/runtime/PSTCustomRuntimeContexts.java`
  - Expand contexts for item bonus hooks and event-dispatch contexts.
- Modify: `src/main/java/com/pickaid/passivestjs/runtime/serializer/PSTCustomRuntimeSerializers.java`
  - Add real serializer-backed runtime instances for custom listeners and custom item bonuses.
- Modify: `src/main/java/com/pickaid/passivestjs/kubejs/registry/builder/PSTEventListenerSerializerBuilder.java`
  - Replace placeholder serializer creation with real runtime listener support.
- Modify: `src/main/java/com/pickaid/passivestjs/kubejs/registry/builder/PSTItemBonusSerializerBuilder.java`
  - Replace placeholder serializer creation with real runtime item-bonus support.
- Create: `src/main/java/com/pickaid/passivestjs/runtime/dispatch/PSTCustomEventDispatchService.java`
  - Pure service that dispatches custom listener runtimes without touching Forge event classes directly.
- Create: `src/main/java/com/pickaid/passivestjs/runtime/dispatch/PSTCustomItemBonusDispatchService.java`
  - Applies custom item-bonus runtime hooks for tooltip and equipment transitions.
- Create: `src/main/java/com/pickaid/passivestjs/runtime/dispatch/PSTCustomForgeEventBridge.java`
  - Forge subscriber that adapts real events into the internal dispatch services.
- Modify: `src/main/java/com/pickaid/passivestjs/PassiveSTJS.java`
  - Register the bridge during mod bootstrap.

### JSON Utility Cleanup

- Create: `src/main/java/com/pickaid/passivestjs/util/json/PSTJson.java`
  - New home for JSON copy/normalize/typed-object helpers.
- Modify: `src/main/java/com/pickaid/passivestjs/kubejs/content/JsonHelper.java`
  - Turn into a compatibility shell or delete once imports are migrated.
- Modify: all direct `JsonHelper` consumers under:
  - `src/main/java/com/pickaid/passivestjs/runtime/**`
  - `src/main/java/com/pickaid/passivestjs/kubejs/builder/**`
  - `src/main/java/com/pickaid/passivestjs/kubejs/content/**`
  - `src/main/java/com/pickaid/passivestjs/kubejs/lang/**`

### Run Scripts And Diagnostics

- Modify: `run/client/kubejs/startup_scripts/passivestjs/10_registry_smoke.js`
  - Register real runtime smoke types for bonus, listener, item bonus, requirement, multiplier, value provider, and conditions.
- Modify: `run/client/kubejs/server_scripts/passivestjs/content/10_test_tree.js`
  - Use those custom smoke types in a tree that proves runtime closure.
- Modify: `run/client/kubejs/server_scripts/passivestjs/runtime/20_tree_diagnostics.js`
  - Log active custom runtime instances and effect-trigger states.
- Modify: `run/client/kubejs/server_scripts/passivestjs/runtime/30_registry_diagnostics.js`
  - Stop describing custom listener/item-bonus families as stub-only once they are real.

### Tests

- Modify: `src/test/java/com/pickaid/passivestjs/config/PassiveSTJSClientConfigTest.java`
  - Lock generated-lang default back to `false`.
- Modify: `src/test/java/com/pickaid/passivestjs/kubejs/PublicApiSurfaceTest.java`
  - Lock the new typed runtime-query methods on `PSTPlayerView`.
- Modify: `src/test/java/com/pickaid/passivestjs/kubejs/BindingsRuntimeViewTest.java`
  - Verify active custom runtime queries from `PST.player(...)`.
- Modify: `src/test/java/com/pickaid/passivestjs/kubejs/registry/PSTRegistriesApiTest.java`
  - Verify custom listener and item-bonus builders now create real runtime serializers.
- Modify: `src/test/java/com/pickaid/passivestjs/kubejs/content/ContentInternalsTrimmedTest.java`
  - Lock the JSON helper relocation and keep dead helper methods gone.
- Create: `src/test/java/com/pickaid/passivestjs/runtime/PSTCustomItemBonusRuntimeTest.java`
  - Verify direct custom item-bonus runtime callbacks actually execute.
- Create: `src/test/java/com/pickaid/passivestjs/runtime/PSTCustomEventDispatchServiceTest.java`
  - Verify custom listener runtime dispatch for attack-like flows without Forge event boilerplate.

## Task 1: Lock The Failing Contract For Safe Config And Runtime Queries

**Files:**
- Modify: `src/test/java/com/pickaid/passivestjs/config/PassiveSTJSClientConfigTest.java`
- Modify: `src/test/java/com/pickaid/passivestjs/kubejs/PublicApiSurfaceTest.java`
- Modify: `src/test/java/com/pickaid/passivestjs/kubejs/BindingsRuntimeViewTest.java`

- [ ] **Step 1: Change the config default test to the safe-off contract**

```java
package com.pickaid.passivestjs.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class PassiveSTJSClientConfigTest {
    @Test
    void generatedLangExportIsDisabledByDefault() {
        assertFalse(PassiveSTJSClientConfig.generatedLangEnabled());
    }
}
```

- [ ] **Step 2: Add public-surface assertions for the new typed runtime-query methods**

```java
assertNotNull(PSTPlayerView.class.getDeclaredMethod("hasBonus", com.pickaid.passivestjs.kubejs.id.PSTSkillBonusId.class));
assertNotNull(PSTPlayerView.class.getDeclaredMethod("bonusInstances", com.pickaid.passivestjs.kubejs.id.PSTSkillBonusId.class));
assertNotNull(PSTPlayerView.class.getDeclaredMethod("bonusNumericTotal", com.pickaid.passivestjs.kubejs.id.PSTSkillBonusId.class, String.class));
assertNotNull(PSTPlayerView.class.getDeclaredMethod("itemBonusInstances", com.pickaid.passivestjs.kubejs.id.PSTItemBonusId.class));
assertNotNull(PSTPlayerView.class.getDeclaredMethod("listenerInstances", com.pickaid.passivestjs.kubejs.id.PSTEventListenerId.class));

assertFalse(hasPublicSignature(PSTPlayerView.class, "hasBonus", ResourceLocation.class));
assertFalse(hasPublicSignature(PSTPlayerView.class, "bonusInstances", ResourceLocation.class));
assertFalse(hasPublicSignature(PSTPlayerView.class, "bonusNumericTotal", ResourceLocation.class, String.class));
assertFalse(hasPublicSignature(PSTPlayerView.class, "itemBonusInstances", ResourceLocation.class));
assertFalse(hasPublicSignature(PSTPlayerView.class, "listenerInstances", ResourceLocation.class));
```

- [ ] **Step 3: Add the failing active-runtime query test to `BindingsRuntimeViewTest`**

```java
@Test
void playerViewExposesActiveCustomBonusInstancesAndNumericTotals() {
    ResourceLocation bonusId = ResourceLocation.fromNamespaceAndPath("kubejs", "smoke_bonus");

    SkillBonus.Serializer serializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillBonusSerializerBuilder(bonusId)
            .onApply(context -> {})
            .createObject();

    JsonObject json = new JsonObject();
    json.addProperty("type", "kubejs:smoke_bonus");
    json.addProperty("amount", 2.5D);

    PassiveSkill smokeSkill = skill("kubejs:smoke_skill");
    smokeSkill.getBonuses().add(serializer.deserialize(json));
    SkillsReloader.getSkills().put(smokeSkill.getId(), smokeSkill);

    PlayerSkills playerSkills = new PlayerSkills();
    playerSkills.getPlayerSkills().add(smokeSkill);
    PSTPlayerView view = new PSTPlayerView(playerSkills);

    var typeId = com.pickaid.passivestjs.kubejs.id.PSTSkillBonusId.of(bonusId);

    assertTrue(view.hasBonus(typeId));
    assertEquals(1, view.bonusInstances(typeId).size());
    assertEquals("kubejs:smoke_skill", view.bonusInstances(typeId).get(0).sourceSkillId());
    assertEquals(2.5D, view.bonusNumericTotal(typeId, "amount"), 0.0001D);
}
```

- [ ] **Step 4: Run the targeted tests to verify they fail**

Run:

```bash
cd /Users/gedwen/Documents/programing/MC/PassiveSTJS
./gradlew test \
  --tests com.pickaid.passivestjs.config.PassiveSTJSClientConfigTest \
  --tests com.pickaid.passivestjs.kubejs.PublicApiSurfaceTest \
  --tests com.pickaid.passivestjs.kubejs.BindingsRuntimeViewTest \
  --console=plain
```

Expected:

- `generatedLangExportIsDisabledByDefault` fails because the config still defaults to `true`
- the reflection assertions fail because the new runtime-query methods do not exist yet
- the runtime-view test fails to compile or execute because the active-runtime query surface does not exist yet

## Task 2: Implement Safe Config Defaults And Real Runtime Query Views

**Files:**
- Modify: `src/main/java/com/pickaid/passivestjs/config/PassiveSTJSClientConfig.java`
- Modify: `src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTPlayerView.java`
- Create: `src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTActiveBonusView.java`
- Create: `src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTActiveItemBonusView.java`
- Create: `src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTActiveListenerView.java`
- Create: `src/main/java/com/pickaid/passivestjs/runtime/query/PSTRuntimeQueryService.java`

- [ ] **Step 1: Make generated lang default safe-off and add overwrite-risk comments**

```java
generatedLangEnabled = builder
        .comment("Enables PassiveSTJS automatic local KubeJS lang export.")
        .comment("Disabled by default because this managed export can add, update, or replace generated entries in the target locale file.")
        .comment("Do not treat the generated target file as purely hand-owned content unless you accept that ownership model.")
        .translation("passivestjs.config.generated_lang.enabled")
        .define("enabled", false);
```

- [ ] **Step 2: Add the public active-runtime view classes**

```java
package com.pickaid.passivestjs.kubejs.runtime;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.typings.Info;

public final class PSTActiveBonusView {
    private final String typeId;
    private final String sourceSkillId;
    private final JsonObject payload;
    private final double multiplier;

    public PSTActiveBonusView(String typeId, String sourceSkillId, JsonObject payload, double multiplier) {
        this.typeId = typeId;
        this.sourceSkillId = sourceSkillId;
        this.payload = payload;
        this.multiplier = multiplier;
    }

    @Info("Returns the active bonus type id.")
    public String typeId() {
        return typeId;
    }

    @Info("Returns the learned skill id that contributed this active bonus instance.")
    public String sourceSkillId() {
        return sourceSkillId;
    }

    @Info("Returns the multiplier currently attached to this active bonus instance.")
    public double multiplier() {
        return multiplier;
    }

    @Info("Returns a numeric payload field from this active bonus instance, or null when absent.")
    public Double number(String key) {
        return payload.has(key) && payload.get(key).isJsonPrimitive() ? payload.get(key).getAsDouble() : null;
    }
}
```

```java
package com.pickaid.passivestjs.runtime.query;

import com.google.gson.JsonObject;
import com.pickaid.passivestjs.kubejs.runtime.PSTActiveBonusView;
import com.pickaid.passivestjs.runtime.model.PSTCustomSkillBonusRuntime;
import daripher.skilltree.capability.skill.IPlayerSkills;
import daripher.skilltree.skill.PassiveSkill;

import java.util.ArrayList;
import java.util.List;

public final class PSTRuntimeQueryService {
    private PSTRuntimeQueryService() {
    }

    public static List<PSTActiveBonusView> activeBonusViews(IPlayerSkills playerSkills, String typeId) {
        List<PSTActiveBonusView> views = new ArrayList<>();
        for (PassiveSkill skill : playerSkills.getPlayerSkills()) {
            if (skill == null || skill.getId() == null) {
                continue;
            }
            for (var bonus : skill.getBonuses()) {
                if (bonus instanceof PSTCustomSkillBonusRuntime runtime && runtime.typeId().equals(typeId)) {
                    JsonObject payload = runtime.payload();
                    views.add(new PSTActiveBonusView(typeId, skill.getId().toString(), payload, runtime.multiplier()));
                }
            }
        }
        return List.copyOf(views);
    }

    public static double activeBonusNumericTotal(IPlayerSkills playerSkills, String typeId, String fieldName) {
        return activeBonusViews(playerSkills, typeId).stream()
                .mapToDouble(view -> {
                    Double value = view.number(fieldName);
                    return value == null ? 0.0D : value * view.multiplier();
                })
                .sum();
    }
}
```

- [ ] **Step 3: Extend `PSTPlayerView` to expose the new runtime queries**

```java
@Info(value = "Returns whether the player currently has at least one active custom bonus instance of the provided type.", params = {
        @Param(name = "id", value = "The custom or built-in skill bonus type id to inspect.")
})
public boolean hasBonus(com.pickaid.passivestjs.kubejs.id.PSTSkillBonusId id) {
    return !bonusInstances(id).isEmpty();
}

@Info(value = "Returns active custom bonus instances contributed by the player's learned skills for the provided type.", params = {
        @Param(name = "id", value = "The skill bonus type id to inspect.")
})
public List<com.pickaid.passivestjs.kubejs.runtime.PSTActiveBonusView> bonusInstances(com.pickaid.passivestjs.kubejs.id.PSTSkillBonusId id) {
    if (id == null) {
        return List.of();
    }
    return com.pickaid.passivestjs.runtime.query.PSTRuntimeQueryService.activeBonusViews(playerSkills, id.location().toString());
}

@Info(value = "Sums a numeric payload field across active custom bonus instances of the provided type, multiplying each field value by that instance's effective multiplier.", params = {
        @Param(name = "id", value = "The skill bonus type id to inspect."),
        @Param(name = "fieldName", value = "The numeric payload field to total.")
})
public double bonusNumericTotal(com.pickaid.passivestjs.kubejs.id.PSTSkillBonusId id, String fieldName) {
    if (id == null || fieldName == null || fieldName.isBlank()) {
        return 0.0D;
    }
    return com.pickaid.passivestjs.runtime.query.PSTRuntimeQueryService.activeBonusNumericTotal(playerSkills, id.location().toString(), fieldName);
}
```

- [ ] **Step 4: Re-run the targeted tests**

Run:

```bash
cd /Users/gedwen/Documents/programing/MC/PassiveSTJS
./gradlew test \
  --tests com.pickaid.passivestjs.config.PassiveSTJSClientConfigTest \
  --tests com.pickaid.passivestjs.kubejs.PublicApiSurfaceTest \
  --tests com.pickaid.passivestjs.kubejs.BindingsRuntimeViewTest \
  --console=plain
```

Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/pickaid/passivestjs/config/PassiveSTJSClientConfig.java \
  src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTPlayerView.java \
  src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTActiveBonusView.java \
  src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTActiveItemBonusView.java \
  src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTActiveListenerView.java \
  src/main/java/com/pickaid/passivestjs/runtime/query/PSTRuntimeQueryService.java \
  src/test/java/com/pickaid/passivestjs/config/PassiveSTJSClientConfigTest.java \
  src/test/java/com/pickaid/passivestjs/kubejs/PublicApiSurfaceTest.java \
  src/test/java/com/pickaid/passivestjs/kubejs/BindingsRuntimeViewTest.java
git commit -m "feat(passivestjs): add safe lang defaults and runtime query views"
```

## Task 3: Lock The Failing Contract For Custom Item Bonus Runtime

**Files:**
- Create: `src/test/java/com/pickaid/passivestjs/runtime/PSTCustomItemBonusRuntimeTest.java`
- Modify: `src/test/java/com/pickaid/passivestjs/kubejs/registry/PSTRegistriesApiTest.java`

- [ ] **Step 1: Add the failing runtime item-bonus execution test**

```java
package com.pickaid.passivestjs.runtime;

import com.google.gson.JsonObject;
import com.pickaid.passivestjs.runtime.model.PSTCustomItemBonusRuntime;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PSTCustomItemBonusRuntimeTest {
    @Test
    void customItemBonusRuntimeExecutesEquipHook() {
        AtomicBoolean equipped = new AtomicBoolean(false);

        var serializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTItemBonusSerializerBuilder(
                ResourceLocation.fromNamespaceAndPath("kubejs", "smoke_item_bonus")
        )
                .onEquip(context -> equipped.set(context.node().number("amount").orElse(0.0D).doubleValue() == 3.0D))
                .createObject();

        JsonObject json = new JsonObject();
        json.addProperty("type", "kubejs:smoke_item_bonus");
        json.addProperty("amount", 3.0D);

        var bonus = serializer.deserialize(json);
        ((PSTCustomItemBonusRuntime) bonus).onEquip(new PSTCustomRuntimeContexts.ItemBonusEquipContext(
                ((PSTCustomItemBonusRuntime) bonus).node(),
                null,
                null
        ));

        assertTrue(equipped.get());
    }
}
```

- [ ] **Step 2: Add the failing registry-level assertion that item-bonus builders now produce real runtime objects**

```java
@Test
void customItemBonusSerializerProducesRuntimeItemBonus() {
    var serializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTItemBonusSerializerBuilder(
            ResourceLocation.fromNamespaceAndPath("kubejs", "runtime_item_bonus")
    )
            .onTooltip(context -> {})
            .createObject();

    JsonObject json = new JsonObject();
    json.addProperty("type", "kubejs:runtime_item_bonus");

    var bonus = serializer.deserialize(json);

    assertTrue(bonus instanceof com.pickaid.passivestjs.runtime.model.PSTCustomItemBonusRuntime);
}
```

- [ ] **Step 3: Run the item-bonus tests to verify they fail**

Run:

```bash
cd /Users/gedwen/Documents/programing/MC/PassiveSTJS
./gradlew test \
  --tests com.pickaid.passivestjs.runtime.PSTCustomItemBonusRuntimeTest \
  --tests com.pickaid.passivestjs.kubejs.registry.PSTRegistriesApiTest \
  --console=plain
```

Expected:

- the new builder methods such as `onEquip(...)` and `onTooltip(...)` do not exist yet
- the item-bonus builder still returns the placeholder serializer path

## Task 4: Implement Real Custom Item Bonus Runtime

**Files:**
- Modify: `src/main/java/com/pickaid/passivestjs/runtime/PSTCustomRuntimeContexts.java`
- Modify: `src/main/java/com/pickaid/passivestjs/runtime/serializer/PSTCustomRuntimeSerializers.java`
- Modify: `src/main/java/com/pickaid/passivestjs/kubejs/registry/builder/PSTItemBonusSerializerBuilder.java`
- Create: `src/main/java/com/pickaid/passivestjs/runtime/model/PSTCustomItemBonusRuntime.java`
- Create: `src/main/java/com/pickaid/passivestjs/runtime/dispatch/PSTCustomItemBonusDispatchService.java`

- [ ] **Step 1: Add item-bonus runtime contexts**

```java
public record ItemBonusTooltipContext(PSTRuntimeNode node, java.util.function.Consumer<net.minecraft.network.chat.MutableComponent> consumer) {
}

public record ItemBonusEquipContext(PSTRuntimeNode node, net.minecraft.world.entity.LivingEntity entity, net.minecraft.world.item.ItemStack stack) {
}

public record ItemBonusUnequipContext(PSTRuntimeNode node, net.minecraft.world.entity.LivingEntity entity, net.minecraft.world.item.ItemStack stack) {
}
```

- [ ] **Step 2: Add the internal runtime marker for custom item bonuses**

```java
package com.pickaid.passivestjs.runtime.model;

import com.google.gson.JsonObject;
import com.pickaid.passivestjs.runtime.PSTCustomRuntimeContexts;
import com.pickaid.passivestjs.runtime.PSTRuntimeNode;

public interface PSTCustomItemBonusRuntime {
    String typeId();

    PSTRuntimeNode node();

    JsonObject payload();

    void onTooltip(PSTCustomRuntimeContexts.ItemBonusTooltipContext context);

    void onEquip(PSTCustomRuntimeContexts.ItemBonusEquipContext context);

    void onUnequip(PSTCustomRuntimeContexts.ItemBonusUnequipContext context);
}
```

- [ ] **Step 3: Replace the placeholder serializer path in `PSTItemBonusSerializerBuilder`**

```java
public class PSTItemBonusSerializerBuilder extends AbstractPSTSerializerBuilder<ItemBonus.Serializer> {
    private Consumer<PSTCustomRuntimeContexts.ItemBonusTooltipContext> onTooltip = context -> {};
    private Consumer<PSTCustomRuntimeContexts.ItemBonusEquipContext> onEquip = context -> {};
    private Consumer<PSTCustomRuntimeContexts.ItemBonusUnequipContext> onUnequip = context -> {};

    public PSTItemBonusSerializerBuilder onTooltip(Consumer<PSTCustomRuntimeContexts.ItemBonusTooltipContext> consumer) {
        this.onTooltip = consumer == null ? context -> {} : consumer;
        return this;
    }

    public PSTItemBonusSerializerBuilder onEquip(Consumer<PSTCustomRuntimeContexts.ItemBonusEquipContext> consumer) {
        this.onEquip = consumer == null ? context -> {} : consumer;
        return this;
    }

    public PSTItemBonusSerializerBuilder onUnequip(Consumer<PSTCustomRuntimeContexts.ItemBonusUnequipContext> consumer) {
        this.onUnequip = consumer == null ? context -> {} : consumer;
        return this;
    }

    @Override
    protected ItemBonus.Serializer createSerializer(PSTSerializerMetadata metadata) {
        return PSTCustomRuntimeSerializers.itemBonus(metadata, onTooltip, onEquip, onUnequip);
    }
}
```

- [ ] **Step 4: Add the runtime serializer and dispatch helper**

```java
public static ItemBonus.Serializer itemBonus(
        PSTSerializerMetadata metadata,
        Consumer<PSTCustomRuntimeContexts.ItemBonusTooltipContext> onTooltip,
        Consumer<PSTCustomRuntimeContexts.ItemBonusEquipContext> onEquip,
        Consumer<PSTCustomRuntimeContexts.ItemBonusUnequipContext> onUnequip
) {
    return new ItemBonus.Serializer() {
        @Override
        public ItemBonus<?> createDefaultInstance() {
            return new RuntimeItemBonus(metadata, new JsonObject(), this, onTooltip, onEquip, onUnequip, 1.0D);
        }

        @Override
        public ItemBonus<?> deserialize(JsonObject json) {
            return new RuntimeItemBonus(metadata, payload(json), this, onTooltip, onEquip, onUnequip, 1.0D);
        }

        @Override
        public void serialize(JsonObject json, ItemBonus<?> value) {
            writeJson(json, ((RuntimeItemBonus) value).node.payload());
        }

        @Override
        public ItemBonus<?> deserialize(CompoundTag tag) {
            return new RuntimeItemBonus(metadata, payload(tag), this, onTooltip, onEquip, onUnequip, 1.0D);
        }

        @Override
        public CompoundTag serialize(ItemBonus<?> value) {
            return tag(((RuntimeItemBonus) value).node.payload());
        }
    };
}
```

```java
package com.pickaid.passivestjs.runtime.dispatch;

import com.pickaid.passivestjs.runtime.PSTCustomRuntimeContexts;
import com.pickaid.passivestjs.runtime.model.PSTCustomItemBonusRuntime;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class PSTCustomItemBonusDispatchService {
    private PSTCustomItemBonusDispatchService() {
    }

    public static void onEquip(ItemBonus<?> bonus, LivingEntity entity, ItemStack stack) {
        if (bonus instanceof PSTCustomItemBonusRuntime runtime) {
            runtime.onEquip(new PSTCustomRuntimeContexts.ItemBonusEquipContext(runtime.node(), entity, stack));
        }
    }
}
```

- [ ] **Step 5: Run the item-bonus tests again**

Run:

```bash
cd /Users/gedwen/Documents/programing/MC/PassiveSTJS
./gradlew test \
  --tests com.pickaid.passivestjs.runtime.PSTCustomItemBonusRuntimeTest \
  --tests com.pickaid.passivestjs.kubejs.registry.PSTRegistriesApiTest \
  --console=plain
```

Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/pickaid/passivestjs/runtime/PSTCustomRuntimeContexts.java \
  src/main/java/com/pickaid/passivestjs/runtime/serializer/PSTCustomRuntimeSerializers.java \
  src/main/java/com/pickaid/passivestjs/kubejs/registry/builder/PSTItemBonusSerializerBuilder.java \
  src/main/java/com/pickaid/passivestjs/runtime/model/PSTCustomItemBonusRuntime.java \
  src/main/java/com/pickaid/passivestjs/runtime/dispatch/PSTCustomItemBonusDispatchService.java \
  src/test/java/com/pickaid/passivestjs/runtime/PSTCustomItemBonusRuntimeTest.java \
  src/test/java/com/pickaid/passivestjs/kubejs/registry/PSTRegistriesApiTest.java
git commit -m "feat(passivestjs): add real custom item bonus runtime"
```

## Task 5: Lock The Failing Contract For Custom Event Listener Dispatch

**Files:**
- Create: `src/test/java/com/pickaid/passivestjs/runtime/PSTCustomEventDispatchServiceTest.java`
- Modify: `src/test/java/com/pickaid/passivestjs/kubejs/BindingsRuntimeViewTest.java`

- [ ] **Step 1: Add the failing event-dispatch service test**

```java
package com.pickaid.passivestjs.runtime;

import com.google.gson.JsonObject;
import com.pickaid.passivestjs.runtime.dispatch.PSTCustomEventDispatchService;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PSTCustomEventDispatchServiceTest {
    @Test
    void customAttackListenerDispatchesThroughService() {
        AtomicBoolean fired = new AtomicBoolean(false);

        var listenerSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTEventListenerSerializerBuilder(
                ResourceLocation.fromNamespaceAndPath("kubejs", "smoke_listener")
        )
                .onAttack(context -> fired.set(context.node().string("mode").orElse("").equals("smoke")))
                .createObject();

        JsonObject listenerJson = new JsonObject();
        listenerJson.addProperty("type", "kubejs:smoke_listener");
        listenerJson.addProperty("mode", "smoke");

        var listener = listenerSerializer.deserialize(listenerJson);

        PSTCustomEventDispatchService.dispatchAttack(listener, null, null, null, null);

        assertTrue(fired.get());
    }
}
```

- [ ] **Step 2: Add the failing listener-instance query test to `BindingsRuntimeViewTest`**

```java
@Test
void playerViewExposesActiveCustomListenerInstances() {
    ResourceLocation bonusId = ResourceLocation.fromNamespaceAndPath("kubejs", "smoke_bonus");
    ResourceLocation listenerId = ResourceLocation.fromNamespaceAndPath("kubejs", "smoke_listener");

    var listenerSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTEventListenerSerializerBuilder(listenerId)
            .onAttack(context -> {})
            .createObject();

    JsonObject listenerJson = new JsonObject();
    listenerJson.addProperty("type", "kubejs:smoke_listener");

    JsonObject bonusJson = new JsonObject();
    bonusJson.addProperty("type", "kubejs:smoke_bonus");
    bonusJson.add("event_listener", listenerJson);

    var bonusSerializer = new com.pickaid.passivestjs.kubejs.registry.builder.PSTSkillBonusSerializerBuilder(bonusId)
            .onApply(context -> {})
            .createObject();

    PassiveSkill smokeSkill = skill("kubejs:smoke_listener_skill");
    smokeSkill.getBonuses().add(bonusSerializer.deserialize(bonusJson));
    SkillsReloader.getSkills().put(smokeSkill.getId(), smokeSkill);

    PlayerSkills playerSkills = new PlayerSkills();
    playerSkills.getPlayerSkills().add(smokeSkill);
    PSTPlayerView view = new PSTPlayerView(playerSkills);

    assertEquals(1, view.listenerInstances(com.pickaid.passivestjs.kubejs.id.PSTEventListenerId.of(listenerId)).size());
}
```

- [ ] **Step 3: Run the listener tests to verify they fail**

Run:

```bash
cd /Users/gedwen/Documents/programing/MC/PassiveSTJS
./gradlew test \
  --tests com.pickaid.passivestjs.runtime.PSTCustomEventDispatchServiceTest \
  --tests com.pickaid.passivestjs.kubejs.BindingsRuntimeViewTest \
  --console=plain
```

Expected:

- `PSTEventListenerSerializerBuilder` does not yet expose `onAttack(...)`
- `PSTCustomEventDispatchService` does not exist
- `listenerInstances(...)` does not exist on `PSTPlayerView`

## Task 6: Implement Custom Event Listener Runtime And Forge Dispatch Bridge

**Files:**
- Modify: `src/main/java/com/pickaid/passivestjs/runtime/PSTCustomRuntimeContexts.java`
- Modify: `src/main/java/com/pickaid/passivestjs/runtime/serializer/PSTCustomRuntimeSerializers.java`
- Modify: `src/main/java/com/pickaid/passivestjs/kubejs/registry/builder/PSTEventListenerSerializerBuilder.java`
- Modify: `src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTPlayerView.java`
- Create: `src/main/java/com/pickaid/passivestjs/runtime/model/PSTCustomEventListenerRuntime.java`
- Create: `src/main/java/com/pickaid/passivestjs/runtime/dispatch/PSTCustomEventDispatchService.java`
- Create: `src/main/java/com/pickaid/passivestjs/runtime/dispatch/PSTCustomForgeEventBridge.java`
- Modify: `src/main/java/com/pickaid/passivestjs/PassiveSTJS.java`

- [ ] **Step 1: Add the event-specific runtime contexts and marker interface**

```java
public record AttackEventContext(
        PSTRuntimeNode node,
        net.minecraft.world.entity.player.Player player,
        net.minecraft.world.entity.LivingEntity target,
        net.minecraft.world.damagesource.DamageSource damageSource,
        daripher.skilltree.skill.bonus.EventListenerBonus<?> bonus
) {
}
```

```java
package com.pickaid.passivestjs.runtime.model;

import com.google.gson.JsonObject;
import com.pickaid.passivestjs.runtime.PSTRuntimeNode;

public interface PSTCustomEventListenerRuntime {
    String typeId();

    PSTRuntimeNode node();

    JsonObject payload();
}
```

- [ ] **Step 2: Add runtime-builder methods and real serializer creation for custom listeners**

```java
public class PSTEventListenerSerializerBuilder extends AbstractPSTSerializerBuilder<SkillEventListener.Serializer> {
    private Consumer<PSTCustomRuntimeContexts.AttackEventContext> onAttack = context -> {};

    public PSTEventListenerSerializerBuilder onAttack(Consumer<PSTCustomRuntimeContexts.AttackEventContext> consumer) {
        this.onAttack = consumer == null ? context -> {} : consumer;
        return this;
    }

    @Override
    protected SkillEventListener.Serializer createSerializer(PSTSerializerMetadata metadata) {
        return PSTCustomRuntimeSerializers.eventListener(metadata, onAttack);
    }
}
```

```java
public static SkillEventListener.Serializer eventListener(
        PSTSerializerMetadata metadata,
        Consumer<PSTCustomRuntimeContexts.AttackEventContext> onAttack
) {
    return new SkillEventListener.Serializer() {
        @Override
        public SkillEventListener createDefaultInstance() {
            return new RuntimeEventListener(metadata, new JsonObject(), this, onAttack);
        }

        @Override
        public SkillEventListener deserialize(JsonObject json) {
            return new RuntimeEventListener(metadata, payload(json), this, onAttack);
        }

        @Override
        public void serialize(JsonObject json, SkillEventListener value) {
            writeJson(json, ((RuntimeEventListener) value).node.payload());
        }
    };
}
```

- [ ] **Step 3: Add the dispatch service and the Forge bridge**

```java
package com.pickaid.passivestjs.runtime.dispatch;

import com.pickaid.passivestjs.runtime.PSTCustomRuntimeContexts;
import com.pickaid.passivestjs.runtime.model.PSTCustomEventListenerRuntime;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class PSTCustomEventDispatchService {
    private PSTCustomEventDispatchService() {
    }

    public static void dispatchAttack(
            SkillEventListener listener,
            Player player,
            LivingEntity target,
            DamageSource damageSource,
            EventListenerBonus<?> bonus
    ) {
        if (listener instanceof PSTCustomRuntimeSerializers.RuntimeEventListener runtime) {
            runtime.onAttack(new PSTCustomRuntimeContexts.AttackEventContext(runtime.node(), player, target, damageSource, bonus));
        }
    }
}
```

```java
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = "passivestjs")
public final class PSTCustomForgeEventBridge {
    @net.minecraftforge.eventbus.api.SubscribeEvent
    public static void onLivingHurt(net.minecraftforge.event.entity.living.LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof net.minecraft.world.entity.player.Player player)) {
            return;
        }
        for (var bonus : daripher.skilltree.skill.bonus.SkillBonusHandler.getMergedSkillBonuses(player, daripher.skilltree.skill.bonus.EventListenerBonus.class)) {
            var listener = ((daripher.skilltree.skill.bonus.EventListenerBonus<?>) bonus).getEventListener();
            if (!(listener instanceof com.pickaid.passivestjs.runtime.model.PSTCustomEventListenerRuntime)) {
                continue;
            }
            PSTCustomEventDispatchService.dispatchAttack(listener, player, event.getEntity(), event.getSource(), (daripher.skilltree.skill.bonus.EventListenerBonus<?>) bonus);
        }
    }
}
```

- [ ] **Step 4: Expose active listener instances on `PSTPlayerView`**

```java
@Info(value = "Returns active custom listener instances contributed by the player's learned skills for the provided type.", params = {
        @Param(name = "id", value = "The event listener type id to inspect.")
})
public List<com.pickaid.passivestjs.kubejs.runtime.PSTActiveListenerView> listenerInstances(com.pickaid.passivestjs.kubejs.id.PSTEventListenerId id) {
    if (id == null) {
        return List.of();
    }
    return com.pickaid.passivestjs.runtime.query.PSTRuntimeQueryService.activeListenerViews(playerSkills, id.location().toString());
}
```

- [ ] **Step 5: Run the listener tests again**

Run:

```bash
cd /Users/gedwen/Documents/programing/MC/PassiveSTJS
./gradlew test \
  --tests com.pickaid.passivestjs.runtime.PSTCustomEventDispatchServiceTest \
  --tests com.pickaid.passivestjs.kubejs.BindingsRuntimeViewTest \
  --console=plain
```

Expected: PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/pickaid/passivestjs/runtime/PSTCustomRuntimeContexts.java \
  src/main/java/com/pickaid/passivestjs/runtime/serializer/PSTCustomRuntimeSerializers.java \
  src/main/java/com/pickaid/passivestjs/kubejs/registry/builder/PSTEventListenerSerializerBuilder.java \
  src/main/java/com/pickaid/passivestjs/kubejs/runtime/PSTPlayerView.java \
  src/main/java/com/pickaid/passivestjs/runtime/model/PSTCustomEventListenerRuntime.java \
  src/main/java/com/pickaid/passivestjs/runtime/dispatch/PSTCustomEventDispatchService.java \
  src/main/java/com/pickaid/passivestjs/runtime/dispatch/PSTCustomForgeEventBridge.java \
  src/main/java/com/pickaid/passivestjs/PassiveSTJS.java \
  src/test/java/com/pickaid/passivestjs/runtime/PSTCustomEventDispatchServiceTest.java \
  src/test/java/com/pickaid/passivestjs/kubejs/BindingsRuntimeViewTest.java
git commit -m "feat(passivestjs): add custom event listener runtime bridge"
```

## Task 7: Clean The Structure And Update The Run Smoke Scripts

**Files:**
- Create: `src/main/java/com/pickaid/passivestjs/util/json/PSTJson.java`
- Modify: `src/main/java/com/pickaid/passivestjs/kubejs/content/JsonHelper.java`
- Modify: JSON-helper consumers under `src/main/java/com/pickaid/passivestjs/**`
- Modify: `src/test/java/com/pickaid/passivestjs/kubejs/content/ContentInternalsTrimmedTest.java`
- Modify: `run/client/kubejs/startup_scripts/passivestjs/10_registry_smoke.js`
- Modify: `run/client/kubejs/server_scripts/passivestjs/content/10_test_tree.js`
- Modify: `run/client/kubejs/server_scripts/passivestjs/runtime/20_tree_diagnostics.js`
- Modify: `run/client/kubejs/server_scripts/passivestjs/runtime/30_registry_diagnostics.js`

- [ ] **Step 1: Add the new JSON utility home and migrate imports**

```java
package com.pickaid.passivestjs.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.rhino.Wrapper;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Array;
import java.util.Map;

public final class PSTJson {
    private PSTJson() {
    }

    public static JsonObject typedObject(String type) {
        JsonObject json = new JsonObject();
        json.addProperty("type", type);
        return json;
    }

    public static JsonElement copy(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return JsonNull.INSTANCE;
        }
        return JsonParser.parseString(element.toString());
    }
}
```

- [ ] **Step 2: Keep `JsonHelper` only as a temporary compatibility shell or delete it once zero imports remain**

```java
package com.pickaid.passivestjs.kubejs.content;

@Deprecated(forRemoval = true)
public final class JsonHelper {
    private JsonHelper() {
    }

    public static com.google.gson.JsonObject typedObject(String type) {
        return com.pickaid.passivestjs.util.json.PSTJson.typedObject(type);
    }

    public static com.google.gson.JsonElement copy(com.google.gson.JsonElement element) {
        return com.pickaid.passivestjs.util.json.PSTJson.copy(element);
    }
}
```

- [ ] **Step 3: Update the content-internals test to lock the cleanup**

```java
@Test
void jsonHelperIsNoLongerThePrimaryUtilityHome() {
    assertThrows(NoSuchMethodException.class, () -> JsonHelper.class.getDeclaredMethod("addObject", com.google.gson.JsonObject.class, String.class, Object.class));
    assertNotNull(com.pickaid.passivestjs.util.json.PSTJson.class.getDeclaredMethod("copy", com.google.gson.JsonElement.class));
}
```

- [ ] **Step 4: Rewrite the smoke startup script so the custom types are all real**

```js
StartupEvents.registry('skilltree:event_listeners', event => {
  event.create('kubejs:smoke_listener')
    .schema(schema => {
      schema.string('mode')
    })
    .onAttack(ctx => {
      const player = ctx.player()
      const target = ctx.target()
      if (player == null || target == null) return
      if (ctx.node().string('mode').orElse('') !== 'smoke') return
      target.setSecondsOnFire(4)
    })
})

StartupEvents.registry('skilltree:item_bonuses', event => {
  event.create('kubejs:smoke_item_bonus')
    .schema(schema => {
      schema.number('amount')
    })
    .onEquip(ctx => {
      const entity = ctx.entity()
      if (entity != null) {
        entity.setRemainingFireTicks(entity.getRemainingFireTicks() + 1)
      }
    })
})
```

- [ ] **Step 5: Update the run tree and diagnostics to prove the full runtime chain**

```js
PassiveSTJSEvents.skillTreeContent(event => {
  const tree = event.editTree('kubejs:passivestjs_showcase')
  tree.skill('kubejs:passivestjs/runtime_spark')
    .title('skill.kubejs.passivestjs.runtime_spark.name')
    .bonus('kubejs:smoke_bonus', bonus => {
      bonus.amount(2)
      bonus.eventListener('kubejs:smoke_listener', listener => {
        listener.mode('smoke')
      })
    })
})
```

```js
ServerEvents.tick(event => {
  const player = event.server.playerList.players[0]
  if (!player) return
  const pst = PST.player(player)
  if (!pst) return
  const smoke = pst.bonusInstances('kubejs:smoke_bonus')
  if (smoke.length > 0) {
    console.info(`[PassiveSTJS Runtime Smoke] activeSmoke=${smoke.length} totalAmount=${pst.bonusNumericTotal('kubejs:smoke_bonus', 'amount')}`)
  }
})
```

- [ ] **Step 6: Run the focused regression suite**

Run:

```bash
cd /Users/gedwen/Documents/programing/MC/PassiveSTJS
./gradlew test \
  --tests com.pickaid.passivestjs.config.PassiveSTJSClientConfigTest \
  --tests com.pickaid.passivestjs.kubejs.PublicApiSurfaceTest \
  --tests com.pickaid.passivestjs.kubejs.BindingsRuntimeViewTest \
  --tests com.pickaid.passivestjs.kubejs.registry.PSTRegistriesApiTest \
  --tests com.pickaid.passivestjs.runtime.PSTCustomItemBonusRuntimeTest \
  --tests com.pickaid.passivestjs.runtime.PSTCustomEventDispatchServiceTest \
  --tests com.pickaid.passivestjs.kubejs.content.ContentInternalsTrimmedTest \
  --console=plain
```

Expected: PASS

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/pickaid/passivestjs/util/json/PSTJson.java \
  src/main/java/com/pickaid/passivestjs/kubejs/content/JsonHelper.java \
  src/test/java/com/pickaid/passivestjs/kubejs/content/ContentInternalsTrimmedTest.java \
  run/client/kubejs/startup_scripts/passivestjs/10_registry_smoke.js \
  run/client/kubejs/server_scripts/passivestjs/content/10_test_tree.js \
  run/client/kubejs/server_scripts/passivestjs/runtime/20_tree_diagnostics.js \
  run/client/kubejs/server_scripts/passivestjs/runtime/30_registry_diagnostics.js
git commit -m "refactor(passivestjs): clean runtime structure and smoke scripts"
```

## Task 8: Final Verification And Manual Runtime Check

**Files:**
- No new files

- [ ] **Step 1: Run the full test suite**

Run:

```bash
cd /Users/gedwen/Documents/programing/MC/PassiveSTJS
./gradlew test --console=plain
```

Expected: PASS

- [ ] **Step 2: Run the dev client**

Run:

```bash
cd /Users/gedwen/Documents/programing/MC/PassiveSTJS
./gradlew runClient
```

Expected:

- client starts normally
- no placeholder-runtime crash for `kubejs:smoke_listener` or `kubejs:smoke_item_bonus`
- showcase tree loads on first world entry

- [ ] **Step 3: Perform the manual smoke sequence in-game**

Manual checks:

1. Join the test world and open the PassiveSTJS showcase tree.
2. Confirm the custom smoke node title and description resolve through translation rather than raw fallback text.
3. Learn the smoke node or let the run script auto-grant it.
4. Attack a target while the smoke listener is active.
5. Confirm the target receives the smoke effect path defined by the listener runtime.
6. Watch the log for:
   - active custom bonus instance count
   - total smoke amount from `bonusNumericTotal(...)`
   - listener dispatch confirmation
7. Leave and re-enter the world.
8. Confirm the same learned state and runtime effect still work without `/reload`.

- [ ] **Step 4: Commit the final verification batch**

```bash
git add src/main/java/com/pickaid/passivestjs \
  src/test/java/com/pickaid/passivestjs \
  run/client/kubejs \
  docs/superpowers/plans/2026-04-12-passivestjs-runtime-closure-cleanup-implementation.md
git commit -m "feat(passivestjs): close custom runtime loop"
```

## Self-Review

### Spec Coverage

This plan covers every requirement from `2026-04-12-passivestjs-runtime-closure-cleanup-design.md`:

- `event_listeners` true runtime support: Tasks 5 and 6
- `item_bonuses` true runtime support: Tasks 3 and 4
- runtime helper expansion: Tasks 1 and 2
- generated lang safe default and warning: Tasks 1 and 2
- structural cleanup and JSON-helper relocation: Task 7
- run-script and diagnostics coverage: Task 7
- automated tests and final manual verification: Tasks 1 through 8

### Placeholder Scan

This plan contains:

- no `TODO`
- no `TBD`
- no “implement later”
- no “write tests for the above” without actual test code

### Type Consistency

The plan consistently uses typed wrapper ids on the public KubeJS surface:

- `PSTSkillBonusId`
- `PSTItemBonusId`
- `PSTEventListenerId`

Internal runtime contracts consistently use:

- `PSTCustomSkillBonusRuntime`
- `PSTCustomItemBonusRuntime`
- `PSTCustomEventListenerRuntime`
- `PSTRuntimeQueryService`
- `PSTCustomEventDispatchService`
- `PSTCustomItemBonusDispatchService`
