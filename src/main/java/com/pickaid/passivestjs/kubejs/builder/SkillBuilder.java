package com.pickaid.passivestjs.kubejs.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.pickaid.passivestjs.skilltree.PSTContentTitles;
import com.pickaid.passivestjs.kubejs.content.JsonHelper;
import com.pickaid.passivestjs.kubejs.id.PSTSkillBonusId;
import com.pickaid.passivestjs.kubejs.id.PSTSkillId;
import com.pickaid.passivestjs.kubejs.id.PSTSkillRequirementId;
import com.pickaid.passivestjs.kubejs.texture.PSTSkillFrameType;
import com.pickaid.passivestjs.kubejs.texture.PSTTexture;
import com.pickaid.passivestjs.kubejs.texture.PSTTooltipFrameType;
import com.pickaid.passivestjs.kubejs.registry.PSTRegistryTypeHandle;
import com.pickaid.passivestjs.schema.PSTNodeFamily;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.Wrapper;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class SkillBuilder implements JsonFragment {
    private static final String LESSER_BACKGROUND = PSTSkillFrameType.LESSER.texture().id();
    private static final String CLASS_BACKGROUND = PSTSkillFrameType.CLASS.texture().id();
    private static final String DEFAULT_BORDER = PSTTooltipFrameType.LESSER.texture().id();
    private static final String DEFAULT_ICON = "minecraft:textures/item/barrier.png";

    private final ResourceLocation id;
    private final List<Object> bonuses = new ArrayList<>();
    private final List<Object> requirements = new ArrayList<>();
    private final Set<ResourceLocation> directConnections = new LinkedHashSet<>();
    private final Set<ResourceLocation> longConnections = new LinkedHashSet<>();
    private final Set<ResourceLocation> oneWayConnections = new LinkedHashSet<>();
    private final Set<String> tags = new LinkedHashSet<>();
    private final List<Object> description = new ArrayList<>();
    private boolean startingPoint;
    private boolean customBackground;
    private boolean customButtonSize;
    private String backgroundTexture;
    private String iconTexture = DEFAULT_ICON;
    private String borderTexture = DEFAULT_BORDER;
    private Component title;
    private String titleColor;
    private double positionX;
    private double positionY;
    private int buttonSize = 16;

    public SkillBuilder(Object id, boolean startingPoint) {
        this.id = Ids.parse(id, "skillId");
        startingPoint(startingPoint);
    }

    private SkillBuilder(JsonObject json) {
        this.id = Ids.parse(json.get("id").getAsString(), "skillId");
        this.startingPoint = getBoolean(json, "isStartingPoint", false);
        String defaultBackground = this.startingPoint ? CLASS_BACKGROUND : LESSER_BACKGROUND;
        this.backgroundTexture = getString(json, "backgroundTexture", defaultBackground);
        this.customBackground = !Objects.equals(this.backgroundTexture, defaultBackground);
        this.iconTexture = getString(json, "iconTexture", DEFAULT_ICON);
        this.borderTexture = getString(json, "borderTexture", DEFAULT_BORDER);
        Component explicitTitle = PSTContentTitles.readExplicit(json);
        this.title = explicitTitle != null ? explicitTitle : literalComponent(getNullableString(json, "title"));
        this.titleColor = getNullableString(json, "titleColor");
        this.positionX = getDouble(json, "positionX", 0.0D);
        this.positionY = getDouble(json, "positionY", 0.0D);
        int defaultButtonSize = this.startingPoint ? 24 : 16;
        this.buttonSize = getInt(json, "buttonSize", defaultButtonSize);
        this.customButtonSize = this.buttonSize != defaultButtonSize;
        addAll(bonuses, json.getAsJsonArray("bonuses"));
        addAll(requirements, json.getAsJsonArray("requirements"));
        addAllDescription(description, json.getAsJsonArray("description"));
        addAllIds(directConnections, json.getAsJsonArray("directConnections"));
        addAllIds(longConnections, json.getAsJsonArray("longConnections"));
        addAllIds(oneWayConnections, json.getAsJsonArray("oneWayConnections"));
        addAllStrings(tags, json.getAsJsonArray("tags"));
    }

    public static SkillBuilder fromJson(JsonObject json) {
        return new SkillBuilder(json);
    }

    public String id() {
        return id.toString();
    }

    public ResourceLocation idLocation() {
        return id;
    }

    @Info("Marks this skill as a starting point and applies PST default starting visuals when not overridden.")
    public SkillBuilder startingPoint(boolean value) {
        this.startingPoint = value;
        if (!customBackground) {
            this.backgroundTexture = value ? CLASS_BACKGROUND : LESSER_BACKGROUND;
        }
        if (!customButtonSize) {
            this.buttonSize = value ? 24 : 16;
        }
        return this;
    }

    @HideFromJS
    public SkillBuilder titleLiteral(String value) {
        this.title = literalComponent(value);
        return this;
    }

    @Info("Sets the display title component for this skill.")
    public SkillBuilder title(Component value) {
        this.title = PSTContentTitles.copy(value);
        return this;
    }

    @Info("Clears the literal title so PST uses its native title translation key. PST does not support arbitrary title keys in skill JSON.")
    @HideFromJS
    public SkillBuilder titleKey(String translationKey) {
        this.title = null;
        return this;
    }

    @HideFromJS
    public String nativeTitleKey() {
        return "skill." + id.getNamespace() + "." + id.getPath() + ".name";
    }

    @HideFromJS
    public String nativeDescriptionKey(int index) {
        return "skill." + id.getNamespace() + "." + id.getPath() + ".desc." + index;
    }

    @Info("Sets the title color for this skill.")
    public SkillBuilder titleColor(String value) {
        this.titleColor = value;
        return this;
    }

    @HideFromJS
    public SkillBuilder background(String value) {
        return background(PSTTexture.parse(value));
    }

    @Info("Sets the background texture for this skill button from a raw texture resource.")
    public SkillBuilder background(PSTTexture value) {
        this.backgroundTexture = value.id();
        this.customBackground = true;
        return this;
    }

    @HideFromJS
    public SkillBuilder icon(String value) {
        return icon(PSTTexture.parse(value));
    }

    @Info("Sets the icon texture for this skill from a raw texture resource.")
    public SkillBuilder icon(PSTTexture value) {
        this.iconTexture = value.id();
        return this;
    }

    @HideFromJS
    public SkillBuilder border(String value) {
        return border(PSTTexture.parse(value));
    }

    @Info("Sets the tooltip border texture for this skill from a raw texture resource.")
    public SkillBuilder border(PSTTexture value) {
        this.borderTexture = value.id();
        return this;
    }

    @HideFromJS
    public SkillBuilder frame(String value) {
        return frame(PSTSkillFrameType.parse(value));
    }

    @Info("Applies one of Passive Skill Tree's built-in skill button background presets.")
    public SkillBuilder frame(PSTSkillFrameType value) {
        this.backgroundTexture = value.texture().id();
        this.customBackground = true;
        return this;
    }

    @HideFromJS
    public SkillBuilder tooltipFrame(String value) {
        return tooltipFrame(PSTTooltipFrameType.parse(value));
    }

    @Info("Applies one of Passive Skill Tree's built-in tooltip frame presets.")
    public SkillBuilder tooltipFrame(PSTTooltipFrameType value) {
        this.borderTexture = value.texture().id();
        return this;
    }

    @Info("Sets the absolute skill position in PST layout coordinates.")
    public SkillBuilder position(Number x, Number y) {
        this.positionX = x.doubleValue();
        this.positionY = y.doubleValue();
        return this;
    }

    @Info("Sets position from polar coordinates (distance, angle degrees).")
    public SkillBuilder positionPolar(Number distance, Number angleDegrees) {
        double radians = Math.toRadians(angleDegrees.doubleValue());
        double radialDistance = distance.doubleValue();
        this.positionX = Math.cos(radians) * radialDistance;
        this.positionY = Math.sin(radians) * radialDistance;
        return this;
    }

    @Info("Sets the skill button size.")
    public SkillBuilder buttonSize(Number value) {
        this.buttonSize = value.intValue();
        this.customButtonSize = true;
        return this;
    }

    @Info("Adds one tag used by PST tree limitations and categorization.")
    public SkillBuilder tag(String value) {
        if (value != null && !value.isBlank()) {
            this.tags.add(value);
        }
        return this;
    }

    @HideFromJS
    public SkillBuilder tags(Object values) {
        Object unwrapped = Wrapper.unwrapped(values);
        if (unwrapped instanceof Iterable<?> iterable) {
            for (Object value : iterable) {
                tag(String.valueOf(value));
            }
        } else if (unwrapped != null && unwrapped.getClass().isArray()) {
            int length = java.lang.reflect.Array.getLength(unwrapped);
            for (int i = 0; i < length; i++) {
                tag(String.valueOf(java.lang.reflect.Array.get(unwrapped, i)));
            }
        } else if (unwrapped != null) {
            tag(String.valueOf(unwrapped));
        }
        return this;
    }

    @Info("Adds a prebuilt skill bonus builder.")
    public SkillBuilder bonus(BonusBuilder value) {
        bonuses.add(value);
        return this;
    }

    @Info(value = "Creates a skill bonus from a registry handle and configures it inline.", params = {
            @Param(name = "type", value = "The skill bonus type handle."),
            @Param(name = "consumer", value = "The callback that configures the created skill bonus builder.")
    })
    public SkillBuilder bonus(PSTRegistryTypeHandle<BonusBuilder> type, Consumer<BonusBuilder> consumer) {
        return bonus(type.create(consumer));
    }

    @Info(value = "Creates a skill bonus from a typed bonus id and configures it inline.", params = {
            @Param(name = "typeId", value = "The skill bonus type id."),
            @Param(name = "consumer", value = "The callback that configures the created skill bonus builder.")
    })
    public SkillBuilder bonus(PSTSkillBonusId typeId, Consumer<BonusBuilder> consumer) {
        BonusBuilder builder = new BonusBuilder(PSTSkillBonusId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return bonus(builder);
    }

    @Info(value = "Creates a schema-driven skill bonus by typed bonus id and configures it inline.", params = {
            @Param(name = "typeId", value = "The skill bonus type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields on the node writer.")
    })
    public SkillBuilder bonusSchema(PSTSkillBonusId typeId, Consumer<PSTNodeWriter> consumer) {
        PSTNodeWriter writer = new PSTNodeWriter(PSTNodeFamily.SKILL_BONUS, PSTSkillBonusId.parse(typeId).location());
        if (consumer != null) {
            consumer.accept(writer);
        }
        return bonus(writer);
    }

    @HideFromJS
    public SkillBuilder bonus(Object value) {
        bonuses.add(value);
        return this;
    }

    @HideFromJS
    public SkillBuilder bonuses(Object values) {
        addMany(bonuses, values);
        return this;
    }

    @Info("Adds a prebuilt skill requirement builder.")
    public SkillBuilder requirement(RequirementBuilder value) {
        requirements.add(value);
        return this;
    }

    @Info(value = "Creates a skill requirement from a registry handle and configures it inline.", params = {
            @Param(name = "type", value = "The skill requirement type handle."),
            @Param(name = "consumer", value = "The callback that configures the created skill requirement builder.")
    })
    public SkillBuilder requirement(PSTRegistryTypeHandle<RequirementBuilder> type, Consumer<RequirementBuilder> consumer) {
        return requirement(type.create(consumer));
    }

    @Info(value = "Creates a skill requirement from a typed requirement id and configures it inline.", params = {
            @Param(name = "typeId", value = "The skill requirement type id."),
            @Param(name = "consumer", value = "The callback that configures the created skill requirement builder.")
    })
    public SkillBuilder requirement(PSTSkillRequirementId typeId, Consumer<RequirementBuilder> consumer) {
        RequirementBuilder builder = new RequirementBuilder(PSTSkillRequirementId.parse(typeId).id());
        if (consumer != null) {
            consumer.accept(builder);
        }
        return requirement(builder);
    }

    @Info(value = "Creates a schema-driven skill requirement by typed requirement id and configures it inline.", params = {
            @Param(name = "typeId", value = "The skill requirement type id."),
            @Param(name = "consumer", value = "The callback that sets schema fields on the node writer.")
    })
    public SkillBuilder requirementSchema(PSTSkillRequirementId typeId, Consumer<PSTNodeWriter> consumer) {
        PSTNodeWriter writer = new PSTNodeWriter(PSTNodeFamily.SKILL_REQUIREMENT, PSTSkillRequirementId.parse(typeId).location());
        if (consumer != null) {
            consumer.accept(writer);
        }
        return requirement(writer);
    }

    @HideFromJS
    public SkillBuilder requirement(Object value) {
        requirements.add(value);
        return this;
    }

    @HideFromJS
    public SkillBuilder requirements(Object values) {
        addMany(requirements, values);
        return this;
    }

    @Info("Adds a learned-skill requirement for the given skill id.")
    public SkillBuilder requiresSkill(PSTSkillId skillId) {
        requirements.add(new RequirementBuilder("skilltree:learned_skill").skill(PSTSkillId.parse(skillId).location()));
        return this;
    }

    @HideFromJS
    public SkillBuilder requiresSkill(Object skillId) {
        requirements.add(new RequirementBuilder("skilltree:learned_skill").skill(skillId));
        return this;
    }

    @HideFromJS
    public SkillBuilder descriptionLineLiteral(String line) {
        Object normalized = normalizeDescriptionEntry(line);
        if (normalized != null) {
            description.add(normalized);
        }
        return this;
    }

    @Info("Adds one component description line.")
    public SkillBuilder descriptionLine(Component line) {
        Object normalized = normalizeDescriptionEntry(line);
        if (normalized != null) {
            description.add(normalized);
        }
        return this;
    }

    @Info("Adds one explicit translation-component description line.")
    @HideFromJS
    public SkillBuilder descriptionKey(String translationKey) {
        if (translationKey == null || translationKey.isBlank()) {
            return this;
        }
        description.add(JsonHelper.translationText(translationKey));
        return this;
    }

    @HideFromJS
    public SkillBuilder descriptionLines(Object lines) {
        addMany(description, lines, SkillBuilder::normalizeDescriptionEntry);
        return this;
    }

    @Info("Adds a direct connection to another skill id.")
    public SkillBuilder connect(PSTSkillId value) {
        directConnections.add(PSTSkillId.parse(value).location());
        return this;
    }

    @HideFromJS
    public SkillBuilder connectId(ResourceLocation value) {
        return connect(PSTSkillId.of(value));
    }

    @HideFromJS
    public SkillBuilder connect(Object value) {
        directConnections.add(resolveSkillId(value));
        return this;
    }

    @Info("Adds a long connection to another skill id.")
    public SkillBuilder longConnect(PSTSkillId value) {
        longConnections.add(PSTSkillId.parse(value).location());
        return this;
    }

    @HideFromJS
    public SkillBuilder longConnectId(ResourceLocation value) {
        return longConnect(PSTSkillId.of(value));
    }

    @HideFromJS
    public SkillBuilder longConnect(Object value) {
        longConnections.add(resolveSkillId(value));
        return this;
    }

    @Info("Adds a one-way connection to another skill id.")
    public SkillBuilder oneWayConnect(PSTSkillId value) {
        oneWayConnections.add(PSTSkillId.parse(value).location());
        return this;
    }

    @HideFromJS
    public SkillBuilder oneWayConnectId(ResourceLocation value) {
        return oneWayConnect(PSTSkillId.of(value));
    }

    @HideFromJS
    public SkillBuilder oneWayConnect(Object value) {
        oneWayConnections.add(resolveSkillId(value));
        return this;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id());
        json.add("bonuses", toArray(bonuses, "bonuses"));
        json.add("requirements", toArray(requirements, "requirements"));
        json.add("directConnections", toIdArray(directConnections));
        json.add("longConnections", toIdArray(longConnections));
        json.add("oneWayConnections", toIdArray(oneWayConnections));
        json.add("tags", toStringArray(tags));
        json.addProperty("backgroundTexture", backgroundTexture);
        json.addProperty("iconTexture", iconTexture);
        json.addProperty("borderTexture", borderTexture);
        if (title != null) {
            String literalTitle = PSTContentTitles.literalText(title);
            if (literalTitle != null) {
                json.addProperty("title", literalTitle);
            }
            PSTContentTitles.writeJson(json, title);
        }
        if (titleColor != null && !titleColor.isBlank()) {
            json.addProperty("titleColor", titleColor);
        }
        if (!description.isEmpty()) {
            json.add("description", toDescriptionArray());
        }
        json.addProperty("positionX", positionX);
        json.addProperty("positionY", positionY);
        json.addProperty("buttonSize", buttonSize);
        json.addProperty("isStartingPoint", startingPoint);
        return json;
    }

    private static void addMany(List<Object> target, Object values) {
        addMany(target, values, Wrapper::unwrapped);
    }

    private static void addMany(List<Object> target, Object values, java.util.function.Function<Object, Object> normalizer) {
        Object unwrapped = Wrapper.unwrapped(values);
        if (unwrapped == null) {
            return;
        }
        if (unwrapped instanceof Iterable<?> iterable) {
            for (Object value : iterable) {
                Object normalized = normalizer.apply(value);
                if (normalized != null) {
                    target.add(normalized);
                }
            }
            return;
        }
        if (unwrapped.getClass().isArray()) {
            int length = java.lang.reflect.Array.getLength(unwrapped);
            for (int i = 0; i < length; i++) {
                Object normalized = normalizer.apply(java.lang.reflect.Array.get(unwrapped, i));
                if (normalized != null) {
                    target.add(normalized);
                }
            }
            return;
        }
        Object normalized = normalizer.apply(unwrapped);
        if (normalized != null) {
            target.add(normalized);
        }
    }

    private static JsonArray toArray(List<Object> values, String fieldName) {
        JsonArray array = new JsonArray();
        for (Object value : values) {
            array.add(JsonHelper.requireElement(value, fieldName));
        }
        return array;
    }

    private JsonArray toDescriptionArray() {
        JsonArray array = new JsonArray();
        for (Object value : description) {
            array.add(JsonHelper.requireElement(value, "description"));
        }
        return array;
    }

    private static JsonArray toIdArray(Set<ResourceLocation> values) {
        JsonArray array = new JsonArray();
        for (ResourceLocation value : values) {
            array.add(new JsonPrimitive(value.toString()));
        }
        return array;
    }

    private static JsonArray toStringArray(Set<String> values) {
        JsonArray array = new JsonArray();
        for (String value : values) {
            array.add(new JsonPrimitive(value));
        }
        return array;
    }

    private static void addAll(List<Object> target, JsonArray source) {
        if (source == null) {
            return;
        }
        for (JsonElement element : source) {
            target.add(JsonHelper.copy(element));
        }
    }

    private static void addAllDescription(List<Object> target, JsonArray source) {
        if (source == null) {
            return;
        }
        for (JsonElement element : source) {
            Object normalized = normalizeDescriptionEntry(JsonHelper.copy(element));
            if (normalized != null) {
                target.add(normalized);
            }
        }
    }

    private static void addAllIds(Set<ResourceLocation> target, JsonArray source) {
        if (source == null) {
            return;
        }
        for (JsonElement element : source) {
            if (element.isJsonPrimitive()) {
                target.add(Ids.parse(element.getAsString(), "skillId"));
            }
        }
    }

    private static void addAllStrings(Set<String> target, JsonArray source) {
        if (source == null) {
            return;
        }
        for (JsonElement element : source) {
            if (element.isJsonPrimitive()) {
                target.add(element.getAsString());
            }
        }
    }

    private static String getString(JsonObject json, String key, String fallback) {
        return json.has(key) && json.get(key).isJsonPrimitive() ? json.get(key).getAsString() : fallback;
    }

    private static String getNullableString(JsonObject json, String key) {
        return json.has(key) && json.get(key).isJsonPrimitive() ? json.get(key).getAsString() : null;
    }

    private static String normalizeText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    private static Component literalComponent(String value) {
        String normalized = normalizeText(value);
        return normalized == null ? null : Component.literal(normalized);
    }

    private static Object normalizeDescriptionEntry(Object value) {
        Object unwrapped = Wrapper.unwrapped(value);
        if (unwrapped == null) {
            return null;
        }
        if (unwrapped instanceof JsonPrimitive primitive && primitive.isString()) {
            String text = normalizeText(primitive.getAsString());
            return text == null ? null : JsonHelper.literalText(text);
        }
        if (unwrapped instanceof CharSequence text) {
            String normalized = normalizeText(text.toString());
            return normalized == null ? null : JsonHelper.literalText(normalized);
        }
        return unwrapped;
    }

    private static boolean getBoolean(JsonObject json, String key, boolean fallback) {
        return json.has(key) && json.get(key).isJsonPrimitive() ? json.get(key).getAsBoolean() : fallback;
    }

    private static int getInt(JsonObject json, String key, int fallback) {
        return json.has(key) && json.get(key).isJsonPrimitive() ? json.get(key).getAsInt() : fallback;
    }

    private static double getDouble(JsonObject json, String key, double fallback) {
        return json.has(key) && json.get(key).isJsonPrimitive() ? json.get(key).getAsDouble() : fallback;
    }

    private static ResourceLocation resolveSkillId(Object value) {
        Object unwrapped = Wrapper.unwrapped(value);
        if (unwrapped instanceof SkillBuilder builder) {
            return builder.idLocation();
        }
        return Ids.parse(unwrapped, "skillId");
    }
}
