package com.pickaid.passivestjs.runtime.serializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.pickaid.passivestjs.kubejs.registry.PSTSerializerMetadata;
import com.pickaid.passivestjs.runtime.PSTRuntimeNode;
import com.pickaid.passivestjs.runtime.tooltip.PSTRuntimeTooltipSupport;
import daripher.skilltree.client.widget.editor.SkillTreeEditor;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.Consumer;

public final class PSTPlaceholderSerializers {
    private PSTPlaceholderSerializers() {
    }

    public static SkillBonus.Serializer skillBonus(PSTSerializerMetadata metadata) {
        return new SkillBonus.Serializer() {
            @Override
            public SkillBonus<?> createDefaultInstance() {
                return new PlaceholderSkillBonus(metadata, this);
            }

            @Override
            public SkillBonus<?> deserialize(JsonObject json) {
                throw unsupportedJson(metadata, "skill bonus", json);
            }

            @Override
            public void serialize(JsonObject json, SkillBonus<?> value) {
            }

            @Override
            public SkillBonus<?> deserialize(CompoundTag tag) {
                throw unsupportedBinary(metadata, "skill bonus", "nbt");
            }

            @Override
            public CompoundTag serialize(SkillBonus<?> value) {
                return tag(metadata);
            }

            @Override
            public SkillBonus<?> deserialize(FriendlyByteBuf buffer) {
                throw unsupportedBinary(metadata, "skill bonus", "network");
            }

            @Override
            public void serialize(FriendlyByteBuf buffer, SkillBonus<?> value) {
                buffer.writeResourceLocation(metadata.id());
            }
        };
    }

    public static LivingMultiplier.Serializer livingMultiplier(PSTSerializerMetadata metadata) {
        return new LivingMultiplier.Serializer() {
            @Override
            public LivingMultiplier createDefaultInstance() {
                return new PlaceholderLivingMultiplier(metadata, this);
            }

            @Override
            public LivingMultiplier deserialize(JsonObject json) {
                throw unsupportedJson(metadata, "living multiplier", json);
            }

            @Override
            public void serialize(JsonObject json, LivingMultiplier value) {
            }

            @Override
            public LivingMultiplier deserialize(CompoundTag tag) {
                throw unsupportedBinary(metadata, "living multiplier", "nbt");
            }

            @Override
            public CompoundTag serialize(LivingMultiplier value) {
                return tag(metadata);
            }

            @Override
            public LivingMultiplier deserialize(FriendlyByteBuf buffer) {
                throw unsupportedBinary(metadata, "living multiplier", "network");
            }

            @Override
            public void serialize(FriendlyByteBuf buffer, LivingMultiplier value) {
                buffer.writeResourceLocation(metadata.id());
            }
        };
    }

    public static LivingEntityPredicate.Serializer livingCondition(PSTSerializerMetadata metadata) {
        return new LivingEntityPredicate.Serializer() {
            @Override
            public LivingEntityPredicate createDefaultInstance() {
                return new PlaceholderLivingCondition(metadata, this);
            }

            @Override
            public LivingEntityPredicate deserialize(JsonObject json) {
                throw unsupportedJson(metadata, "living condition", json);
            }

            @Override
            public void serialize(JsonObject json, LivingEntityPredicate value) {
            }

            @Override
            public LivingEntityPredicate deserialize(CompoundTag tag) {
                throw unsupportedBinary(metadata, "living condition", "nbt");
            }

            @Override
            public CompoundTag serialize(LivingEntityPredicate value) {
                return tag(metadata);
            }

            @Override
            public LivingEntityPredicate deserialize(FriendlyByteBuf buffer) {
                throw unsupportedBinary(metadata, "living condition", "network");
            }

            @Override
            public void serialize(FriendlyByteBuf buffer, LivingEntityPredicate value) {
                buffer.writeResourceLocation(metadata.id());
            }
        };
    }

    public static DamageCondition.Serializer damageCondition(PSTSerializerMetadata metadata) {
        return new DamageCondition.Serializer() {
            @Override
            public DamageCondition createDefaultInstance() {
                return new PlaceholderDamageCondition(metadata, this);
            }

            @Override
            public DamageCondition deserialize(JsonObject json) {
                throw unsupportedJson(metadata, "damage condition", json);
            }

            @Override
            public void serialize(JsonObject json, DamageCondition value) {
            }

            @Override
            public DamageCondition deserialize(CompoundTag tag) {
                throw unsupportedBinary(metadata, "damage condition", "nbt");
            }

            @Override
            public CompoundTag serialize(DamageCondition value) {
                return tag(metadata);
            }

            @Override
            public DamageCondition deserialize(FriendlyByteBuf buffer) {
                throw unsupportedBinary(metadata, "damage condition", "network");
            }

            @Override
            public void serialize(FriendlyByteBuf buffer, DamageCondition value) {
                buffer.writeResourceLocation(metadata.id());
            }
        };
    }

