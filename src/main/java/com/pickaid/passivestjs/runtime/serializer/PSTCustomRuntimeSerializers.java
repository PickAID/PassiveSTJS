package com.pickaid.passivestjs.runtime.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.pickaid.passivestjs.kubejs.content.JsonHelper;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadata;
import com.pickaid.passivestjs.runtime.PSTCustomRuntimeEventListener;
import com.pickaid.passivestjs.runtime.PSTCustomRuntimeItemBonus;
import com.pickaid.passivestjs.runtime.PSTCustomRuntimeSkillBonus;
import com.pickaid.passivestjs.runtime.PSTCustomRuntimeSkillRequirement;
import com.pickaid.passivestjs.runtime.PSTCustomRuntimeContexts;
import com.pickaid.passivestjs.runtime.PSTRuntimeNode;
import com.pickaid.passivestjs.runtime.tooltip.PSTRuntimeTooltipSupport;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
import daripher.skilltree.data.serializers.SerializationHelper;
import daripher.skilltree.skill.bonus.EventListenerBonus;
import daripher.skilltree.skill.bonus.SkillBonus;
import daripher.skilltree.skill.bonus.event.SkillEventListener;
import daripher.skilltree.skill.bonus.function.FloatFunction;
import daripher.skilltree.skill.bonus.item.ItemBonus;
import daripher.skilltree.skill.bonus.multiplier.LivingMultiplier;
import daripher.skilltree.skill.bonus.predicate.damage.DamageCondition;
import daripher.skilltree.skill.bonus.predicate.enchantment.EnchantmentCondition;
import daripher.skilltree.skill.bonus.predicate.item.ItemStackPredicate;
import daripher.skilltree.skill.bonus.predicate.living.FloatFunctionEntityPredicate;
import daripher.skilltree.skill.bonus.predicate.living.LivingEntityPredicate;
import daripher.skilltree.skill.requirement.SkillRequirement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class PSTCustomRuntimeSerializers {
    private static final String PAYLOAD_KEY = "passivestjs_payload";

    private PSTCustomRuntimeSerializers() {
    }

    public static SkillRequirement.Serializer skillRequirement(
            PSTSerializerMetadata metadata,
            Predicate<PSTCustomRuntimeContexts.SkillRequirementContext> tester
    ) {
        Predicate<PSTCustomRuntimeContexts.SkillRequirementContext> safeTester = tester != null ? tester : context -> false;
        return new SkillRequirement.Serializer() {
            @Override
            public SkillRequirement<?> createDefaultInstance() {
                return new RuntimeSkillRequirement(metadata, new JsonObject(), this, safeTester);
            }

            @Override
            public SkillRequirement<?> deserialize(JsonObject json) {
                return new RuntimeSkillRequirement(metadata, payload(json), this, safeTester);
            }

            @Override
            public void serialize(JsonObject json, SkillRequirement<?> value) {
                writeJson(json, ((RuntimeSkillRequirement) value).node.payload());
            }

            @Override
            public SkillRequirement<?> deserialize(CompoundTag tag) {
                return new RuntimeSkillRequirement(metadata, payload(tag), this, safeTester);
            }

            @Override
            public CompoundTag serialize(SkillRequirement<?> value) {
                return tag(((RuntimeSkillRequirement) value).node.payload());
            }

            @Override
            public SkillRequirement<?> deserialize(FriendlyByteBuf buffer) {
                return new RuntimeSkillRequirement(metadata, payload(buffer), this, safeTester);
            }

            @Override
            public void serialize(FriendlyByteBuf buffer, SkillRequirement<?> value) {
                write(buffer, ((RuntimeSkillRequirement) value).node.payload());
            }
        };
    }

    public static LivingEntityPredicate.Serializer livingCondition(
            PSTSerializerMetadata metadata,
            Predicate<PSTCustomRuntimeContexts.LivingConditionContext> tester
    ) {
        Predicate<PSTCustomRuntimeContexts.LivingConditionContext> safeTester = tester != null ? tester : context -> false;
        return new LivingEntityPredicate.Serializer() {
            @Override
            public LivingEntityPredicate createDefaultInstance() {
                return new RuntimeLivingCondition(metadata, new JsonObject(), this, safeTester);
            }

            @Override
            public LivingEntityPredicate deserialize(JsonObject json) {
                return new RuntimeLivingCondition(metadata, payload(json), this, safeTester);
            }

            @Override
            public void serialize(JsonObject json, LivingEntityPredicate value) {
                writeJson(json, ((RuntimeLivingCondition) value).node.payload());
            }

            @Override
            public LivingEntityPredicate deserialize(CompoundTag tag) {
                return new RuntimeLivingCondition(metadata, payload(tag), this, safeTester);
            }

            @Override
            public CompoundTag serialize(LivingEntityPredicate value) {
                return tag(((RuntimeLivingCondition) value).node.payload());
            }

            @Override
            public LivingEntityPredicate deserialize(FriendlyByteBuf buffer) {
                return new RuntimeLivingCondition(metadata, payload(buffer), this, safeTester);
            }

            @Override
            public void serialize(FriendlyByteBuf buffer, LivingEntityPredicate value) {
                write(buffer, ((RuntimeLivingCondition) value).node.payload());
            }
        };
    }

    public static DamageCondition.Serializer damageCondition(
            PSTSerializerMetadata metadata,
            Predicate<PSTCustomRuntimeContexts.DamageConditionContext> tester
    ) {
        Predicate<PSTCustomRuntimeContexts.DamageConditionContext> safeTester = tester != null ? tester : context -> false;
        return new DamageCondition.Serializer() {
            @Override
            public DamageCondition createDefaultInstance() {
                return new RuntimeDamageCondition(metadata, new JsonObject(), this, safeTester);
            }

            @Override
            public DamageCondition deserialize(JsonObject json) {
                return new RuntimeDamageCondition(metadata, payload(json), this, safeTester);
            }

            @Override
            public void serialize(JsonObject json, DamageCondition value) {
                writeJson(json, ((RuntimeDamageCondition) value).node.payload());
            }

            @Override
            public DamageCondition deserialize(CompoundTag tag) {
                return new RuntimeDamageCondition(metadata, payload(tag), this, safeTester);
            }

            @Override
            public CompoundTag serialize(DamageCondition value) {
                return tag(((RuntimeDamageCondition) value).node.payload());
            }

            @Override
            public DamageCondition deserialize(FriendlyByteBuf buffer) {
                return new RuntimeDamageCondition(metadata, payload(buffer), this, safeTester);
            }

            @Override
            public void serialize(FriendlyByteBuf buffer, DamageCondition value) {
                write(buffer, ((RuntimeDamageCondition) value).node.payload());
            }
        };
    }

    public static ItemStackPredicate.Serializer itemCondition(
            PSTSerializerMetadata metadata,
            Predicate<PSTCustomRuntimeContexts.ItemConditionContext> tester
    ) {
        Predicate<PSTCustomRuntimeContexts.ItemConditionContext> safeTester = tester != null ? tester : context -> false;
        return new ItemStackPredicate.Serializer() {
            @Override
            public ItemStackPredicate createDefaultInstance() {
                return new RuntimeItemCondition(metadata, new JsonObject(), this, safeTester);
            }

            @Override
            public ItemStackPredicate deserialize(JsonObject json) {
                return new RuntimeItemCondition(metadata, payload(json), this, safeTester);
            }

            @Override
            public void serialize(JsonObject json, ItemStackPredicate value) {
                writeJson(json, ((RuntimeItemCondition) value).node.payload());
            }

            @Override
            public ItemStackPredicate deserialize(CompoundTag tag) {
                return new RuntimeItemCondition(metadata, payload(tag), this, safeTester);
            }

            @Override
            public CompoundTag serialize(ItemStackPredicate value) {
                return tag(((RuntimeItemCondition) value).node.payload());
            }

            @Override
            public ItemStackPredicate deserialize(FriendlyByteBuf buffer) {
                return new RuntimeItemCondition(metadata, payload(buffer), this, safeTester);
            }

            @Override
            public void serialize(FriendlyByteBuf buffer, ItemStackPredicate value) {
                write(buffer, ((RuntimeItemCondition) value).node.payload());
            }
        };
    }

    public static EnchantmentCondition.Serializer enchantmentCondition(
            PSTSerializerMetadata metadata,
            Predicate<PSTCustomRuntimeContexts.EnchantmentConditionContext> tester
    ) {
        Predicate<PSTCustomRuntimeContexts.EnchantmentConditionContext> safeTester =
                tester != null ? tester : context -> false;
        return new EnchantmentCondition.Serializer() {
            @Override
            public EnchantmentCondition createDefaultInstance() {
                return new RuntimeEnchantmentCondition(metadata, new JsonObject(), this, safeTester);
            }

            @Override
            public EnchantmentCondition deserialize(JsonObject json) {
                return new RuntimeEnchantmentCondition(metadata, payload(json), this, safeTester);
            }

            @Override
            public void serialize(JsonObject json, EnchantmentCondition value) {
                writeJson(json, ((RuntimeEnchantmentCondition) value).node.payload());
            }

            @Override
            public EnchantmentCondition deserialize(CompoundTag tag) {
                return new RuntimeEnchantmentCondition(metadata, payload(tag), this, safeTester);
            }

            @Override
            public CompoundTag serialize(EnchantmentCondition value) {
                return tag(((RuntimeEnchantmentCondition) value).node.payload());
            }

            @Override
            public EnchantmentCondition deserialize(FriendlyByteBuf buffer) {
                return new RuntimeEnchantmentCondition(metadata, payload(buffer), this, safeTester);
            }

            @Override
            public void serialize(FriendlyByteBuf buffer, EnchantmentCondition value) {
                write(buffer, ((RuntimeEnchantmentCondition) value).node.payload());
            }
        };
    }

    public static LivingMultiplier.Serializer livingMultiplier(
            PSTSerializerMetadata metadata,
            Function<PSTCustomRuntimeContexts.LivingMultiplierContext, Number> valueFactory
    ) {
        Function<PSTCustomRuntimeContexts.LivingMultiplierContext, Number> safeFactory =
                valueFactory != null ? valueFactory : context -> 0.0D;
        return new LivingMultiplier.Serializer() {
            @Override
            public LivingMultiplier createDefaultInstance() {
                return new RuntimeLivingMultiplier(metadata, new JsonObject(), this, safeFactory);
            }

            @Override
            public LivingMultiplier deserialize(JsonObject json) {
                return new RuntimeLivingMultiplier(metadata, payload(json), this, safeFactory);
            }

            @Override
            public void serialize(JsonObject json, LivingMultiplier value) {
                writeJson(json, ((RuntimeLivingMultiplier) value).node.payload());
            }

            @Override
            public LivingMultiplier deserialize(CompoundTag tag) {
                return new RuntimeLivingMultiplier(metadata, payload(tag), this, safeFactory);
            }

            @Override
            public CompoundTag serialize(LivingMultiplier value) {
                return tag(((RuntimeLivingMultiplier) value).node.payload());
            }

            @Override
            public LivingMultiplier deserialize(FriendlyByteBuf buffer) {
                return new RuntimeLivingMultiplier(metadata, payload(buffer), this, safeFactory);
            }

            @Override
            public void serialize(FriendlyByteBuf buffer, LivingMultiplier value) {
                write(buffer, ((RuntimeLivingMultiplier) value).node.payload());
            }
        };
    }

    public static FloatFunction.Serializer floatFunction(
            PSTSerializerMetadata metadata,
            Function<PSTCustomRuntimeContexts.FloatFunctionContext, Number> valueFactory
    ) {
        Function<PSTCustomRuntimeContexts.FloatFunctionContext, Number> safeFactory =
                valueFactory != null ? valueFactory : context -> 0.0D;
        return new FloatFunction.Serializer() {
            @Override
            public FloatFunction<?> createDefaultInstance() {
                return new RuntimeFloatFunction(metadata, new JsonObject(), this, safeFactory);
            }

            @Override
            public FloatFunction<?> deserialize(JsonObject json) {
                return new RuntimeFloatFunction(metadata, payload(json), this, safeFactory);
            }

            @Override
            public void serialize(JsonObject json, FloatFunction<?> value) {
                writeJson(json, ((RuntimeFloatFunction) value).node.payload());
            }

            @Override
            public FloatFunction<?> deserialize(CompoundTag tag) {
                return new RuntimeFloatFunction(metadata, payload(tag), this, safeFactory);
            }

            @Override
            public CompoundTag serialize(FloatFunction<?> value) {
                return tag(((RuntimeFloatFunction) value).node.payload());
            }

            @Override
            public FloatFunction<?> deserialize(FriendlyByteBuf buffer) {
                return new RuntimeFloatFunction(metadata, payload(buffer), this, safeFactory);
            }

            @Override
            public void serialize(FriendlyByteBuf buffer, FloatFunction<?> value) {
                write(buffer, ((RuntimeFloatFunction) value).node.payload());
            }
        };
    }

    public static SkillBonus.Serializer skillBonus(
            PSTSerializerMetadata metadata,
            Consumer<PSTCustomRuntimeContexts.SkillBonusLearnContext> onLearn,
            Consumer<PSTCustomRuntimeContexts.SkillBonusRemoveContext> onRemove,
            Consumer<PSTCustomRuntimeContexts.SkillBonusApplyContext> onApply
    ) {
        Consumer<PSTCustomRuntimeContexts.SkillBonusLearnContext> safeOnLearn = onLearn != null ? onLearn : context -> {
        };
        Consumer<PSTCustomRuntimeContexts.SkillBonusRemoveContext> safeOnRemove = onRemove != null ? onRemove : context -> {
        };
        Consumer<PSTCustomRuntimeContexts.SkillBonusApplyContext> safeOnApply = onApply != null ? onApply : context -> {
        };
        return new SkillBonus.Serializer() {
            @Override
            public SkillBonus<?> createDefaultInstance() {
                return new RuntimeSkillBonus(metadata, new JsonObject(), this, NO_EVENT_LISTENER, safeOnLearn, safeOnRemove, safeOnApply, 1.0D);
            }

            @Override
            public SkillBonus<?> deserialize(JsonObject json) {
                JsonObject payload = payload(json);
                SkillEventListener listener = nestedEventListener(metadata, payload);
                return new RuntimeSkillBonus(metadata, payload, this, listener, safeOnLearn, safeOnRemove, safeOnApply, 1.0D);
            }

            @Override
            public void serialize(JsonObject json, SkillBonus<?> value) {
                writeJson(json, ((RuntimeSkillBonus) value).node.payload());
            }

            @Override
            public SkillBonus<?> deserialize(CompoundTag tag) {
                JsonObject payload = payload(tag);
                SkillEventListener listener = nestedEventListener(metadata, payload);
                return new RuntimeSkillBonus(metadata, payload, this, listener, safeOnLearn, safeOnRemove, safeOnApply, 1.0D);
            }

            @Override
            public CompoundTag serialize(SkillBonus<?> value) {
                return tag(((RuntimeSkillBonus) value).node.payload());
            }

            @Override
            public SkillBonus<?> deserialize(FriendlyByteBuf buffer) {
                JsonObject payload = payload(buffer);
                SkillEventListener listener = nestedEventListener(metadata, payload);
                return new RuntimeSkillBonus(metadata, payload, this, listener, safeOnLearn, safeOnRemove, safeOnApply, 1.0D);
            }

            @Override
            public void serialize(FriendlyByteBuf buffer, SkillBonus<?> value) {
                write(buffer, ((RuntimeSkillBonus) value).node.payload());
            }
        };
    }

    public static SkillEventListener.Serializer eventListener(
            PSTSerializerMetadata metadata,
            Consumer<PSTCustomRuntimeContexts.SkillLearnedListenerContext> onSkillLearned,
            Consumer<PSTCustomRuntimeContexts.SkillRemovedListenerContext> onSkillRemoved,
            Consumer<PSTCustomRuntimeContexts.TickListenerContext> onTick,
            Consumer<PSTCustomRuntimeContexts.AttackListenerContext> onAttack,
            Consumer<PSTCustomRuntimeContexts.DamageTakenListenerContext> onDamageTaken,
            Consumer<PSTCustomRuntimeContexts.CriticalHitListenerContext> onCriticalHit,
            Consumer<PSTCustomRuntimeContexts.BlockListenerContext> onBlock,
            Consumer<PSTCustomRuntimeContexts.ItemUsedListenerContext> onItemUsed,
            Consumer<PSTCustomRuntimeContexts.KillListenerContext> onKill
    ) {
        Consumer<PSTCustomRuntimeContexts.SkillLearnedListenerContext> safeOnSkillLearned =
                onSkillLearned != null ? onSkillLearned : context -> {
                };
        Consumer<PSTCustomRuntimeContexts.SkillRemovedListenerContext> safeOnSkillRemoved =
                onSkillRemoved != null ? onSkillRemoved : context -> {
                };
        Consumer<PSTCustomRuntimeContexts.TickListenerContext> safeOnTick =
                onTick != null ? onTick : context -> {
                };
        Consumer<PSTCustomRuntimeContexts.AttackListenerContext> safeOnAttack =
                onAttack != null ? onAttack : context -> {
                };
        Consumer<PSTCustomRuntimeContexts.DamageTakenListenerContext> safeOnDamageTaken =
                onDamageTaken != null ? onDamageTaken : context -> {
                };
        Consumer<PSTCustomRuntimeContexts.CriticalHitListenerContext> safeOnCriticalHit =
                onCriticalHit != null ? onCriticalHit : context -> {
                };
        Consumer<PSTCustomRuntimeContexts.BlockListenerContext> safeOnBlock =
                onBlock != null ? onBlock : context -> {
                };
        Consumer<PSTCustomRuntimeContexts.ItemUsedListenerContext> safeOnItemUsed =
                onItemUsed != null ? onItemUsed : context -> {
                };
        Consumer<PSTCustomRuntimeContexts.KillListenerContext> safeOnKill =
                onKill != null ? onKill : context -> {
                };

        return new SkillEventListener.Serializer() {
            @Override
            public SkillEventListener createDefaultInstance() {
                return new RuntimeSkillEventListener(
                        metadata,
                        new JsonObject(),
                        this,
                        safeOnSkillLearned,
                        safeOnSkillRemoved,
                        safeOnTick,
                        safeOnAttack,
                        safeOnDamageTaken,
                        safeOnCriticalHit,
                        safeOnBlock,
                        safeOnItemUsed,
                        safeOnKill
                );
            }

            @Override
            public SkillEventListener deserialize(JsonObject json) {
                return new RuntimeSkillEventListener(
                        metadata,
                        payload(json),
                        this,
                        safeOnSkillLearned,
                        safeOnSkillRemoved,
                        safeOnTick,
                        safeOnAttack,
                        safeOnDamageTaken,
                        safeOnCriticalHit,
                        safeOnBlock,
                        safeOnItemUsed,
                        safeOnKill
                );
            }

            @Override
            public void serialize(JsonObject json, SkillEventListener value) {
                writeJson(json, ((RuntimeSkillEventListener) value).node.payload());
            }

            @Override
            public SkillEventListener deserialize(CompoundTag tag) {
                return new RuntimeSkillEventListener(
                        metadata,
                        payload(tag),
                        this,
                        safeOnSkillLearned,
                        safeOnSkillRemoved,
                        safeOnTick,
                        safeOnAttack,
                        safeOnDamageTaken,
                        safeOnCriticalHit,
                        safeOnBlock,
                        safeOnItemUsed,
                        safeOnKill
                );
            }

            @Override
            public CompoundTag serialize(SkillEventListener value) {
                return tag(((RuntimeSkillEventListener) value).node.payload());
            }

            @Override
            public SkillEventListener deserialize(FriendlyByteBuf buffer) {
                return new RuntimeSkillEventListener(
                        metadata,
                        payload(buffer),
                        this,
                        safeOnSkillLearned,
                        safeOnSkillRemoved,
                        safeOnTick,
                        safeOnAttack,
                        safeOnDamageTaken,
                        safeOnCriticalHit,
                        safeOnBlock,
                        safeOnItemUsed,
                        safeOnKill
                );
            }

            @Override
            public void serialize(FriendlyByteBuf buffer, SkillEventListener value) {
                write(buffer, ((RuntimeSkillEventListener) value).node.payload());
            }
        };
    }

    public static ItemBonus.Serializer itemBonus(PSTSerializerMetadata metadata) {
        return new ItemBonus.Serializer() {
            @Override
            public ItemBonus<?> createDefaultInstance() {
                return new RuntimeItemBonus(metadata, new JsonObject(), this);
            }

            @Override
            public ItemBonus<?> deserialize(JsonObject json) {
                return new RuntimeItemBonus(metadata, payload(json), this);
            }

            @Override
            public void serialize(JsonObject json, ItemBonus<?> value) {
                writeJson(json, ((RuntimeItemBonus) value).node.payload());
            }

            @Override
            public ItemBonus<?> deserialize(CompoundTag tag) {
                return new RuntimeItemBonus(metadata, payload(tag), this);
            }

            @Override
            public CompoundTag serialize(ItemBonus<?> value) {
                return tag(((RuntimeItemBonus) value).node.payload());
            }

            @Override
            public ItemBonus<?> deserialize(FriendlyByteBuf buffer) {
                return new RuntimeItemBonus(metadata, payload(buffer), this);
            }

            @Override
            public void serialize(FriendlyByteBuf buffer, ItemBonus<?> value) {
                write(buffer, ((RuntimeItemBonus) value).node.payload());
            }
        };
    }

    private static JsonObject payload(JsonObject json) {
        JsonObject payload = JsonHelper.copy(json).getAsJsonObject();
        payload.remove("type");
        return payload;
    }

    private static JsonObject payload(CompoundTag tag) {
        if (!tag.contains(PAYLOAD_KEY)) {
            return new JsonObject();
        }
        String raw = tag.getString(PAYLOAD_KEY);
        if (raw == null || raw.isBlank()) {
            return new JsonObject();
        }
        JsonElement parsed = JsonParser.parseString(raw);
        if (!parsed.isJsonObject()) {
            throw new JsonParseException("PassiveSTJS runtime payload must be a JSON object: " + raw);
        }
        return parsed.getAsJsonObject();
    }

    private static JsonObject payload(FriendlyByteBuf buffer) {
        String raw = buffer.readUtf();
        if (raw.isBlank()) {
            return new JsonObject();
        }
        JsonElement parsed = JsonParser.parseString(raw);
        if (!parsed.isJsonObject()) {
            throw new JsonParseException("PassiveSTJS runtime payload must be a JSON object: " + raw);
        }
        return parsed.getAsJsonObject();
    }

    private static CompoundTag tag(JsonObject payload) {
        CompoundTag tag = new CompoundTag();
        tag.putString(PAYLOAD_KEY, payload == null ? "{}" : payload.toString());
        return tag;
    }

    private static void write(FriendlyByteBuf buffer, JsonObject payload) {
        buffer.writeUtf(payload == null ? "{}" : payload.toString());
    }

    private static void writeJson(JsonObject json, JsonObject payload) {
        for (Map.Entry<String, JsonElement> entry : payload.entrySet()) {
            json.add(entry.getKey(), JsonHelper.copy(entry.getValue()));
        }
    }

    private static SkillEventListener nestedEventListener(PSTSerializerMetadata metadata, JsonObject payload) {
        SkillEventListener listener = new PSTRuntimeNode(metadata, payload).eventListener("event_listener");
        return listener == null ? NO_EVENT_LISTENER : listener;
    }

    private static JsonObject typed(PSTSerializerMetadata metadata, JsonObject payload) {
        JsonObject typed = JsonHelper.copy(payload).getAsJsonObject();
        typed.addProperty("type", metadata.id().toString());
        return typed;
    }

    private static float number(Number value) {
        return value == null ? 0.0F : value.floatValue();
    }

    private static SkillBonus.Target target(JsonObject payload) {
        String raw = payload != null && payload.has("target") ? payload.get("target").getAsString() : SkillBonus.Target.PLAYER.getName();
        try {
            return SkillBonus.Target.fromName(raw);
        } catch (IllegalArgumentException exception) {
            return SkillBonus.Target.PLAYER;
        }
    }

    private static final SkillEventListener NO_EVENT_LISTENER = new SkillEventListener() {
        @Override
        public SkillBonus.Target getTarget() {
            return SkillBonus.Target.PLAYER;
        }

        @Override
        public SkillEventListener.Serializer getSerializer() {
            throw new UnsupportedOperationException("PassiveSTJS internal no-op listener is not serializable");
        }

        @Override
        public void addEditorWidgets(SkillTreeEditor editor, Consumer<SkillEventListener> consumer) {
        }
    };

    private static final class RuntimeSkillRequirement implements SkillRequirement<RuntimeSkillRequirement>, PSTCustomRuntimeSkillRequirement {
        private final PSTRuntimeNode node;
        private final SkillRequirement.Serializer serializer;
        private final Predicate<PSTCustomRuntimeContexts.SkillRequirementContext> tester;

        private RuntimeSkillRequirement(
                PSTSerializerMetadata metadata,
                JsonObject payload,
                SkillRequirement.Serializer serializer,
                Predicate<PSTCustomRuntimeContexts.SkillRequirementContext> tester
        ) {
            this.node = new PSTRuntimeNode(metadata, payload);
            this.serializer = serializer;
            this.tester = tester;
        }

        @Override
        public PSTRuntimeNode node() {
            return node;
        }

        @Override
        public boolean test(Player player) {
            return tester.test(new PSTCustomRuntimeContexts.SkillRequirementContext(node, player));
        }

        @Override
        public MutableComponent getTooltip() {
            return PSTRuntimeTooltipSupport.skillRequirementTooltip(node);
        }

        @Override
        public void addEditorWidgets(SkillTreeEditor editor, Consumer<RuntimeSkillRequirement> consumer) {
        }

        @Override
        public SkillRequirement.Serializer getSerializer() {
            return serializer;
        }

        @Override
        public RuntimeSkillRequirement copy() {
            return new RuntimeSkillRequirement(node.metadata(), node.payload(), serializer, tester);
        }
    }

    private static final class RuntimeLivingCondition implements LivingEntityPredicate {
        private final PSTRuntimeNode node;
        private final LivingEntityPredicate.Serializer serializer;
        private final Predicate<PSTCustomRuntimeContexts.LivingConditionContext> tester;

        private RuntimeLivingCondition(
                PSTSerializerMetadata metadata,
                JsonObject payload,
                LivingEntityPredicate.Serializer serializer,
                Predicate<PSTCustomRuntimeContexts.LivingConditionContext> tester
        ) {
            this.node = new PSTRuntimeNode(metadata, payload);
            this.serializer = serializer;
            this.tester = tester;
        }

        @Override
        public boolean test(LivingEntity entity) {
            return tester.test(new PSTCustomRuntimeContexts.LivingConditionContext(node, entity));
        }

        @Override
        public MutableComponent getTooltip(MutableComponent component, SkillBonus.Target target) {
            return PSTRuntimeTooltipSupport.wrappedPrefixTooltip(
                    node,
                    component,
                    target,
                    () -> PSTRuntimeTranslationKeys.describe(node.metadata(), component)
            );
        }

        @Override
        public LivingEntityPredicate.Serializer getSerializer() {
            return serializer;
        }
    }

    private static final class RuntimeDamageCondition implements DamageCondition {
        private final PSTRuntimeNode node;
        private final DamageCondition.Serializer serializer;
        private final Predicate<PSTCustomRuntimeContexts.DamageConditionContext> tester;

        private RuntimeDamageCondition(
                PSTSerializerMetadata metadata,
                JsonObject payload,
                DamageCondition.Serializer serializer,
                Predicate<PSTCustomRuntimeContexts.DamageConditionContext> tester
        ) {
            this.node = new PSTRuntimeNode(metadata, payload);
            this.serializer = serializer;
            this.tester = tester;
        }

        @Override
        public boolean met(DamageSource damageSource) {
            return tester.test(new PSTCustomRuntimeContexts.DamageConditionContext(node, damageSource));
        }

        @Override
        public MutableComponent getTooltip() {
            return PSTRuntimeTooltipSupport.standalonePrefixTooltip(node);
        }

        @Override
        public DamageCondition.Serializer getSerializer() {
            return serializer;
        }
    }

    private static final class RuntimeItemCondition implements ItemStackPredicate {
        private final PSTRuntimeNode node;
        private final ItemStackPredicate.Serializer serializer;
        private final Predicate<PSTCustomRuntimeContexts.ItemConditionContext> tester;

        private RuntimeItemCondition(
                PSTSerializerMetadata metadata,
                JsonObject payload,
                ItemStackPredicate.Serializer serializer,
                Predicate<PSTCustomRuntimeContexts.ItemConditionContext> tester
        ) {
            this.node = new PSTRuntimeNode(metadata, payload);
            this.serializer = serializer;
            this.tester = tester;
        }

        @Override
        public boolean test(ItemStack stack) {
            return tester.test(new PSTCustomRuntimeContexts.ItemConditionContext(node, stack));
        }

        @Override
        public Component getTooltip() {
            return PSTRuntimeTooltipSupport.standalonePrefixTooltip(node);
        }

        @Override
        public ItemStackPredicate.Serializer getSerializer() {
            return serializer;
        }
    }

    private static final class RuntimeEnchantmentCondition implements EnchantmentCondition {
        private final PSTRuntimeNode node;
        private final EnchantmentCondition.Serializer serializer;
        private final Predicate<PSTCustomRuntimeContexts.EnchantmentConditionContext> tester;

        private RuntimeEnchantmentCondition(
                PSTSerializerMetadata metadata,
                JsonObject payload,
                EnchantmentCondition.Serializer serializer,
                Predicate<PSTCustomRuntimeContexts.EnchantmentConditionContext> tester
        ) {
            this.node = new PSTRuntimeNode(metadata, payload);
            this.serializer = serializer;
            this.tester = tester;
        }

        @Override
        public boolean met(EnchantmentCategory category) {
            return tester.test(new PSTCustomRuntimeContexts.EnchantmentConditionContext(node, category));
        }

        @Override
        public EnchantmentCondition.Serializer getSerializer() {
            return serializer;
        }
    }

    private static final class RuntimeLivingMultiplier implements LivingMultiplier {
        private final PSTRuntimeNode node;
        private final LivingMultiplier.Serializer serializer;
        private final Function<PSTCustomRuntimeContexts.LivingMultiplierContext, Number> valueFactory;

        private RuntimeLivingMultiplier(
                PSTSerializerMetadata metadata,
                JsonObject payload,
                LivingMultiplier.Serializer serializer,
                Function<PSTCustomRuntimeContexts.LivingMultiplierContext, Number> valueFactory
        ) {
            this.node = new PSTRuntimeNode(metadata, payload);
            this.serializer = serializer;
            this.valueFactory = valueFactory;
        }

        @Override
        public float getValue(LivingEntity entity) {
            return number(valueFactory.apply(new PSTCustomRuntimeContexts.LivingMultiplierContext(node, entity)));
        }

        @Override
        public LivingMultiplier.Serializer getSerializer() {
            return serializer;
        }

        @Override
        public MutableComponent getTooltip(MutableComponent component, SkillBonus.Target target) {
            return PSTRuntimeTooltipSupport.wrappedPrefixTooltip(
                    node,
                    component,
                    target,
                    () -> PSTRuntimeTranslationKeys.describe(node.metadata(), component)
            );
        }
    }

    private static final class RuntimeFloatFunction implements FloatFunction<RuntimeFloatFunction> {
        private final PSTRuntimeNode node;
        private final FloatFunction.Serializer serializer;
        private final Function<PSTCustomRuntimeContexts.FloatFunctionContext, Number> valueFactory;

        private RuntimeFloatFunction(
                PSTSerializerMetadata metadata,
                JsonObject payload,
                FloatFunction.Serializer serializer,
                Function<PSTCustomRuntimeContexts.FloatFunctionContext, Number> valueFactory
        ) {
            this.node = new PSTRuntimeNode(metadata, payload);
            this.serializer = serializer;
            this.valueFactory = valueFactory;
        }

        @Override
        public float apply(LivingEntity entity) {
            return number(valueFactory.apply(new PSTCustomRuntimeContexts.FloatFunctionContext(node, entity)));
        }

        @Override
        public MutableComponent getMultiplierTooltip(SkillBonus.Target target, float value, Component component) {
            return PSTRuntimeTranslationKeys.describe(
                    PSTRuntimeTranslationKeys.descriptionId(node.metadata()) + ".multiplier",
                    component,
                    formatNumber(value)
            );
        }

        @Override
        public MutableComponent getConditionTooltip(
                SkillBonus.Target target,
                FloatFunctionEntityPredicate.Logic logic,
                Component component,
                float value
        ) {
            return PSTRuntimeTranslationKeys.describe(
                    PSTRuntimeTranslationKeys.descriptionId(node.metadata()) + ".condition",
                    component,
                    logic.name().toLowerCase(),
                    formatNumber(value)
            );
        }

        @Override
        public MutableComponent getRequirementTooltip(FloatFunctionEntityPredicate.Logic logic, float value) {
            return PSTRuntimeTranslationKeys.describe(
                    PSTRuntimeTranslationKeys.descriptionId(node.metadata()) + ".requirement",
                    logic.name().toLowerCase(),
                    formatNumber(value)
            );
        }

        @Override
        public FloatFunction.Serializer getSerializer() {
            return serializer;
        }

        @Override
        public void addEditorWidgets(SkillTreeEditor editor, Consumer<FloatFunction<?>> consumer) {
        }
    }

    private static final class RuntimeSkillEventListener implements PSTCustomRuntimeEventListener {
        private final PSTRuntimeNode node;
        private final SkillEventListener.Serializer serializer;
        private final SkillBonus.Target target;
        private final Consumer<PSTCustomRuntimeContexts.SkillLearnedListenerContext> onSkillLearned;
        private final Consumer<PSTCustomRuntimeContexts.SkillRemovedListenerContext> onSkillRemoved;
        private final Consumer<PSTCustomRuntimeContexts.TickListenerContext> onTick;
        private final Consumer<PSTCustomRuntimeContexts.AttackListenerContext> onAttack;
        private final Consumer<PSTCustomRuntimeContexts.DamageTakenListenerContext> onDamageTaken;
        private final Consumer<PSTCustomRuntimeContexts.CriticalHitListenerContext> onCriticalHit;
        private final Consumer<PSTCustomRuntimeContexts.BlockListenerContext> onBlock;
        private final Consumer<PSTCustomRuntimeContexts.ItemUsedListenerContext> onItemUsed;
        private final Consumer<PSTCustomRuntimeContexts.KillListenerContext> onKill;

        private RuntimeSkillEventListener(
                PSTSerializerMetadata metadata,
                JsonObject payload,
                SkillEventListener.Serializer serializer,
                Consumer<PSTCustomRuntimeContexts.SkillLearnedListenerContext> onSkillLearned,
                Consumer<PSTCustomRuntimeContexts.SkillRemovedListenerContext> onSkillRemoved,
                Consumer<PSTCustomRuntimeContexts.TickListenerContext> onTick,
                Consumer<PSTCustomRuntimeContexts.AttackListenerContext> onAttack,
                Consumer<PSTCustomRuntimeContexts.DamageTakenListenerContext> onDamageTaken,
                Consumer<PSTCustomRuntimeContexts.CriticalHitListenerContext> onCriticalHit,
                Consumer<PSTCustomRuntimeContexts.BlockListenerContext> onBlock,
                Consumer<PSTCustomRuntimeContexts.ItemUsedListenerContext> onItemUsed,
                Consumer<PSTCustomRuntimeContexts.KillListenerContext> onKill
        ) {
            this.node = new PSTRuntimeNode(metadata, payload);
            this.serializer = serializer;
            this.target = target(payload);
            this.onSkillLearned = onSkillLearned;
            this.onSkillRemoved = onSkillRemoved;
            this.onTick = onTick;
            this.onAttack = onAttack;
            this.onDamageTaken = onDamageTaken;
            this.onCriticalHit = onCriticalHit;
            this.onBlock = onBlock;
            this.onItemUsed = onItemUsed;
            this.onKill = onKill;
        }

        @Override
        public PSTRuntimeNode node() {
            return node;
        }

        @Override
        public void onSkillLearned(PSTCustomRuntimeContexts.SkillLearnedListenerContext context) {
            onSkillLearned.accept(context);
        }

        @Override
        public void onSkillRemoved(PSTCustomRuntimeContexts.SkillRemovedListenerContext context) {
            onSkillRemoved.accept(context);
        }

        @Override
        public void onTick(PSTCustomRuntimeContexts.TickListenerContext context) {
            onTick.accept(context);
        }

        @Override
        public void onAttack(PSTCustomRuntimeContexts.AttackListenerContext context) {
            onAttack.accept(context);
        }

        @Override
        public void onDamageTaken(PSTCustomRuntimeContexts.DamageTakenListenerContext context) {
            onDamageTaken.accept(context);
        }

        @Override
        public void onCriticalHit(PSTCustomRuntimeContexts.CriticalHitListenerContext context) {
            onCriticalHit.accept(context);
        }

        @Override
        public void onBlock(PSTCustomRuntimeContexts.BlockListenerContext context) {
            onBlock.accept(context);
        }

        @Override
        public void onItemUsed(PSTCustomRuntimeContexts.ItemUsedListenerContext context) {
            onItemUsed.accept(context);
        }

        @Override
        public void onKill(PSTCustomRuntimeContexts.KillListenerContext context) {
            onKill.accept(context);
        }

        @Override
        public MutableComponent getTooltip(Component bonusTooltip) {
            return PSTRuntimeTooltipSupport.wrappedPrefixTooltip(
                    node,
                    bonusTooltip,
                    target,
                    () -> PSTRuntimeTranslationKeys.describe(node.metadata(), bonusTooltip)
            );
        }

        @Override
        public SkillBonus.Target getTarget() {
            return target;
        }

        @Override
        public SkillEventListener.Serializer getSerializer() {
            return serializer;
        }

        @Override
        public void addEditorWidgets(SkillTreeEditor editor, Consumer<SkillEventListener> consumer) {
        }
    }

    private static final class RuntimeSkillBonus implements EventListenerBonus<RuntimeSkillBonus>, PSTCustomRuntimeSkillBonus {
        private final PSTRuntimeNode node;
        private final SkillBonus.Serializer serializer;
        private final SkillEventListener eventListener;
        private final Consumer<PSTCustomRuntimeContexts.SkillBonusLearnContext> onLearn;
        private final Consumer<PSTCustomRuntimeContexts.SkillBonusRemoveContext> onRemove;
        private final Consumer<PSTCustomRuntimeContexts.SkillBonusApplyContext> onApply;
        private final double multiplier;

        private RuntimeSkillBonus(
                PSTSerializerMetadata metadata,
                JsonObject payload,
                SkillBonus.Serializer serializer,
                SkillEventListener eventListener,
                Consumer<PSTCustomRuntimeContexts.SkillBonusLearnContext> onLearn,
                Consumer<PSTCustomRuntimeContexts.SkillBonusRemoveContext> onRemove,
                Consumer<PSTCustomRuntimeContexts.SkillBonusApplyContext> onApply,
                double multiplier
        ) {
            this.node = new PSTRuntimeNode(metadata, payload);
            this.serializer = serializer;
            this.eventListener = eventListener == null ? NO_EVENT_LISTENER : eventListener;
            this.onLearn = onLearn;
            this.onRemove = onRemove;
            this.onApply = onApply;
            this.multiplier = multiplier;
        }

        @Override
        public PSTRuntimeNode node() {
            return node;
        }

        @Override
        public void onSkillLearned(ServerPlayer player, boolean notify) {
            EventListenerBonus.super.onSkillLearned(player, notify);
            onLearn.accept(new PSTCustomRuntimeContexts.SkillBonusLearnContext(node, player, notify));
        }

        @Override
        public void onSkillRemoved(ServerPlayer player) {
            EventListenerBonus.super.onSkillRemoved(player);
            onRemove.accept(new PSTCustomRuntimeContexts.SkillBonusRemoveContext(node, player));
        }

        @Override
        public boolean canMerge(SkillBonus<?> other) {
            return false;
        }

        @Override
        public RuntimeSkillBonus merge(SkillBonus<?> other) {
            return copy();
        }

        @Override
        public RuntimeSkillBonus copy() {
            return new RuntimeSkillBonus(
                    node.metadata(),
                    node.payload(),
                    serializer,
                    eventListener,
                    onLearn,
                    onRemove,
                    onApply,
                    multiplier
            );
        }

        @Override
        public RuntimeSkillBonus multiply(double value) {
            return new RuntimeSkillBonus(
                    node.metadata(),
                    node.payload(),
                    serializer,
                    eventListener,
                    onLearn,
                    onRemove,
                    onApply,
                    multiplier * value
            );
        }

        @Override
        public SkillBonus.Serializer getSerializer() {
            return serializer;
        }

        @Override
        public MutableComponent getTooltip() {
            return PSTRuntimeTooltipSupport.skillBonusTooltip(node);
        }

        @Override
        public boolean isPositive() {
            return true;
        }

        @Override
        public void addEditorWidgets(
                SkillTreeEditor editor,
                int x,
                Consumer<EventListenerBonus<RuntimeSkillBonus>> consumer
        ) {
        }

        @Override
        public SkillEventListener getEventListener() {
            return eventListener;
        }

        @Override
        public void applyEffect(LivingEntity target) {
            onApply.accept(new PSTCustomRuntimeContexts.SkillBonusApplyContext(node, target, multiplier, (DamageSource) null));
        }
    }

    private static final class RuntimeItemBonus implements ItemBonus<RuntimeItemBonus>, PSTCustomRuntimeItemBonus {
        private final PSTRuntimeNode node;
        private final ItemBonus.Serializer serializer;

        private RuntimeItemBonus(
                PSTSerializerMetadata metadata,
                JsonObject payload,
                ItemBonus.Serializer serializer
        ) {
            this.node = new PSTRuntimeNode(metadata, payload);
            this.serializer = serializer;
        }

        @Override
        public boolean canMerge(ItemBonus<?> other) {
            return false;
        }

        @Override
        public RuntimeItemBonus merge(ItemBonus<?> other) {
            return copy();
        }

        @Override
        public RuntimeItemBonus copy() {
            return new RuntimeItemBonus(node.metadata(), node.payload(), serializer);
        }

        @Override
        public RuntimeItemBonus multiply(double value) {
            return copy();
        }

        @Override
        public ItemBonus.Serializer getSerializer() {
            return serializer;
        }

        @Override
        public void addTooltip(Consumer<MutableComponent> consumer) {
            consumer.accept(PSTRuntimeTooltipSupport.standaloneEffectTooltip(node));
        }

        @Override
        public boolean isPositive() {
            return true;
        }

        @Override
        public PSTRuntimeNode node() {
            return node;
        }
    }
}
