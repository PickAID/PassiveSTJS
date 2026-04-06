package com.pickaid.passivestjs.kubejs.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.rhino.Wrapper;
import net.minecraft.resources.ResourceLocation;
import com.pickaid.passivestjs.kubejs.content.JsonHelper;
import com.pickaid.passivestjs.kubejs.content.Requirements;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SkillBuilder implements JsonFragment {
    private static final String LESSER_BACKGROUND = "skilltree:textures/icons/background/lesser.png";
    private static final String CLASS_BACKGROUND = "skilltree:textures/icons/background/class.png";
    private static final String DEFAULT_BORDER = "skilltree:textures/tooltip/lesser.png";
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
    private String title;
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
        this.title = getNullableString(json, "title");
        this.titleColor = getNullableString(json, "titleColor");
        this.positionX = getDouble(json, "positionX", 0.0D);
        this.positionY = getDouble(json, "positionY", 0.0D);
        int defaultButtonSize = this.startingPoint ? 24 : 16;
        this.buttonSize = getInt(json, "buttonSize", defaultButtonSize);
        this.customButtonSize = this.buttonSize != defaultButtonSize;
        addAll(bonuses, json.getAsJsonArray("bonuses"));
        addAll(requirements, json.getAsJsonArray("requirements"));
        addAll(description, json.getAsJsonArray("description"));
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

    public SkillBuilder title(String value) {
        this.title = value;
        return this;
    }

    public SkillBuilder titleColor(String value) {
        this.titleColor = value;
        return this;
    }

    public SkillBuilder background(String value) {
        this.backgroundTexture = value;
        this.customBackground = true;
        return this;
    }

    public SkillBuilder icon(String value) {
        this.iconTexture = value;
        return this;
    }

    public SkillBuilder border(String value) {
        this.borderTexture = value;
        return this;
    }

    public SkillBuilder position(Number x, Number y) {
        this.positionX = x.doubleValue();
        this.positionY = y.doubleValue();
        return this;
    }

    public SkillBuilder buttonSize(Number value) {
        this.buttonSize = value.intValue();
        this.customButtonSize = true;
        return this;
    }

    public SkillBuilder tag(String value) {
        if (value != null && !value.isBlank()) {
            this.tags.add(value);
        }
        return this;
    }

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

    public SkillBuilder bonus(Object value) {
        bonuses.add(value);
        return this;
    }

    public SkillBuilder bonuses(Object values) {
        addMany(bonuses, values);
        return this;
    }

    public SkillBuilder requirement(Object value) {
        requirements.add(value);
        return this;
    }

    public SkillBuilder requirements(Object values) {
        addMany(requirements, values);
        return this;
    }

    public SkillBuilder requiresSkill(Object skillId) {
        requirements.add(Requirements.INSTANCE.learnedSkill(skillId));
        return this;
    }

    public SkillBuilder descriptionLine(Object line) {
        if (line != null) {
            description.add(line);
        }
        return this;
    }

    public SkillBuilder descriptionLines(Object lines) {
        addMany(description, lines);
        return this;
    }

    public SkillBuilder connect(Object value) {
        directConnections.add(resolveSkillId(value));
        return this;
    }

    public SkillBuilder longConnect(Object value) {
        longConnections.add(resolveSkillId(value));
        return this;
    }

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
        if (title != null && !title.isBlank()) {
            json.addProperty("title", title);
        }
        if (titleColor != null && !titleColor.isBlank()) {
            json.addProperty("titleColor", titleColor);
        }
        if (!description.isEmpty()) {
            json.add("description", toArray(description, "description"));
        }
        json.addProperty("positionX", positionX);
        json.addProperty("positionY", positionY);
        json.addProperty("buttonSize", buttonSize);
        json.addProperty("isStartingPoint", startingPoint);
        return json;
    }

    private static void addMany(List<Object> target, Object values) {
        Object unwrapped = Wrapper.unwrapped(values);
        if (unwrapped == null) {
            return;
        }
        if (unwrapped instanceof Iterable<?> iterable) {
            for (Object value : iterable) {
                target.add(value);
            }
            return;
        }
        if (unwrapped.getClass().isArray()) {
            int length = java.lang.reflect.Array.getLength(unwrapped);
            for (int i = 0; i < length; i++) {
                target.add(java.lang.reflect.Array.get(unwrapped, i));
            }
            return;
        }
        target.add(unwrapped);
    }

    private static JsonArray toArray(List<Object> values, String fieldName) {
        JsonArray array = new JsonArray();
        for (Object value : values) {
            array.add(JsonHelper.requireElement(value, fieldName));
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