    public static ItemStackPredicate.Serializer itemCondition(PSTSerializerMetadata metadata) {
        return new ItemStackPredicate.Serializer() {
            @Override
            public ItemStackPredicate createDefaultInstance() {
                return new PlaceholderItemCondition(metadata, this);
            }

            @Override
            public ItemStackPredicate deserialize(JsonObject json) {
                throw unsupportedJson(metadata, "item condition", json);
            }

            @Override
            public void serialize(JsonObject json, ItemStackPredicate value) {
            }

            @Override
            public ItemStackPredicate deserialize(CompoundTag tag) {
                throw unsupportedBinary(metadata, "item condition", "nbt");
            }

            @Override
            public CompoundTag serialize(ItemStackPredicate value) {
                return tag(metadata);
            }

            @Override
            public ItemStackPredicate deserialize(FriendlyByteBuf buffer) {
                throw unsupportedBinary(metadata, "item condition", "network");
            }

            @Override
            public void serialize(FriendlyByteBuf buffer, ItemStackPredicate value) {
                buffer.writeResourceLocation(metadata.id());
            }
        };
    }

    public static EnchantmentCondition.Serializer enchantmentCondition(PSTSerializerMetadata metadata) {
        return new EnchantmentCondition.Serializer() {
            @Override
            public EnchantmentCondition createDefaultInstance() {
                return new PlaceholderEnchantmentCondition(metadata, this);
            }

            @Override
            public EnchantmentCondition deserialize(JsonObject json) {
                throw unsupportedJson(metadata, "enchantment condition", json);
            }

            @Override
            public void serialize(JsonObject json, EnchantmentCondition value) {
            }

            @Override
            public EnchantmentCondition deserialize(CompoundTag tag) {
                throw unsupportedBinary(metadata, "enchantment condition", "nbt");
            }

            @Override
            public CompoundTag serialize(EnchantmentCondition value) {
                return tag(metadata);
            }

            @Override
            public EnchantmentCondition deserialize(FriendlyByteBuf buffer) {
                throw unsupportedBinary(metadata, "enchantment condition", "network");
            }

            @Override
            public void serialize(FriendlyByteBuf buffer, EnchantmentCondition value) {
                buffer.writeResourceLocation(metadata.id());
            }
        };
    }

    public static SkillEventListener.Serializer eventListener(PSTSerializerMetadata metadata) {
        return new SkillEventListener.Serializer() {
            @Override
            public SkillEventListener createDefaultInstance() {
                return new PlaceholderEventListener(metadata, this);
            }

            @Override
            public SkillEventListener deserialize(JsonObject json) {
                throw unsupportedJson(metadata, "event listener", json);
            }

            @Override
            public void serialize(JsonObject json, SkillEventListener value) {
            }

            @Override
            public SkillEventListener deserialize(CompoundTag tag) {
                throw unsupportedBinary(metadata, "event listener", "nbt");
            }

            @Override
            public CompoundTag serialize(SkillEventListener value) {
                return tag(metadata);
            }

            @Override
            public SkillEventListener deserialize(FriendlyByteBuf buffer) {
                throw unsupportedBinary(metadata, "event listener", "network");
            }

            @Override
            public void serialize(FriendlyByteBuf buffer, SkillEventListener value) {
                buffer.writeResourceLocation(metadata.id());
            }
        };
    }

    public static FloatFunction.Serializer floatFunction(PSTSerializerMetadata metadata) {
        return new FloatFunction.Serializer() {
            @Override
            public FloatFunction<?> createDefaultInstance() {
                return new PlaceholderFloatFunction(metadata, this);
            }

            @Override
            public FloatFunction<?> deserialize(JsonObject json) {
                throw unsupportedJson(metadata, "numeric value provider", json);
            }

            @Override
            public void serialize(JsonObject json, FloatFunction<?> value) {
            }

            @Override
            public FloatFunction<?> deserialize(CompoundTag tag) {
                throw unsupportedBinary(metadata, "numeric value provider", "nbt");
            }

            @Override
            public CompoundTag serialize(FloatFunction<?> value) {
                return tag(metadata);
            }

            @Override
            public FloatFunction<?> deserialize(FriendlyByteBuf buffer) {
                throw unsupportedBinary(metadata, "numeric value provider", "network");
            }

            @Override
            public void serialize(FriendlyByteBuf buffer, FloatFunction<?> value) {
                buffer.writeResourceLocation(metadata.id());
            }
        };
    }

