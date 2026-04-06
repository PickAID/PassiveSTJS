package com.pickaid.passivestjs.kubejs.content;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.rhino.Wrapper;
import net.minecraft.resources.ResourceLocation;
import com.pickaid.passivestjs.kubejs.builder.JsonFragment;

import java.lang.reflect.Array;
import java.util.Map;

public final class JsonHelper {
    private JsonHelper() {
    }

    public static JsonObject typedObject(String type) {
        JsonObject json = new JsonObject();
        json.addProperty("type", type);
        return json;
    }

    public static JsonArray array() {
        return new JsonArray();
    }

    public static JsonElement copy(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return JsonNull.INSTANCE;
        }
        return new JsonParser().parse(element.toString());
    }

    public static JsonElement optionalElement(Object value) {
        JsonElement element = normalize(value);
        if (element == null || element.isJsonNull()) {
            return null;
        }
        return element;
    }

    public static JsonElement requireElement(Object value, String fieldName) {
        JsonElement element = normalize(value);
        if (element == null || element.isJsonNull()) {
            throw new IllegalArgumentException(fieldName + " can't be null");
        }
        return element;
    }

    public static JsonObject requireObject(Object value, String fieldName) {
        JsonElement element = requireElement(value, fieldName);
        if (!element.isJsonObject()) {
            throw new IllegalArgumentException(fieldName + " must be a JSON object");
        }
        return element.getAsJsonObject();
    }

    public static void addObject(JsonObject target, String key, Object value) {
        JsonElement element = optionalElement(value);
        if (element == null) {
            return;
        }
        if (!element.isJsonObject()) {
            throw new IllegalArgumentException(key + " must be a JSON object");
        }
        target.add(key, element);
    }

    public static String pretty(Object value) {
        return JsonIO.toPrettyString(requireElement(value, "value"));
    }

    private static JsonElement normalize(Object value) {
        Object unwrapped = Wrapper.unwrapped(value);
        if (unwrapped == null) {
            return JsonNull.INSTANCE;
        }
        if (unwrapped instanceof JsonFragment fragment) {
            return copy(fragment.toJson());
        }
        if (unwrapped instanceof JsonElement element) {
            return copy(element);
        }
        if (unwrapped instanceof ResourceLocation resourceLocation) {
            return JsonIO.of(resourceLocation.toString());
        }
        if (unwrapped instanceof Map<?, ?> map) {
            JsonObject object = new JsonObject();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                object.add(String.valueOf(entry.getKey()), normalize(entry.getValue()));
            }
            return object;
        }
        if (unwrapped instanceof Iterable<?> iterable) {
            JsonArray array = new JsonArray();
            for (Object element : iterable) {
                array.add(normalize(element));
            }
            return array;
        }
        if (unwrapped.getClass().isArray()) {
            JsonArray array = new JsonArray();
            int length = Array.getLength(unwrapped);
            for (int i = 0; i < length; i++) {
                array.add(normalize(Array.get(unwrapped, i)));
            }
            return array;
        }

        JsonElement element = JsonIO.copy(JsonIO.of(unwrapped));
        return element == null ? JsonNull.INSTANCE : element;
    }
}