    public static SkillRequirement.Serializer skillRequirement(PSTSerializerMetadata metadata) {
        return new SkillRequirement.Serializer() {
            @Override
            public SkillRequirement<?> createDefaultInstance() {
                return new PlaceholderSkillRequirement(metadata, this);
            }

            @Override
            public SkillRequirement<?> deserialize(JsonObject json) {
                throw unsupportedJson(metadata, "skill requirement", json);
            }

            @Override
            public void serialize(JsonObject json, SkillRequirement<?> value) {
            }

            @Override
            public SkillRequirement<?> deserialize(CompoundTag tag) {
                throw unsupportedBinary(metadata, "skill requirement", "nbt");
            }

            @Override
            public CompoundTag serialize(SkillRequirement<?> value) {
                return tag(metadata);
            }

            @Override
            public SkillRequirement<?> deserialize(FriendlyByteBuf buffer) {
                throw unsupportedBinary(metadata, "skill requirement", "network");
            }

            @Override
            public void serialize(FriendlyByteBuf buffer, SkillRequirement<?> value) {
                buffer.writeResourceLocation(metadata.id());
            }
        };
    }

    public static ItemBonus.Serializer itemBonus(PSTSerializerMetadata metadata) {
        return new ItemBonus.Serializer() {
            @Override
            public ItemBonus<?> createDefaultInstance() {
                return new PlaceholderItemBonus(metadata, this);
            }

            @Override
            public ItemBonus<?> deserialize(JsonObject json) {
                throw unsupportedJson(metadata, "item bonus", json);
            }

            @Override
            public void serialize(JsonObject json, ItemBonus<?> value) {
            }

            @Override
            public ItemBonus<?> deserialize(CompoundTag tag) {
                throw unsupportedBinary(metadata, "item bonus", "nbt");
            }

            @Override
            public CompoundTag serialize(ItemBonus<?> value) {
                return tag(metadata);
            }

            @Override
            public ItemBonus<?> deserialize(FriendlyByteBuf buffer) {
                throw unsupportedBinary(metadata, "item bonus", "network");
            }

            @Override
            public void serialize(FriendlyByteBuf buffer, ItemBonus<?> value) {
                buffer.writeResourceLocation(metadata.id());
            }
        };
    }

    private static CompoundTag tag(PSTSerializerMetadata metadata) {
        CompoundTag tag = new CompoundTag();
        tag.putString("passivestjs_id", metadata.id().toString());
        return tag;
    }

    private static JsonParseException unsupportedJson(PSTSerializerMetadata metadata, String label, JsonObject json) {
        return new JsonParseException(
                unsupportedMessage(metadata, label, "json") + " Payload: " + json
        );
    }

    private static UnsupportedOperationException unsupportedBinary(
            PSTSerializerMetadata metadata,
            String label,
            String channel
    ) {
        return new UnsupportedOperationException(unsupportedMessage(metadata, label, channel));
    }

    private static String unsupportedMessage(PSTSerializerMetadata metadata, String label, String channel) {
        return "PassiveSTJS custom " + label + " runtime is not implemented for '" + metadata.id()
                + "' in family '" + metadata.family().name().toLowerCase() + "' during " + channel
                + " deserialization. The entry is available for KubeJS registry metadata and editor stubs only.";
    }

    private static final class PlaceholderSkillBonus implements SkillBonus<PlaceholderSkillBonus> {
        private final PSTSerializerMetadata metadata;
        private final SkillBonus.Serializer serializer;

        private PlaceholderSkillBonus(PSTSerializerMetadata metadata, SkillBonus.Serializer serializer) {
            this.metadata = metadata;
            this.serializer = serializer;
        }

        @Override
        public boolean canMerge(SkillBonus<?> other) {
            return false;
        }

        @Override
        public PlaceholderSkillBonus merge(SkillBonus<?> other) {
            return copy();
        }

        @Override
        public PlaceholderSkillBonus copy() {
            return new PlaceholderSkillBonus(metadata, serializer);
        }

        @Override
        public PlaceholderSkillBonus multiply(double value) {
            return copy();
        }

        @Override
        public SkillBonus.Serializer getSerializer() {
            return serializer;
        }

        @Override
        public MutableComponent getTooltip() {
            return PSTRuntimeTooltipSupport.standaloneEffectTooltip(new PSTRuntimeNode(metadata, new JsonObject()));
        }

        @Override
        public boolean isPositive() {
            return true;
        }

        @Override
        public void addEditorWidgets(SkillTreeEditor editor, int x, Consumer<PlaceholderSkillBonus> consumer) {
        }
    }

    private static final class PlaceholderLivingMultiplier implements LivingMultiplier {
        private final PSTSerializerMetadata metadata;
        private final LivingMultiplier.Serializer serializer;

        private PlaceholderLivingMultiplier(PSTSerializerMetadata metadata, LivingMultiplier.Serializer serializer) {
            this.metadata = metadata;
            this.serializer = serializer;
        }

        @Override
        public float getValue(LivingEntity entity) {
            return 0.0F;
        }

        @Override
        public LivingMultiplier.Serializer getSerializer() {
            return serializer;
        }

        @Override
        public MutableComponent getTooltip(MutableComponent component, SkillBonus.Target target) {
            return PSTRuntimeTooltipSupport.wrappedPrefixTooltip(
                    new PSTRuntimeNode(metadata, new JsonObject()),
                    component,
                    target,
                    () -> PSTRuntimeTranslationKeys.describe(metadata, component)
            );
        }
    }

    private static final class PlaceholderLivingCondition implements LivingEntityPredicate {
        private final PSTSerializerMetadata metadata;
        private final LivingEntityPredicate.Serializer serializer;

        private PlaceholderLivingCondition(PSTSerializerMetadata metadata, LivingEntityPredicate.Serializer serializer) {
            this.metadata = metadata;
            this.serializer = serializer;
        }

        @Override
        public boolean test(LivingEntity entity) {
            return false;
        }

        @Override
        public MutableComponent getTooltip(MutableComponent component, SkillBonus.Target target) {
            return PSTRuntimeTooltipSupport.wrappedPrefixTooltip(
                    new PSTRuntimeNode(metadata, new JsonObject()),
                    component,
                    target,
                    () -> PSTRuntimeTranslationKeys.describe(metadata, component)
            );
        }

        @Override
        public LivingEntityPredicate.Serializer getSerializer() {
            return serializer;
        }
    }

    private static final class PlaceholderDamageCondition implements DamageCondition {
        private final PSTSerializerMetadata metadata;
        private final DamageCondition.Serializer serializer;

        private PlaceholderDamageCondition(PSTSerializerMetadata metadata, DamageCondition.Serializer serializer) {
            this.metadata = metadata;
            this.serializer = serializer;
        }

        @Override
        public boolean met(DamageSource damageSource) {
            return false;
        }

        @Override
        public MutableComponent getTooltip() {
            return PSTRuntimeTooltipSupport.standalonePrefixTooltip(new PSTRuntimeNode(metadata, new JsonObject()));
        }

        @Override
        public DamageCondition.Serializer getSerializer() {
            return serializer;
        }
    }

    private static final class PlaceholderItemCondition implements ItemStackPredicate {
        private final PSTSerializerMetadata metadata;
        private final ItemStackPredicate.Serializer serializer;

        private PlaceholderItemCondition(PSTSerializerMetadata metadata, ItemStackPredicate.Serializer serializer) {
            this.metadata = metadata;
            this.serializer = serializer;
        }

        @Override
        public boolean test(ItemStack stack) {
            return false;
        }

        @Override
        public Component getTooltip() {
            return PSTRuntimeTooltipSupport.standalonePrefixTooltip(new PSTRuntimeNode(metadata, new JsonObject()));
        }

        @Override
        public ItemStackPredicate.Serializer getSerializer() {
            return serializer;
        }
    }

    private static final class PlaceholderEnchantmentCondition implements EnchantmentCondition {
        private final PSTSerializerMetadata metadata;
        private final EnchantmentCondition.Serializer serializer;

        private PlaceholderEnchantmentCondition(PSTSerializerMetadata metadata, EnchantmentCondition.Serializer serializer) {
            this.metadata = metadata;
            this.serializer = serializer;
        }

        @Override
        public boolean met(EnchantmentCategory category) {
            return false;
        }

        @Override
        public EnchantmentCondition.Serializer getSerializer() {
            return serializer;
        }
    }

    private static final class PlaceholderEventListener implements SkillEventListener {
        private final PSTSerializerMetadata metadata;
        private final SkillEventListener.Serializer serializer;

        private PlaceholderEventListener(PSTSerializerMetadata metadata, SkillEventListener.Serializer serializer) {
            this.metadata = metadata;
            this.serializer = serializer;
        }

        @Override
        public MutableComponent getTooltip(Component component) {
            return PSTRuntimeTooltipSupport.wrappedPrefixTooltip(
                    new PSTRuntimeNode(metadata, new JsonObject()),
                    component,
                    SkillBonus.Target.PLAYER,
                    () -> PSTRuntimeTranslationKeys.describe(metadata, component)
            );
        }

        @Override
        public SkillBonus.Target getTarget() {
            return SkillBonus.Target.PLAYER;
        }

        @Override
        public SkillEventListener.Serializer getSerializer() {
            return serializer;
        }

        @Override
        public void addEditorWidgets(SkillTreeEditor editor, Consumer<SkillEventListener> consumer) {
        }
    }

    private static final class PlaceholderFloatFunction implements FloatFunction<PlaceholderFloatFunction> {
        private final PSTSerializerMetadata metadata;
        private final FloatFunction.Serializer serializer;

        private PlaceholderFloatFunction(PSTSerializerMetadata metadata, FloatFunction.Serializer serializer) {
            this.metadata = metadata;
            this.serializer = serializer;
        }

        @Override
        public float apply(LivingEntity entity) {
            return 0.0F;
        }

        @Override
        public MutableComponent getMultiplierTooltip(SkillBonus.Target target, float value, Component component) {
            return PSTRuntimeTranslationKeys.describe(
                    PSTRuntimeTranslationKeys.descriptionId(metadata) + ".multiplier",
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
                    PSTRuntimeTranslationKeys.descriptionId(metadata) + ".condition",
                    component,
                    logic.name().toLowerCase(),
                    formatNumber(value)
            );
        }

        @Override
        public MutableComponent getRequirementTooltip(FloatFunctionEntityPredicate.Logic logic, float value) {
            return PSTRuntimeTranslationKeys.describe(
                    PSTRuntimeTranslationKeys.descriptionId(metadata) + ".requirement",
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

    private static final class PlaceholderSkillRequirement implements SkillRequirement<PlaceholderSkillRequirement> {
        private final PSTSerializerMetadata metadata;
        private final SkillRequirement.Serializer serializer;

        private PlaceholderSkillRequirement(PSTSerializerMetadata metadata, SkillRequirement.Serializer serializer) {
            this.metadata = metadata;
            this.serializer = serializer;
        }

        @Override
        public boolean test(Player player) {
            return false;
        }

        @Override
        public MutableComponent getTooltip() {
            return PSTRuntimeTooltipSupport.skillRequirementTooltip(new PSTRuntimeNode(metadata, new JsonObject()));
        }

        @Override
        public void addEditorWidgets(SkillTreeEditor editor, Consumer<PlaceholderSkillRequirement> consumer) {
        }

        @Override
        public SkillRequirement.Serializer getSerializer() {
            return serializer;
        }

        @Override
        public PlaceholderSkillRequirement copy() {
            return new PlaceholderSkillRequirement(metadata, serializer);
        }
    }

    private static final class PlaceholderItemBonus implements ItemBonus<PlaceholderItemBonus> {
        private final PSTSerializerMetadata metadata;
        private final ItemBonus.Serializer serializer;

        private PlaceholderItemBonus(PSTSerializerMetadata metadata, ItemBonus.Serializer serializer) {
            this.metadata = metadata;
            this.serializer = serializer;
        }

        @Override
        public boolean canMerge(ItemBonus<?> other) {
            return false;
        }

        @Override
        public PlaceholderItemBonus merge(ItemBonus<?> other) {
            return copy();
        }

        @Override
        public PlaceholderItemBonus copy() {
            return new PlaceholderItemBonus(metadata, serializer);
        }

        @Override
        public PlaceholderItemBonus multiply(double value) {
            return copy();
        }

        @Override
        public ItemBonus.Serializer getSerializer() {
            return serializer;
        }

        @Override
        public void addTooltip(Consumer<MutableComponent> consumer) {
            consumer.accept(PSTRuntimeTooltipSupport.standaloneEffectTooltip(new PSTRuntimeNode(metadata, new JsonObject())));
        }

        @Override
        public boolean isPositive() {
            return true;
        }
    }
}
